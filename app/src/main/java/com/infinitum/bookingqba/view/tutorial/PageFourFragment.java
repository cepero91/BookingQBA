package com.infinitum.bookingqba.view.tutorial;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentPageFourBinding;
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

    private SyncViewModel syncViewModel;
    private Disposable disposable;

    private FragmentPageFourBinding pageFourBinding;


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
        pageFourBinding.progressViewCircle.setGraduatedEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pageFourBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_page_four, container, false);
        return pageFourBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        syncViewModel = ViewModelProviders.of(this, viewModelFactory).get(SyncViewModel.class);

        pageFourBinding.progressViewCircle.setProgressViewUpdateListener(this);
        pageFourBinding.progressViewCircle.setEndProgress(100);
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
                        pageFourBinding.progressViewCircle.setProgress(5);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mListener.onDownloadError(e.getMessage());
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
