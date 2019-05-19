package com.infinitum.bookingqba.view.info;


import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentInfoBinding;
import com.infinitum.bookingqba.view.base.BaseNavigationFragment;
import com.infinitum.bookingqba.viewmodel.InformationViewModel;
import com.infinitum.bookingqba.viewmodel.RentViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class InfoFragment extends BaseNavigationFragment {

    private FragmentInfoBinding infoBinding;
    private Disposable disposable;

    @Inject
    SharedPreferences sharedPreferences;

    private InformationViewModel informationViewModel;


    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment newInstance() {
        InfoFragment fragment = new InfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        infoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_info, container, false);
        return infoBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        informationViewModel = ViewModelProviders.of(this, viewModelFactory).get(InformationViewModel.class);

        loadData();

    }

    private void loadData() {
        disposable = Flowable.combineLatest(informationViewModel.lastLocalDatabaseUpdate(),
                informationViewModel.lastRemoteDatabaseUpdate(), (t1, t2) -> {
                    List<String>result = new ArrayList<>();
                    result.add(t1);
                    result.add(t2);
                    return result;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateUi, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void updateUi(List<String> strings) {
        infoBinding.setLocalUpdate(strings.get(0));
        infoBinding.setRemoteAvailable(strings.get(1));
    }
}
