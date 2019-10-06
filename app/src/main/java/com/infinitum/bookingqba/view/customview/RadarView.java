package com.infinitum.bookingqba.view.customview;

import android.content.Context;
import android.location.Location;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.model.remote.pojo.Poi;
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.profile.AddPoiAdapter;
import com.jaygoo.widget.RangeSeekBar;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.BaseRatingBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.infinitum.bookingqba.util.Constants.THUMB_HEIGHT;
import static com.infinitum.bookingqba.util.Constants.THUMB_WIDTH;

public class RadarView extends ConstraintLayout implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private RadarInteraction radarInteraction;
    private RecyclerView rvNearRents;
    private TextView btnSearch;
    private RangeSeekBar rsbRadius;
    private RadioGroup radioGroupRentMode;
    private LinearLayout llContentRadarFilter;
    private LinearLayout llContentProgress;
    private FrameLayout flTitleRadarBar;
    private LottieAnimationView lottieProgress;
    private AppCompatImageView ivClose;
    private Map<String, Object> filterParams;
    private List<String> rentModeUuid;
    private Location currentLocation;
    private RendererRecyclerViewAdapter nearAdapter;

    public RadarView(Context context) {
        this(context, null, 0);
    }

    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.radar_map_layout, this);
        filterParams = new HashMap<>();
        rentModeUuid = new ArrayList<>();
        rentModeUuid.add(Constants.BY_HOURS_UUID);
        rentModeUuid.add(Constants.BY_NIGHT_UUID);
        setupSubviews();
    }

    private void setupSubviews() {
        rvNearRents = findViewById(R.id.rv_near_rents);
        rsbRadius = findViewById(R.id.rsb_radius);
        rsbRadius.setIndicatorTextDecimalFormat("0");
        rsbRadius.setProgress(1000f);
        btnSearch = findViewById(R.id.tv_btn_search);
        btnSearch.setOnClickListener(this);
        radioGroupRentMode = findViewById(R.id.rg_rent_mode);
        radioGroupRentMode.setOnCheckedChangeListener(this);
        llContentRadarFilter = findViewById(R.id.ll_content_radar_filter);
        llContentProgress = findViewById(R.id.ll_content_progress);
        flTitleRadarBar = findViewById(R.id.fl_title_radar_bar);
        lottieProgress = findViewById(R.id.lv_progress);
        ivClose = findViewById(R.id.iv_near_close);
        ivClose.setOnClickListener(this);
    }

    public void setRadarInteraction(RadarInteraction interaction){
        this.radarInteraction = interaction;
    }

    public void setCurrentLocation(Location location){
        this.currentLocation = location;
    }

    public void setGeoRentList(List<GeoRent> geoRentList){
        nearAdapter = new RendererRecyclerViewAdapter();
        nearAdapter.registerRenderer(viewBinderNearRent(R.layout.recycler_rent_near_item));
        nearAdapter.setItems(geoRentList);
        rvNearRents.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        lottieProgress.cancelAnimation();
        llContentProgress.setVisibility(GONE);
        flTitleRadarBar.setVisibility(VISIBLE);
        rvNearRents.setVisibility(VISIBLE);
        rvNearRents.setAdapter(nearAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_search:
                if (rsbRadius.getLeftSeekBar().getProgress() > 0) {
                    filterParams.put(Constants.FILTER_RADAR_RENT_MODE, rentModeUuid);
                    filterParams.put(Constants.FILTER_RADAR_RADIUS, rsbRadius.getLeftSeekBar().getProgress());
                    flTitleRadarBar.setVisibility(GONE);
                    llContentRadarFilter.setVisibility(GONE);
                    llContentProgress.setVisibility(VISIBLE);
                    lottieProgress.playAnimation();
                    radarInteraction.onSearchRadarClick(filterParams);
                } else {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.shake_animation);
                    rsbRadius.startAnimation(animation);
                }
                break;
            case R.id.iv_near_close:
                radarInteraction.onSearchRadarCloseClick();
                break;
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_by_night:
                rentModeUuid = new ArrayList<>();
                rentModeUuid.add(Constants.BY_NIGHT_UUID);
                break;
            case R.id.rb_by_hour:
                rentModeUuid = new ArrayList<>();
                rentModeUuid.add(Constants.BY_HOURS_UUID);
                break;
            case R.id.rb_all:
                rentModeUuid = new ArrayList<>();
                rentModeUuid.add(Constants.BY_NIGHT_UUID);
                rentModeUuid.add(Constants.BY_HOURS_UUID);
                break;
        }
    }

    private ViewBinder<?> viewBinderNearRent(int layout) {
        return new ViewBinder<>(
                layout,
                GeoRent.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_title, (ViewProvider<TextView>) view -> view.setText(model.getName()))
                        .find(R.id.sr_scale_rating, (ViewProvider<BaseRatingBar>) view -> view.setRating(model.getRating()))
                        .find(R.id.tv_rating_count, (ViewProvider<TextView>) view -> view.setText(String.format("(%s voto/s)", model.getRating())))
                        .find(R.id.tv_price, (ViewProvider<TextView>) view -> view.setText(String.format("$ %.2f", model.getPrice())))
                        .find(R.id.tv_distance, (ViewProvider<TextView>) view -> {
                            double km = model.getDistanceBetween(currentLocation) / 1000;
                            view.setText(String.format(getContext().getString(R.string.km_msg), km));
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

    public interface RadarInteraction {

        void onSearchRadarClick(Map<String, Object> filterParams);

        void onSearchRadarCloseClick();

    }
}
