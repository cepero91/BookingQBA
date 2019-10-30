package com.infinitum.bookingqba.view.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentMapFormBinding;
import com.infinitum.bookingqba.view.base.BaseMapFragment;

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
import org.oscim.map.Map;
import org.oscim.utils.animation.Easing;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;

public class MapFormFragment extends BaseMapFragment implements ItemizedLayer.OnItemGestureListener<MarkerItem>,
        View.OnClickListener {

    @Inject
    SharedPreferences sharedPreferences;

    private FragmentMapFormBinding binding;
    private MapRentLocation mapRentLocation;

    private String argLatitude;
    private String argLongitude;
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";

    private MapEventsReceiver mapEventsReceiver;
    private boolean alreadyDoneShow;

    private MarkerSymbol rentMarkerUnpressed;
    private ItemizedLayer<MarkerItem> mMarkerLayer;


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

        binding.ivDone.setOnClickListener(this);
        binding.ivDone.hide();

        binding.ivLocation.setOnClickListener(this);

        binding.setIsLoading(true);

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
        if (disposable!=null && !disposable.isDisposed())
            disposable.dispose();
        compositeDisposable.clear();
        super.onDetach();
    }

    @Override
    protected void publishProgress(String msgProgress) {

    }

    @Override
    public void onDestroyView() {
        if (disposable!=null && !disposable.isDisposed())
            disposable.dispose();
        compositeDisposable.clear();
        binding.mapview.map().layers().remove(mapEventsReceiver);
        binding.mapview.onDestroy();
        super.onDestroyView();
    }

    //-------------------------------------- METHODS

    @Override
    protected void initializeMarker() {
        mapEventsReceiver = new MapEventsReceiver(map);
        map.layers().add(mapEventsReceiver);

        //Marker
        Bitmap rentOff = drawableToBitmap(getResources().getDrawable(R.drawable.casa_placeholder_off));
        rentMarkerUnpressed = new MarkerSymbol(rentOff, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);

        mMarkerLayer = new ItemizedLayer<>(map, new ArrayList<>(), rentMarkerUnpressed, this);
        map.layers().add(mMarkerLayer);
    }

    @Override
    protected void showViews() {
        if (!argLatitude.equals("") && !argLongitude.equals("")) {
            //do something with location
            map.animator().animateTo(1000, getMapPositionWithZoom(new GeoPoint(Float.parseFloat(argLatitude),Float.parseFloat(argLongitude)), 15), Easing.Type.SINE_IN);
            showDoneFAB();
        } else {
            mapView.map().setMapPosition(23.1165, -82.3882, 2 << 12);
        }
        binding.setIsLoading(false);
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
                if (!argLatitude.equals("") && !argLongitude.equals("")) {
                    map.animator().animateTo(1000, getMapPositionWithZoom(new GeoPoint(Float.parseFloat(argLatitude),Float.parseFloat(argLongitude)), 15), Easing.Type.SINE_IN);
                } else {
                    mapRentLocation.onLocationButtonClick();
                }
                break;
            case R.id.iv_done:
                mapRentLocation.onLocationUpdates(Float.parseFloat(argLatitude),Float.parseFloat(argLongitude));
                mapRentLocation.showLocationConfirmDialog();
                break;

        }
    }

    private MapPosition getMapPositionWithZoom(GeoPoint geoPoint, int zoom) {
        MapPosition mapPosition = new MapPosition();
        mapPosition.setPosition(geoPoint);
        mapPosition.setZoomLevel(zoom);
        return mapPosition;
    }

    public void updateGPSCurrentLocation(Location location) {
        argLatitude = String.valueOf(location.getLatitude());
        argLongitude = String.valueOf(location.getLongitude());

        updateCurrentLocation(location.getLatitude(),location.getLongitude());

        showDoneFAB();
    }

    public void updateGestureCurrentLocation(double latitude, double longitude) {
        argLatitude = String.valueOf(latitude);
        argLongitude = String.valueOf(longitude);

        updateCurrentLocation(latitude,longitude);

        showDoneFAB();
    }

    private void showDoneFAB(){
        if(!alreadyDoneShow) {
            binding.ivDone.show();
            alreadyDoneShow = true;
        }
    }

    //------------------------------------ USER LOCATION ------------------------

    public void updateCurrentLocation(double latitude, double longitude) {
        if(mMarkerLayer!= null && mMarkerLayer.getItemList()!=null) {
            disposable = Single.just(findPosUserMarker())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(integer -> {
                        if (integer != -1) {
                            mMarkerLayer.removeItem(integer);
                        }
                        addUserMarker(latitude, longitude);
                    }, Timber::e);
            compositeDisposable.add(disposable);
        }
    }

    private int findPosUserMarker() {
        if(mMarkerLayer.getItemList()!=null) {
            for (int i = 0; i < mMarkerLayer.getItemList().size(); i++) {
                if (mMarkerLayer.getItemList().get(i).title.equals("rent")) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void addUserMarker(double latitude, double longitude) {
        MarkerItem userMarker = new MarkerItem("rent", "", new GeoPoint(latitude, longitude));
        userMarker.setMarker(rentMarkerUnpressed);
        mMarkerLayer.addItem(userMarker);
        map.render();
    }

    //------------------------------------ INNER CLASS --------------------------
    class MapEventsReceiver extends Layer implements GestureListener {

        MapEventsReceiver(Map map) {
            super(map);
        }

        @Override
        public boolean onGesture(Gesture g, MotionEvent e) {
            if (g instanceof Gesture.LongPress) {
                GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
                updateGestureCurrentLocation(p.getLatitude(), p.getLongitude());
                return true;
            }
            return false;
        }

    }
}
