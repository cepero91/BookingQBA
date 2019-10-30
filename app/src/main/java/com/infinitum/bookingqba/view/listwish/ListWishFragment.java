package com.infinitum.bookingqba.view.listwish;


import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentListWishBinding;
import com.infinitum.bookingqba.view.adapters.WishRentAdapter;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;
import com.infinitum.bookingqba.view.adapters.items.listwish.ListWishItem;
import com.infinitum.bookingqba.view.base.BaseNavigationFragment;
import com.infinitum.bookingqba.view.customview.StateView;
import com.infinitum.bookingqba.view.interaction.FragmentNavInteraction;
import com.infinitum.bookingqba.view.widgets.BetweenSpacesItemDecoration;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.BaseRatingBar;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.PROVINCE_UUID;
import static com.infinitum.bookingqba.util.Constants.PROVINCE_UUID_DEFAULT;
import static com.infinitum.bookingqba.util.Constants.THUMB_HEIGHT;
import static com.infinitum.bookingqba.util.Constants.THUMB_WIDTH;
import static com.infinitum.bookingqba.util.Constants.USER_ID;


public class ListWishFragment extends BaseNavigationFragment {

    private FragmentListWishBinding wishBinding;
    private Disposable disposable;

    private RentViewModel rentViewModel;

    @Inject
    SharedPreferences sharedPreferences;
    private String province;
    private String userId;

    public ListWishFragment() {
        // Required empty public constructor
    }

    public static ListWishFragment newInstance() {
        return new ListWishFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        province = sharedPreferences.getString(PROVINCE_UUID, PROVINCE_UUID_DEFAULT);
        userId = sharedPreferences.getString(USER_ID, "");
        initLoading(true, StateView.Status.LOADING, true);

        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        setupRecyclerView();

        loadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        wishBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_wish, container, false);
        return wishBinding.getRoot();
    }

    private void initLoading(boolean loading, StateView.Status status, boolean isEmpty) {
        wishBinding.setIsLoading(loading);
        wishBinding.setIsEmpty(isEmpty);
        wishBinding.stateView.setStatus(status);
    }

    public void setupRecyclerView() {
        wishBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        wishBinding.recyclerView.addItemDecoration(new BetweenSpacesItemDecoration(2, 0));
    }

    public void loadData() {
        disposable = rentViewModel.getRentListWish(province, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> setItemsAdapter(listResource.data), throwable -> {
                    Timber.e(throwable);
                    initLoading(false, StateView.Status.EMPTY, true);
                });
        compositeDisposable.add(disposable);
    }


    public void setItemsAdapter(List<BaseItem> rendererViewModelList) {
        if (rendererViewModelList.size() > 0) {
            WishRentAdapter recyclerViewAdapter = new WishRentAdapter(rendererViewModelList, getLayoutInflater(), (FragmentNavInteraction) getActivity());
            wishBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            wishBinding.recyclerView.setAdapter(recyclerViewAdapter);
            initLoading(false, StateView.Status.SUCCESS, false);
        } else {
            initLoading(false, StateView.Status.EMPTY, true);
        }
    }


}
