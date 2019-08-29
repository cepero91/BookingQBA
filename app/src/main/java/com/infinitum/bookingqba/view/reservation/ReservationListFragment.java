package com.infinitum.bookingqba.view.reservation;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentReservationListBinding;
import com.infinitum.bookingqba.model.remote.pojo.Reservation;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.view.adapters.RentBookAdapter;
import com.infinitum.bookingqba.view.adapters.items.reservation.ReservationItem;
import com.infinitum.bookingqba.view.base.BaseNavigationFragment;
import com.infinitum.bookingqba.view.customview.StateView;
import com.infinitum.bookingqba.viewmodel.UserViewModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ReservationListFragment extends BaseNavigationFragment implements RentBookAdapter.RentBookInteraction,
        SwipeRefreshLayout.OnRefreshListener {

    private FragmentReservationListBinding reservationListBinding;
    private String token;
    private String userId;
    private static final String USER_ID = "userid";
    private static final String TOKEN = "token";
    private UserViewModel userViewModel;
    private Disposable disposable;

    @Inject
    NetworkHelper networkHelper;

    public static ReservationListFragment newInstance(String token, String userId) {
        ReservationListFragment fragment = new ReservationListFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID, userId);
        args.putString(TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            token = getArguments().getString(TOKEN);
            userId = getArguments().getString(USER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        reservationListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reservation_list, container, false);
        return reservationListBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initLoading(true, StateView.Status.LOADING, true);
        reservationListBinding.swipeRefresh.setOnRefreshListener(this);

        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);

        loadReservationData();

    }

    private void loadReservationData() {
//        if (networkHelper.isNetworkAvailable()) {
        disposable = userViewModel.getReservationByUserId(token, userId, Reservation.ReservationStatus.PENDING)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> {
                    if (listResource.data != null && listResource.data.size() > 0) {
                        initLoading(false, StateView.Status.SUCCESS, false);
                        setupReservationAdapter(listResource.data);
                    } else {
                        initLoading(false, StateView.Status.EMPTY, true);
                    }
                }, throwable -> {
                    initLoading(false, StateView.Status.EMPTY, true);
                    Timber.e(throwable);
                });
        compositeDisposable.add(disposable);
//        } else {
//        stopLoading(StateView.Status.NO_CONNECTION);
//        }
    }

    private void initLoading(boolean loading, StateView.Status status, boolean isEmpty) {
        reservationListBinding.setLoading(loading);
        reservationListBinding.setEmpty(isEmpty);
        reservationListBinding.swipeRefresh.setRefreshing(loading);
        reservationListBinding.stateView.setStatus(status);
    }


    private void setupReservationAdapter(List<ReservationItem> data) {
        RentBookAdapter rentBookAdapter = new RentBookAdapter(getLayoutInflater(), data, this);
        reservationListBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reservationListBinding.recyclerView.setAdapter(rentBookAdapter);
    }

    @Override
    public void onBookReservationClick(ReservationItem item) {

    }

    @Override
    public void onDestroyView() {
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        reservationListBinding.swipeRefresh.setRefreshing(true);
        loadReservationData();
    }
}
