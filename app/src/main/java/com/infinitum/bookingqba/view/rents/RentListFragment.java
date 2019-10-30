package com.infinitum.bookingqba.view.rents;


import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentRentListBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.geo.GeoRentSort;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentlist.RentListItem;
import com.infinitum.bookingqba.view.adapters.RentListAdapter;
import com.infinitum.bookingqba.view.base.BaseNavigationFragment;
import com.infinitum.bookingqba.view.customview.RentListShortCutMapView;
import com.infinitum.bookingqba.view.customview.StateView;
import com.infinitum.bookingqba.view.interaction.FragmentNavInteraction;
import com.infinitum.bookingqba.viewmodel.RentViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.ORDER_TYPE_MOST_COMMENTED;
import static com.infinitum.bookingqba.util.Constants.ORDER_TYPE_MOST_RATING;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RentListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RentListFragment extends BaseNavigationFragment implements RentListShortCutMapView.ShortCutMapInteraction {

    private FragmentRentListBinding rentListBinding;

    private static final String PROVINCE_PARAM = "param1";
    private static final String ORDER_TYPE_PARAM = "param3";

    private String mProvinceParam;
    private char mOrderType = ORDER_TYPE_MOST_COMMENTED;

    private RentViewModel rentViewModel;

    private RentListAdapter pagerAdapter;

    private Disposable disposable;

    public RentListFragment() {
        // Required empty public constructor
    }


    public static RentListFragment newInstance(String province, char orderType) {
        RentListFragment fragment = new RentListFragment();
        Bundle args = new Bundle();
        args.putString(PROVINCE_PARAM, province);
        args.putChar(ORDER_TYPE_PARAM, orderType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProvinceParam = getArguments().getString(PROVINCE_PARAM);
            mOrderType = getArguments().getChar(ORDER_TYPE_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rentListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rent_list, container, false);
        return rentListBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initLoading(true, StateView.Status.LOADING, true);
        rentListBinding.shortCutMapView.setShortCutMapInteraction(this);

        setHasOptionsMenu(true);

        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        loadPaginatedData();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        initSearch(searchView);
    }

    private void initSearch(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pagerAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void initLoading(boolean loading, StateView.Status status, boolean isEmpty) {
        rentListBinding.setIsLoading(loading);
        rentListBinding.setIsEmpty(isEmpty);
        rentListBinding.stateView.setStatus(status);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_filter_panel).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    private void loadPaginatedData() {
        disposable = rentViewModel.getLiveDataRentList(mOrderType, mProvinceParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> {
                    setupPagerAdapter(listResource, null);
                }, throwable -> initLoading(false, StateView.Status.EMPTY, true));
        compositeDisposable.add(disposable);
    }

    public void needToRefresh(boolean refresh) {
        if (refresh) {
            initLoading(true, StateView.Status.LOADING, true);
            loadPaginatedData();
        }
    }

    public void filterListResult(Resource<List<GeoRent>> pagedList, @Nullable Location lastLocationKnow) {
        setupPagerAdapter(pagedList, lastLocationKnow);
    }

    private void setupPagerAdapter(Resource<List<GeoRent>> pagedList, @Nullable Location lastLocationKnow) {
        if (pagedList.data != null && pagedList.data.size() > 0) {
            List<GeoRent> geoList = pagedList.data;
            if(lastLocationKnow!=null)
                Collections.sort(geoList,new GeoRentSort(lastLocationKnow));
            pagerAdapter = new RentListAdapter(getLayoutInflater(), (FragmentNavInteraction) getActivity(),
                    geoList, lastLocationKnow != null);
            if (lastLocationKnow != null)
                pagerAdapter.setLastLocationKnow(lastLocationKnow);
            rentListBinding.recyclerView.setAdapter(pagerAdapter);
            rentListBinding.recyclerView.setLayoutManager(setupLayoutManager());
            initLoading(false, StateView.Status.SUCCESS, false);
        } else {
            initLoading(false, StateView.Status.EMPTY, true);
        }
    }


    public RecyclerView.LayoutManager setupLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }


    @Override
    public void onDestroyView() {
        rentListBinding.recyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (disposable != null && disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onDestroy();
    }


    @Override
    public void onButtonClick(View view) {
        int listSize = pagerAdapter.getFilteredList() != null ? pagerAdapter.getFilteredList().size() : 0;
        if (listSize > 0) {
            switch (view.getId()) {
                case R.id.button_five:
                    if (listSize >= 5) {
                        mListener.shortCutToMap(pagerAdapter.getFilteredList().subList(0, 5));
                    } else {
                        mListener.shortCutToMap(pagerAdapter.getFilteredList() != null ? pagerAdapter.getFilteredList().subList(0, listSize) : new ArrayList<>());
                    }
                    break;
                case R.id.button_ten:
                    if (listSize >= 10) {
                        mListener.shortCutToMap(pagerAdapter.getFilteredList().subList(0, 10));
                    } else {
                        mListener.shortCutToMap(pagerAdapter.getFilteredList() != null ? pagerAdapter.getFilteredList().subList(0, listSize) : new ArrayList<>());
                    }
                    break;
                case R.id.button_all:
                    mListener.shortCutToMap(pagerAdapter.getFilteredList() != null ? pagerAdapter.getFilteredList().subList(0, listSize) : new ArrayList<>());
                    break;
            }
        } else {
            AlertUtils.showErrorToast(getActivity(),"No hay nada que mostrar");
        }
    }
}
