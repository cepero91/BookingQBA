package com.infinitum.bookingqba.view.customview;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.graphhopper.util.InstructionList;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.UnitConverterUtil;
import com.infinitum.bookingqba.view.adapters.RouteInstructionsAdapter;
import com.infinitum.bookingqba.view.map.RouteHelper;
import com.thekhaeng.pushdownanim.PushDownAnim;
import com.thekhaeng.pushdownanim.PushDownAnimList;

import org.mapsforge.core.model.LatLong;

public class MarkerRouteDetailView extends ConstraintLayout implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private CardView cvWalk, cvBike, cvCar, cvDistance, cvTime;
    private AppCompatImageView ivWalk, ivBike, ivCar;
    private RadioGroup rgFrom, rgTo;
    private RelativeLayout rlContentFrom, rlContentTo;
    private RadioButton rbRentFrom, rbMyLocationFrom, rbRentTo, rbPoiTo;
    private TextView tvBtnCalculate, tvDistanceValue, tvDistanceUnit, tvTimeValue, tvTimeUnit;
    String vehicle = "foot";

    private LatLong rentLocation;
    private LatLong currentLocation;
    private LatLong poiLocation;
    private LatLong from;
    private LatLong to;
    private MarkerRouteInteraction markerRouteInteraction;
    private RecyclerView rvIntructions;

    public MarkerRouteDetailView(Context context) {
        this(context, null, 0);
    }

    public MarkerRouteDetailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkerRouteDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.route_layout, this);
        setupSubviews();
    }

    private void setupSubviews() {
        cvWalk = findViewById(R.id.cv_walk);
        cvBike = findViewById(R.id.cv_bike);
        cvCar = findViewById(R.id.cv_car);
        PushDownAnim.setPushDownAnimTo(cvWalk).setOnClickListener(this);
        PushDownAnim.setPushDownAnimTo(cvBike).setOnClickListener(this);
        PushDownAnim.setPushDownAnimTo(cvCar).setOnClickListener(this);
        cvDistance = findViewById(R.id.cv_distance);
        cvTime = findViewById(R.id.cv_time);
        tvDistanceValue = findViewById(R.id.tv_distance_value);
        tvDistanceUnit = findViewById(R.id.tv_distance_unit);
        tvTimeValue = findViewById(R.id.tv_time_value);
        tvTimeUnit = findViewById(R.id.tv_time_unit);
        ivWalk = findViewById(R.id.iv_walk);
        ivBike = findViewById(R.id.iv_bike);
        ivCar = findViewById(R.id.iv_car);
        ivWalk.setImageTintList(ColorStateList.valueOf(Color.parseColor("#00BFA5")));
        rgFrom = findViewById(R.id.rg_from);
        rgTo = findViewById(R.id.rg_to);
        rgFrom.setOnCheckedChangeListener(this);
        rgTo.setOnCheckedChangeListener(this);
        rbRentFrom = findViewById(R.id.tv_rb_my_rent);
        rbMyLocationFrom = findViewById(R.id.tv_rb_my_location);
        rbRentTo = findViewById(R.id.tv_rb_my_rent_to);
        rbPoiTo = findViewById(R.id.tv_rb_poi);
        tvBtnCalculate = findViewById(R.id.tv_btn_calc);
        PushDownAnim.setPushDownAnimTo(tvBtnCalculate).setOnClickListener(this);
        rvIntructions = findViewById(R.id.rv_instructions);
        rlContentFrom = findViewById(R.id.rl_from);
        rlContentTo = findViewById(R.id.rl_to);
        rvIntructions.setVisibility(GONE);
        rlContentTo.setVisibility(GONE);
        tvBtnCalculate.setVisibility(GONE);
        rbMyLocationFrom.setVisibility(GONE);
    }

    public void setCurrentLocation(LatLong currentLocation) {
        this.currentLocation = currentLocation;
        rbMyLocationFrom.setVisibility(VISIBLE);
    }

    public void setRentLocation(LatLong rentLocation) {
        this.rentLocation = rentLocation;
    }

    public void setPoiLocation(LatLong poiLocation) {
        this.poiLocation = poiLocation;
        if(rbPoiTo.isChecked()){
            to = poiLocation;
            tvBtnCalculate.setVisibility(VISIBLE);
        }
    }

    public void setMarkerRouteInteraction(MarkerRouteInteraction markerRouteInteraction) {
        this.markerRouteInteraction = markerRouteInteraction;
    }

    public void setInstructions(RouteHelper routeHelper) {
        tvDistanceValue.setText(routeHelper.getDistanceValue());
        tvDistanceUnit.setText(routeHelper.getDistanceUnit());
        tvTimeValue.setText(routeHelper.getTimeValue());
        tvTimeUnit.setText(routeHelper.getTimeUnit());
        cvDistance.setVisibility(VISIBLE);
        cvTime.setVisibility(VISIBLE);
        RouteInstructionsAdapter adapter = new RouteInstructionsAdapter(routeHelper.getPathWrapper().getInstructions());
        rvIntructions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvIntructions.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvIntructions.addItemDecoration(dividerItemDecoration);
        rvIntructions.setVisibility(VISIBLE);
    }

    public void setPoiLocationSelected(LatLong poiLocation, LatLong rentLocation) {
        this.poiLocation = poiLocation;
        this.rentLocation = rentLocation;
        from = rentLocation;
        to = poiLocation;
        rbRentFrom.setChecked(true);
        rbPoiTo.setChecked(true);
        rbRentTo.setVisibility(View.GONE);
        rlContentTo.setVisibility(View.VISIBLE);
        tvBtnCalculate.setVisibility(VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_walk:
                vehicle = "foot";
                ivWalk.setImageTintList(ColorStateList.valueOf(Color.parseColor("#00BFA5")));
                ivBike.setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.material_color_blue_grey_500)));
                ivCar.setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.material_color_blue_grey_500)));
                break;
            case R.id.cv_bike:
                vehicle = "bike";
                ivWalk.setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.material_color_blue_grey_500)));
                ivBike.setImageTintList(ColorStateList.valueOf(Color.parseColor("#00BFA5")));
                ivCar.setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.material_color_blue_grey_500)));
                break;
            case R.id.cv_car:
                vehicle = "car";
                ivWalk.setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.material_color_blue_grey_500)));
                ivBike.setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.material_color_blue_grey_500)));
                ivCar.setImageTintList(ColorStateList.valueOf(Color.parseColor("#00BFA5")));
                break;
            case R.id.tv_btn_calc:
                markerRouteInteraction.onCalcClick(from, to, vehicle);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.tv_rb_my_rent:
                rlContentTo.setVisibility(VISIBLE);
                rbRentTo.setVisibility(GONE);
                from = rentLocation;
                break;
            case R.id.tv_rb_my_location:
                rlContentTo.setVisibility(VISIBLE);
                rbRentTo.setVisibility(VISIBLE);
                from = currentLocation;
                break;
            case R.id.tv_rb_my_rent_to:
                tvBtnCalculate.setVisibility(VISIBLE);
                to = rentLocation;
                break;
            case R.id.tv_rb_poi:
                if (poiLocation != null) {
                    tvBtnCalculate.setVisibility(VISIBLE);
                    to = poiLocation;
                } else {
                    AlertUtils.showErrorToast(getContext(), "Seleccione un lugar de interes!");
                }
                break;
        }
    }

    public interface MarkerRouteInteraction {
        void onCalcClick(LatLong from, LatLong to, String vehicle);
    }
}
