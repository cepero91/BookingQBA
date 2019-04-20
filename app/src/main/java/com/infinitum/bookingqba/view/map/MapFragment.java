package com.infinitum.bookingqba.view.map;

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

    private OnFragmentMapInteraction mListener;

    private FragmentMapBinding mapBinding;

    private MapScaleBar mapScaleBar;

    private String mapFilePath;

    //Marker
    private MarkerSymbol mFocusMarker;
    private ItemizedLayer<MarkerItem> mMarkerLayer;


    private static double[] lat = new double[]{
            23.1266d,
            23.1175d,
            23.1145d,
            23.1105d
    };

    private static double[] lon = new double[]{
            -82.2802d,
            -82.1092d,
            -82.2532d,
            -82.1332d
    };


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
                    public void completed(CopyCreator copyCreator, java.util.Map<File, Boolean> results) {
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

            mapBinding.mapview.map().layers().add(new MapEventsReceiver(mapBinding.mapview.map()));

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

            //Marker
            Bitmap bitmapPoi = drawableToBitmap(getResources().getDrawable(R.drawable.ic_map_pin));
            MarkerSymbol symbol = new MarkerSymbol(bitmapPoi, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);
            mMarkerLayer = new ItemizedLayer<>(mapBinding.mapview.map(), new ArrayList<MarkerItem>(), symbol, this);
            mapBinding.mapview.map().layers().add(mMarkerLayer);

            List<MarkerItem> pts = new ArrayList<>();
            Random random = new Random();
            for (int i = 0;i < 4; i++){
                int randomLat = random.nextInt(4);
                int randomLon = random.nextInt(4);
                double latitud = lat[randomLat];
                double longitud = lon[randomLon];
                pts.add(new MarkerItem("MyMarker "+i, "", new GeoPoint(latitud, longitud)));
            }
            mMarkerLayer.addItems(pts);


            // Note: this map position is specific to Habana area
            mapBinding.mapview.map().setMapPosition(23.1165, -82.3882, 3 << 12);



        }
    }

    private void showViews(){
        mapBinding.vOverlap.bringToFront();
        mapBinding.setIsLoading(false);
        mapBinding.mapview.map().postDelayed(() -> mapBinding.vOverlap.animate()
                .alpha(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(500)
                .start(),100);
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
    }


    public interface OnFragmentMapInteraction {
        void onMapInteraction();
    }


    class MapEventsReceiver extends Layer implements GestureListener {

        MapEventsReceiver(Map map) {
            super(map);
        }

        @Override
        public boolean onGesture(Gesture g, MotionEvent e) {
            if (g instanceof Gesture.Tap) {
                GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
                Timber.e("latitud %s, longitud %s",p.getLatitude(),p.getLongitude());
                return true;
            }
            return false;
        }
    }
}
