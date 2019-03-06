package com.infinitum.bookingqba.view.tutorial;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.model.local.entity.ProvinceEntity;
import com.infinitum.bookingqba.viewmodel.SyncViewModel;
import com.moos.library.CircleProgressView;

import java.util.List;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PageFourFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageFourFragment extends BasePageFragment implements CircleProgressView.CircleProgressUpdateListener{

    private CircleProgressView circleProgressView;

    private SyncViewModel syncViewModel;
    private Disposable disposable;


    public PageFourFragment() {
        // Required empty public constructor
    }

    public static PageFourFragment newInstance() {
        PageFourFragment fragment = new PageFourFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_four, container, false);
        circleProgressView = view.findViewById(R.id.progressView_circle);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        syncViewModel = ViewModelProviders.of(this, viewModelFactory).get(SyncViewModel.class);
        circleProgressView.setGraduatedEnabled(true);
        circleProgressView.setEndProgress(100);
        compositeDisposable = new CompositeDisposable();
    }

    public void startDownload(){
        syncProvinces();
    }


    private void syncProvinces() {
        disposable = syncViewModel.provinceList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<ProvinceEntity>>() {
                    @Override
                    public void onSuccess(List<ProvinceEntity> provinceEntityList) {
                        circleProgressView.setProgress(5);
                        circleProgressView.startProgressAnimation();
                        Timber.e("Success");
                    }

                    @Override
                    public void onError(Throwable e) {
//                        Light.error(getView(), "Error", Snackbar.LENGTH_SHORT).show();
                    }
                });
        compositeDisposable.add(disposable);
    }


    @Override
    public void onCircleProgressStart(View view) {

    }

    @Override
    public void onCircleProgressUpdate(View view, float progress) {

    }

    @Override
    public void onCircleProgressFinished(View view) {

    }


}
