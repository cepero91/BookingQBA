package com.infinitum.bookingqba.view.filter;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentFilterBinding;
import com.infinitum.bookingqba.view.adapters.FilterAdapter;
import com.infinitum.bookingqba.view.adapters.items.filter.CheckableItem;
import com.infinitum.bookingqba.view.interaction.FilterInteraction;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment implements View.OnClickListener, FilterAdapter.OnShipClick {

    @Inject
    protected ViewModelFactory viewModelFactory;

    private FragmentFilterBinding filterBinding;

    private FilterInteraction interaction;

    private RentViewModel rentViewModel;

    private CompositeDisposable compositeDisposable;

    private Disposable disposable;

    private RendererRecyclerViewAdapter viewAdapter;

    FilterAdapter rentModeAdapter;
    FilterAdapter amenitiesAdapter;
    FilterAdapter munAdapter;
    private FilterAdapter poiTypeAdapter;

    public static final String PROVINCE = "province";
    private String argProvince;


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

        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        filterBinding.btnFilter.setOnClickListener(this);
        filterBinding.btnClean.setOnClickListener(this);

        filterBinding.setIsLoading(true);

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
        super.onDestroy();
        interaction = null;
        compositeDisposable.clear();
    }

    public void loadFilterParams() {
        disposable = rentViewModel.getMapFilterItems(argProvince)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateUI, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void updateUI(Map<String, List<CheckableItem>> mapResourse) {
        if (mapResourse.containsKey("Amenities")) {
            setAmenitiesAdapter(mapResourse.get("Amenities"));
        }
        if (mapResourse.containsKey("Municipality")) {
            setReferenceZoneAdapter(mapResourse.get("Municipality"));
        }
        if (mapResourse.containsKey("RentMode")) {
            setRentModeAdapter(mapResourse.get("RentMode"));
        }
        if (mapResourse.containsKey("PoiType")) {
            setPoiTypeAdapter(mapResourse.get("PoiType"));
        }
        if (mapResourse.containsKey("Price")) {
            updateSeekBarPrice(mapResourse.get("Price").get(0).getName());
        }
        setupOrderingRadioGroup();
        filterBinding.setIsLoading(false);
    }

    private void updateSeekBarPrice(String price) {
        filterBinding.sbMaxPrice.setMax((int) Double.parseDouble(price));
        filterBinding.sbMaxPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                filterBinding.tvMaxPrice.setText(String.format("$ %s",progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                onShipClick();
            }
        });
    }

    private void setupOrderingRadioGroup(){
        filterBinding.rgOrdering.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onShipClick();
            }
        });
    }

    private void setPoiTypeAdapter(List<CheckableItem> checkableItems) {
        poiTypeAdapter = new FilterAdapter(checkableItems, this);
        filterBinding.rvPoiCategory.setAdapter(poiTypeAdapter);
        filterBinding.rvPoiCategory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void setAmenitiesAdapter(List<CheckableItem> items) {
        amenitiesAdapter = new FilterAdapter(items, this);
        filterBinding.rvAmenities.setAdapter(amenitiesAdapter);
        filterBinding.rvAmenities.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void setReferenceZoneAdapter(List<CheckableItem> items) {
        munAdapter = new FilterAdapter(items, this);
        filterBinding.rvMunicipality.setAdapter(munAdapter);
        filterBinding.rvMunicipality.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void setRentModeAdapter(List<CheckableItem> items) {
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
                rentModeAdapter.resetSelectedItem();
                munAdapter.resetSelectedItem();
                amenitiesAdapter.resetSelectedItem();
                poiTypeAdapter.resetSelectedItem();
                filterBinding.sbMaxPrice.setProgress(0);
                filterBinding.tvMaxPrice.setText("$ 0");
                filterBinding.rgOrdering.clearCheck();
                interaction.onFilterClean();
                break;
        }
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
        if (filterBinding.sbMaxPrice.getProgress() > 0) {
            List<String> params = new ArrayList<>();
            params.add(String.valueOf(filterBinding.sbMaxPrice.getProgress()));
            filterParams.put("Price", params);
        }
        if(filterBinding.rbComment.isChecked()){
            List<String> params = new ArrayList<>();
            params.add("c");
            filterParams.put("Order", params);
        }
        if(filterBinding.rbRating.isChecked()){
            List<String> params = new ArrayList<>();
            params.add("r");
            filterParams.put("Order", params);
        }
        return filterParams;
    }

    @Override
    public void onShipClick() {
        Map<String,List<String>> filterParams = getFilterParams();
        if (filterParams.size() > 0) {
            rentViewModel.filter(filterParams, argProvince).observe(this, pagedList ->
                    interaction.onFilterElement(pagedList));
        } else {
            interaction.onFilterClean();
        }
    }
}



