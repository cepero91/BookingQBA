package com.infinitum.bookingqba.view.rents;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentRentDetailPoiBinding;
import com.infinitum.bookingqba.databinding.FragmentRentPoiBinding;
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.view.adapters.RDPoiAdapter;
import com.infinitum.bookingqba.view.adapters.RDPoiCategoryAdapter;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.PoiCategory;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentPoiItem;
import com.infinitum.bookingqba.view.widgets.CenterSmoothScroller;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import org.mapsforge.core.model.LatLong;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RentPoiFragment extends DialogFragment implements RDPoiCategoryAdapter.PoiCategorySelection {

    private FragmentRentPoiBinding rentPoiBinding;
    private static final String RENT_UUID = "rentUuid";
    private static final String ARG_REFERENCE_ZONE = "refZone";
    private static final String ARG_LAT_LONG = "latLon";
    private Map<PoiCategory, List<RentPoiItem>> argPoiCategory;
    private String argRefZone;
    private String argLatLon;
    private String argRentUuid;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SharedPreferences sharedPreferences;

    private RentViewModel rentViewModel;

    public RentPoiFragment() {
        // Required empty public constructor
    }

    public static RentPoiFragment newInstance(String argRentUuid, String argRefZone, String argLatLon) {
        RentPoiFragment fragment = new RentPoiFragment();
        Bundle args = new Bundle();
        args.putString(RENT_UUID, argRentUuid);
        args.putString(ARG_REFERENCE_ZONE, argRefZone);
        args.putString(ARG_LAT_LONG, argLatLon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.MyDialogFragment);
        if (getArguments() != null) {
            argRentUuid = getArguments().getString(RENT_UUID);
            argRefZone = getArguments().getString(ARG_REFERENCE_ZONE);
            argLatLon = getArguments().getString(ARG_LAT_LONG);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rentPoiBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rent_poi, container, false);
        return rentPoiBinding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);

        rentPoiBinding.setLoading(true);

        compositeDisposable = new CompositeDisposable();
        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        setupReferenceZone(argRefZone);

        loadRentPoiById(argRentUuid);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

    }

    private void loadRentPoiById(String argRentUuid) {
        disposable = rentViewModel.getRentPoiById(argRentUuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mapResource -> {
                    if(mapResource.data!=null){
                        argPoiCategory = mapResource.data;
                        setupPoiCategoryAdapter(mapResource.data);
                        PoiCategory poiCategory = (PoiCategory) mapResource.data.keySet().toArray()[0];
                        setupPoiAdapter(poiCategory);
                        rentPoiBinding.setLoading(false);
                    }
                },Timber::e);
        compositeDisposable.add(disposable);
    }

    private void setupReferenceZone(String argRefZone) {
        if (argRefZone.equalsIgnoreCase(Constants.URBAN)) {
            rentPoiBinding.tvRzTitle.setText(getString(R.string.neibor_enviroment));
            rentPoiBinding.tvRzDescription.setText(getString(R.string.neiborhood_reference));
            rentPoiBinding.tvRzDescription.setCompoundDrawablesWithIntrinsicBounds(null,null,getActivity().getDrawable(R.drawable.compass),null);
        } else if (argRefZone.equalsIgnoreCase(Constants.BEACH)) {
            rentPoiBinding.tvRzTitle.setText(getString(R.string.beach_enviroment));
            rentPoiBinding.tvRzDescription.setText(getString(R.string.beach_reference));
            rentPoiBinding.tvRzDescription.setCompoundDrawablesWithIntrinsicBounds(null,null,getActivity().getDrawable(R.drawable.beach),null);
        } else if (argRefZone.equalsIgnoreCase(Constants.NATURAL)) {
            rentPoiBinding.tvRzTitle.setText(getString(R.string.natural_enviroment));
            rentPoiBinding.tvRzDescription.setText(getString(R.string.natural_reference));
            rentPoiBinding.tvRzDescription.setCompoundDrawablesWithIntrinsicBounds(null,null,getActivity().getDrawable(R.drawable.caravan),null);
        } else if (argRefZone.equalsIgnoreCase(Constants.HISTORIC)) {
            rentPoiBinding.tvRzTitle.setText(getString(R.string.historic_enviroment));
            rentPoiBinding.tvRzDescription.setText(getString(R.string.historic_reference));
            rentPoiBinding.tvRzDescription.setCompoundDrawablesWithIntrinsicBounds(null,null,getActivity().getDrawable(R.drawable.pictures),null);
        } else if (argRefZone.equalsIgnoreCase(Constants.CULTURE)) {
            rentPoiBinding.tvRzTitle.setText(getString(R.string.culture_enviroment));
            rentPoiBinding.tvRzDescription.setText(getString(R.string.culture_reference));
            rentPoiBinding.tvRzDescription.setCompoundDrawablesWithIntrinsicBounds(null,null,getActivity().getDrawable(R.drawable.photo_camera),null);
        }
    }

    private void setupPoiCategoryAdapter(Map<PoiCategory, List<RentPoiItem>> poiCategories) {
        if (poiCategories != null && poiCategories.size() > 0) {
            RDPoiCategoryAdapter adapter = new RDPoiCategoryAdapter(poiCategories.keySet().toArray(new PoiCategory[poiCategories.keySet().size()]), this);
            rentPoiBinding.rvCategory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            rentPoiBinding.rvCategory.setAdapter(adapter);
            ViewCompat.setNestedScrollingEnabled(rentPoiBinding.rvCategory, false);
        }
    }


    @Override
    public void onPoiCategorySelected(PoiCategory poiCategory, int i) {
        setupPoiAdapter(poiCategory);
        RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(rentPoiBinding.rvCategory.getContext());
        smoothScroller.setTargetPosition(i);
        rentPoiBinding.rvCategory.getLayoutManager().startSmoothScroll(smoothScroller);
    }

    private void setupPoiAdapter(PoiCategory poiCategory) {
        List<RentPoiItem> rentPoiItemList = argPoiCategory.get(poiCategory);
        RDPoiAdapter adapter = new RDPoiAdapter(rentPoiItemList, LatLong.fromString(argLatLon));
        rentPoiBinding.rvPoi.setLayoutManager(new LinearLayoutManager(getActivity()));
        rentPoiBinding.rvPoi.setAdapter(adapter);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_anim_fall_down);
        rentPoiBinding.rvPoi.setLayoutAnimation(animationController);
    }

    @Override
    public void onDetach() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        compositeDisposable.clear();
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        compositeDisposable.clear();
        super.onDestroyView();
    }
}
