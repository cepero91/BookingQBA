package com.infinitum.bookingqba.view.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentMyRentsBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.view.adapters.MyRentAdapter;
import com.infinitum.bookingqba.view.adapters.items.addrent.MyRentItem;
import com.infinitum.bookingqba.view.customview.StateView;
import com.infinitum.bookingqba.viewmodel.RentFormViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.USER_HAS_ACTIVE_RENT;
import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;

public class MyRentsFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        MyRentAdapter.MyRentListInteraction {

    private FragmentMyRentsBinding binding;
    private AddRentClick addRentClick;
    private CompositeDisposable compositeDisposable;
    private Disposable disposable;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    NetworkHelper networkHelper;


    private RentFormViewModel rentViewModel;
    private String token;
    private String userid;


    public MyRentsFragment() {
    }

    public static MyRentsFragment newInstance() {
        return new MyRentsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_rents, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.fabAddRent.setOnClickListener(this);
        binding.srlRefresh.setOnRefreshListener(this);
        binding.srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.material_color_lime_A700);

        token = sharedPreferences.getString(USER_TOKEN, "");
        userid = sharedPreferences.getString(USER_ID, "");

        initLoading(true, StateView.Status.LOADING, true);

        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentFormViewModel.class);

        rentsByUserId();
    }

    private void initLoading(boolean loading, StateView.Status status, boolean isEmpty) {
        binding.setIsLoading(loading);
        binding.setIsEmpty(isEmpty);
        binding.srlRefresh.setRefreshing(loading);
        binding.stateView.setStatus(status);
    }

    private void rentsByUserId() {
        if (networkHelper.isNetworkAvailable()) {
            disposable = rentViewModel.fetchRentByUserId(token, userid)
                    .map(this::checkActiveRents)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(listResource -> {
                        if (listResource.second.data != null && listResource.second.data.size() > 0) {
                            initLoading(false, StateView.Status.SUCCESS, false);
                            setItemsAdapter(listResource.second.data);
                            updateMenuIfNeeded(listResource.first);
                        } else {
                            initLoading(false, StateView.Status.EMPTY, true);
                        }
                    }, throwable -> {
                        Timber.e(throwable);
                        initLoading(false, StateView.Status.EMPTY, true);
                    });
            compositeDisposable.add(disposable);
        } else {
            initLoading(false, StateView.Status.NO_CONNECTION, true);
        }
    }

    private void updateMenuIfNeeded(Boolean first) {
        sharedPreferences.edit().putBoolean(USER_HAS_ACTIVE_RENT, first).apply();
        addRentClick.refreshMenuIfUserIsActiveHost(first);
    }

    private Pair<Boolean, Resource<List<MyRentItem>>> checkActiveRents(Resource<List<MyRentItem>> listResource) {
        if (listResource.data != null && listResource.data.size() > 0) {
            for (MyRentItem myRentItem : listResource.data) {
                if (myRentItem.isActive()) {
                    return new Pair<>(true, listResource);
                }
            }
        }
        return new Pair<>(false, listResource);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        if (context instanceof AddRentClick) {
            this.addRentClick = (AddRentClick) context;
            compositeDisposable = new CompositeDisposable();
        }
    }

    @Override
    public void onDetach() {
        this.addRentClick = null;
        compositeDisposable.clear();
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_rent:
                addRentClick.onAddRentClick();
                break;
        }
    }

    @Override
    public void onRefresh() {
        rentsByUserId();
    }

    @Override
    public void onEditClick(String uuid) {
        addRentClick.onEditRent(uuid);
    }

    public void setItemsAdapter(List<MyRentItem> rendererViewModelList) {
        MyRentAdapter recyclerViewAdapter = new MyRentAdapter(rendererViewModelList, getLayoutInflater(), this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(recyclerViewAdapter);
    }

    public interface AddRentClick {
        void onAddRentClick();

        void refreshMenuIfUserIsActiveHost(boolean userHasRentActive);

        void onEditRent(String uuid);
    }
}
