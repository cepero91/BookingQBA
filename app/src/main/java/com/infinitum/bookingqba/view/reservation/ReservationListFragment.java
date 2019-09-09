package com.infinitum.bookingqba.view.reservation;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
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

import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;

public class ReservationListFragment extends BaseNavigationFragment implements
        SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private FragmentReservationListBinding reservationListBinding;
    private UserViewModel userViewModel;
    private Disposable disposable;
    private ReservationType reservationType;

    @Inject
    NetworkHelper networkHelper;

    @Inject
    SharedPreferences sharedPreferences;

    public static ReservationListFragment newInstance() {
        return new ReservationListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        reservationListBinding.tvBtnPending.setOnClickListener(this);
        reservationListBinding.tvBtnAccepted.setOnClickListener(this);

        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);

        setupBtnBarReservationType(ReservationType.PENDING);

        loadPendingReservation();

    }

    private void setupBtnBarReservationType(ReservationType rsType) {
        reservationType = rsType;
        switch (reservationType){
            case PENDING:
                reservationListBinding.tvBtnPending.setBackgroundResource(R.drawable.shape_btn_color_primary);
                reservationListBinding.tvBtnPending.setTextColor(getResources().getColor(R.color.White_100));
                reservationListBinding.tvBtnAccepted.setBackgroundResource(R.drawable.shape_white_round_10dp);
                reservationListBinding.tvBtnAccepted.setTextColor(getResources().getColor(R.color.material_color_blue_grey_500));
                break;
            case ACCEPTED:
                reservationListBinding.tvBtnAccepted.setBackgroundResource(R.drawable.shape_btn_color_primary);
                reservationListBinding.tvBtnAccepted.setTextColor(getResources().getColor(R.color.White_100));
                reservationListBinding.tvBtnPending.setBackgroundResource(R.drawable.shape_white_round_10dp);
                reservationListBinding.tvBtnPending.setTextColor(getResources().getColor(R.color.material_color_blue_grey_500));
                break;
        }

    }

    private void loadPendingReservation() {
        if (networkHelper.isNetworkAvailable()) {
            String token = sharedPreferences.getString(USER_TOKEN, "");
            String userId = sharedPreferences.getString(USER_ID, "");
            disposable = userViewModel.getPendingReservationByUser(token, userId)
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
        } else {
            initLoading(false, StateView.Status.NO_CONNECTION, true);
        }
    }

    private void loadAcceptedReservation() {
        if (networkHelper.isNetworkAvailable()) {
            String token = sharedPreferences.getString(USER_TOKEN, "");
            String userId = sharedPreferences.getString(USER_ID, "");
            disposable = userViewModel.getAcceptedReservationByUser(token, userId)
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
        } else {
            initLoading(false, StateView.Status.NO_CONNECTION, true);
        }
    }

    private void initLoading(boolean loading, StateView.Status status, boolean isEmpty) {
        reservationListBinding.setLoading(loading);
        reservationListBinding.setEmpty(isEmpty);
        reservationListBinding.swipeRefresh.setRefreshing(loading);
        reservationListBinding.stateView.setStatus(status);
    }


    private void setupReservationAdapter(List<ReservationItem> data) {
        RentBookAdapter rentBookAdapter = new RentBookAdapter(getLayoutInflater(), data, (RentBookAdapter.RentBookInteraction) getActivity());
        reservationListBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reservationListBinding.recyclerView.setAdapter(rentBookAdapter);
    }

    @Override
    public void onDestroyView() {
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        switch (reservationType) {
            case PENDING:
                setupBtnBarReservationType(ReservationType.PENDING);
                reservationListBinding.swipeRefresh.setRefreshing(true);
                loadPendingReservation();
                break;
            case ACCEPTED:
                setupBtnBarReservationType(ReservationType.ACCEPTED);
                reservationListBinding.swipeRefresh.setRefreshing(true);
                loadAcceptedReservation();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_pending:
                reservationType = ReservationType.PENDING;
                reservationListBinding.swipeRefresh.setRefreshing(true);
                loadPendingReservation();
                break;
            case R.id.tv_btn_accepted:
                reservationType = ReservationType.ACCEPTED;
                reservationListBinding.swipeRefresh.setRefreshing(true);
                loadAcceptedReservation();
                break;
        }
    }

    private enum ReservationType {
        PENDING, ACCEPTED
    }
}
