package com.infinitum.bookingqba.view.tutorial;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;


import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.model.local.entity.MunicipalityEntity;
import com.infinitum.bookingqba.model.local.entity.ProvinceEntity;
import com.infinitum.bookingqba.view.base.BasePageFragment;
import com.infinitum.bookingqba.viewmodel.SyncViewModel;
import com.moos.library.CircleProgressView;


import java.util.List;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PageFourFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageFourFragment extends BasePageFragment {

    private CircleProgressView circleProgressView;
    private AppCompatCheckBox mCheckBoxTourist,mCheckBoxCuban;
    private LinearLayout mContentCheckBox;

    private SyncViewModel syncViewModel;
    private Disposable disposable;


    private boolean isSync = false;


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
        circleProgressView.setEndProgress(20);
        circleProgressView.setGraduatedEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_four, container, false);
        circleProgressView = view.findViewById(R.id.progressView_circle);
        mCheckBoxTourist = view.findViewById(R.id.cb_tourist);
        mCheckBoxCuban = view.findViewById(R.id.cb_cuban);
        mContentCheckBox = view.findViewById(R.id.ll_checkbox_content);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        syncViewModel = ViewModelProviders.of(this, viewModelFactory).get(SyncViewModel.class);

    }

    public void startDownload() {
        if(!isSync && validateCheckBoxes()) {
            syncProvinces();
        }
    }

    private boolean validateCheckBoxes(){
        boolean isValid = true;
        if(!mCheckBoxTourist.isChecked() && !mCheckBoxCuban.isChecked()){
            isValid = false;
        }
        if(!isValid){
            Animation animationText = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
            animationText.setFillAfter(true);
            mContentCheckBox.startAnimation(animationText);
        }
        return isValid;
    }


    private void syncProvinces() {
        isSync = true;
        disposable = syncViewModel.provinceList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<ProvinceEntity>>() {
                    @Override
                    public void onSuccess(List<ProvinceEntity> provinceEntityList) {
                        circleProgressView.setProgress(5);
                        addProvinces(provinceEntityList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void addProvinces(List<ProvinceEntity> provinceEntityList) {
        disposable = syncViewModel.insertProvinces(provinceEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        circleProgressView.setProgress(10);
                        syncMunicipalities();
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncMunicipalities() {
        disposable = syncViewModel.municipalityList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<MunicipalityEntity>>() {
                    @Override
                    public void onSuccess(List<MunicipalityEntity> municipalityEntityList) {
                        circleProgressView.setProgress(15);
                        addMunicipalities(municipalityEntityList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void addMunicipalities(List<MunicipalityEntity> municipalityEntityList) {
        disposable = syncViewModel.insertMunicipality(municipalityEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        circleProgressView.setProgress(20);
                        isSync = false;
                        mListener.onDownloadSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

}
