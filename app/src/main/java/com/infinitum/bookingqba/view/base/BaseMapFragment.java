package com.infinitum.bookingqba.view.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.infinitum.bookingqba.R;
import com.wshunli.assets.CopyAssets;
import com.wshunli.assets.CopyCreator;
import com.wshunli.assets.CopyListener;

import org.oscim.android.MapView;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Color;
import org.oscim.layers.LocationLayer;
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
import org.oscim.tiling.source.mapfile.MapFileTileSource;

import java.io.File;
import java.lang.ref.WeakReference;
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

public abstract class BaseMapFragment extends Fragment {

    protected Disposable disposable;
    protected CompositeDisposable compositeDisposable;
    private String mapFilePath;

    protected MapView mapView;
    protected Map map;

    @Inject
    SharedPreferences sharedPreferences;
    protected WeakReference<LocationLayer> locationLayer;

    public BaseMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mapFilePath = sharedPreferences.getString(MAP_PATH, "");
        mapView = getActivity().findViewById(R.id.mapview);
        map = mapView.map();
        initializeMap();

    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
        mapView.map().layers().remove(locationLayer.get());
        mapView.onDestroy();
        super.onDetach();
    }

    private void initializeMap() {
        if (mapFilePath.equals("")) {
            disposable = Completable.fromAction(this::copyAssetMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .andThen(Completable.fromAction(this::setupMapView))
                    .andThen(Completable.fromAction(this::initializeMarker))
                    .doOnComplete(this::showViews).subscribe();
            compositeDisposable.add(disposable);
        } else {
            disposable = Completable.fromAction(this::setupMapView)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .andThen(Completable.fromAction(this::initializeMarker))
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
        MapRenderer.setBackgroundColor(Color.WHITE);
        MapFileTileSource tileSource = new MapFileTileSource();
        String mapPath = new File(mapFilePath).getAbsolutePath();
        if (tileSource.setMapFile(mapPath)) {

            locationLayer = new WeakReference<>(new LocationLayer(map));
            locationLayer.get().locationRenderer.setShader("location_1_reverse");
            locationLayer.get().locationRenderer.setColor(Color.parseColor("#F44336"));
            locationLayer.get().setEnabled(false);
            map.layers().add(locationLayer.get());


            map.viewport().setMinZoomLevel(10);

            // Vector layer
            VectorTileLayer tileLayer = map.setBaseMap(tileSource);

            // Building layer
            map.layers().add(new BuildingLayer(map, tileLayer));

            // Label layer
            map.layers().add(new LabelLayer(map, tileLayer));

            // Render theme
            map.setTheme(VtmThemes.DEFAULT);

            // Scale bar
            MapScaleBar mapScaleBar = new DefaultMapScaleBar(map);
            MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(map, mapScaleBar);
            mapScaleBarLayer.getRenderer().setPosition(GLViewport.Position.BOTTOM_LEFT);
            mapScaleBarLayer.getRenderer().setOffset(5 * CanvasAdapter.getScale(), 0);
            map.layers().add(mapScaleBarLayer);

        }
    }

    protected abstract void initializeMarker();

    protected abstract void showViews();

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.clear();
        mapView.map().layers().remove(locationLayer.get());
        mapView.onDestroy();
        super.onDestroyView();
    }
}
