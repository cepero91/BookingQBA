package com.infinitum.bookingqba.view.map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
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
import com.infinitum.bookingqba.databinding.FragmentMapBinding;
import com.infinitum.bookingqba.databinding.NearMapLayoutBinding;
import com.infinitum.bookingqba.databinding.RentMapMarkerLayoutBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.view.adapters.InnerViewPagerAdapter;
import com.infinitum.bookingqba.view.adapters.MapPoiAdapter;
import com.infinitum.bookingqba.view.adapters.RouteInstructionsAdapter;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.map.PoiItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentPoiItem;
import com.infinitum.bookingqba.view.base.BaseMapFragment;
import com.infinitum.bookingqba.view.customview.MarkerPoiDetailView;
import com.infinitum.bookingqba.view.customview.MarkerRouteDetailView;
import com.infinitum.bookingqba.view.customview.NestedScrollableViewHelper;
import com.infinitum.bookingqba.view.customview.RadarView;
import com.infinitum.bookingqba.view.rents.RentDetailPoiFragment;
import com.infinitum.bookingqba.view.widgets.CenterSmoothScroller;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.thekhaeng.pushdownanim.PushDownAnim;

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
import java.util.Map;
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


public class MapFragment extends BaseMapFragment implements ItemizedLayer.OnItemGestureListener<MarkerItem>,
        View.OnClickListener, MapPoiAdapter.MapPoiClick, SlidingUpPanelLayout.PanelSlideListener, MarkerPoiDetailView.MarkerPoiInteraction,
        MarkerRouteDetailView.MarkerRouteInteraction {

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
    private RentMapMarkerLayoutBinding rentMapMarkerLayoutBinding;
    private NearMapLayoutBinding nearMapLayoutBinding;

    //Marker
    private MarkerSymbol rentMarkerPressed, rentMarkerUnpressed, userMarkerUnpressed, poiMarkerUnpressed;
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

    private GraphCreator graphCreator;
    private RadarView radarView;

    // MARKER DETAIL ---------------------------------
    private RentDetailPoiFragment poiDetail;
    private boolean markerDetailLoaded = false;
    private int detailTabSelected = 1;
    private MarkerPoiDetailView markerPoiDetailView;
    private MarkerRouteDetailView markerRouteDetailView;
    private LatLong latLongPoiSelected;
    private RentPoiItem lastRentPoiItem;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        return mapBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        super.onActivityCreated(savedInstanceState);

        mapBinding.setIsLoading(true);

        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        setupMarkerView();

        graphCreator = GraphHelper.with(getActivity()).composite(new CompositeDisposable()).share(sharedPreferences);
        graphCreator.config();


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
        rentMapMarkerLayoutBinding = null;
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
        graphCreator.cleanComposite();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    //------------------------------------ METHODS -----------------------

    private void setupMarkerView() {
        mapBinding.sivRentImage.setOnClickListener(this);
        mapBinding.ivLocation.setOnClickListener(this);
        mapBinding.slidingLayout.addPanelSlideListener(this);
        mapBinding.slidingLayout.setScrollableViewHelper(new NestedScrollableViewHelper());
        mapBinding.llBtnPoi.setOnClickListener(this);
        mapBinding.llBtnRoute.setOnClickListener(this);
    }

    private void showOrHideRouteContent(boolean b, int gone, String s) {
        isRouteOpen = b;
        rentMapMarkerLayoutBinding.llRouteContent.setVisibility(gone);
        rentMapMarkerLayoutBinding.ivRoute.setImageTintList(ColorStateList.valueOf(Color.parseColor(s)));
        if (isRouteActive)
            rentMapMarkerLayoutBinding.llRouteBtnBar.setVisibility(gone);
    }

    private void showOrHidePoiContent(boolean b, int gone, String s) {
        isRentPoiOpen = b;
        rentMapMarkerLayoutBinding.llPoiContent.setVisibility(gone);
        rentMapMarkerLayoutBinding.ivPointOfInterest.setImageTintList(ColorStateList.valueOf(Color.parseColor(s)));
    }

    private void findPath(LatLong from, LatLong to, String vehicle) {
        if (pathLayer != null)
            map.layers().remove(pathLayer);
        if (graphCreator.routeIsReady()) {
            disposable = graphCreator.calculateRoute(from, to, vehicle)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::showRoutePathIntoMap, Timber::e);
            compositeDisposable.add(disposable);
        } else {
            AlertUtils.showErrorToast(getActivity(), "No se puede hallar la ruta");
        }
    }

    private void showRoutePathIntoMap(RouteHelper routeHelper) {
        if (routeHelper.getPathWrapper() != null && !routeHelper.getPathWrapper().hasErrors()) {
            pathLayer = graphCreator.createPathLayer(routeHelper.getPathWrapper(), map);
            map.layers().add(pathLayer);
            map.render();
            markerRouteDetailView.setInstructions(routeHelper);
        }
    }


    @Override
    protected void initializeMarker() {
        if (geoRentArrayList == null) {
            disposable = rentViewModel.getGeoRent().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(listResource -> setupMarkers(listResource.data), Timber::e);
            compositeDisposable.add(disposable);
        } else {
            setupMarkers(geoRentArrayList);
        }
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

        Bitmap poiOff = drawableToBitmap(getResources().getDrawable(R.drawable.poi_placeholder_off));
        poiMarkerUnpressed = new MarkerSymbol(poiOff, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);

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
        mapBinding.lvProgress.cancelAnimation();
        if (isFromDetail && geoRentArrayList != null) {
            MapPosition mapPosition = new MapPosition();
            mapPosition.setPosition(geoRentArrayList.get(0).getGeoPoint());
            mapPosition.setZoomLevel(15);
            map.setMapPosition(mapPosition);
            mMarkerLayer.getItemList().get(0).setMarker(rentMarkerPressed);
            currentMarkerIndex = 0;
            mapBinding.ivLocation.hide();
            buildPoiMarkerDetail();
        } else {
            // Position Habana
            map.setMapPosition(23.1165, -82.3882, 2 << 12);
        }
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerItem item) {
        switch (item.title) {
            case "rent":
                rentMarkerClickT(index, item);
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
        map.animator().animateTo(1000, getMapPositionWithZoom(item.getPoint(), 15), Easing.Type.SINE_IN);
    }

    private void rentMarkerClickT(int index, MarkerItem item) {
        if (item.getMarker() == null && currentMarkerIndex == -1) {
            currentMarkerIndex = index;
            item.setMarker(rentMarkerPressed);
            map.animator().animateTo(500, getMapPositionWithZoom(item.getPoint(), 15), Easing.Type.SINE_IN);
            mapBinding.ivLocation.hide();
            buildPoiMarkerDetail();
        } else if (currentMarkerIndex != -1 && index != currentMarkerIndex) {
            mMarkerLayer.getItemList().get(currentMarkerIndex).setMarker(null);
            currentMarkerIndex = -1;
            mapBinding.slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        } else {
            item.setMarker(null);
            currentMarkerIndex = -1;
            mapBinding.slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
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


    private void resetRouteParams() {
        rentMapMarkerLayoutBinding.tvRouteValidation.setText("");
        rentMapMarkerLayoutBinding.tvRouteValidation.setVisibility(View.GONE);
        rentMapMarkerLayoutBinding.tvBtnRemoveRoute.setVisibility(View.GONE);
        rentMapMarkerLayoutBinding.llRouteBtnBar.setVisibility(View.GONE);
        shipUnselected(rentMapMarkerLayoutBinding.tvRbMyRent);
        shipUnselected(rentMapMarkerLayoutBinding.tvRbMyLocation);
        shipUnselected(rentMapMarkerLayoutBinding.tvRbMyRentTo);
        shipUnselected(rentMapMarkerLayoutBinding.tvRbPoi);
        if (currentLocation == null)
            rentMapMarkerLayoutBinding.tvRbMyLocation.setVisibility(View.GONE);
        rentMapMarkerLayoutBinding.rlTo.setVisibility(View.GONE);
    }

    private void removeAllPoiMarker() {
        disposable = Single.just(prepareToRemovePOIMarker())
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(markerItems -> Completable.fromAction(() -> removePoiMarker(markerItems)).subscribeOn(Schedulers.io()))
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
        if (pathLayer != null) {
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
                    rentMapMarkerLayoutBinding.tvRbMyLocation.setVisibility(View.GONE);
                    rentMapMarkerLayoutBinding.llRouteBtnBar.setVisibility(View.GONE);
                    showOrHideRouteContent(false, View.GONE, "#B0BEC5");
                } else {
                    if (currentLocation != null) {
                        rentMapMarkerLayoutBinding.tvRbMyLocation.setVisibility(View.VISIBLE);
                    }
                    if (isRouteActive) {
                        rentMapMarkerLayoutBinding.llRouteBtnBar.setVisibility(View.VISIBLE);
                    }
                    showOrHideRouteContent(true, View.VISIBLE, "#26A69A");
                }
                break;
            case R.id.tv_rb_my_rent:
                geoRent = rentMapMarkerLayoutBinding.getItem();
                from = new LatLong(geoRent.getGeoPoint().getLatitude(), geoRent.getGeoPoint().getLongitude());
                shipSelected(rentMapMarkerLayoutBinding.tvRbMyRent);
                shipUnselected(rentMapMarkerLayoutBinding.tvRbMyLocation);
                rentMapMarkerLayoutBinding.tvRbMyRentTo.setVisibility(View.GONE);
                rentMapMarkerLayoutBinding.rlTo.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_rb_my_location:
                from = new LatLong(currentLocation.getLatitude(), currentLocation.getLongitude());
                shipSelected(rentMapMarkerLayoutBinding.tvRbMyLocation);
                shipUnselected(rentMapMarkerLayoutBinding.tvRbMyRent);
                rentMapMarkerLayoutBinding.tvRbMyRentTo.setVisibility(View.VISIBLE);
                rentMapMarkerLayoutBinding.rlTo.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_rb_my_rent_to:
                geoRent = rentMapMarkerLayoutBinding.getItem();
                to = new LatLong(geoRent.getGeoPoint().getLatitude(), geoRent.getGeoPoint().getLongitude());
                shipSelected(rentMapMarkerLayoutBinding.tvRbMyRentTo);
                shipUnselected(rentMapMarkerLayoutBinding.tvRbPoi);
                rentMapMarkerLayoutBinding.llRouteBtnBar.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_rb_poi:
                if (mapPoiAdapter.isOnlyOneSelected()) {
                    PoiItem poiItem = mapPoiAdapter.getOnlyOneSelected();
                    to = new LatLong(poiItem.getLatitude(), poiItem.getLongitude());
                    shipSelected(rentMapMarkerLayoutBinding.tvRbPoi);
                    shipUnselected(rentMapMarkerLayoutBinding.tvRbMyRentTo);
                    rentMapMarkerLayoutBinding.llRouteBtnBar.setVisibility(View.VISIBLE);
                } else {
                    rentMapMarkerLayoutBinding.tvRouteValidation.setVisibility(View.VISIBLE);
                    rentMapMarkerLayoutBinding.tvRouteValidation.setTextColor(getResources().getColor(R.color.material_color_red_500));
                    rentMapMarkerLayoutBinding.tvRouteValidation.setText("Error: Debe estar seleccionado solo un lugar de interes");
                }
                break;
            case R.id.tv_btn_remove_route:
                removePathLayer();
                resetRouteParams();
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
            case R.id.ll_btn_poi:
                mapBinding.llBtnPoi.setTextColor(getResources().getColor(R.color.material_color_teal_A700));
                mapBinding.llBtnRoute.setTextColor(getResources().getColor(R.color.material_color_blue_grey_200));
                if (markerPoiDetailView != null) {
                    mapBinding.flDetail.removeAllViews();
                    mapBinding.flDetail.addView(markerPoiDetailView);
                }
                break;
            case R.id.ll_btn_route:
                mapBinding.llBtnRoute.setTextColor(getResources().getColor(R.color.material_color_teal_A700));
                mapBinding.llBtnPoi.setTextColor(getResources().getColor(R.color.material_color_blue_grey_200));
                if (markerRouteDetailView == null) {
                    markerRouteDetailView = new MarkerRouteDetailView(getActivity());
                    markerRouteDetailView.setMarkerRouteInteraction(MapFragment.this);
                }
                if (currentLocation != null) {
                    markerRouteDetailView.setCurrentLocation(new LatLong(currentLocation.getLatitude(), currentLocation.getLongitude()));
                }
                if (lastRentPoiItem != null) {
                    markerRouteDetailView.setPoiLocation(new LatLong(lastRentPoiItem.getLatitude(), lastRentPoiItem.getLongitude()));
                }
                if (currentMarkerIndex != -1) {
                    GeoRent rent = geoRentArrayList.get(currentMarkerIndex);
                    markerRouteDetailView.setRentLocation(new LatLong(rent.getGeoPoint().getLatitude(), rent.getGeoPoint().getLongitude()));
                }
                mapBinding.flDetail.removeAllViews();
                mapBinding.flDetail.addView(markerRouteDetailView);
                break;
            case R.id.siv_rent_image:
                if (fragmentMapInteraction != null) {
                    if (currentMarkerIndex != -1 && geoRentArrayList != null && geoRentArrayList.size() > 0)
                        fragmentMapInteraction.onMapInteraction(geoRentArrayList.get(currentMarkerIndex));
                }
                break;
        }
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

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    //-------------------------------------- MARKER DETAIL INTERACTION --------------------------
    private void buildPoiMarkerDetail() {
        mapBinding.ivLocation.setEnabled(false);
        mapBinding.slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        GeoRent geoRent = geoRentArrayList.get(currentMarkerIndex);
        mapBinding.tvRentName.setText(geoRent.getName());
        mapBinding.tvRatingCount.setText(geoRent.humanRatingCount());
        mapBinding.srScaleRating.setRating(geoRent.getRating());
        mapBinding.tvPrice.setText(geoRent.humanPrice());
        mapBinding.tvRentMode.setText(geoRent.humanRentMode());
        Picasso.get()
                .load("file:" + geoRent.getImagePath())
                .resize(420, 280)
                .placeholder(R.drawable.placeholder)
                .into(mapBinding.sivRentImage);
        markerPoiDetailView = new MarkerPoiDetailView(getActivity());
        markerPoiDetailView.setupPoiCategoryAdapter(geoRent.getPoiItemMap(),new LatLong(geoRent.getGeoPoint().getLatitude(),geoRent.getGeoPoint().getLongitude()));
        markerPoiDetailView.setupReferenceZone(geoRent.getReferenceZone());
        markerPoiDetailView.setMarkerPoiInteraction(this);
    }

    @Override
    public void onPoiClick(RentPoiItem poiItem) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getActivity());
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        // Title and message
        builder.setTitle("Que desea hacer?");
        builder.setTextGravity(Gravity.START);
        builder.setTextColor(Color.parseColor("#607D8B"));
        builder.setItems(new String[]{"Ubicar en el mapa", "Exportar hacia Ruta"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    updatePoiSelectedToMap(poiItem);
                    dialog.dismiss();
                    mapBinding.slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    map.animator().animateTo(500, getMapPositionWithZoom(new GeoPoint(poiItem.getLatitude(), poiItem.getLongitude()), 15), Easing.Type.SINE_IN);
                    break;
                case 1:
                    mapBinding.llBtnRoute.setTextColor(getResources().getColor(R.color.material_color_teal_A700));
                    mapBinding.llBtnPoi.setTextColor(getResources().getColor(R.color.material_color_blue_grey_200));
                    updatePoiSelectedToMap(poiItem);
                    if (markerRouteDetailView == null) {
                        markerRouteDetailView = new MarkerRouteDetailView(getActivity());
                        markerRouteDetailView.setMarkerRouteInteraction(MapFragment.this);
                    }
                    if (currentLocation != null)
                        markerRouteDetailView.setCurrentLocation(new LatLong(currentLocation.getLatitude(), currentLocation.getLongitude()));
                    GeoRent geoRent = geoRentArrayList.get(currentMarkerIndex);
                    LatLong rentLocation = new LatLong(geoRent.getGeoPoint().getLatitude(), geoRent.getGeoPoint().getLongitude());
                    markerRouteDetailView.setPoiLocationSelected(new LatLong(poiItem.getLatitude(), poiItem.getLongitude()), rentLocation);
                    mapBinding.flDetail.removeAllViews();
                    mapBinding.flDetail.addView(markerRouteDetailView);
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }

    private void updatePoiSelectedToMap(RentPoiItem poiItem) {
        if (lastRentPoiItem != null) {
            int pos = findPoiMarker(lastRentPoiItem.getId());
            if (pos != -1)
                mMarkerLayer.removeItem(pos);
        }
        lastRentPoiItem = poiItem;
        GeoPoint geoPoint = new GeoPoint(poiItem.getLatitude(), poiItem.getLongitude());
        MarkerItem poiMarker = new MarkerItem(poiItem.getId(), "poi", poiItem.getName(), geoPoint);
        poiMarker.setMarker(poiMarkerUnpressed);
        mMarkerLayer.addItem(poiMarker);
        map.render();
    }

    @Override
    public void onCalcClick(LatLong from, LatLong to, String vehicle) {
        findPath(from, to, vehicle);
    }

    //-------------------------------------- PANEL LISTENER -------------------------------------

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        if (newState == SlidingUpPanelLayout.PanelState.EXPANDED && !markerDetailLoaded) {
            mapBinding.flDetail.removeAllViews();
            mapBinding.flDetail.addView(markerPoiDetailView);
            markerDetailLoaded = true;
        } else if (newState == SlidingUpPanelLayout.PanelState.HIDDEN) {
            mapBinding.llBtnPoi.setTextColor(getResources().getColor(R.color.material_color_blue_grey_500));
            mapBinding.llBtnRoute.setTextColor(getResources().getColor(R.color.material_color_blue_grey_200));
            mapBinding.flDetail.removeAllViews();
            markerDetailLoaded = false;
            removeAllPoiMarker();
            removePathLayer();
            mapBinding.ivLocation.show();
            mapBinding.ivLocation.setEnabled(true);
        }
    }

    //--------------------------------------- INTERFACE -----------------------
    public interface OnFragmentMapInteraction {

        void onMapInteraction(GeoRent geoRent);

        void getLastLocation();

    }

}
