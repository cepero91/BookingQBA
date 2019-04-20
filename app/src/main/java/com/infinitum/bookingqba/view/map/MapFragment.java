package com.infinitum.bookingqba.view.map;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentMapBinding;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.wshunli.assets.CopyAssets;
import com.wshunli.assets.CopyCreator;
import com.wshunli.assets.CopyListener;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.GeoPoint;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


import static com.infinitum.bookingqba.util.Constants.MAP_PATH;
import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;


public class MapFragment extends Fragment implements ItemizedLayer.OnItemGestureListener<MarkerItem> {

    private CompositeDisposable compositeDisposable;
    private Disposable disposable;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    ViewModelFactory viewModelFactory;

    private RentViewModel rentViewModel;

    private OnFragmentMapInteraction mListener;

    private FragmentMapBinding mapBinding;

    private MapScaleBar mapScaleBar;

    private String mapFilePath;

    //Marker
    private MarkerSymbol mFocusMarker;
    private ItemizedLayer<MarkerItem> mMarkerLayer;

    private static final String GEORENTS_PARAM = "param1";
    private ArrayList<GeoRent> geoRentArrayList;


    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(@Nullable ArrayList<GeoRent> geoRentList) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(GEORENTS_PARAM, geoRentList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            geoRentArrayList = getArguments().getParcelableArrayList(GEORENTS_PARAM);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        return mapBinding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        super.onActivityCreated(savedInstanceState);

        mapBinding.setIsLoading(true);

        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        mapFilePath = sharedPreferences.getString(MAP_PATH, "");

        initializeMap();
    }

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
                        Timber.e("progress copy files %s", copyProgress);
                    }

                    @Override
                    public void completed(CopyCreator copyCreator, java.util.Map<File, Boolean> results) {
                        Timber.i("File copy completed");
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString(((File) results.keySet().toArray()[0]).getAbsolutePath(), "");
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

            // Vector layer
            VectorTileLayer tileLayer = mapBinding.mapview.map().setBaseMap(tileSource);

            // Building layer
            mapBinding.mapview.map().layers().add(new BuildingLayer(mapBinding.mapview.map(), tileLayer));

            // Label layer
            mapBinding.mapview.map().layers().add(new LabelLayer(mapBinding.mapview.map(), tileLayer));

            // Render theme
            mapBinding.mapview.map().setTheme(VtmThemes.OSMAGRAY);

            // Scale bar
            mapScaleBar = new DefaultMapScaleBar(mapBinding.mapview.map());
            MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mapBinding.mapview.map(), mapScaleBar);
            mapScaleBarLayer.getRenderer().setPosition(GLViewport.Position.BOTTOM_LEFT);
            mapScaleBarLayer.getRenderer().setOffset(5 * CanvasAdapter.getScale(), 0);
            mapBinding.mapview.map().layers().add(mapScaleBarLayer);

            if (geoRentArrayList != null) {
                setupMarkers(geoRentArrayList);
            } else {
                initializeMarker();
            }

            // Note: this map position is specific to Habana area
            mapBinding.mapview.map().setMapPosition(23.1165, -82.3882, 2 << 12);
        }
    }

    private void initializeMarker() {
        disposable = rentViewModel.getGeoRent().subscribeOn(Schedulers.io())
                .subscribe(listResource -> setupMarkers(listResource.data), Timber::e);
        compositeDisposable.add(disposable);
    }

    private void setupMarkers(List<GeoRent> geoRentList) {
        //Marker
        Bitmap bitmapPoi = drawableToBitmap(getResources().getDrawable(R.drawable.ic_map_pin));
        MarkerSymbol symbol = new MarkerSymbol(bitmapPoi, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);
        mMarkerLayer = new ItemizedLayer<>(mapBinding.mapview.map(), new ArrayList<MarkerItem>(), symbol, this);
        mapBinding.mapview.map().layers().add(mMarkerLayer);

        List<MarkerItem> pts = new ArrayList<>();
        if (geoRentList != null && geoRentList.size() > 0) {
            for (GeoRent geoRent : geoRentList) {
                pts.add(new MarkerItem(geoRent.getId(), geoRent.getName(), geoRent.getGeoPoint()));
            }
        }
        mMarkerLayer.addItems(pts);
    }

    private void showViews() {
        mapBinding.vOverlap.bringToFront();
        mapBinding.setIsLoading(false);
        mapBinding.mapview.map().postDelayed(() -> mapBinding.vOverlap.animate()
                .alpha(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start(), 300);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentMapInteraction) {
            mListener = (OnFragmentMapInteraction) context;
            compositeDisposable = new CompositeDisposable();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        compositeDisposable.clear();
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerItem item) {
        Toast.makeText(getActivity(), "Marker tap\n" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onItemLongPress(int index, MarkerItem item) {
        Toast.makeText(getActivity(), "Marker long press\n" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        mapBinding.mapview.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapBinding.mapview.onDestroy();
        if(disposable!=null && !disposable.isDisposed()){
            disposable.dispose();
        }
    }


    public interface OnFragmentMapInteraction {
        void onMapInteraction();
    }
}
