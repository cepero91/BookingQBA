package com.infinitum.bookingqba.view.map;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.CafeBarMapMarkerBinding;
import com.infinitum.bookingqba.databinding.FragmentMapBinding;
import com.infinitum.bookingqba.databinding.NearMapLayoutBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.view.adapters.MapPoiAdapter;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.map.PoiItem;
import com.infinitum.bookingqba.view.base.BaseMapFragment;
import com.infinitum.bookingqba.view.widgets.CenterSmoothScroller;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.mikhaellopez.rxanimation.RxAnimation;
import com.squareup.picasso.Picasso;

import org.mapsforge.core.model.LatLong;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.vector.PathLayer;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.utils.animation.Easing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


import static com.infinitum.bookingqba.util.Constants.THUMB_HEIGHT;
import static com.infinitum.bookingqba.util.Constants.THUMB_WIDTH;
import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;


public class MapFragment extends BaseMapFragment implements ItemizedLayer.OnItemGestureListener<MarkerItem>
        , View.OnClickListener, MapPoiAdapter.MapPoiClick {

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
    private NearMapLayoutBinding nearMapLayoutBinding;

    //Marker
    private MarkerSymbol rentMarkerPressed, rentMarkerUnpressed, userMarkerPressed, userMarkerUnpressed, poiMarkerPressed, poiMarkerUnpressed;
    private ItemizedLayer<MarkerItem> mMarkerLayer;
    private int lastMarkerFocus = -1;
    private int currentMarkerIndex = -1;
    private int userMarkerIndex = -1;

    public static final String USER_INFO = "user_info";
    public static final String RENT_INFO = "rent_info";
    private static final String GEORENTS_PARAM = "param1";
    private static final String IS_FROM_DETAIL_PARAM = "param2";
    private ArrayList<GeoRent> geoRentArrayList;
    private boolean isFromDetail;
    private boolean isRentPoiOpen;
    private MapPoiAdapter mapPoiAdapter;
    private LatLong from;
    private PoiItem lastPoiSelected;
    private PathLayer pathLayer;
    private LatLong to;
    private RendererRecyclerViewAdapter nearAdapter;
    private boolean isRouteActive, isRouteOpen, isNearOpen;


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
    protected void publishProgress(String msgProgress) {
        mapBinding.tvProgress.setText(msgProgress);
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
        isNearOpen = false;
        isRentPoiOpen = false;
        isRouteOpen = false;
        cafeBarMapMarkerBinding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.cafe_bar_map_marker, mapBinding.flContentMap, false);
        cafeBarMapMarkerBinding.cvBtnView.setOnClickListener(this);
        cafeBarMapMarkerBinding.ivPointOfInterest.setOnClickListener(this);
        cafeBarMapMarkerBinding.cbShowAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                removeAllPoiMarker();
                mapPoiAdapter.hideAll();
            } else {
                mapPoiAdapter.showAll();
                addAfterRemoveAllPoiMarker();
            }
        });
        cafeBarMapMarkerBinding.ivRoute.setOnClickListener(this);
        cafeBarMapMarkerBinding.tvRbMyRent.setOnClickListener(this);
        cafeBarMapMarkerBinding.tvRbMyLocation.setOnClickListener(this);
        cafeBarMapMarkerBinding.tvRbMyRentTo.setOnClickListener(this);
        cafeBarMapMarkerBinding.tvRbPoi.setOnClickListener(this);
        cafeBarMapMarkerBinding.tvBtnPlayRoute.setOnClickListener(this);
        cafeBarMapMarkerBinding.tvBtnRemoveRoute.setOnClickListener(this);
        mapBinding.ivNearRent.setOnClickListener(this);
    }

    private void showOrHideRouteContent(boolean b, int gone, String s) {
        isRouteOpen = b;
        cafeBarMapMarkerBinding.llRouteContent.setVisibility(gone);
        cafeBarMapMarkerBinding.ivRoute.setImageTintList(ColorStateList.valueOf(Color.parseColor(s)));
        if (isRouteActive)
            cafeBarMapMarkerBinding.llRouteBtnBar.setVisibility(gone);
    }

    private void showOrHidePoiContent(boolean b, int gone, String s) {
        isRentPoiOpen = b;
        cafeBarMapMarkerBinding.llPoiContent.setVisibility(gone);
        cafeBarMapMarkerBinding.ivPointOfInterest.setImageTintList(ColorStateList.valueOf(Color.parseColor(s)));
    }

    private void findPath(LatLong from, LatLong to) {
        disposable = Single.just(map.layers().contains(pathLayer))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(aBoolean -> {
                    if (aBoolean) {
                        map.layers().remove(pathLayer);
                    }
                    return Single.just(calcPath(from, to));
                })
                .map(pathWrapper -> {
                    if (!pathWrapper.hasErrors()) {
                        createDistanceMessage(pathWrapper);
                        return createPathLayer(pathWrapper);
                    } else {
                        return null;
                    }
                })
                .subscribe(pathLayer -> {
                    if (pathLayer != null) {
                        isRouteActive = true;
                        map.layers().add(pathLayer);
                        map.render();
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void createDistanceMessage(PathWrapper pathWrapper) {
        String routeMsg = String.format(getString(R.string.distance_msg), (int) (pathWrapper.getDistance() / 100) / 10f, getMinFromLong(pathWrapper.getTime()));
        cafeBarMapMarkerBinding.tvRouteValidation.setTextColor(getResources().getColor(R.color.material_color_blue_grey_500));
        cafeBarMapMarkerBinding.tvRouteValidation.setText(routeMsg);
        cafeBarMapMarkerBinding.tvRouteValidation.setVisibility(View.VISIBLE);
        cafeBarMapMarkerBinding.tvBtnRemoveRoute.setVisibility(View.VISIBLE);
    }

    private String getMinFromLong(long time) {
        return String.format(getString(R.string.min_msg), TimeUnit.MILLISECONDS.toMinutes(time));
    }

    private PathWrapper calcPath(LatLong from, LatLong to) {
        GHRequest req = new GHRequest(from.latitude, from.longitude, to.latitude, to.longitude).
                setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI);
        req.getHints().
                put(Parameters.Routing.INSTRUCTIONS, "false");
        GHResponse resp = graphHopper.route(req);
        return resp.getBest();
    }

    private PathLayer createPathLayer(PathWrapper response) {
        Style style = Style.builder()
                .fixed(true)
                .generalization(Style.GENERALIZATION_SMALL)
                .strokeColor(0x9900cc33)
                .strokeWidth(4 * getResources().getDisplayMetrics().density)
                .build();
        pathLayer = new PathLayer(map, style);
        List<GeoPoint> geoPoints = new ArrayList<>();
        PointList pointList = response.getPoints();
        for (int i = 0; i < pointList.getSize(); i++)
            geoPoints.add(new GeoPoint(pointList.getLatitude(i), pointList.getLongitude(i)));
        pathLayer.setPoints(geoPoints);
        return pathLayer;
    }

    private void showAllPoiMarker() {
        GeoRent geoRent = geoRentArrayList.get(currentMarkerIndex);
        if (geoRent != null) {
            MarkerItem poiMarker;
            for (PoiItem poiItem : geoRent.getPoiItems()) {
                poiMarker = new MarkerItem(poiItem.getId(), "poi", poiItem.getName(), new GeoPoint(poiItem.getLatitude(), poiItem.getLongitude()));
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
        if (geoRentArrayList != null && geoRentArrayList.size() > 0) {
            for (GeoRent geoRent : geoRentList) {
                pts.add(new MarkerItem(geoRent.getId(), "rent", geoRent.getName(), geoRent.getGeoPoint()));
            }
        }
        mMarkerLayer.addItems(pts);
    }

    @Override
    protected void showViews() {
        mapBinding.setIsLoading(false);
        mapBinding.progressPvCircularInout.stop();
        if (isFromDetail && geoRentArrayList != null) {
            MapPosition mapPosition = new MapPosition();
            mapPosition.setPosition(geoRentArrayList.get(0).getGeoPoint());
            mapPosition.setZoomLevel(15);
            map.setMapPosition(mapPosition);
            mMarkerLayer.getItemList().get(0).setMarker(rentMarkerPressed);
            currentMarkerIndex = 0;
            mapBinding.llContentFloating.setVisibility(View.GONE);
            showMarkerView();
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
        if (!isNearOpen) {
            if (item.getMarker() == null) {
                mapBinding.llContentFloating.setVisibility(View.GONE);
                if (currentMarkerIndex != -1) {
                    mMarkerLayer.getItemList().get(currentMarkerIndex).setMarker(null);
                }
                currentMarkerIndex = index;
                item.setMarker(rentMarkerPressed);
                map.animator().animateTo(1000, getMapPositionWithZoom(item.getPoint(), 15), Easing.Type.SINE_IN);
                showMarkerView();
            } else {
                item.setMarker(null);
                currentMarkerIndex = -1;
                hideMarkerView();
                mapBinding.llContentFloating.setVisibility(View.VISIBLE);
            }
        } else {
            hideNearRent();
            mapBinding.llContentFloating.setVisibility(View.VISIBLE);
        }
    }

    private void userMarkerClick(MarkerItem item) {
        map.animator().animateTo(1000, getMapPositionWithZoom(item.getPoint(), 15), Easing.Type.SINE_IN);
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


    private void showMarkerView() {
        GeoRent geoRent = geoRentArrayList.get(currentMarkerIndex);
        cafeBarMapMarkerBinding.setItem(geoRent);
        if (mapBinding.flMarker.getChildCount() > 0) {
            mapBinding.flMarker.removeAllViews();
        }
        mapBinding.flMarker.addView(cafeBarMapMarkerBinding.getRoot());
        mapPoiAdapter = new MapPoiAdapter(geoRent.getPoiItems(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        cafeBarMapMarkerBinding.rvPoi.setLayoutManager(linearLayoutManager);
        cafeBarMapMarkerBinding.rvPoi.setAdapter(mapPoiAdapter);
        
        cafeBarMapMarkerBinding.flRentContent.animate().alpha(1).setDuration(500).start();

    }

    private void hideMarkerView() {
        isRentPoiOpen = false;
        isRouteOpen = false;
        cafeBarMapMarkerBinding.ivPointOfInterest.setImageTintList(ColorStateList.valueOf(Color.parseColor("#B0BEC5")));
        cafeBarMapMarkerBinding.ivRoute.setImageTintList(ColorStateList.valueOf(Color.parseColor("#B0BEC5")));
        cafeBarMapMarkerBinding.cbShowAll.setChecked(false);
        resetRouteParams();
        cafeBarMapMarkerBinding.llRouteContent.setVisibility(View.GONE);
        cafeBarMapMarkerBinding.llPoiContent.setVisibility(View.GONE);
        cafeBarMapMarkerBinding.flRentContent.setAlpha(0);
        mapBinding.flMarker.removeAllViews();
        currentMarkerIndex = -1;
        removeAllPoiMarker();
        if (isRouteActive) {
            isRouteActive = false;
            removePathLayer();
        }
    }

    private void resetRouteParams() {
        cafeBarMapMarkerBinding.tvRouteValidation.setText("");
        cafeBarMapMarkerBinding.tvRouteValidation.setVisibility(View.GONE);
        cafeBarMapMarkerBinding.tvBtnRemoveRoute.setVisibility(View.GONE);
        cafeBarMapMarkerBinding.llRouteBtnBar.setVisibility(View.GONE);
        shipUnselected(cafeBarMapMarkerBinding.tvRbMyRent);
        shipUnselected(cafeBarMapMarkerBinding.tvRbMyLocation);
        shipUnselected(cafeBarMapMarkerBinding.tvRbMyRentTo);
        shipUnselected(cafeBarMapMarkerBinding.tvRbPoi);
        if (currentLocation == null)
            cafeBarMapMarkerBinding.tvRbMyLocation.setVisibility(View.GONE);
        cafeBarMapMarkerBinding.rlTo.setVisibility(View.GONE);
    }

    private void removeAllPoiMarker() {
        disposable = Single.just(prepareToRemovePOIMarker())
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(markerItems -> Completable.fromAction(() -> removePoiMarker(markerItems)).subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> map.render(), Timber::e);
        compositeDisposable.add(disposable);
    }

    private void addAfterRemoveAllPoiMarker() {
        disposable = Single.just(prepareToRemovePOIMarker())
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(markerItems -> Completable.fromAction(() -> removePoiMarker(markerItems)))
                .andThen(Completable.fromAction(this::showAllPoiMarker))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> map.render(), Timber::e);
        compositeDisposable.add(disposable);
    }

    private void removePoiMarker(List<MarkerItem> markerItems) {
        if (markerItems.size() > 0) {
            for (MarkerItem markerItem : markerItems) {
                mMarkerLayer.removeItem(markerItem);
            }
        }
    }

    private void removePathLayer() {
        disposable = Single.just(map.layers().contains(pathLayer))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        map.layers().remove(pathLayer);
                        map.render();
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private List<MarkerItem> prepareToRemovePOIMarker() {
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
        GeoRent geoRent;
        switch (v.getId()) {
            case R.id.iv_location:
                if (lastKnowLocation != null) {
                    map.animator().animateTo(1000, getMapPositionWithZoom(new GeoPoint(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude()), 15), Easing.Type.SINE_IN);
                } else {
                    fragmentMapInteraction.getLastLocation();
                }
                break;
            case R.id.iv_route:
                showOrHidePoiContent(false, View.GONE, "#B0BEC5");
                if (isRouteOpen) {
                    cafeBarMapMarkerBinding.tvRbMyLocation.setVisibility(View.GONE);
                    cafeBarMapMarkerBinding.llRouteBtnBar.setVisibility(View.GONE);
                    showOrHideRouteContent(false, View.GONE, "#B0BEC5");
                } else {
                    if (currentLocation != null) {
                        cafeBarMapMarkerBinding.tvRbMyLocation.setVisibility(View.VISIBLE);
                    }
                    if (isRouteActive) {
                        cafeBarMapMarkerBinding.llRouteBtnBar.setVisibility(View.VISIBLE);
                    }
                    showOrHideRouteContent(true, View.VISIBLE, "#26A69A");
                }
                break;
            case R.id.tv_rb_my_rent:
                geoRent = cafeBarMapMarkerBinding.getItem();
                from = new LatLong(geoRent.getGeoPoint().getLatitude(), geoRent.getGeoPoint().getLongitude());
                shipSelected(cafeBarMapMarkerBinding.tvRbMyRent);
                shipUnselected(cafeBarMapMarkerBinding.tvRbMyLocation);
                cafeBarMapMarkerBinding.tvRbMyRentTo.setVisibility(View.GONE);
                cafeBarMapMarkerBinding.rlTo.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_rb_my_location:
                from = new LatLong(currentLocation.getLatitude(), currentLocation.getLongitude());
                shipSelected(cafeBarMapMarkerBinding.tvRbMyLocation);
                shipUnselected(cafeBarMapMarkerBinding.tvRbMyRent);
                cafeBarMapMarkerBinding.tvRbMyRentTo.setVisibility(View.VISIBLE);
                cafeBarMapMarkerBinding.rlTo.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_rb_my_rent_to:
                geoRent = cafeBarMapMarkerBinding.getItem();
                to = new LatLong(geoRent.getGeoPoint().getLatitude(), geoRent.getGeoPoint().getLongitude());
                shipSelected(cafeBarMapMarkerBinding.tvRbMyRentTo);
                shipUnselected(cafeBarMapMarkerBinding.tvRbPoi);
                cafeBarMapMarkerBinding.llRouteBtnBar.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_rb_poi:
                if (mapPoiAdapter.isOnlyOneSelected()) {
                    PoiItem poiItem = mapPoiAdapter.getOnlyOneSelected();
                    to = new LatLong(poiItem.getLatitude(), poiItem.getLongitude());
                    shipSelected(cafeBarMapMarkerBinding.tvRbPoi);
                    shipUnselected(cafeBarMapMarkerBinding.tvRbMyRentTo);
                    cafeBarMapMarkerBinding.llRouteBtnBar.setVisibility(View.VISIBLE);
                } else {
                    cafeBarMapMarkerBinding.tvRouteValidation.setVisibility(View.VISIBLE);
                    cafeBarMapMarkerBinding.tvRouteValidation.setTextColor(getResources().getColor(R.color.material_color_red_500));
                    cafeBarMapMarkerBinding.tvRouteValidation.setText("Error: Debe estar seleccionado solo un lugar de interes");
                }
                break;
            case R.id.tv_btn_play_route:
                if (from != null && to != null) {
                    findPath(from, to);
                } else {
                    cafeBarMapMarkerBinding.tvRouteValidation.setText("Imposible trazar ruta");
                }
                break;
            case R.id.tv_btn_remove_route:
                removePathLayer();
                resetRouteParams();
                break;
            case R.id.iv_near_rent:
                mapBinding.llContentFloating.setVisibility(View.GONE);
                searchRentNearLocation();
                break;
            case R.id.cv_btn_view:
                if (fragmentMapInteraction != null) {
                    if (currentMarkerIndex != -1 && geoRentArrayList != null && geoRentArrayList.size() > 0)
                        fragmentMapInteraction.onMapInteraction(geoRentArrayList.get(currentMarkerIndex));
                }
                break;
            case R.id.iv_point_of_interest:
                showOrHideRouteContent(false, View.GONE, "#B0BEC5");
                if (isRentPoiOpen) {
                    showOrHidePoiContent(false, View.GONE, "#B0BEC5");
                } else {
                    showOrHidePoiContent(true, View.VISIBLE, "#26A69A");
                }
                break;
            case R.id.iv_near_close:
                hideNearRent();
                mapBinding.llContentFloating.setVisibility(View.VISIBLE);
                break;
        }
    }


    private void searchRentNearLocation() {
        if (mapBinding.flNear.getChildCount() > 0) {
            mapBinding.flNear.removeAllViews();
        }
        nearMapLayoutBinding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.near_map_layout, mapBinding.flNear, false);
        mapBinding.flNear.addView(nearMapLayoutBinding.getRoot());
        nearMapLayoutBinding.ivNearClose.setOnClickListener(this);
        nearMapLayoutBinding.setLoading(true);
        nearMapLayoutBinding.tvProgress.setText("Buscando...");
        nearAdapter = new RendererRecyclerViewAdapter();
        nearAdapter.registerRenderer(viewBinderNearRent(R.layout.recycler_rent_near_item));

        LatLong latLong = new LatLong(currentLocation.getLatitude(), currentLocation.getLongitude());
        disposable = rentViewModel.getGeoRentNearLatLon(latLong, 3000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> {
                    showNearRent(listResource);
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void shipSelected(TextView textView) {
        textView.setBackgroundResource(R.drawable.shape_filter_small_ship_selected);
        textView.setTextColor(getResources().getColor(R.color.White_100));
    }

    private void shipUnselected(TextView textView) {
        textView.setBackgroundResource(R.drawable.shape_filter_small_ship_unselected);
        textView.setTextColor(getResources().getColor(R.color.material_color_blue_grey_500));
    }

    public void updateCurrentLocation(Location location) {
        currentLocation = location;
        lastKnowLocation = currentLocation;
        disposable = Single.just(findPosUserMarker())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> {
                    if (integer != -1) {
                        mMarkerLayer.removeItem(integer);
                    }
                    addUserMarker(location);
                    mapBinding.ivNearRent.show();
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void addUserMarker(Location location) {
        MarkerItem userMarker = new MarkerItem("user", "", new GeoPoint(location.getLatitude(), location.getLongitude()));
        userMarker.setMarker(userMarkerUnpressed);
        mMarkerLayer.addItem(userMarker);
        map.render();
    }

    private int findPosUserMarker() {
        for (int i = 0; i < mMarkerLayer.getItemList().size(); i++) {
            if (mMarkerLayer.getItemList().get(i).title.equals("user")) {
                return i;
            }
        }
        return -1;
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

    //-------------------------------- ADAPTER CLICKS ----------------------

    @Override
    public void onPoiSelected(PoiItem poiItem, boolean selected) {
        if (selected) {
            GeoPoint geoPoint = new GeoPoint(poiItem.getLatitude(), poiItem.getLongitude());
            map.animator().animateTo(1000, getMapPositionWithZoom(geoPoint, 15), Easing.Type.SINE_IN);
            MarkerItem poiMarker = new MarkerItem(poiItem.getId(), "poi", poiItem.getName(), geoPoint);
            poiMarker.setMarker(poiMarkerUnpressed);
            mMarkerLayer.addItem(poiMarker);
            map.render();
        } else {
            int pos = findPoiMarker(poiItem.getId());
            if (pos != -1) {
                mMarkerLayer.removeItem(pos);
                map.render();
            }
        }
    }


    //--------------------------------------- NEAR RENT -----------------------

    private void showNearRent(Resource<List<GeoRent>> listResource) {
        isNearOpen = true;
        if (listResource.data != null && listResource.data.size() > 0) {
            nearAdapter.setItems(listResource.data);
            nearMapLayoutBinding.setLoading(false);
            nearMapLayoutBinding.rvNearRents.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            nearMapLayoutBinding.rvNearRents.setAdapter(nearAdapter);
        }
    }

    private void hideNearRent() {
        isNearOpen = false;
        mapBinding.flNear.removeAllViews();
    }

    private ViewBinder<?> viewBinderNearRent(int layout) {
        return new ViewBinder<>(
                layout,
                GeoRent.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_title, (ViewProvider<TextView>) view -> view.setText(model.getName()))
                        .find(R.id.tv_rating, (ViewProvider<TextView>) view -> view.setText(String.format("%.1f", model.getRating())))
                        .find(R.id.tv_rating_count, (ViewProvider<TextView>) view -> view.setText(String.format("(%s voto/s)", model.getRating())))
                        .find(R.id.tv_price, (ViewProvider<TextView>) view -> view.setText(String.format("$ %.2f", model.getPrice())))
                        .find(R.id.tv_distance, (ViewProvider<TextView>) view -> {
                            double km = model.getDistanceBetween(currentLocation) / 1000;
                            view.setText(String.format(getString(R.string.km_msg), km));
                        })
                        .find(R.id.iv_rent, (ViewProvider<RoundedImageView>) view -> {
                                    String path = "file:" + model.getImagePath();
                                    Picasso.get().load(path)
                                            .resize(THUMB_WIDTH, THUMB_HEIGHT)
                                            .placeholder(R.drawable.placeholder)
                                            .into(view);
                                }
                        ).setOnClickListener(R.id.cl_rent_home_content, (v -> {
                        }))
        );
    }

    //--------------------------------------- INTERFACE -----------------------
    public interface OnFragmentMapInteraction {

        void onMapInteraction(GeoRent geoRent);

        void getLastLocation();

    }


}
