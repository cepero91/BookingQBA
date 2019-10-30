package com.infinitum.bookingqba.view.reservation;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentReservationListBinding;
import com.infinitum.bookingqba.model.remote.ReservationType;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.view.adapters.RentBookAdapter;
import com.infinitum.bookingqba.view.adapters.items.reservation.ReservationItem;
import com.infinitum.bookingqba.view.base.BaseNavigationFragment;
import com.infinitum.bookingqba.view.customview.StateView;
import com.infinitum.bookingqba.viewmodel.UserViewModel;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.DONT_OPEN_SWIPE_TO_DELETE_DIALOG;
import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;

public class ReservationListFragment extends BaseNavigationFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener,
        RentBookAdapter.RentBookInteraction {

    private FragmentReservationListBinding reservationListBinding;
    private UserViewModel userViewModel;
    private Disposable disposable;
    private ReservationType reservationType;

    @Inject
    NetworkHelper networkHelper;

    @Inject
    SharedPreferences sharedPreferences;

    private String token;
    private String userId;

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
        token = sharedPreferences.getString(USER_TOKEN, "");
        userId = sharedPreferences.getString(USER_ID, "");

        initLoading(true, StateView.Status.LOADING, true);
        reservationListBinding.swipeRefresh.setOnRefreshListener(this);
        PushDownAnim.setPushDownAnimTo(reservationListBinding.tvBtnPending).setOnClickListener(this);
        PushDownAnim.setPushDownAnimTo(reservationListBinding.tvBtnChecked).setOnClickListener(this);
        PushDownAnim.setPushDownAnimTo(reservationListBinding.tvBtnAccepted).setOnClickListener(this);
        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);

        setupBtnBarReservationType(ReservationType.PENDING);

        loadReservation(ReservationType.PENDING);

        showSwipeDialog();

    }

    private void setupBtnBarReservationType(ReservationType rsType) {
        reservationType = rsType;
        switch (reservationType) {
            case PENDING:
                reservationListBinding.tvBtnPending.setBackgroundResource(R.drawable.shape_btn_color_primary);
                reservationListBinding.tvBtnPending.setTextColor(getResources().getColor(R.color.White_100));
                reservationListBinding.tvBtnChecked.setBackgroundResource(R.drawable.shape_white_round_10dp);
                reservationListBinding.tvBtnChecked.setTextColor(getResources().getColor(R.color.material_color_blue_grey_500));
                reservationListBinding.tvBtnAccepted.setBackgroundResource(R.drawable.shape_white_round_10dp);
                reservationListBinding.tvBtnAccepted.setTextColor(getResources().getColor(R.color.material_color_blue_grey_500));
                break;
            case CHECKED:
                reservationListBinding.tvBtnChecked.setBackgroundResource(R.drawable.shape_btn_color_primary);
                reservationListBinding.tvBtnChecked.setTextColor(getResources().getColor(R.color.White_100));
                reservationListBinding.tvBtnPending.setBackgroundResource(R.drawable.shape_white_round_10dp);
                reservationListBinding.tvBtnPending.setTextColor(getResources().getColor(R.color.material_color_blue_grey_500));
                reservationListBinding.tvBtnAccepted.setBackgroundResource(R.drawable.shape_white_round_10dp);
                reservationListBinding.tvBtnAccepted.setTextColor(getResources().getColor(R.color.material_color_blue_grey_500));
                break;
            case ACCEPTED:
                reservationListBinding.tvBtnAccepted.setBackgroundResource(R.drawable.shape_btn_color_primary);
                reservationListBinding.tvBtnAccepted.setTextColor(getResources().getColor(R.color.White_100));
                reservationListBinding.tvBtnPending.setBackgroundResource(R.drawable.shape_white_round_10dp);
                reservationListBinding.tvBtnPending.setTextColor(getResources().getColor(R.color.material_color_blue_grey_500));
                reservationListBinding.tvBtnChecked.setBackgroundResource(R.drawable.shape_white_round_10dp);
                reservationListBinding.tvBtnChecked.setTextColor(getResources().getColor(R.color.material_color_blue_grey_500));
                break;
        }

    }

    private void loadReservation(ReservationType type) {
        if (networkHelper.isNetworkAvailable()) {
            disposable = userViewModel.getReservationUserByType(token, userId, type)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(listResource -> {
                        if (listResource.data != null && listResource.data.size() > 0) {
                            initLoading(false, StateView.Status.SUCCESS, false);
                            setupReservationAdapter(listResource.data, type);
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


    private void setupReservationAdapter(List<ReservationItem> data, ReservationType type) {
        RentBookAdapter rentBookAdapter = new RentBookAdapter(getLayoutInflater(), data, this, type);
        reservationListBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reservationListBinding.recyclerView.setAdapter(rentBookAdapter);
    }

    private void showSwipeDialog() {
        if (!sharedPreferences.getBoolean(DONT_OPEN_SWIPE_TO_DELETE_DIALOG, false)) {
            View view = getLayoutInflater().inflate(R.layout.dialog_swipe_delete, null);
            ((AppCompatCheckBox) view.findViewById(R.id.cb_dont_show_again)).setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    sharedPreferences.edit().putBoolean(DONT_OPEN_SWIPE_TO_DELETE_DIALOG, true).apply();
                }
            });
            AlertUtils.showCFDialogWithCustomViewAndAction(getActivity(), view, "Ok, lo entiendo", "#00BFA5",
                    CFAlertDialog.CFAlertActionStyle.POSITIVE, (dialog, which) -> {
                        dialog.dismiss();
                    });
        }
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
                loadReservation(ReservationType.PENDING);
                break;
            case CHECKED:
                setupBtnBarReservationType(ReservationType.CHECKED);
                reservationListBinding.swipeRefresh.setRefreshing(true);
                loadReservation(ReservationType.CHECKED);
                break;
            case ACCEPTED:
                setupBtnBarReservationType(ReservationType.ACCEPTED);
                reservationListBinding.swipeRefresh.setRefreshing(true);
                loadReservation(ReservationType.ACCEPTED);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_pending:
                reservationType = ReservationType.PENDING;
                setupBtnBarReservationType(reservationType);
                reservationListBinding.swipeRefresh.setRefreshing(true);
                loadReservation(ReservationType.PENDING);
                break;
            case R.id.tv_btn_checked:
                reservationType = ReservationType.CHECKED;
                setupBtnBarReservationType(reservationType);
                reservationListBinding.swipeRefresh.setRefreshing(true);
                loadReservation(ReservationType.CHECKED);
                break;
            case R.id.tv_btn_accepted:
                reservationType = ReservationType.ACCEPTED;
                setupBtnBarReservationType(reservationType);
                reservationListBinding.swipeRefresh.setRefreshing(true);
                loadReservation(ReservationType.ACCEPTED);
                break;
        }
    }

    @Override
    public void onBookReservationClick(ReservationItem item, ReservationType reservationType) {
        Toast.makeText(getActivity(), "Aqui se va a detalle", Toast.LENGTH_SHORT).show();
        mListener.onBookItemClick(item, reservationType);
    }

    @Override
    public void onBookReservationDelete(ReservationItem item) {
        Toast.makeText(getActivity(), "Aqui se elimina", Toast.LENGTH_SHORT).show();
        disposable = userViewModel.deniedReservation(token, item.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultResource -> {
                    if (resultResource.data != null && resultResource.data.getCode() == 200) {
                        AlertUtils.showErrorToast(getActivity(), "Solicitud cancelada");
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }
}
