package com.infinitum.bookingqba.view.tutorial;


import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
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
import com.infinitum.bookingqba.databinding.FragmentPageFourBinding;
import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.DrawTypeEntity;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.entity.MunicipalityEntity;
import com.infinitum.bookingqba.model.local.entity.PoiEntity;
import com.infinitum.bookingqba.model.local.entity.PoiTypeEntity;
import com.infinitum.bookingqba.model.local.entity.ProvinceEntity;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.view.base.BasePageFragment;
import com.infinitum.bookingqba.viewmodel.SyncViewModel;
import com.moos.library.CircleProgressView;


import java.util.List;


import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.infinitum.bookingqba.util.Constants.PREF_DOWNLOAD_LEVEL;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PageFourFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageFourFragment extends BasePageFragment {

    private FragmentPageFourBinding fourBinding;

    private SyncViewModel syncViewModel;
    private Disposable disposable;

    @Inject
    SharedPreferences sharedPreferences;

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
        fourBinding.progressViewCircle.setEndProgress(100);
        fourBinding.progressViewCircle.setGraduatedEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fourBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_page_four, container, false);
        return fourBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        syncViewModel = ViewModelProviders.of(this, viewModelFactory).get(SyncViewModel.class);

    }

    public void startDownload() {
        if (!isSync && validateCheckBoxes()) {
            int downloadLevel = sharedPreferences.getInt(PREF_DOWNLOAD_LEVEL, 0);
            startSync(downloadLevel);
        }
    }

    private void startSync(int downloadLevel) {
        switch (downloadLevel) {
            case 0:
                syncReferencesZone();
                break;
            case 1:
                syncProvinces();
                break;
            case 2:
                syncMunicipalities();
                break;
            case 3:
                syncAmenities();
                break;
            case 4:
                syncPoiTypes();
                break;
            case 5:
                syncPois();
                break;
            case 6:
                syncRentsMode();
                break;
            case 7:
                syncDrawsType();
                break;
            case 8:
                syncRents();
                break;
            case 9:
                syncGaleries();
                break;
        }
    }


    private boolean validateCheckBoxes() {
        boolean isValid = true;
        if (!fourBinding.cbTourist.isChecked() && !fourBinding.cbCuban.isChecked()) {
            isValid = false;
        }
        if (!isValid) {
            Animation animationText = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
            animationText.setFillAfter(true);
            fourBinding.llCheckboxContent.startAnimation(animationText);
        }
        return isValid;
    }


    private void syncReferencesZone() {
        disposable = syncViewModel.referenceZoneList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<ReferenceZoneEntity>>() {
                    @Override
                    public void onSuccess(List<ReferenceZoneEntity> referenceZoneEntities) {
                        fourBinding.progressViewCircle.setProgress(5);
                        saveReferencesZone(referenceZoneEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void saveReferencesZone(List<ReferenceZoneEntity> referenceZoneEntityList) {
        disposable = syncViewModel.insertReferenceZone(referenceZoneEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        fourBinding.progressViewCircle.setProgress(10);
                        saveDownloadLevel(1);
                        syncProvinces();
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncProvinces() {
        disposable = syncViewModel.provinceList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<ProvinceEntity>>() {
                    @Override
                    public void onSuccess(List<ProvinceEntity> provinceEntityList) {
                        fourBinding.progressViewCircle.setProgress(15);
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
                        fourBinding.progressViewCircle.setProgress(20);
                        saveDownloadLevel(2);
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
                        fourBinding.progressViewCircle.setProgress(25);
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
                        fourBinding.progressViewCircle.setProgress(30);
                        saveDownloadLevel(3);
                        syncAmenities();
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncAmenities() {
        disposable = syncViewModel.amenitiesList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<AmenitiesEntity>>() {
                    @Override
                    public void onSuccess(List<AmenitiesEntity> amenitiesEntities) {
                        fourBinding.progressViewCircle.setProgress(35);
                        saveAmenities(amenitiesEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);

    }

    private void saveAmenities(List<AmenitiesEntity> amenitiesEntityList) {
        disposable = syncViewModel.insertAmenities(amenitiesEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        fourBinding.progressViewCircle.setProgress(40);
                        saveDownloadLevel(4);
                        syncPoiTypes();
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncPoiTypes() {
        disposable = syncViewModel.poiTypeList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<PoiTypeEntity>>() {
                    @Override
                    public void onSuccess(List<PoiTypeEntity> poiTypeEntities) {
                        fourBinding.progressViewCircle.setProgress(45);
                        savePoiTypes(poiTypeEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);

    }

    private void savePoiTypes(List<PoiTypeEntity> poiTypeEntityList) {
        disposable = syncViewModel.insertPoiType(poiTypeEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        fourBinding.progressViewCircle.setProgress(50);
                        saveDownloadLevel(5);
                        syncPois();
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncPois() {
        disposable = syncViewModel.poiList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<PoiEntity>>() {
                    @Override
                    public void onSuccess(List<PoiEntity> poiEntities) {
                        fourBinding.progressViewCircle.setProgress(55);
                        savePois(poiEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);

    }

    private void savePois(List<PoiEntity> poiEntityList) {
        disposable = syncViewModel.insertPoi(poiEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        fourBinding.progressViewCircle.setProgress(60);
                        saveDownloadLevel(6);
                        syncRentsMode();
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncRentsMode() {
        disposable = syncViewModel.rentModeList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<RentModeEntity>>() {
                    @Override
                    public void onSuccess(List<RentModeEntity> rentModeEntities) {
                        fourBinding.progressViewCircle.setProgress(65);
                        saveRentsMode(rentModeEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);

    }

    private void saveRentsMode(List<RentModeEntity> rentModeEntityList) {
        disposable = syncViewModel.insertRentMode(rentModeEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        fourBinding.progressViewCircle.setProgress(70);
                        saveDownloadLevel(7);
                        syncRents();
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncRents() {
        disposable = syncViewModel.rentList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<RentEntity>>() {
                    @Override
                    public void onSuccess(List<RentEntity> rentEntities) {
                        fourBinding.progressViewCircle.setProgress(75);
                        saveRents(rentEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void saveRents(List<RentEntity> rentEntityList) {
        disposable = syncViewModel.insertRent(rentEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        fourBinding.progressViewCircle.setProgress(80);
                        saveDownloadLevel(8);
                        syncDrawsType();
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncDrawsType() {
        disposable = syncViewModel.drawTypeList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<DrawTypeEntity>>() {
                    @Override
                    public void onSuccess(List<DrawTypeEntity> drawTypeEntities) {
                        fourBinding.progressViewCircle.setProgress(85);
                        saveDrawsType(drawTypeEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);

    }

    private void saveDrawsType(List<DrawTypeEntity> drawTypeEntitiesList) {
        disposable = syncViewModel.insertDrawType(drawTypeEntitiesList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        fourBinding.progressViewCircle.setProgress(90);
                        saveDownloadLevel(9);
                        syncGaleries();
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncGaleries() {
        disposable = syncViewModel.galerieList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<GalerieEntity>>() {
                    @Override
                    public void onSuccess(List<GalerieEntity> galerieEntities) {
                        fourBinding.progressViewCircle.setProgress(95);
                        saveGaleries(galerieEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSync = false;
                        mListener.onDownloadError(e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void saveGaleries(List<GalerieEntity> galerieEntityList) {
        disposable = syncViewModel.insertGalerie(galerieEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        isSync = false;
                        fourBinding.progressViewCircle.setProgress(100);
                        saveDownloadLevel(10);
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

    private void saveDownloadLevel(int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_DOWNLOAD_LEVEL, value);
        editor.apply();
    }


}
