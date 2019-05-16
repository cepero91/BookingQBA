package com.infinitum.bookingqba.view.filter;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.guilhe.views.SeekBarRangedView;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentFilterBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.view.adapters.FilterAdapter;
import com.infinitum.bookingqba.view.adapters.items.filter.AmenitieViewItem;
import com.infinitum.bookingqba.view.adapters.items.filter.CheckableItem;
import com.infinitum.bookingqba.view.adapters.items.filter.ReferenceZoneViewItem;
import com.infinitum.bookingqba.view.adapters.items.rentlist.RentListItem;
import com.infinitum.bookingqba.view.interaction.FilterInteraction;
import com.infinitum.bookingqba.view.widgets.BetweenSpacesItemDecoration;
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
import io.reactivex.subscribers.ResourceSubscriber;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment implements View.OnClickListener{

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

    public static final String PROVINCE = "province";
    private String argProvince;


    public FilterFragment() {
        // Required empty public constructor
    }

    public static FilterFragment newInstance(String argProvince) {
        FilterFragment fragment = new FilterFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PROVINCE,argProvince);
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

        filterBinding.priceSeek.setOnSeekBarRangedChangeListener(new SeekBarRangedView.OnSeekBarRangedChangeListener() {
            @Override
            public void onChanged(SeekBarRangedView view, float minValue, float maxValue) {

            }

            @Override
            public void onChanging(SeekBarRangedView view, float minValue, float maxValue) {
                filterBinding.tvMinMax.setText(String.format("min: %2.0f  max: %2.0f", minValue, maxValue));
            }
        });


        testingFilter();


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

    private void testingFilter() {
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
        filterBinding.setIsLoading(false);
    }

    public void setAmenitiesAdapter(List<CheckableItem> items) {
        amenitiesAdapter = new FilterAdapter(items);
        filterBinding.rvAmenities.setAdapter(amenitiesAdapter);
        filterBinding.rvAmenities.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        filterBinding.rvAmenities.addItemDecoration(new BetweenSpacesItemDecoration(5, 5));
    }

    public void setReferenceZoneAdapter(List<CheckableItem> items) {
        munAdapter = new FilterAdapter(items);
        filterBinding.rvMunicipality.setAdapter(munAdapter);
        filterBinding.rvMunicipality.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        filterBinding.rvMunicipality.addItemDecoration(new BetweenSpacesItemDecoration(5, 5));
    }

    public void setRentModeAdapter(List<CheckableItem> items) {
        rentModeAdapter = new FilterAdapter(items);
        filterBinding.rvRentMode.setAdapter(rentModeAdapter);
        filterBinding.rvRentMode.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        filterBinding.rvRentMode.addItemDecoration(new BetweenSpacesItemDecoration(5, 5));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_filter:
                rentViewModel.filter(getFilterParams()).observe(this, pagedList ->
                        interaction.onFilterElement(pagedList));
                break;
            case R.id.btn_clean:
                rentModeAdapter.resetSelectedItem();
                munAdapter.resetSelectedItem();
                amenitiesAdapter.resetSelectedItem();
                interaction.onFilterClean();
                break;
        }
    }

    private void filterResult(Resource<List<RentAndGalery>> resource) {
        if(resource.data!=null && resource.data.size()>0){

        }
    }

    private Map<String,List<String>> getFilterParams(){
        Map<String,List<String>> filterParams = new HashMap<>();
        if(amenitiesAdapter.getSelectedItems().size()>0){
            filterParams.put("Amenities",amenitiesAdapter.getSelectedItems());
        }
        if(munAdapter.getSelectedItems().size()>0){
            filterParams.put("Municipality",munAdapter.getSelectedItems());
        }
        if(rentModeAdapter.getSelectedItems().size()>0){
            filterParams.put("RentMode",rentModeAdapter.getSelectedItems());
        }
        if(filterBinding.priceSeek.getSelectedMaxValue()>0){
            float min = filterBinding.priceSeek.getSelectedMinValue();
            float max = filterBinding.priceSeek.getSelectedMaxValue();
            List<String> params = new ArrayList<>();
            params.add(String.valueOf(min));
            params.add(String.valueOf(max));
            filterParams.put("Price",params);
        }
        return filterParams;
    }
}



