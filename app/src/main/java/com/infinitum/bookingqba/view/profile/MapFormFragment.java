package com.infinitum.bookingqba.view.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Location;
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
import com.infinitum.bookingqba.view.base.BaseMapFragment;
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

public class MapFormFragment extends BaseMapFragment implements ItemizedLayer.OnItemGestureListener<MarkerItem>
        , View.OnClickListener {

    @Inject
    SharedPreferences sharedPreferences;

    private FragmentMapFormBinding binding;
    private MapRentLocation mapRentLocation;
    private GeoPoint geoPointLocation;
    private boolean isLocationEmpty;

    private String mapFilePath;
    private MarkerSymbol userMarker;
    private ItemizedLayer<MarkerItem> mMarkerLayer;

    private String argLatitude;
    private String argLongitude;
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";

    private MapEventsReceiver mapEventsReceiver;
    private Location currentLocation;
    private Location lastKnowLocation;
    private boolean alreadyDoneShow;


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
        binding.progressPvCircularInout.start();

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
        if (disposable!=null && !disposable.isDisposed())
            disposable.dispose();
        compositeDisposable.clear();
        super.onDetach();
    }

    //-------------------------------------- MAP

    @Override
    protected void initializeMarker() {
        mapEventsReceiver = new MapEventsReceiver(map);
        map.layers().add(mapEventsReceiver);
    }

    @Override
    protected void showViews() {
        binding.setIsLoading(false);
        if (!argLatitude.equals("") && !argLongitude.equals("")) {
            updateGestureCurrentLocation(Float.parseFloat(argLatitude), Float.parseFloat(argLongitude),0);
        } else {
            mapView.map().setMapPosition(23.1165, -82.3882, 2 << 12);
        }
        binding.progressPvCircularInout.stop();
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
                showDialogLocationConfirm();
                break;

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
                updateGestureCurrentLocation(p.getLatitude(), p.getLongitude(),0);
                return true;
            }
            return false;
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
        locationLayer.setEnabled(true);
        locationLayer.setPosition(location.getLatitude(), location.getLongitude(), location.getAccuracy());
        map.animator().animateTo(1000, getMapPositionWithZoom(new GeoPoint(Float.parseFloat(argLatitude),Float.parseFloat(argLongitude)), 15), Easing.Type.SINE_IN);
        showDoneFAB();
    }

    public void updateGestureCurrentLocation(double latitude, double longitude, double accuracy) {
        argLatitude = String.valueOf(latitude);
        argLongitude = String.valueOf(longitude);
        locationLayer.setEnabled(true);
        locationLayer.setPosition(latitude, longitude, accuracy);
        showDoneFAB();
    }

    private void showDoneFAB(){
        if(!alreadyDoneShow) {
            binding.ivDone.show();
            alreadyDoneShow = true;
        }
    }

    private void showDialogLocationConfirm() {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getActivity());
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET);
        // Title and message
        builder.setTitle("Ubicacion obtenida");
        builder.setTextGravity(Gravity.START);
        builder.setIcon(R.drawable.ic_map_marker_alt_blue_grey);
        builder.setTextColor(Color.parseColor("#607D8B"));

        DialogLocationConfirmView dialogLocationConfirmView = new DialogLocationConfirmView(getActivity());
        builder.setFooterView(dialogLocationConfirmView);

        builder.show();
    }


}
