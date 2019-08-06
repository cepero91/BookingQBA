package com.infinitum.bookingqba.view.map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.CafeBarMapMarkerBinding;
import com.infinitum.bookingqba.databinding.FragmentMapBinding;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.base.BaseMapFragment;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.wshunli.assets.CopyAssets;
import com.wshunli.assets.CopyCreator;
import com.wshunli.assets.CopyListener;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Color;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.layers.LocationLayer;
import org.oscim.layers.marker.ClusterMarkerRenderer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerLayer;
import org.oscim.layers.marker.MarkerRenderer;
import org.oscim.layers.marker.MarkerRendererFactory;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.MapRenderer;
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
import io.reactivex.CompletableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


import static com.infinitum.bookingqba.util.Constants.MAP_PATH;
import static com.infinitum.bookingqba.util.Constants.USER_GPS;
import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;


public class MapFragment extends BaseMapFragment implements ItemizedLayer.OnItemGestureListener<MarkerItem>, View.OnClickListener {

    //Location variables
    private Location lastKnowLocation;
    private Location currentLocation;

    // Rxjava
    private CompositeDisposable compositeDisposable;
    private Disposable disposable;

    @Inject
    SharedPreferences sharedPreferences;

    // View model
    @Inject
    ViewModelFactory viewModelFactory;
    private RentViewModel rentViewModel;

    private OnFragmentMapInteraction fragmentMapInteraction;

    //Databinding
    private FragmentMapBinding mapBinding;
    private CafeBarMapMarkerBinding cafeBarMapMarkerBinding;

    //Marker
    private MarkerSymbol mFocusMarker;
    private ItemizedLayer<MarkerItem> mMarkerLayer;
    private int lastMarkerFocus = -1;
    private int currentMarkerIndex = -1;

    private static final String GEORENTS_PARAM = "param1";
    private static final String IS_FROM_DETAIL_PARAM = "param2";
    private ArrayList<GeoRent> geoRentArrayList;
    private boolean isFromDetail;


    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(@Nullable ArrayList<GeoRent> geoRentList, boolean isFromDetail) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(GEORENTS_PARAM, geoRentList);
        args.putBoolean(IS_FROM_DETAIL_PARAM, isFromDetail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            geoRentArrayList = getArguments().getParcelableArrayList(GEORENTS_PARAM);
            isFromDetail = getArguments().getBoolean(IS_FROM_DETAIL_PARAM, false);
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

        mapBinding.ivLocation.setOnClickListener(this);

        mapBinding.setIsLoading(true);

        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        setupMarkerView();

    }

    private void setupMarkerView() {
        cafeBarMapMarkerBinding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.cafe_bar_map_marker, mapBinding.flContentMap, false);
        cafeBarMapMarkerBinding.contentCafebar.setOnClickListener(v -> {
            if (fragmentMapInteraction != null) {
                if (currentMarkerIndex != -1 && geoRentArrayList != null && geoRentArrayList.size() > 0)
                    fragmentMapInteraction.onMapInteraction(geoRentArrayList.get(currentMarkerIndex));
            }
        });
    }

    @Override
    protected void initializeMarker() {
        disposable = rentViewModel.getGeoRent().subscribeOn(Schedulers.io())
                .subscribe(listResource -> setupMarkers(listResource.data), Timber::e);
        compositeDisposable.add(disposable);
    }

    private void setupMarkers(List<GeoRent> geoRentList) {
        if (geoRentArrayList == null) {
            geoRentArrayList = (ArrayList<GeoRent>) geoRentList;
        }
        //Marker
        Bitmap bitmapPoi = drawableToBitmap(getResources().getDrawable(R.drawable.ic_map_pin));
        MarkerSymbol symbol = new MarkerSymbol(bitmapPoi, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);

        Bitmap bitmapFocus = drawableToBitmap(getResources().getDrawable(R.drawable.ic_map_pin_focus));
        mFocusMarker = new MarkerSymbol(bitmapFocus, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);

        MarkerRendererFactory markerRendererFactory = new MarkerRendererFactory() {
            @Override
            public MarkerRenderer create(MarkerLayer markerLayer) {
                return new ClusterMarkerRenderer(markerLayer, symbol, new ClusterMarkerRenderer.ClusterStyle(Color.WHITE, Color.parseColor("#607D8B"))) {
                    @Override
                    protected Bitmap getClusterBitmap(int size) {
                        // Can customize cluster bitmap here
                        return super.getClusterBitmap(size);
                    }
                };
            }
        };

        mMarkerLayer = new ItemizedLayer<>(map, new ArrayList<>(), markerRendererFactory, this);
        map.layers().add(mMarkerLayer);

        List<MarkerItem> pts = new ArrayList<>();
        if (geoRentList != null && geoRentList.size() > 0) {
            for (GeoRent geoRent : geoRentList) {
                pts.add(new MarkerItem(geoRent.getId(), geoRent.getName(), geoRent.getGeoPoint()));
            }
        }
        mMarkerLayer.addItems(pts);
    }

    @Override
    protected void showViews() {
        mapBinding.setIsLoading(false);
        mapBinding.progressPvLinear.stop();
        if (isFromDetail && geoRentArrayList != null) {
            MapPosition mapPosition = new MapPosition();
            mapPosition.setPosition(geoRentArrayList.get(0).getGeoPoint());
            mapPosition.setZoomLevel(17);
            map.setMapPosition(mapPosition);
            mMarkerLayer.getItemList().get(0).setMarker(mFocusMarker);
            showMarkerView(0);
        } else {
            // Position Habana
            map.setMapPosition(23.1165, -82.3882, 2 << 12);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentMapInteraction) {
            fragmentMapInteraction = (OnFragmentMapInteraction) context;
            compositeDisposable = new CompositeDisposable();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentMapInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        cafeBarMapMarkerBinding = null;
        fragmentMapInteraction = null;
        compositeDisposable.clear();
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerItem item) {
        if (item.getMarker() == null) {
            if (lastMarkerFocus != -1) {
                mMarkerLayer.getItemList().get(lastMarkerFocus).setMarker(null);
            }
            lastMarkerFocus = index;
            item.setMarker(mFocusMarker);
            map.animator().animateTo(1000, getMapPositionWithZoom(item.getPoint(), 17), Easing.Type.SINE_IN);
            showMarkerView(index);
        } else {
            item.setMarker(null);
            lastMarkerFocus = -1;
            map.animator().animateTo(500, getMapPositionWithZoom(item.getPoint(), 15), Easing.Type.SINE_IN);
            hideMarkerView();
        }
        return true;
    }

    private MapPosition getMapPositionWithZoom(GeoPoint geoPoint, int zoom) {
        MapPosition mapPosition = new MapPosition();
        mapPosition.setPosition(geoPoint);
        mapPosition.setZoomLevel(zoom);
        return mapPosition;
    }

    @Override
    public boolean onItemLongPress(int index, MarkerItem item) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.e("onDestroy");
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private void showMarkerView(int markerIndex) {
        if (geoRentArrayList != null && geoRentArrayList.size() > 0) {
            GeoRent geoRent = geoRentArrayList.get(markerIndex);
            cafeBarMapMarkerBinding.setItem(geoRent);
            currentMarkerIndex = markerIndex;
            if (mapBinding.flMarker.getChildCount() > 0) {
                mapBinding.flMarker.removeAllViews();
                mapBinding.flMarker.addView(cafeBarMapMarkerBinding.getRoot());
            } else {
                mapBinding.flMarker.addView(cafeBarMapMarkerBinding.getRoot());
            }
            mapBinding.ivLocation.hide();
            ObjectAnimator markerAnimator = ObjectAnimator.ofFloat(mapBinding.flMarker, "translationY", 150f, 0f);
            markerAnimator.setDuration(500);
            markerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            markerAnimator.start();
        }
    }

    private void hideMarkerView() {
        currentMarkerIndex = -1;
        ObjectAnimator markerAnimator = ObjectAnimator.ofFloat(mapBinding.flMarker, "translationY", 0f, 150f);
        markerAnimator.setDuration(500);
        markerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        markerAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mapBinding.flMarker.removeAllViews();
                mapBinding.ivLocation.show();
            }
        });
        markerAnimator.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_location:
                if (lastKnowLocation != null) {
                    map.animator().animateTo(1000, getMapPositionWithZoom(new GeoPoint(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude()), 15), Easing.Type.SINE_IN);
                } else {
                    fragmentMapInteraction.getLastLocation();
                }
        }
    }

    public interface OnFragmentMapInteraction {

        void onMapInteraction(GeoRent geoRent);

        void getLastLocation();

    }


    public void updateCurrentLocation(Location location) {
        currentLocation = location;
        lastKnowLocation = currentLocation;
        locationLayer.get().setEnabled(true);
        locationLayer.get().setPosition(location.getLatitude(), location.getLongitude(), location.getAccuracy());
    }


}
