package com.infinitum.bookingqba.view.filter;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentFilterBinding;
import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.util.GlideApp;
import com.infinitum.bookingqba.view.adapters.filter.AmenitieViewItem;
import com.infinitum.bookingqba.view.adapters.filter.ReferenceZoneViewItem;
import com.infinitum.bookingqba.view.adapters.filter.StarViewItem;
import com.infinitum.bookingqba.view.interaction.FilterInteraction;
import com.infinitum.bookingqba.view.widgets.BetweenSpacesItemDecoration;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment {

    @Inject
    protected ViewModelFactory viewModelFactory;

    private FragmentFilterBinding filterBinding;

    private FilterInteraction interaction;

    private RentViewModel rentViewModel;

    private CompositeDisposable compositeDisposable;

    private Disposable disposable;

    private RendererRecyclerViewAdapter viewAdapter;


    public FilterFragment() {
        // Required empty public constructor
    }

    public static FilterFragment newInstance() {
        FilterFragment fragment = new FilterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        filterBinding.setIsLoading(true);
        filterBinding.slShimmerRzone.startShimmer();
        filterBinding.slShimmerAmenities.startShimmer();
        filterBinding.slShimmerStar.startShimmer();

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


    public void addAmenitiesView(List<AmenitiesEntity> amenitiesEntities) {
        TextView textView;
        for (AmenitiesEntity entity : amenitiesEntities) {
            textView = new TextView(getActivity());
            textView.setText(entity.getName());
            textView.setTag(entity.getId());
            textView.setBackgroundResource(R.drawable.shape_btn_ship);
//            filterBinding.fbAmenities.addView(textView);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        interaction = null;
        compositeDisposable.clear();
    }


    private void testingFilter() {
        disposable = rentViewModel.getMapFilterItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new ResourceSubscriber<Map<String, List<ViewModel>>>() {
                    @Override
                    public void onNext(Map<String, List<ViewModel>> mapResourse) {
                        if (mapResourse.containsKey("Amenities")) {
                            setAmenitiesAdapter(mapResourse.get("Amenities"));
                        }
                        if (mapResourse.containsKey("Zone")) {
                            setReferenceZoneAdapter(mapResourse.get("Zone"));
                        }
                        if (mapResourse.containsKey("Star")) {
                            setStarAdapter(mapResourse.get("Star"));
                        }
                        filterBinding.setIsLoading(false);
                        filterBinding.slShimmerRzone.stopShimmer();
                        filterBinding.slShimmerAmenities.stopShimmer();
                        filterBinding.slShimmerStar.stopShimmer();
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        compositeDisposable.add(disposable);
    }

    public void setAmenitiesAdapter(List<ViewModel> items) {
        RendererRecyclerViewAdapter recyclerViewAdapter = new RendererRecyclerViewAdapter();
        recyclerViewAdapter.registerRenderer(viewBinderAmenitiesFilter(R.layout.recycler_amenities_filter_ships));
        recyclerViewAdapter.setItems(items);
        filterBinding.rvAmenities.setAdapter(recyclerViewAdapter);
        filterBinding.rvAmenities.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        filterBinding.rvAmenities.addItemDecoration(new BetweenSpacesItemDecoration(5, 5));
    }

    public void setReferenceZoneAdapter(List<ViewModel> items) {
        RendererRecyclerViewAdapter recyclerViewAdapter = new RendererRecyclerViewAdapter();
        recyclerViewAdapter.registerRenderer(viewBinderRZoneFilter(R.layout.recycler_rzone_filter_ships));
        recyclerViewAdapter.setItems(items);
        filterBinding.rvReferenceZone.setAdapter(recyclerViewAdapter);
        filterBinding.rvReferenceZone.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        filterBinding.rvReferenceZone.addItemDecoration(new BetweenSpacesItemDecoration(5, 5));
    }

    public void setStarAdapter(List<ViewModel> items) {
        RendererRecyclerViewAdapter recyclerViewAdapter = new RendererRecyclerViewAdapter();
        recyclerViewAdapter.registerRenderer(viewBinderStarFilter(R.layout.recycler_stars_filter_ships));
        recyclerViewAdapter.setItems(items);
        filterBinding.rvStar.setAdapter(recyclerViewAdapter);
        filterBinding.rvStar.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        filterBinding.rvStar.addItemDecoration(new BetweenSpacesItemDecoration(5, 5));
    }


    private ViewBinder<AmenitieViewItem> viewBinderAmenitiesFilter(int layout) {
        return new ViewBinder<>(
                layout,
                AmenitieViewItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_amenitie_filter_title, (ViewProvider<TextView>) view -> {
                            view.setText(model.getmName());
                            changeAmenitieViewState(view,view.isSelected());
                        })
                        .setOnClickListener(view -> {
                            rentViewModel.changeStateFilterItem("Amenities", model.getId());
                            if (view.isSelected()) {
                                view.setSelected(false);
                                changeAmenitieViewState(view.findViewById(R.id.tv_amenitie_filter_title),view.isSelected());
                            } else {
                                view.setSelected(true);
                                changeAmenitieViewState(view.findViewById(R.id.tv_amenitie_filter_title),view.isSelected());
                            }
                        })
        );
    }


    private ViewBinder<ReferenceZoneViewItem> viewBinderRZoneFilter(int layout) {
        return new ViewBinder<>(
                layout,
                ReferenceZoneViewItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_rzone_filter_title, (ViewProvider<TextView>) view -> {
                            view.setText(model.getmName());
                        })
                        .find(R.id.iv_rzone_filter, (ViewProvider<AppCompatImageView>) view ->
                            GlideApp.with(getView()).load(model.getByteImage()).into(view))
                        .find(R.id.ll_rzone_filter_content, (ViewProvider<LinearLayout>) view ->
                            changeReferenceZoneViewState(view,view.isSelected()))
                        .setOnClickListener(view -> {
                            rentViewModel.changeStateFilterItem("Zone", model.getId());
                            if (view.isSelected()) {
                                view.setSelected(false);
                                changeReferenceZoneViewState(view.findViewById(R.id.ll_rzone_filter_content),view.isSelected());
                            } else {
                                view.setSelected(true);
                                changeReferenceZoneViewState(view.findViewById(R.id.ll_rzone_filter_content),view.isSelected());
                            }
                        })
        );
    }

    private ViewBinder<StarViewItem> viewBinderStarFilter(int layout) {
        return new ViewBinder<>(
                layout,
                StarViewItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_star_filter_title, (ViewProvider<TextView>) view -> {
                            view.setText(model.getmName());
                        })
                        .find(R.id.ll_star_filter_content, (ViewProvider<LinearLayout>) view ->
                                changeStarViewState(view,view.isSelected()))
                        .setOnClickListener(view -> {
                            rentViewModel.changeStateFilterItem("Star", model.getId());
                            if (view.isSelected()) {
                                view.setSelected(false);
                                changeStarViewState(view.findViewById(R.id.ll_star_filter_content),view.isSelected());
                            } else {
                                view.setSelected(true);
                                changeStarViewState(view.findViewById(R.id.ll_star_filter_content),view.isSelected());
                            }
                        })
        );
    }


    private void changeAmenitieViewState(TextView textView, boolean isSelected){
        if(isSelected){
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundResource(R.drawable.shape_amenitie_filter_ship_selected);
        }else{
            textView.setTextColor(Color.parseColor("#009689"));
            textView.setBackgroundResource(R.drawable.shape_amenitie_filter_ship_unselected);
        }
    }

    private void changeReferenceZoneViewState(LinearLayout view, boolean isSelected){
        if(isSelected){
            TextView textView = view.findViewById(R.id.tv_rzone_filter_title);
            textView.setTextColor(Color.parseColor("#009689"));
            view.setBackgroundResource(R.drawable.shape_rzone_filter_ship_selected);
        }else{
            TextView textView = view.findViewById(R.id.tv_rzone_filter_title);
            textView.setTextColor(Color.parseColor("#9E9E9E"));
            view.setBackgroundResource(R.drawable.shape_rzone_filter_ship_unselected);
        }
    }

    private void changeStarViewState(LinearLayout view, boolean isSelected){
        if(isSelected){
            TextView textView = view.findViewById(R.id.tv_star_filter_title);
            textView.setTextColor(Color.parseColor("#009689"));
            view.setBackgroundResource(R.drawable.shape_rzone_filter_ship_selected);
        }else{
            TextView textView = view.findViewById(R.id.tv_star_filter_title);
            textView.setTextColor(Color.parseColor("#9E9E9E"));
            view.setBackgroundResource(R.drawable.shape_rzone_filter_ship_unselected);
        }
    }

}



