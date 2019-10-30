package com.infinitum.bookingqba.view.filter;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentFilterBinding;
import com.infinitum.bookingqba.view.adapters.FilterAdapter;
import com.infinitum.bookingqba.view.adapters.items.filter.BaseFilterItem;
import com.infinitum.bookingqba.view.adapters.items.filter.CheckableFilterItem;
import com.infinitum.bookingqba.view.adapters.items.filter.NumberFilterItem;
import com.infinitum.bookingqba.view.adapters.items.filter.RangeFilterItem;
import com.infinitum.bookingqba.view.interaction.FilterInteraction;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.FILTER_AMENITIES;
import static com.infinitum.bookingqba.util.Constants.FILTER_CAPABILITY;
import static com.infinitum.bookingqba.util.Constants.FILTER_MUNICIPALITIES;
import static com.infinitum.bookingqba.util.Constants.FILTER_POITYPES;
import static com.infinitum.bookingqba.util.Constants.FILTER_PRICE;
import static com.infinitum.bookingqba.util.Constants.FILTER_RENTMODE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment implements View.OnClickListener,
        FilterAdapter.OnShipClick, CheckBox.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {

    @Inject
    protected ViewModelFactory viewModelFactory;

    private FragmentFilterBinding filterBinding;

    private FilterInteraction interaction;

    private RentViewModel rentViewModel;

    private CompositeDisposable compositeDisposable;

    private Disposable disposable;

    private Location userLocation;

    private FilterAdapter rentModeAdapter;
    private FilterAdapter amenitiesAdapter;
    private FilterAdapter munAdapter;
    private FilterAdapter poiTypeAdapter;

    public static final String PROVINCE = "province";
    private String argProvince;
    private boolean activateDistance = false;

    public FilterFragment() {
        // Required empty public constructor
    }

    public static FilterFragment newInstance(String argProvince) {
        FilterFragment fragment = new FilterFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PROVINCE, argProvince);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            argProvince = getArguments().getString(PROVINCE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        filterBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter, container, false);
        return filterBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        filterBinding.setIsLoading(true);
        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);
        filterBinding.btnFilter.setOnClickListener(this);
        filterBinding.btnClean.setOnClickListener(this);
        filterBinding.cbUseLocation.setOnCheckedChangeListener(this);

        loadFilterParams();
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        if (context instanceof FilterInteraction) {
            interaction = (FilterInteraction) context;
            compositeDisposable = new CompositeDisposable();
        }
    }

    @Override
    public void onDestroy() {
        interaction = null;
        compositeDisposable.clear();
        super.onDestroy();
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
        filterBinding.flUserLocation.setVisibility(View.VISIBLE);
    }

    public void loadFilterParams() {
        disposable = rentViewModel.getMapFilterItems(argProvince)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateUI, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void updateUI(Map<String, List<? extends BaseFilterItem>> mapResourse) {
        if (mapResourse.containsKey(FILTER_AMENITIES)) {
            setAmenitiesAdapter((List<CheckableFilterItem>) mapResourse.get(FILTER_AMENITIES));
        }
        if (mapResourse.containsKey(FILTER_MUNICIPALITIES)) {
            setMunicipalitiesAdapter((List<CheckableFilterItem>) mapResourse.get(FILTER_MUNICIPALITIES));
        }
        if (mapResourse.containsKey(FILTER_RENTMODE)) {
            setRentModeAdapter((List<CheckableFilterItem>) mapResourse.get(FILTER_RENTMODE));
        }
        if (mapResourse.containsKey(FILTER_POITYPES)) {
            setPoiTypeAdapter((List<CheckableFilterItem>) mapResourse.get(FILTER_POITYPES));
        }
        if (mapResourse.containsKey(FILTER_PRICE)) {
            updateRangePrice((RangeFilterItem) mapResourse.get(FILTER_PRICE).get(0));
        }
        if (mapResourse.containsKey(FILTER_CAPABILITY)) {
            updateSeekBarCapability((NumberFilterItem) mapResourse.get(FILTER_CAPABILITY).get(0));
        }
        setupOrderingRadioGroup();
        setupSeekBarDistance();
        filterBinding.setIsLoading(false);
    }

    private void setupSeekBarDistance() {
        filterBinding.rsbDistance.setIndicatorTextDecimalFormat("0.0");
        filterBinding.rsbDistance.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                onShipClick();
            }
        });
    }

    private void updateSeekBarCapability(NumberFilterItem numberFilterItem) {
        filterBinding.rsbCapability.setRange(0, numberFilterItem.getMaxNumber());
        filterBinding.rsbCapability.setIndicatorTextDecimalFormat("0");
        filterBinding.rsbCapability.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                onShipClick();
            }
        });
    }

    private void updateRangePrice(RangeFilterItem rangeFilterItem) {
        filterBinding.rsbPrice.setRange(0f, rangeFilterItem.getMaxPrice());
        filterBinding.rsbPrice.setIndicatorTextDecimalFormat("0.0");
        filterBinding.rsbPrice.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                onShipClick();
            }
        });
    }

    private void setupOrderingRadioGroup() {
        filterBinding.rgOrdering.setOnCheckedChangeListener(this);
    }

    private void setPoiTypeAdapter(List<CheckableFilterItem> checkableItems) {
        poiTypeAdapter = new FilterAdapter(checkableItems, this);
        filterBinding.rvPoiCategory.setAdapter(poiTypeAdapter);
        filterBinding.rvPoiCategory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void setAmenitiesAdapter(List<CheckableFilterItem> items) {
        amenitiesAdapter = new FilterAdapter(items, this);
        filterBinding.rvAmenities.setAdapter(amenitiesAdapter);
        filterBinding.rvAmenities.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void setMunicipalitiesAdapter(List<CheckableFilterItem> items) {
        munAdapter = new FilterAdapter(items, this);
        filterBinding.rvMunicipality.setAdapter(munAdapter);
        filterBinding.rvMunicipality.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void setRentModeAdapter(List<CheckableFilterItem> items) {
        rentModeAdapter = new FilterAdapter(items, this);
        filterBinding.rvRentMode.setAdapter(rentModeAdapter);
        filterBinding.rvRentMode.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_filter:
                interaction.closeFilter();
                break;
            case R.id.btn_clean:
                resetAllParams();
                interaction.onFilterClean();
                break;
        }
    }

    public void resetAllParams() {
        rentModeAdapter.resetSelectedItem();
        filterBinding.rvRentMode.scrollToPosition(0);
        munAdapter.resetSelectedItem();
        filterBinding.rvMunicipality.scrollToPosition(0);
        amenitiesAdapter.resetSelectedItem();
        filterBinding.rvAmenities.scrollToPosition(0);
        poiTypeAdapter.resetSelectedItem();
        filterBinding.rvPoiCategory.scrollToPosition(0);
        filterBinding.rsbPrice.setProgress(0, 0);
        filterBinding.rsbCapability.setProgress(0);
        filterBinding.rgOrdering.setOnCheckedChangeListener(null);
        filterBinding.rgOrdering.clearCheck();
        filterBinding.rgOrdering.setOnCheckedChangeListener(this);
        activateDistance = false;
        filterBinding.rsbDistance.setProgress(0);
        filterBinding.tvDistanceTitle.setVisibility(View.GONE);
        filterBinding.rsbDistance.setVisibility(View.GONE);
        filterBinding.llContentMun.setVisibility(View.VISIBLE);
        filterBinding.cbUseLocation.setOnCheckedChangeListener(null);
        filterBinding.cbUseLocation.setChecked(false);
        filterBinding.cbUseLocation.setOnCheckedChangeListener(this);
    }

    private Map<String, List<String>> getFilterParams() {
        Map<String, List<String>> filterParams = new HashMap<>();
        if (amenitiesAdapter.getSelectedItems().size() > 0) {
            filterParams.put("Amenities", amenitiesAdapter.getSelectedItems());
        }
        if (munAdapter.getSelectedItems().size() > 0) {
            filterParams.put("Municipality", munAdapter.getSelectedItems());
        }
        if (rentModeAdapter.getSelectedItems().size() > 0) {
            filterParams.put("RentMode", rentModeAdapter.getSelectedItems());
        }
        if (poiTypeAdapter.getSelectedItems().size() > 0) {
            filterParams.put("Poi", poiTypeAdapter.getSelectedItems());
        }
        if (filterBinding.rsbPrice.getRightSeekBar().getProgress() > 0) {
            List<String> params = new ArrayList<>();
            params.add(String.format("%.2f", filterBinding.rsbPrice.getLeftSeekBar().getProgress()));
            params.add(String.format("%.2f", filterBinding.rsbPrice.getRightSeekBar().getProgress()));
            filterParams.put("Price", params);
        }
        if (filterBinding.rsbCapability.getLeftSeekBar().getProgress() > 0) {
            List<String> params = new ArrayList<>();
            Float progress = filterBinding.rsbCapability.getLeftSeekBar().getProgress();
            params.add(String.format("%s", progress.intValue()));
            filterParams.put("Capability", params);
        }
        if (filterBinding.rbComment.isChecked()) {
            List<String> params = new ArrayList<>();
            params.add("c");
            filterParams.put("Order", params);
        }
        if (filterBinding.rbRating.isChecked()) {
            List<String> params = new ArrayList<>();
            params.add("r");
            filterParams.put("Order", params);
        }
        if (filterBinding.rbPrice.isChecked()) {
            List<String> params = new ArrayList<>();
            params.add("p");
            filterParams.put("Order", params);
        }
        if (filterBinding.cbUseLocation.isChecked() && filterBinding.rsbDistance.getLeftSeekBar().getProgress() > 0) {
            activateDistance = true;
            Float distance = filterBinding.rsbDistance.getLeftSeekBar().getProgress();
            List<String> params = new ArrayList<>();
            params.add(String.valueOf(distance.floatValue()));
            params.add(String.valueOf(userLocation.getLatitude()));
            params.add(String.valueOf(userLocation.getLongitude()));
            filterParams.put("Distance", params);
        }
        return filterParams;
    }

    @Override
    public void onShipClick() {
        disposable = Flowable.just(getFilterParams())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringListMap -> {
                    if (stringListMap.size() > 0) {
                        filter(stringListMap, argProvince);
                    } else {
                        interaction.onFilterClean();
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void filter(Map<String, List<String>> filterParams, String argProvince) {
        disposable = rentViewModel.filter(filterParams, argProvince)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> {
                    interaction.onFilterElement(listResource, activateDistance && userLocation != null ? userLocation : null);
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            filterBinding.llContentMun.setVisibility(View.GONE);
            filterBinding.tvDistanceTitle.setVisibility(View.VISIBLE);
            filterBinding.rsbDistance.setVisibility(View.VISIBLE);
            if(munAdapter.isAtLessOneSelected()) {
                munAdapter.resetSelectedItem();
                filterBinding.rvMunicipality.scrollToPosition(0);
                onShipClick();
            }
        } else {
            if(filterBinding.rsbDistance.getLeftSeekBar().getProgress() > 0) {
                activateDistance = false;
                filterBinding.rsbDistance.setProgress(0);
                onShipClick();
            }
            filterBinding.tvDistanceTitle.setVisibility(View.GONE);
            filterBinding.rsbDistance.setVisibility(View.GONE);
            filterBinding.llContentMun.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton rb = group.findViewById(checkedId);
        if (rb.isChecked()) {
            onShipClick();
        }
    }
}



