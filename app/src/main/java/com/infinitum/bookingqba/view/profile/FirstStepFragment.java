package com.infinitum.bookingqba.view.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentFirstStepBinding;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.LocationHelpers;
import com.infinitum.bookingqba.view.interaction.OnStepFormEnd;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;
import com.wshunli.assets.CopyAssets;
import com.wshunli.assets.CopyCreator;
import com.wshunli.assets.CopyListener;

import org.oscim.android.cache.TileCache;
import org.oscim.android.tiling.source.mbtiles.MBTilesBitmapTileSource;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.event.Gesture;
import org.oscim.event.GestureListener;
import org.oscim.event.MotionEvent;
import org.oscim.layers.Layer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Map;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.MapRenderer;
import org.oscim.scalebar.DefaultMapScaleBar;
import org.oscim.scalebar.MapScaleBar;
import org.oscim.scalebar.MapScaleBarLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.bitmap.BitmapTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;
import org.oscim.utils.animation.Easing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.MAP_PATH;
import static com.infinitum.bookingqba.util.Constants.USER_GPS;
import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;

public class FirstStepFragment extends Fragment implements Step, ItemizedLayer.OnItemGestureListener<MarkerItem>,
        View.OnClickListener {

    @Inject
    SharedPreferences sharedPreferences;

    private FragmentFirstStepBinding binding;
    private OnStepFormEnd onStepFormEnd;
    private GeoPoint geoPointLocation;
    private boolean isLocationEmpty;
    private CompositeDisposable compositeDisposable;
    private Disposable disposable;

    private String mapFilePath;
    private MarkerSymbol userMarker;
    private ItemizedLayer<MarkerItem> mMarkerLayer;

    //Bitmap Map
    private BitmapTileSource mTileSource;
    protected BitmapTileLayer mBitmapLayer;
    private static final boolean USE_CACHE = false;
    private TileCache mCache;

    private double argLatitude;
    private double argLongitude;


    public FirstStepFragment() {
    }

    public static FirstStepFragment newInstance() {
        FirstStepFragment firstStepFragment = new FirstStepFragment();
        return firstStepFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_first_step, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.setIsLoading(true);
        binding.progressPvLinear.start();
        binding.ivLocation.setOnClickListener(this);

        mapFilePath = sharedPreferences.getString(MAP_PATH, "");

        initializeMap();

    }

    //---------------------------- LIFECYCLE

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        if (context instanceof OnStepFormEnd) {
            this.onStepFormEnd = (OnStepFormEnd) context;
            compositeDisposable = new CompositeDisposable();
        }
    }

    @Override
    public void onDetach() {
        this.onStepFormEnd = null;
        if(!disposable.isDisposed())
            disposable.dispose();
        compositeDisposable.clear();
        binding.mapview.onDestroy();
        super.onDetach();
    }

    @Override
    public void onResume() {
        binding.mapview.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        binding.mapview.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        onStepFormEnd = null;
        binding.mapview.onDestroy();
        super.onDestroyView();
    }

    //-------------------------------------- MAP
    private void initializeMap() {
        if (mapFilePath.equals("")) {
            disposable = Completable.fromAction(this::copyAssetMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .andThen(Completable.fromAction(this::setupMapView))
                    .doOnComplete(this::showViews).subscribe();
            compositeDisposable.add(disposable);
        } else {
            disposable = Completable.fromAction(this::setupMapView)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(this::showViews).subscribe();
            compositeDisposable.add(disposable);
//            setupMapView();
//            showViews();
        }
    }

    private void copyAssetMap() {
        CopyAssets.with(getActivity())
                .from("map")
                .setListener(new CopyListener() {
                    @Override
                    public void pending(CopyCreator copyCreator, String oriPath, String desPath, List<String> names) {

                    }

                    @Override
                    public void progress(CopyCreator copyCreator, File currentFile, int copyProgress) {
                    }

                    @Override
                    public void completed(CopyCreator copyCreator, java.util.Map<File, Boolean> results) {
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString(MAP_PATH, ((File) results.keySet().toArray()[0]).getAbsolutePath());
                        edit.apply();
                        mapFilePath = ((File) results.keySet().toArray()[0]).getAbsolutePath();
                    }

                    @Override
                    public void error(CopyCreator copyCreator, Throwable e) {
                        Timber.e(e);
                    }
                })
                .copy();
    }

    public void setupMapView() {
        MapFileTileSource tileSource = new MapFileTileSource();
        String mapPath = new File(mapFilePath).getAbsolutePath();
        if (tileSource.setMapFile(mapPath)) {

            binding.mapview.map().layers().add(new MapEventsReceiver(binding.mapview.map()));

            // Vector layer
            VectorTileLayer tileLayer = binding.mapview.map().setBaseMap(tileSource);

            // Building layer
            binding.mapview.map().layers().add(new BuildingLayer(binding.mapview.map(), tileLayer));

            // Label layer
            binding.mapview.map().layers().add(new LabelLayer(binding.mapview.map(), tileLayer));

            // Render theme
            binding.mapview.map().setTheme(VtmThemes.OSMARENDER);

            // Scale bar
            MapScaleBar mapScaleBar = new DefaultMapScaleBar(binding.mapview.map());
            MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(binding.mapview.map(), mapScaleBar);
            mapScaleBarLayer.getRenderer().setPosition(GLViewport.Position.BOTTOM_LEFT);
            mapScaleBarLayer.getRenderer().setOffset(5 * CanvasAdapter.getScale(), 0);
            binding.mapview.map().layers().add(mapScaleBarLayer);

            mMarkerLayer = new ItemizedLayer<>(binding.mapview.map(), new ArrayList<>(), userMarker, this);
            binding.mapview.map().layers().add(mMarkerLayer);

        }
    }

    private void showViews() {
        binding.setIsLoading(false);
        binding.mapview.map().setMapPosition(23.1165, -82.3882, 2 << 12);
        binding.progressPvLinear.stop();
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerItem item) {
        return false;
    }

    @Override
    public boolean onItemLongPress(int index, MarkerItem item) {
        return false;
    }

    class MapEventsReceiver extends Layer implements GestureListener {

        MapEventsReceiver(Map map) {
            super(map);
        }

        @Override
        public boolean onGesture(Gesture g, MotionEvent e) {
            if (g instanceof Gesture.LongPress) {
                GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
                setGeoPointLocation(p.getLatitude(), p.getLongitude());
                return true;
            }
            return false;
        }
    }


    public void setGeoPointLocation(double latitude, double longitude) {
        this.geoPointLocation = new GeoPoint(latitude,longitude);
        onStepFormEnd.barNavigationEnabled(true);
        updateUserTracking(latitude, longitude);
        onStepFormEnd.onLocationCatch(latitude, longitude);
    }

    public void setPasiveGeoPointLocation(double latitude, double longitude) {
        this.geoPointLocation = new GeoPoint(latitude,longitude);
        updateUserTracking(latitude, longitude);
        onStepFormEnd.barNavigationEnabled(true);
    }

    public void changeIconColor(boolean locationEnabled) {
        if (locationEnabled) {
            binding.ivLocation.setImageResource(R.drawable.ic_crosshairs_yellow);
        } else {
            binding.ivLocation.setImageResource(R.drawable.ic_crosshairs_grey);
        }
    }

    public void updateUserTracking(double lati, double longi) {
        Completable.fromAction(this::findAndRemoveLastUserTrack)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .andThen(Completable.fromAction(() -> addUserMarkerToMap(lati, longi)))
                .subscribe();
    }

    private void addUserMarkerToMap(double lati, double longi) {
        Bitmap bitmapUser = drawableToBitmap(getResources().getDrawable(R.drawable.ic_male));
        userMarker = new MarkerSymbol(bitmapUser, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);
        MarkerItem item = new MarkerItem(USER_GPS, "User", new GeoPoint(lati, longi));
        item.setMarker(userMarker);
        mMarkerLayer.addItem(item);
        binding.mapview.map().animator().animateTo(1000, getMapPositionWithZoom(item.getPoint(), 16), Easing.Type.SINE_IN);
    }

    private void findAndRemoveLastUserTrack() {
        List<MarkerItem> markerItemList = mMarkerLayer.getItemList();
        for (int i = 0; i < markerItemList.size(); i++) {
            if (markerItemList.get(i).getTitle().equals(USER_GPS)) {
                mMarkerLayer.removeItem(i);
                break;
            }
        }
    }

    private MapPosition getMapPositionWithZoom(GeoPoint geoPoint, int zoom) {
        MapPosition mapPosition = new MapPosition();
        mapPosition.setPosition(geoPoint);
        mapPosition.setZoomLevel(zoom);
        return mapPosition;
    }

    //--------------------------------------- STEP

    @Nullable
    @Override
    public VerificationError verifyStep() {
        if (geoPointLocation != null) {
            return null;
        } else {
            return new VerificationError("No se ha obtenido localizacion");
        }
    }

    @Override
    public void onSelected() {
        onStepFormEnd.barNavigationEnabled(false);
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        AlertUtils.showErrorTopToast(getActivity(), error.getErrorMessage());
    }

    //------------------------------------ EVENTS

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_location:
                onStepFormEnd.onLocationClick();
                break;
        }
    }
}
