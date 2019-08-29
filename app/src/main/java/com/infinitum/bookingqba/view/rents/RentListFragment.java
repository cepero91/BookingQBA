package com.infinitum.bookingqba.view.rents;


import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentRentListBinding;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentlist.RentListItem;
import com.infinitum.bookingqba.view.adapters.RentListAdapter;
import com.infinitum.bookingqba.view.base.BaseNavigationFragment;
import com.infinitum.bookingqba.view.customview.RentListShortCutMapView;
import com.infinitum.bookingqba.viewmodel.RentViewModel;

import java.util.ArrayList;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

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

        rentListBinding.setIsLoading(true);
        rentListBinding.setIsEmpty(false);
        rentListBinding.shortCutMapView.setShortCutMapInteraction(this);

        setHasOptionsMenu(true);

        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        loadPaginatedData();

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_filter_panel);
        menuItem.setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    private void loadPaginatedData() {
        pagerAdapter = new RentListAdapter(getActivity().getLayoutInflater(), mListener);
        rentViewModel.getLiveDataRentList(mOrderType, mProvinceParam).observe(this, this::setupPagerAdapter);
    }

    public void needToRefresh(boolean refresh) {
        if (refresh) {
            rentListBinding.setIsLoading(true);
            loadPaginatedData();
        }
    }

    public void filterListResult(PagedList<GeoRent> pagedList) {
        setupPagerAdapter(pagedList);
    }

    private void setupPagerAdapter(PagedList<GeoRent> pagedList) {
        if (pagedList.size() > 0) {
            pagerAdapter.submitList(pagedList);
            rentListBinding.recyclerView.setAdapter(pagerAdapter);
            rentListBinding.recyclerView.setLayoutManager(setupLayoutManager());
            rentListBinding.setIsLoading(false);
            rentListBinding.setIsEmpty(false);
        } else {
            rentListBinding.setIsLoading(false);
            rentListBinding.setIsEmpty(true);
        }
    }


    public RecyclerView.LayoutManager setupLayoutManager() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        return mLayoutManager;
    }


    @Override
    public void onDestroyView() {
        rentListBinding.recyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onButtonClick(View view) {
        int listSize = pagerAdapter.getCurrentList() != null ? pagerAdapter.getCurrentList().size() : 0;
        switch (view.getId()) {
            case R.id.button_five:
                if (listSize >= 5) {
                    mListener.shortCutToMap(pagerAdapter.getCurrentList().subList(0, 5));
                } else {
                    mListener.shortCutToMap(pagerAdapter.getCurrentList() != null ? pagerAdapter.getCurrentList().subList(0, listSize) : new ArrayList<>());
                }
                break;
            case R.id.button_ten:
                if (listSize >= 10) {
                    mListener.shortCutToMap(pagerAdapter.getCurrentList().subList(0, 10));
                } else {
                    mListener.shortCutToMap(pagerAdapter.getCurrentList() != null ? pagerAdapter.getCurrentList().subList(0, listSize) : new ArrayList<>());
                }
                break;
            case R.id.button_all:
                mListener.shortCutToMap(pagerAdapter.getCurrentList() != null ? pagerAdapter.getCurrentList().subList(0, listSize) : new ArrayList<>());
                break;
        }
    }
}
