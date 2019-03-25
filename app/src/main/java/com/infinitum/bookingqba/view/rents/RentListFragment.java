package com.infinitum.bookingqba.view.rents;


import android.arch.lifecycle.Observer;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentRentListBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.util.GlideApp;
import com.infinitum.bookingqba.view.adapters.rent.RentListItem;
import com.infinitum.bookingqba.view.adapters.rent.RentPagerAdapter;
import com.infinitum.bookingqba.view.base.BaseNavigationFragment;
import com.infinitum.bookingqba.view.widgets.BetweenSpacesItemDecoration;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.willy.ratingbar.BaseRatingBar;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;

import static com.infinitum.bookingqba.util.Constants.ORDER_TYPE_POPULAR;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RentListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RentListFragment extends BaseNavigationFragment {

    private FragmentRentListBinding rentListBinding;

    private static final String PROVINCE_PARAM = "param1";
    private static final String REFERENCE_ZONE_PARAM = "param2";
    private static final String ORDER_TYPE_PARAM = "param3";

    private String mProvinceParam;
    private String mReferenceZoneParam;
    private char mOrderType = ORDER_TYPE_POPULAR;

    private RentViewModel rentViewModel;

    private RentPagerAdapter pagerAdapter;


    public RentListFragment() {
        // Required empty public constructor
    }


    public static RentListFragment newInstance(String province, String reference, char orderType) {
        RentListFragment fragment = new RentListFragment();
        Bundle args = new Bundle();
        args.putString(PROVINCE_PARAM, province);
        args.putString(REFERENCE_ZONE_PARAM, reference);
        args.putChar(ORDER_TYPE_PARAM, orderType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProvinceParam = getArguments().getString(PROVINCE_PARAM);
            mReferenceZoneParam = getArguments().getString(REFERENCE_ZONE_PARAM);
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

        setHasOptionsMenu(true);

        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        rentListBinding.setIsLoading(true);


//        loadData();

        loadDataPag();

//        initRecycleView();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_filter_panel);
        menuItem.setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    private void loadDataPag() {

        pagerAdapter = new RentPagerAdapter(getActivity().getLayoutInflater(),mListener);

        rentViewModel.getLdRentsList().observe(this, new Observer<PagedList<RentListItem>>() {
            @Override
            public void onChanged(@Nullable PagedList<RentListItem> rentListItems) {
                pagerAdapter.submitList(rentListItems);
                rentListBinding.recyclerView.setAdapter(pagerAdapter);
                rentListBinding.recyclerView.setLayoutManager(setupLayoutManager());
                rentListBinding.recyclerView.addItemDecoration(new BetweenSpacesItemDecoration(0, 5));
                rentListBinding.setIsLoading(false);
            }
        });

    }


    private void loadData() {
        Disposable disposable = rentViewModel.getRents(mOrderType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new ResourceSubscriber<Resource<List<RentListItem>>>() {
                    @Override
                    public void onNext(Resource<List<RentListItem>> listResource) {
                        if (listResource.data != null) {
                            setItemsAdapter(listResource.data);
                        }
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

//    public void initRecycleView(){
//        RendererRecyclerViewAdapter recyclerViewAdapter = new RendererRecyclerViewAdapter();
//        recyclerViewAdapter.registerRenderer(getRentListItem(R.layout.recycler_rent_list_item));
//        recyclerViewAdapter.setItems(dataGenerator.getRentListItems());
//
//        rentListBinding.recyclerView.setAdapter(recyclerViewAdapter);
//        rentListBinding.recyclerView.setLayoutManager(setupLayoutManager());
//        rentListBinding.recyclerView.addItemDecoration(new BetweenSpacesItemDecoration(5, 5));
//
//    }

    public void setItemsAdapter(List<RentListItem> rentList) {
        RendererRecyclerViewAdapter recyclerViewAdapter = new RendererRecyclerViewAdapter();
        recyclerViewAdapter.registerRenderer(getRentListItem(R.layout.recycler_rent_list_item));
        recyclerViewAdapter.setItems(rentList);
        rentListBinding.setIsLoading(false);
        rentListBinding.recyclerView.setAdapter(recyclerViewAdapter);
        rentListBinding.recyclerView.setLayoutManager(setupLayoutManager());
        rentListBinding.recyclerView.addItemDecoration(new BetweenSpacesItemDecoration(0, 5));
    }

    public RecyclerView.LayoutManager setupLayoutManager() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        return mLayoutManager;
    }

    private ViewBinder<?> getRentListItem(int layout) {
        return new ViewBinder<>(
                layout,
                RentListItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_name, (ViewProvider<TextView>) view -> view.setText(model.getmName()))
                        .find(R.id.tv_address, (ViewProvider<TextView>) view -> view.setText(model.getmAddress()))
                        .find(R.id.sr_scale_rating, (ViewProvider<BaseRatingBar>) view -> {
                            view.setRating(model.getRating());
                            view.setEnabled(false);
                            view.setClickable(false);
                            view.setClearRatingEnabled(false);
                        })
                        .find(R.id.iv_rent, (ViewProvider<ImageView>) view ->
                                GlideApp.with(getView()).load(model.getImageByte()).placeholder(R.drawable.placeholder).into(view))
                        .find(R.id.tv_price, (ViewProvider<TextView>) view -> view.setText(String.valueOf(String.valueOf(model.getmPrice()))))
                        .setOnClickListener(R.id.cv_content_rent_item, (v ->
                        {
                            Toast.makeText(getActivity(), "Click", Toast.LENGTH_SHORT).show();
                            mListener.onItemClick(v, model);
                        }))
        );
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


}
