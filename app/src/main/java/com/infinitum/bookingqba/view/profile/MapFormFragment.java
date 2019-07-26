package com.infinitum.bookingqba.view.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentMapFormBinding;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.view.widgets.DialogLocationConfirmView;
import com.wshunli.assets.CopyAssets;
import com.wshunli.assets.CopyCreator;
import com.wshunli.assets.CopyListener;

import org.oscim.android.MapView;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.event.Gesture;
import org.oscim.event.GestureListener;
import org.oscim.event.MotionEvent;
import org.oscim.layers.Layer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Map;
import org.oscim.renderer.GLViewport;
import org.oscim.scalebar.DefaultMapScaleBar;
import org.oscim.scalebar.MapScaleBar;
import org.oscim.scalebar.MapScaleBarLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
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

public class MapFormFragment extends Fragment implements ItemizedLayer.OnItemGestureListener<MarkerItem>
        , View.OnClickListener {

    @Inject
    SharedPreferences sharedPreferences;

    private FragmentMapFormBinding binding;
    private MapRentLocation mapRentLocation;
    private GeoPoint geoPointLocation;
    private boolean isLocationEmpty;
    private CompositeDisposable compositeDisposable;
    private Disposable disposable;

    private String mapFilePath;
    private MarkerSymbol userMarker;
    private ItemizedLayer<MarkerItem> mMarkerLayer;

    private String argLatitude;
    private String argLongitude;
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";

    private MapEventsReceiver mapEventsReceiver;
    private MapView mapView;


    public MapFormFragment() {
    }

    public static MapFormFragment newInstance(String latitude, String longitude) {
        MapFormFragment mapFormFragment = new MapFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LATITUDE, latitude);
        bundle.putString(LONGITUDE, longitude);
        mapFormFragment.setArguments(bundle);
        return mapFormFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            argLatitude = getArguments().getString(LATITUDE);
            argLongitude = getArguments().getString(LONGITUDE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map_form, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.ivLocation.setOnClickListener(this);

        binding.setIsLoading(true);
        binding.progressPvCircularInout.start();

        mapFilePath = sharedPreferences.getString(MAP_PATH, "");

        this.mapView = binding.mapview;

        initializeMap();

    }

    //---------------------------- LIFECYCLE

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        compositeDisposable = new CompositeDisposable();
        if (context instanceof MapRentLocation)
            mapRentLocation = (MapRentLocation) context;
    }

    @Override
    public void onDetach() {
        Timber.e("Fragment onDetach");
        if (!disposable.isDisposed())
            disposable.dispose();
        compositeDisposable.clear();
        mapView.onDestroy();
        super.onDetach();
    }

    @Override
    public void onResume() {
        Timber.e("Fragment onResume");
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        Timber.e("Fragment onPause");
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        Timber.e("Fragment onDestroyView");
        if (!disposable.isDisposed())
            disposable.dispose();
        compositeDisposable.clear();
        mapView.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
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

            mapEventsReceiver = new MapEventsReceiver(mapView.map());

            mapView.map().layers().add(mapEventsReceiver);

            // Vector layer
            VectorTileLayer tileLayer = mapView.map().setBaseMap(tileSource);

            // Building layer
            mapView.map().layers().add(new BuildingLayer(mapView.map(), tileLayer));

            // Label layer
            mapView.map().layers().add(new LabelLayer(mapView.map(), tileLayer));

            // Render theme
            mapView.map().setTheme(VtmThemes.DEFAULT);

            // Scale bar
            MapScaleBar mapScaleBar = new DefaultMapScaleBar(mapView.map());
            MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mapView.map(), mapScaleBar);
            mapScaleBarLayer.getRenderer().setPosition(GLViewport.Position.BOTTOM_LEFT);
            mapScaleBarLayer.getRenderer().setOffset(5 * CanvasAdapter.getScale(), 0);
            mapView.map().layers().add(mapScaleBarLayer);

            mMarkerLayer = new ItemizedLayer<>(mapView.map(), new ArrayList<>(), userMarker, this);
            mapView.map().layers().add(mMarkerLayer);

        }
    }

    private void showViews() {
        binding.setIsLoading(false);
        if (!argLatitude.equals("") && !argLongitude.equals("")) {
            setPasiveGeoPointLocation(Float.parseFloat(argLatitude), Float.parseFloat(argLongitude));
        } else {
            mapView.map().setMapPosition(23.1165, -82.3882, 2 << 12);
        }
        binding.progressPvCircularInout.stop();
        new Handler().postDelayed(() -> AlertUtils.showCFInfoAlert(getActivity(),
                "Presiona prolongadamente o pulse el boton de localizar"),2000);
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerItem item) {
        return false;
    }

    @Override
    public boolean onItemLongPress(int index, MarkerItem item) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_location:
                mapRentLocation.onLocationButtonClick();
        }
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
        this.geoPointLocation = new GeoPoint(latitude, longitude);
        updateUserTracking(latitude, longitude);
        mapRentLocation.showLocationConfirmDialog();
    }


    public void setPasiveGeoPointLocation(double latitude, double longitude) {
        this.geoPointLocation = new GeoPoint(latitude, longitude);
        updateUserTracking(latitude, longitude);
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
        mapView.map().animator().animateTo(1000, getMapPositionWithZoom(item.getPoint(), 14), Easing.Type.SINE_IN);
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


}
