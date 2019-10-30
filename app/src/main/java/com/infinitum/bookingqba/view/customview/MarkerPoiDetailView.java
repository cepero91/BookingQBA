package com.infinitum.bookingqba.view.customview;

import android.content.Context;
import android.location.Location;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
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
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.view.adapters.MarkerDetailPoiAdapter;
import com.infinitum.bookingqba.view.adapters.RDPoiAdapter;
import com.infinitum.bookingqba.view.adapters.RDPoiCategoryAdapter;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.map.PoiItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.PoiCategory;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentPoiItem;
import com.infinitum.bookingqba.view.widgets.CenterSmoothScroller;
import com.jaygoo.widget.RangeSeekBar;
import com.squareup.picasso.Picasso;

import org.mapsforge.core.model.LatLong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.infinitum.bookingqba.util.Constants.THUMB_HEIGHT;
import static com.infinitum.bookingqba.util.Constants.THUMB_WIDTH;

public class MarkerPoiDetailView extends ConstraintLayout implements RDPoiCategoryAdapter.PoiCategorySelection, MarkerDetailPoiAdapter.PoiClick{

    private RecyclerView rvCategory, rvPoi;
    private TextView tvReferenceTitle, tvReferenceDescription;
    private Map<PoiCategory, List<RentPoiItem>> argPoiCategory;
    private String argReferenceZone;
    private MarkerPoiInteraction markerPoiInteraction;
    private LatLong rentLocation;

    public MarkerPoiDetailView(Context context) {
        this(context, null, 0);
    }

    public MarkerPoiDetailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkerPoiDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.poi_reference_layout, this);
        setupSubviews();
    }

    private void setupSubviews() {
        rvCategory = findViewById(R.id.rv_category);
        rvPoi = findViewById(R.id.rv_poi);
        tvReferenceTitle = findViewById(R.id.tv_rz_title);
        tvReferenceDescription = findViewById(R.id.tv_rz_description);
    }

    public void setMarkerPoiInteraction(MarkerPoiInteraction markerPoiInteraction) {
        this.markerPoiInteraction = markerPoiInteraction;
    }

    public void setRentLocation(LatLong rentLocation) {
        this.rentLocation = rentLocation;
    }

    public void setupReferenceZone(String argRefZone) {
        if (argRefZone.equalsIgnoreCase(Constants.URBAN)) {
            tvReferenceTitle.setText(getContext().getString(R.string.neibor_enviroment));
            tvReferenceDescription.setText(getContext().getString(R.string.neiborhood_reference));
            tvReferenceDescription.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getDrawable(R.drawable.compass), null);
        } else if (argRefZone.equalsIgnoreCase(Constants.BEACH)) {
            tvReferenceTitle.setText(getContext().getString(R.string.beach_enviroment));
            tvReferenceDescription.setText(getContext().getString(R.string.beach_reference));
            tvReferenceDescription.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getDrawable(R.drawable.beach), null);
        } else if (argRefZone.equalsIgnoreCase(Constants.NATURAL)) {
            tvReferenceTitle.setText(getContext().getString(R.string.natural_enviroment));
            tvReferenceDescription.setText(getContext().getString(R.string.natural_reference));
            tvReferenceDescription.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getDrawable(R.drawable.caravan), null);
        } else if (argRefZone.equalsIgnoreCase(Constants.HISTORIC)) {
            tvReferenceTitle.setText(getContext().getString(R.string.historic_enviroment));
            tvReferenceDescription.setText(getContext().getString(R.string.historic_reference));
            tvReferenceDescription.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getDrawable(R.drawable.pictures), null);
        } else if (argRefZone.equalsIgnoreCase(Constants.CULTURE)) {
            tvReferenceTitle.setText(getContext().getString(R.string.culture_enviroment));
            tvReferenceDescription.setText(getContext().getString(R.string.culture_reference));
            tvReferenceDescription.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getDrawable(R.drawable.photo_camera), null);
        }
    }

    public void setupPoiCategoryAdapter(Map<PoiCategory, List<RentPoiItem>> poiCategories, LatLong rentLocation) {
        this.rentLocation = rentLocation;
        this.argPoiCategory = poiCategories;
        if (poiCategories != null && poiCategories.size() > 0) {
            RDPoiCategoryAdapter adapter = new RDPoiCategoryAdapter(poiCategories.keySet().toArray(new PoiCategory[poiCategories.keySet().size()]), this);
            rvCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            rvCategory.setAdapter(adapter);
            ViewCompat.setNestedScrollingEnabled(rvCategory, false);
            setupPoiAdapter((PoiCategory) argPoiCategory.keySet().toArray()[0]);
        }
    }

    @Override
    public void onPoiCategorySelected(PoiCategory poiCategory, int i) {
        setupPoiAdapter(poiCategory);
        RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(rvCategory.getContext());
        smoothScroller.setTargetPosition(i);
        rvCategory.getLayoutManager().startSmoothScroll(smoothScroller);
    }

    private void setupPoiAdapter(PoiCategory poiCategory) {
        List<RentPoiItem> rentPoiItemList = argPoiCategory.get(poiCategory);
        MarkerDetailPoiAdapter adapter = new MarkerDetailPoiAdapter(rentPoiItemList,this, rentLocation);
        rvPoi.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPoi.setAdapter(adapter);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_anim_fall_down);
        rvPoi.setLayoutAnimation(animationController);
    }

    @Override
    public void onPoiClick(RentPoiItem poiItem) {
        if(markerPoiInteraction!=null)
            markerPoiInteraction.onPoiClick(poiItem);
    }


    public interface MarkerPoiInteraction {
        void onPoiClick(RentPoiItem poiItem);
    }
}
