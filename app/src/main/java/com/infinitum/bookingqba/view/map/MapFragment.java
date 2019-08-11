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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.CafeBarMapMarkerBinding;
import com.infinitum.bookingqba.databinding.FragmentMapBinding;
import com.infinitum.bookingqba.view.adapters.MapPoiAdapter;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.map.PoiItem;
import com.infinitum.bookingqba.view.base.BaseMapFragment;
import com.infinitum.bookingqba.view.widgets.CenterSmoothScroller;
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
import java.util.function.Predicate;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
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
    private MarkerSymbol rentMarkerPressed, rentMarkerUnpressed, userMarkerPressed, userMarkerUnpressed, poiMarkerPressed, poiMarkerUnpressed;
    private ItemizedLayer<MarkerItem> mMarkerLayer;
    private int lastMarkerFocus = -1;
    private int currentMarkerIndex = -1;
    private int userMarkerIndex = -1;

    private static final String GEORENTS_PARAM = "param1";
    private static final String IS_FROM_DETAIL_PARAM = "param2";
    private ArrayList<GeoRent> geoRentArrayList;
    private boolean isFromDetail;
    private boolean isRentPoiOpen;
    private MapPoiAdapter mapPoiAdapter;




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

    //------------------------------ LIFECYCLE ---------------------------

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
    public void onDestroyView() {
        mMarkerLayer.removeAllItems();
        mapView.onDestroy();
        compositeDisposable.clear();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.e("onDestroy");
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    //------------------------------------ METHODS -----------------------

    private void setupMarkerView() {
        isRentPoiOpen = false;
        cafeBarMapMarkerBinding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.cafe_bar_map_marker, mapBinding.flContentMap, false);
        cafeBarMapMarkerBinding.cvBtnView.setOnClickListener(v -> {
            if (fragmentMapInteraction != null) {
                if (currentMarkerIndex != -1 && geoRentArrayList != null && geoRentArrayList.size() > 0)
                    fragmentMapInteraction.onMapInteraction(geoRentArrayList.get(currentMarkerIndex));
            }
        });
        cafeBarMapMarkerBinding.flArrow.setOnClickListener(v -> {
            if (isRentPoiOpen) {
                isRentPoiOpen = false;
                cafeBarMapMarkerBinding.llPoiContent.setVisibility(View.GONE);
            } else {
                isRentPoiOpen = true;
                cafeBarMapMarkerBinding.llPoiContent.setVisibility(View.VISIBLE);
            }
        });
        cafeBarMapMarkerBinding.cbShowAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!isChecked){
                removeAllPoiMarker();
                mapPoiAdapter.hideAll();
            }else{
                mapPoiAdapter.showAll();
                addAfterRemoveAllPoiMarker();
            }
        });
    }

    private void showAllPoiMarker() {
        GeoRent geoRent = geoRentArrayList.get(currentMarkerIndex);
        if(geoRent!=null){
            MarkerItem poiMarker;
            for(PoiItem poiItem:geoRent.getPoiItems()){
                poiMarker = new MarkerItem(poiItem.getId(), "poi", poiItem.getName(), new GeoPoint(poiItem.getLatitude(),poiItem.getLongitude()));
                poiMarker.setMarker(poiMarkerUnpressed);
                mMarkerLayer.addItem(poiMarker);
            }
        }
    }

    @Override
    protected void initializeMarker() {
        disposable = rentViewModel.getGeoRent().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> setupMarkers(listResource.data), Timber::e);
        compositeDisposable.add(disposable);
    }

    private void setupMarkers(List<GeoRent> geoRentList) {
        if (geoRentArrayList == null) {
            geoRentArrayList = (ArrayList<GeoRent>) geoRentList;
        }
        //Marker
        Bitmap rentOff = drawableToBitmap(getResources().getDrawable(R.drawable.casa_placeholder_off));
        rentMarkerUnpressed = new MarkerSymbol(rentOff, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);

        Bitmap rentOn = drawableToBitmap(getResources().getDrawable(R.drawable.casa_placeholder_on));
        rentMarkerPressed = new MarkerSymbol(rentOn, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);

        Bitmap userOff = drawableToBitmap(getResources().getDrawable(R.drawable.user_placeholder_off));
        userMarkerUnpressed = new MarkerSymbol(userOff, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);

        Bitmap userOn = drawableToBitmap(getResources().getDrawable(R.drawable.user_placeholder_on));
        userMarkerPressed = new MarkerSymbol(userOn, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);

        Bitmap poiOff = drawableToBitmap(getResources().getDrawable(R.drawable.poi_placeholder_off));
        poiMarkerUnpressed = new MarkerSymbol(poiOff, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);

        Bitmap poiOn = drawableToBitmap(getResources().getDrawable(R.drawable.poi_placeholder_on));
        poiMarkerPressed = new MarkerSymbol(poiOn, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);

        mMarkerLayer = new ItemizedLayer<>(map, new ArrayList<>(), rentMarkerUnpressed, this);
        map.layers().add(mMarkerLayer);

        List<MarkerItem> pts = new ArrayList<>();
        if (geoRentList != null && geoRentList.size() > 0) {
            for (GeoRent geoRent : geoRentList) {
                pts.add(new MarkerItem(geoRent.getId(), "rent", geoRent.getName(), geoRent.getGeoPoint()));
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
            mapPosition.setZoomLevel(15);
            map.setMapPosition(mapPosition);
            mMarkerLayer.getItemList().get(0).setMarker(rentMarkerUnpressed);
            showMarkerView(0);
        } else {
            // Position Habana
            map.setMapPosition(23.1165, -82.3882, 2 << 12);
        }
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerItem item) {
        switch (item.title) {
            case "rent":
                rentMarkerClick(index, item);
                break;
            case "user":
                userMarkerClick(item);
                break;
            case "poi":
                poiMarkerClick(item);
                break;
        }
        return true;
    }

    private void poiMarkerClick(MarkerItem item) {
        int pos = mapPoiAdapter.posByUuid(item.uid.toString());
        if (pos != -1) {
            map.animator().animateTo(1000, getMapPositionWithZoom(item.getPoint(), 15), Easing.Type.SINE_IN);
            RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(cafeBarMapMarkerBinding.rvPoi.getContext());
            smoothScroller.setTargetPosition(pos);
            cafeBarMapMarkerBinding.rvPoi.getLayoutManager().startSmoothScroll(smoothScroller);
        }
    }

    private void rentMarkerClick(int index, MarkerItem item) {
        if (userMarkerIndex != -1)
            mMarkerLayer.getItemList().get(userMarkerIndex).setMarker(userMarkerUnpressed);
        if (item.getMarker() == null) {
            if (lastMarkerFocus != -1) {
                mMarkerLayer.getItemList().get(lastMarkerFocus).setMarker(null);
            }
            lastMarkerFocus = index;
            item.setMarker(rentMarkerPressed);
            map.animator().animateTo(1000, getMapPositionWithZoom(item.getPoint(), 16), Easing.Type.SINE_IN);
            showMarkerView(index);
        } else {
            item.setMarker(null);
            lastMarkerFocus = -1;
//            map.animator().animateTo(500, getMapPositionWithZoom(item.getPoint(), 15), Easing.Type.SINE_IN);
            hideMarkerView();
        }
    }

    private void userMarkerClick(MarkerItem item) {
        if (lastMarkerFocus != -1) {
            mMarkerLayer.getItemList().get(lastMarkerFocus).setMarker(null);
            hideMarkerView();
            lastMarkerFocus = -1;
        }
        if (item.getMarker() == userMarkerUnpressed) {
            item.setMarker(userMarkerPressed);
            map.animator().animateTo(1000, getMapPositionWithZoom(item.getPoint(), 15), Easing.Type.SINE_IN);
            //do something to show a view
        } else {
            item.setMarker(userMarkerUnpressed);
//            map.animator().animateTo(500, getMapPositionWithZoom(item.getPoint(), 15), Easing.Type.SINE_IN);
            //do something to hide a view
        }
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


    private void showMarkerView(int markerIndex) {
        mapBinding.vOverlap.setBackgroundResource(R.color.Black_20);
        if (geoRentArrayList != null && geoRentArrayList.size() > 0) {
            GeoRent geoRent = geoRentArrayList.get(markerIndex);
            cafeBarMapMarkerBinding.setItem(geoRent);
            currentMarkerIndex = markerIndex;
            if (mapBinding.flMarker.getChildCount() > 0) {
                mapBinding.flMarker.removeAllViews();
                mapBinding.flMarker.addView(cafeBarMapMarkerBinding.getRoot());
                cafeBarMapMarkerBinding.flRentContent.setVisibility(View.VISIBLE);
            } else {
                mapBinding.flMarker.addView(cafeBarMapMarkerBinding.getRoot());
                cafeBarMapMarkerBinding.flRentContent.setVisibility(View.VISIBLE);
            }
            mapBinding.ivLocation.hide();

            mapPoiAdapter = new MapPoiAdapter(geoRent.getPoiItems(), new MapPoiAdapter.MapPoiClick() {
                @Override
                public void onPoiSelected(PoiItem item, boolean selected) {
                    if (selected) {
                        GeoPoint geoPoint = new GeoPoint(item.getLatitude(), item.getLongitude());
                        map.animator().animateTo(1000, getMapPositionWithZoom(geoPoint, 15), Easing.Type.SINE_IN);
                        MarkerItem poiMarker = new MarkerItem(item.getId(), "poi", item.getName(), geoPoint);
                        poiMarker.setMarker(poiMarkerUnpressed);
                        mMarkerLayer.addItem(poiMarker);
                        map.render();
                    } else {
                        int pos = findPoiMarker(item.getId());
                        if (pos != -1) {
                            mMarkerLayer.removeItem(pos);
                            map.render();
                        }
                    }
                }
            });
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            cafeBarMapMarkerBinding.rvPoi.setLayoutManager(linearLayoutManager);
            cafeBarMapMarkerBinding.rvPoi.setAdapter(mapPoiAdapter);
        }
    }

    private void hideMarkerView() {
        mapBinding.vOverlap.setBackgroundResource(R.color.White_0);
        cafeBarMapMarkerBinding.flRentContent.setVisibility(View.GONE);
        cafeBarMapMarkerBinding.llPoiContent.setVisibility(View.GONE);
        mapBinding.flMarker.removeAllViews();
        mapBinding.ivLocation.show();
        currentMarkerIndex = -1;
        removeAllPoiMarker();
    }

    private void removeAllPoiMarker() {
        disposable = Single.just(toRemovePoiMarker())
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(markerItems-> Completable.fromAction(()->removePoiMarker(markerItems)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()->map.render(), Timber::e);
        compositeDisposable.add(disposable);
    }

    private void addAfterRemoveAllPoiMarker() {
        disposable = Single.just(toRemovePoiMarker())
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(markerItems-> Completable.fromAction(()->removePoiMarker(markerItems)))
                .andThen(Completable.fromAction(this::showAllPoiMarker))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()->map.render(), Timber::e);
        compositeDisposable.add(disposable);
    }

    private void removePoiMarker(List<MarkerItem> markerItems) {
        for(MarkerItem markerItem: markerItems){
            mMarkerLayer.removeItem(markerItem);
        }
    }

    private List<MarkerItem> toRemovePoiMarker() {
        List<MarkerItem> markerItemList = new ArrayList<>();
        for (MarkerItem markerItem : mMarkerLayer.getItemList()) {
            if (markerItem.getTitle().equals("poi")) {
              markerItemList.add(markerItem);
            }
        }
        return markerItemList;
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

    public void updateCurrentLocation(Location location) {
        currentLocation = location;
        lastKnowLocation = currentLocation;
        MarkerItem userMarker = new MarkerItem("user", "", new GeoPoint(location.getLatitude(), location.getLongitude()));
        userMarker.setMarker(userMarkerUnpressed);
        mMarkerLayer.addItem(userMarker);
        map.render();
        if (userMarkerIndex == -1) {
            userMarkerIndex = mMarkerLayer.getItemList().size() - 1;
        } else {
            mMarkerLayer.removeItem(userMarkerIndex);
            userMarkerIndex = mMarkerLayer.getItemList().size() - 1;
        }
    }

    private int findPosUserMarker() {
        int pos = -1;
        List<MarkerItem> markerItemList = mMarkerLayer.getItemList();
        for (int i = 0; i < markerItemList.size(); i++) {
            if (markerItemList.get(i).title.equals("user")) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    private int findPoiMarker(String uuid) {
        int pos = -1;
        List<MarkerItem> markerItemList = mMarkerLayer.getItemList();
        for (int i = 0; i < markerItemList.size(); i++) {
            if (markerItemList.get(i).title.equals("poi") && markerItemList.get(i).uid.equals(uuid)) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    public boolean isMarkerLayerLoaded() {
        return mMarkerLayer != null && mMarkerLayer.getItemList() != null;
    }


    //--------------------------------------- INTERFACE -----------------------
    public interface OnFragmentMapInteraction {

        void onMapInteraction(GeoRent geoRent);

        void getLastLocation();

    }


}
