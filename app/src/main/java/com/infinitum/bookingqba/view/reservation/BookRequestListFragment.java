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
import android.widget.Toast;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentRequestBookListBinding;
import com.infinitum.bookingqba.model.remote.pojo.BookRequestInfo;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.view.adapters.RentBookAdapter;
import com.infinitum.bookingqba.view.adapters.UserBookRequestInfoAdapter;
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

public class BookRequestListFragment extends BaseNavigationFragment implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, UserBookRequestInfoAdapter.UserBookRequestInteraction {

    private FragmentRequestBookListBinding fragmentRequestBookListBinding;
    private UserViewModel userViewModel;
    private Disposable disposable;

    @Inject
    NetworkHelper networkHelper;

    @Inject
    SharedPreferences sharedPreferences;

    public static BookRequestListFragment newInstance() {
        return new BookRequestListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRequestBookListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_request_book_list, container, false);
        return fragmentRequestBookListBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initLoading(true, StateView.Status.LOADING, true);
        fragmentRequestBookListBinding.swipeRefresh.setOnRefreshListener(this);

        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);

        fetchBookRequetByUser();

    }

    private void fetchBookRequetByUser() {
        if (networkHelper.isNetworkAvailable()) {
            String token = sharedPreferences.getString(USER_TOKEN, "");
            String userId = sharedPreferences.getString(USER_ID, "");
            disposable = userViewModel.getUserBookRequestInfo(token, userId)
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
        fragmentRequestBookListBinding.setLoading(loading);
        fragmentRequestBookListBinding.setEmpty(isEmpty);
        fragmentRequestBookListBinding.swipeRefresh.setRefreshing(loading);
        fragmentRequestBookListBinding.stateView.setStatus(status);
    }


    private void setupReservationAdapter(List<BookRequestInfo> data) {
        UserBookRequestInfoAdapter infoAdapter = new UserBookRequestInfoAdapter(getLayoutInflater(), data, this);
        fragmentRequestBookListBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fragmentRequestBookListBinding.recyclerView.setAdapter(infoAdapter);
    }

    @Override
    public void onDestroyView() {
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        fetchBookRequetByUser();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }


    @Override
    public void onBookReservationClick(BookRequestInfo item) {
        disposable = userViewModel.deniedReservationByUser(sharedPreferences.getString(USER_TOKEN, ""), item.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultResource -> {
                    if (resultResource.data != null && resultResource.data.getCode() == 200){
                        Toast.makeText(getActivity(), "Si", Toast.LENGTH_SHORT).show();
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }
}
