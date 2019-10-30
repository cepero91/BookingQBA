package com.infinitum.bookingqba.view.rents;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentRentDetailPoiBinding;
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.view.adapters.RDPoiAdapter;
import com.infinitum.bookingqba.view.adapters.RDPoiCategoryAdapter;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.PoiCategory;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentPoiItem;
import com.infinitum.bookingqba.view.widgets.CenterSmoothScroller;

import org.mapsforge.core.model.LatLong;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class RentDetailPoiFragment extends Fragment implements RDPoiCategoryAdapter.PoiCategorySelection {

    private FragmentRentDetailPoiBinding fragmentRentDetailPoiBinding;

    private static final String ARG_POI_CATEGORY = "poiCategory";

    private static final String ARG_REFERENCE_ZONE = "refZone";

    private static final String ARG_LAT_LONG = "latLon";


    private Map<PoiCategory, List<RentPoiItem>> argPoiCategory;

    private String argRefZone;

    private String argLatLon;

    public RentDetailPoiFragment() {
        // Required empty public constructor
    }

    public static RentDetailPoiFragment newInstance(Map<PoiCategory, List<RentPoiItem>> argPoiCategory, String argRefZone, String argLatLon) {
        RentDetailPoiFragment fragment = new RentDetailPoiFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POI_CATEGORY, (Serializable) argPoiCategory);
        args.putString(ARG_REFERENCE_ZONE, argRefZone);
        args.putString(ARG_LAT_LONG, argLatLon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            argPoiCategory = (Map<PoiCategory, List<RentPoiItem>>) getArguments().getSerializable(ARG_POI_CATEGORY);
            argRefZone = getArguments().getString(ARG_REFERENCE_ZONE);
            argLatLon = getArguments().getString(ARG_LAT_LONG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRentDetailPoiBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rent_detail_poi, container, false);
        return fragmentRentDetailPoiBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);
        setupReferenceZone(argRefZone);
        setupPoiCategoryAdapter(argPoiCategory);
        if (argPoiCategory != null) {
            setupPoiAdapter((PoiCategory) argPoiCategory.keySet().toArray()[0]);
        }
    }

    private void setupReferenceZone(String argRefZone) {
        if (argRefZone.equalsIgnoreCase(Constants.URBAN)) {
            fragmentRentDetailPoiBinding.tvRzTitle.setText(getString(R.string.neibor_enviroment));
            fragmentRentDetailPoiBinding.tvRzDescription.setText(getString(R.string.neiborhood_reference));
            fragmentRentDetailPoiBinding.tvRzDescription.setCompoundDrawablesWithIntrinsicBounds(null,null,getActivity().getDrawable(R.drawable.compass),null);
        } else if (argRefZone.equalsIgnoreCase(Constants.BEACH)) {
            fragmentRentDetailPoiBinding.tvRzTitle.setText(getString(R.string.beach_enviroment));
            fragmentRentDetailPoiBinding.tvRzDescription.setText(getString(R.string.beach_reference));
            fragmentRentDetailPoiBinding.tvRzDescription.setCompoundDrawablesWithIntrinsicBounds(null,null,getActivity().getDrawable(R.drawable.beach),null);
        } else if (argRefZone.equalsIgnoreCase(Constants.NATURAL)) {
            fragmentRentDetailPoiBinding.tvRzTitle.setText(getString(R.string.natural_enviroment));
            fragmentRentDetailPoiBinding.tvRzDescription.setText(getString(R.string.natural_reference));
            fragmentRentDetailPoiBinding.tvRzDescription.setCompoundDrawablesWithIntrinsicBounds(null,null,getActivity().getDrawable(R.drawable.caravan),null);
        } else if (argRefZone.equalsIgnoreCase(Constants.HISTORIC)) {
            fragmentRentDetailPoiBinding.tvRzTitle.setText(getString(R.string.historic_enviroment));
            fragmentRentDetailPoiBinding.tvRzDescription.setText(getString(R.string.historic_reference));
            fragmentRentDetailPoiBinding.tvRzDescription.setCompoundDrawablesWithIntrinsicBounds(null,null,getActivity().getDrawable(R.drawable.pictures),null);
        } else if (argRefZone.equalsIgnoreCase(Constants.CULTURE)) {
            fragmentRentDetailPoiBinding.tvRzTitle.setText(getString(R.string.culture_enviroment));
            fragmentRentDetailPoiBinding.tvRzDescription.setText(getString(R.string.culture_reference));
            fragmentRentDetailPoiBinding.tvRzDescription.setCompoundDrawablesWithIntrinsicBounds(null,null,getActivity().getDrawable(R.drawable.photo_camera),null);
        }
    }

    private void setupPoiCategoryAdapter(Map<PoiCategory, List<RentPoiItem>> poiCategories) {
        if (poiCategories != null && poiCategories.size() > 0) {
            RDPoiCategoryAdapter adapter = new RDPoiCategoryAdapter(poiCategories.keySet().toArray(new PoiCategory[poiCategories.keySet().size()]), this);
            fragmentRentDetailPoiBinding.rvCategory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            fragmentRentDetailPoiBinding.rvCategory.setAdapter(adapter);
            ViewCompat.setNestedScrollingEnabled(fragmentRentDetailPoiBinding.rvCategory, false);
        }
    }


    @Override
    public void onPoiCategorySelected(PoiCategory poiCategory, int i) {
        setupPoiAdapter(poiCategory);
        RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(fragmentRentDetailPoiBinding.rvCategory.getContext());
        smoothScroller.setTargetPosition(i);
        fragmentRentDetailPoiBinding.rvCategory.getLayoutManager().startSmoothScroll(smoothScroller);
    }

    private void setupPoiAdapter(PoiCategory poiCategory) {
        List<RentPoiItem> rentPoiItemList = argPoiCategory.get(poiCategory);
        RDPoiAdapter adapter = new RDPoiAdapter(rentPoiItemList, LatLong.fromString(argLatLon));
        fragmentRentDetailPoiBinding.rvPoi.setLayoutManager(new LinearLayoutManager(getActivity()));
        fragmentRentDetailPoiBinding.rvPoi.setAdapter(adapter);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_anim_fall_down);
        fragmentRentDetailPoiBinding.rvPoi.setLayoutAnimation(animationController);
    }
}
