package com.infinitum.bookingqba.view.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentMapBinding;
import com.infinitum.bookingqba.view.filter.FilterFragment;
import com.wshunli.assets.CopyAssets;
import com.wshunli.assets.CopyCreator;
import com.wshunli.assets.CopyListener;

import org.oscim.backend.CanvasAdapter;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.renderer.GLViewport;
import org.oscim.scalebar.DefaultMapScaleBar;
import org.oscim.scalebar.MapScaleBar;
import org.oscim.scalebar.MapScaleBarLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


import static com.infinitum.bookingqba.util.Constants.MAP_PATH;


public class MapFragment extends Fragment {

    private CompositeDisposable compositeDisposable;
    private Disposable disposable;

    @Inject
    SharedPreferences sharedPreferences;

    private OnFragmentMapInteraction mListener;

    private FragmentMapBinding mapBinding;

    private MapScaleBar mapScaleBar;

    private String mapFilePath;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
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

        mapFilePath = sharedPreferences.getString(MAP_PATH, "");

        if (mapFilePath.equals("")) {
            disposable = Completable.fromAction(this::copyAssetMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .andThen(Completable.fromAction(this::setupMapView))
                    .doOnComplete(this::showViews).subscribe();
            compositeDisposable.add(disposable);
        }else{
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
                        Timber.e("progress copy files %s",copyProgress);
                    }

                    @Override
                    public void completed(CopyCreator copyCreator, Map<File, Boolean> results) {
                        Timber.i("File copy completed");
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString(((File)results.keySet().toArray()[0]).getAbsolutePath(),"");
                        edit.apply();
                        mapFilePath = ((File)results.keySet().toArray()[0]).getAbsolutePath();
                    }

                    @Override
                    public void error(CopyCreator copyCreator, Throwable e) {
                        Timber.e(e);
                    }
                })
                .copy();
    }

    public void setupMapView(){
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
            mapBinding.mapview.map().setTheme(VtmThemes.DEFAULT);

            // Scale bar
            mapScaleBar = new DefaultMapScaleBar(mapBinding.mapview.map());
            MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mapBinding.mapview.map(), mapScaleBar);
            mapScaleBarLayer.getRenderer().setPosition(GLViewport.Position.BOTTOM_LEFT);
            mapScaleBarLayer.getRenderer().setOffset(5 * CanvasAdapter.getScale(), 0);
            mapBinding.mapview.map().layers().add(mapScaleBarLayer);

            // Note: this map position is specific to Berlin area
            mapBinding.mapview.map().setMapPosition(23.1165, -82.3882, 1 << 12);

        }
    }

    private void showViews(){
        mapBinding.setIsLoading(false);
        new Handler().postDelayed(() -> mapBinding.setOverlay(false),500);
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

    public interface OnFragmentMapInteraction {
        void onMapInteraction();
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
    }
}
