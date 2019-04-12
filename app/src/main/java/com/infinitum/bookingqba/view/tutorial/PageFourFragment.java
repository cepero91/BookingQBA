//package com.infinitum.bookingqba.view.tutorial;
//
//
//import android.arch.lifecycle.ViewModelProviders;
//import android.content.SharedPreferences;
//import android.databinding.DataBindingUtil;
//import android.os.Bundle;
//import android.os.SystemClock;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//
//
//import com.infinitum.bookingqba.R;
//import com.infinitum.bookingqba.databinding.FragmentPageFourBinding;
//import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
//import com.infinitum.bookingqba.model.local.entity.DrawTypeEntity;
//import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
//import com.infinitum.bookingqba.model.local.entity.MunicipalityEntity;
//import com.infinitum.bookingqba.model.local.entity.PoiEntity;
//import com.infinitum.bookingqba.model.local.entity.PoiTypeEntity;
//import com.infinitum.bookingqba.model.local.entity.ProvinceEntity;
//import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
//import com.infinitum.bookingqba.model.local.entity.RentAmenitiesEntity;
//import com.infinitum.bookingqba.model.local.entity.RentEntity;
//import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
//import com.infinitum.bookingqba.model.local.entity.RentPoiEntity;
//import com.infinitum.bookingqba.util.FileUtils;
//import com.infinitum.bookingqba.util.GlideApp;
//import com.infinitum.bookingqba.view.base.BasePageFragment;
//import com.infinitum.bookingqba.viewmodel.SyncViewModel;
//
//
//import org.reactivestreams.Publisher;
//
//import java.io.File;
//import java.net.ConnectException;
//import java.util.List;
//
//
//import javax.inject.Inject;
//
//import io.reactivex.Completable;
//import io.reactivex.Flowable;
//import io.reactivex.Single;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Consumer;
//import io.reactivex.functions.Function;
//import io.reactivex.observers.DisposableCompletableObserver;
//import io.reactivex.observers.DisposableSingleObserver;
//import io.reactivex.schedulers.Schedulers;
//import okhttp3.ResponseBody;
//import timber.log.Timber;
//
//import static com.infinitum.bookingqba.util.Constants.PREF_DOWNLOAD_LEVEL;
//
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link PageFourFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class PageFourFragment extends BasePageFragment {
//
//    private FragmentPageFourBinding fourBinding;
//
//    private SyncViewModel syncViewModel;
//    private Disposable disposable;
//
//    @Inject
//    SharedPreferences sharedPreferences;
//
//
//    private boolean isSync = false;
//    private int progressGalery = 0;
//    private int sizeGalerieList = 0;
//
//
//    public PageFourFragment() {
//        // Required empty public constructor
//    }
//
//    public static PageFourFragment newInstance() {
//        PageFourFragment fragment = new PageFourFragment();
//        return fragment;
//    }
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        GlideApp.with(this).load(R.drawable.download_use_case).into(fourBinding.ivUseCase);
//        fourBinding.setIsDownloading(false);
//        fourBinding.setDownloadImages(false);
//        fourBinding.pbEntities.setEndProgress(100);
//        fourBinding.pbGalery.setEndProgress(100);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        fourBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_page_four, container, false);
//        return fourBinding.getRoot();
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        syncViewModel = ViewModelProviders.of(this, viewModelFactory).get(SyncViewModel.class);
//
//    }
//
//    public void startDownload() {
////        if (!isSync) {
////            int downloadLevel = sharedPreferences.getInt(PREF_DOWNLOAD_LEVEL, 0);
////            startSync(downloadLevel);
////        }
//        fourBinding.setIsDownloading(true);
//        syncReferencesZone();
//    }
//
//    private void startSync(int downloadLevel) {
//        switch (downloadLevel) {
//            case 0:
//                syncReferencesZone();
//                break;
//            case 1:
//                syncProvinces();
//                break;
//            case 2:
//                syncMunicipalities();
//                break;
//            case 3:
//                syncAmenities();
//                break;
//            case 4:
//                syncPoiTypes();
//                break;
//            case 5:
//                syncPois();
//                break;
//            case 6:
//                syncRentsMode();
//                break;
//            case 7:
//                syncDrawsType();
//                break;
//            case 8:
//                syncRents();
//                break;
//            case 9:
//                syncRentAmenities();
//                break;
//            case 10:
//                syncRentPois();
//                break;
//            case 11:
//                syncGaleries();
//                break;
//        }
//    }
//
//
//    private boolean validateCheckBoxes() {
//        boolean isValid = true;
//        if (!fourBinding.cbImages.isChecked()) {
//            isValid = false;
//        }
//        if (!isValid) {
//            Animation animationText = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
//            animationText.setFillAfter(true);
//            fourBinding.llCheckboxContent.startAnimation(animationText);
//        }
//        return isValid;
//    }
//
//
//    private void syncReferencesZone() {
//        disposable = syncViewModel.referenceZoneList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableSingleObserver<List<ReferenceZoneEntity>>() {
//                    @Override
//                    public void onSuccess(List<ReferenceZoneEntity> referenceZoneEntities) {
//                        fourBinding.pbEntities.setProgress(5);
//                        saveReferencesZone(referenceZoneEntities);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//    }
//
//    private void saveReferencesZone(List<ReferenceZoneEntity> referenceZoneEntityList) {
//        disposable = syncViewModel.insertReferenceZone(referenceZoneEntityList)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableCompletableObserver() {
//                    @Override
//                    public void onComplete() {
//                        fourBinding.pbEntities.setProgress(10);
//                        saveDownloadLevel(1);
//                        syncProvinces();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//    }
//
//    private void syncProvinces() {
//        disposable = syncViewModel.provinceList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableSingleObserver<List<ProvinceEntity>>() {
//                    @Override
//                    public void onSuccess(List<ProvinceEntity> provinceEntityList) {
//                        fourBinding.pbEntities.setProgress(15);
//                        addProvinces(provinceEntityList);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//    }
//
//    private void addProvinces(List<ProvinceEntity> provinceEntityList) {
//        disposable = syncViewModel.insertProvinces(provinceEntityList)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableCompletableObserver() {
//                    @Override
//                    public void onComplete() {
//                        fourBinding.pbEntities.setProgress(20);
//                        saveDownloadLevel(2);
//                        syncMunicipalities();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//    }
//
//    private void syncMunicipalities() {
//        disposable = syncViewModel.municipalityList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableSingleObserver<List<MunicipalityEntity>>() {
//                    @Override
//                    public void onSuccess(List<MunicipalityEntity> municipalityEntityList) {
//                        fourBinding.pbEntities.setProgress(25);
//                        addMunicipalities(municipalityEntityList);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//    }
//
//    private void addMunicipalities(List<MunicipalityEntity> municipalityEntityList) {
//        disposable = syncViewModel.insertMunicipality(municipalityEntityList)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableCompletableObserver() {
//                    @Override
//                    public void onComplete() {
//                        fourBinding.pbEntities.setProgress(30);
//                        saveDownloadLevel(3);
//                        syncAmenities();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//    }
//
//    private void syncAmenities() {
//        disposable = syncViewModel.amenitiesList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableSingleObserver<List<AmenitiesEntity>>() {
//                    @Override
//                    public void onSuccess(List<AmenitiesEntity> amenitiesEntities) {
//                        fourBinding.pbEntities.setProgress(35);
//                        saveAmenities(amenitiesEntities);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//
//    }
//
//    private void saveAmenities(List<AmenitiesEntity> amenitiesEntityList) {
//        disposable = syncViewModel.insertAmenities(amenitiesEntityList)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableCompletableObserver() {
//                    @Override
//                    public void onComplete() {
//                        fourBinding.pbEntities.setProgress(40);
//                        saveDownloadLevel(4);
//                        syncPoiTypes();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//    }
//
//    private void syncPoiTypes() {
//        disposable = syncViewModel.poiTypeList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableSingleObserver<List<PoiTypeEntity>>() {
//                    @Override
//                    public void onSuccess(List<PoiTypeEntity> poiTypeEntities) {
//                        fourBinding.pbEntities.setProgress(45);
//                        savePoiTypes(poiTypeEntities);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//
//    }
//
//    private void savePoiTypes(List<PoiTypeEntity> poiTypeEntityList) {
//        disposable = syncViewModel.insertPoiType(poiTypeEntityList)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableCompletableObserver() {
//                    @Override
//                    public void onComplete() {
//                        fourBinding.pbEntities.setProgress(50);
//                        saveDownloadLevel(5);
//                        syncPois();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//    }
//
//    private void syncPois() {
//        disposable = syncViewModel.poiList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableSingleObserver<List<PoiEntity>>() {
//                    @Override
//                    public void onSuccess(List<PoiEntity> poiEntities) {
//                        fourBinding.pbEntities.setProgress(55);
//                        savePois(poiEntities);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//
//    }
//
//    private void savePois(List<PoiEntity> poiEntityList) {
//        disposable = syncViewModel.insertPoi(poiEntityList)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableCompletableObserver() {
//                    @Override
//                    public void onComplete() {
//                        fourBinding.pbEntities.setProgress(60);
//                        saveDownloadLevel(6);
//                        syncRentsMode();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//    }
//
//    private void syncRentsMode() {
//        disposable = syncViewModel.rentModeList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableSingleObserver<List<RentModeEntity>>() {
//                    @Override
//                    public void onSuccess(List<RentModeEntity> rentModeEntities) {
//                        fourBinding.pbEntities.setProgress(65);
//                        saveRentsMode(rentModeEntities);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//
//    }
//
//    private void saveRentsMode(List<RentModeEntity> rentModeEntityList) {
//        disposable = syncViewModel.insertRentMode(rentModeEntityList)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableCompletableObserver() {
//                    @Override
//                    public void onComplete() {
//                        fourBinding.pbEntities.setProgress(70);
//                        saveDownloadLevel(7);
//                        syncRents();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//    }
//
//    private void syncRents() {
//        disposable = syncViewModel.rentList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableSingleObserver<List<RentEntity>>() {
//                    @Override
//                    public void onSuccess(List<RentEntity> rentEntities) {
//                        fourBinding.pbEntities.setProgress(75);
//                        saveRents(rentEntities);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//    }
//
//    private void saveRents(List<RentEntity> rentEntityList) {
//        disposable = syncViewModel.insertRent(rentEntityList)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableCompletableObserver() {
//                    @Override
//                    public void onComplete() {
//                        fourBinding.pbEntities.setProgress(80);
//                        saveDownloadLevel(8);
//                        syncDrawsType();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//    }
//
//    private void syncDrawsType() {
//        disposable = syncViewModel.drawTypeList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableSingleObserver<List<DrawTypeEntity>>() {
//                    @Override
//                    public void onSuccess(List<DrawTypeEntity> drawTypeEntities) {
//                        fourBinding.pbEntities.setProgress(85);
//                        saveDrawsType(drawTypeEntities);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//
//    }
//
//    private void saveDrawsType(List<DrawTypeEntity> drawTypeEntitiesList) {
//        disposable = syncViewModel.insertDrawType(drawTypeEntitiesList)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableCompletableObserver() {
//                    @Override
//                    public void onComplete() {
//                        fourBinding.pbEntities.setProgress(90);
//                        saveDownloadLevel(9);
//                        syncRentAmenities();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//    }
//
//    private void syncRentAmenities() {
//        disposable = syncViewModel.rentAmenitiesList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableSingleObserver<List<RentAmenitiesEntity>>() {
//                    @Override
//                    public void onSuccess(List<RentAmenitiesEntity> rentAmenitiesEntityList) {
//                        fourBinding.pbEntities.setProgress(92);
//                        saveRentAmenities(rentAmenitiesEntityList);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//
//    }
//
//    private void saveRentAmenities(List<RentAmenitiesEntity> rentAmenitiesEntityList) {
//        disposable = syncViewModel.insertRentAmenities(rentAmenitiesEntityList)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableCompletableObserver() {
//                    @Override
//                    public void onComplete() {
//                        fourBinding.pbEntities.setProgress(94);
//                        saveDownloadLevel(10);
//                        syncRentPois();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//    }
//
//    private void syncRentPois() {
//        disposable = syncViewModel.rentPoiList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableSingleObserver<List<RentPoiEntity>>() {
//                    @Override
//                    public void onSuccess(List<RentPoiEntity> rentPoiEntityList) {
//                        fourBinding.pbEntities.setProgress(96);
//                        saveRentPois(rentPoiEntityList);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//
//    }
//
//    private void saveRentPois(List<RentPoiEntity> rentPoiEntityList) {
//        disposable = syncViewModel.insertRentPois(rentPoiEntityList)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableCompletableObserver() {
//                    @Override
//                    public void onComplete() {
//                        fourBinding.pbEntities.setProgress(98);
//                        saveDownloadLevel(11);
//                        syncGaleries();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//    }
//
//    private void syncGaleries() {
//        disposable = syncViewModel.galerieList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableSingleObserver<List<GalerieEntity>>() {
//                    @Override
//                    public void onSuccess(List<GalerieEntity> galerieEntities) {
//                        fourBinding.pbEntities.setProgress(99);
//                        saveGaleries(galerieEntities);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//    }
//
//    private void saveGaleries(List<GalerieEntity> galerieEntityList) {
//        disposable = syncViewModel.insertGalerie(galerieEntityList)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableCompletableObserver() {
//                    @Override
//                    public void onComplete() {
//                        isSync = false;
//                        fourBinding.pbEntities.setProgress(100);
//                        saveDownloadLevel(12);
//                        if (fourBinding.cbImages.isChecked()) {
//                            fourBinding.setDownloadImages(true);
//                            sizeGalerieList = galerieEntityList.size();
//                            fourBinding.pbGalery.setEndProgress(sizeGalerieList);
//                            prepareForDownload();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        isSync = false;
//                        mListener.onDownloadError(e);
//                    }
//                });
//        compositeDisposable.add(disposable);
//    }
//
//    private void prepareForDownload() {
//        disposable = syncViewModel
//                .galerieEntityList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(this::TESTImagesFromList2, Timber::e);
//        compositeDisposable.add(disposable);
//    }
//
//    private void getImagesFromList(List<GalerieEntity> galerieEntities) {
//        disposable = Flowable
//                .fromIterable(galerieEntities)
//                .flatMapCompletable(galerieEntity -> downloadImage(galerieEntity)
//                        .andThen(updateGalerieEntity(galerieEntity)))
//                .doOnError(throwable -> mListener.onDownloadError(throwable))
//                .subscribeOn(Schedulers.single())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe();
//        compositeDisposable.add(disposable);
//    }
//
//    private Completable downloadImage(GalerieEntity entitie) {
//        return syncViewModel
//                .fetchImage(entitie.getImageUrl())
//                .subscribeOn(Schedulers.io())
//                .flatMap(responseBody -> createFileFromResponse(entitie.getId(), responseBody))
//                .flatMapCompletable(s -> Completable.fromAction(() -> entitie.setImageLocalPath(s)));
//    }
//
//    private Single<String> createFileFromResponse(String fileName, ResponseBody responseBody) {
//        File fileRoot = new File(getActivity().getFilesDir().getAbsolutePath() + File.separator + "galerie");
//        if (!fileRoot.exists()) {
//            fileRoot.mkdir();
//        }
//        File imageFile = new File(fileRoot.getAbsolutePath() + File.separator + fileName + ".png");
//        boolean saved = FileUtils.writeFileFromIS(imageFile, responseBody.byteStream());
//        if (saved) {
//            return Single.just(imageFile.getAbsolutePath());
//        } else {
//            return Single.error(NullPointerException::new);
//        }
//    }
//
//
//    private Completable updateGalerieEntity(GalerieEntity entity) {
//        return Completable.fromAction(() -> {
//            SystemClock.sleep(2000);
//            progressGalery++;
//        })
//                .subscribeOn(Schedulers.single())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnError(throwable -> mListener.onDownloadError(throwable))
//                .doOnComplete(() -> {
//                    float percent = ((float) progressGalery / sizeGalerieList) * 100;
//                    fourBinding.pbGalery.setProgress(percent);
//                    Timber.e("progress %s", percent);
//                });
////        return syncViewModel
////                .updateGalerie(entity)
////                .subscribeOn(Schedulers.computation())
////                .observeOn(AndroidSchedulers.mainThread())
////                .doOnComplete(new Action() {
////                    @Override
////                    public void run() throws Exception {
////                        Timber.e(entity.getImageLocalPath() != null ? entity.getImageLocalPath() : "null ---- NULL");
////                        fourBinding.pbGalery.setProgress(progressGalery);
////                        if (sizeGalerieList == progressGalery) {
////                            saveDownloadLevel(13);
////                            mListener.onDownloadSuccess();
////                        } else {
////                            progressGalery++;
////                        }
////                    }
////                });
//    }
//
//    //----------------------- TEST METHOD ------------------------------//
//
//    private void TESTImagesFromList2(List<GalerieEntity> galerieEntities) {
//        final boolean[] errorOcurred = {false};
//        for (int i = 0; i < galerieEntities.size() && !errorOcurred[0]; ++i) {
//            int index = i;
//            TESTdownload2(galerieEntities.get(i))
//                    .subscribeOn(Schedulers.io())
//                    .subscribe(new DisposableCompletableObserver() {
//                        @Override
//                        public void onComplete() {
//                            Timber.e("onComplete %s",index);
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            errorOcurred[0] = true;
//                            Timber.e("onError %s, %s",index, e.getMessage());
//                        }
//                    });
//
//        }
//    }
//
//
//    private void TESTImagesFromList(List<GalerieEntity> galerieEntities) {
//        disposable = Flowable
//                .fromIterable(galerieEntities)
//                .flatMapCompletable(galerieEntity -> {
//                    Timber.e("FlatMapCompletable level 0");
//                    return TESTdownload(galerieEntity);
//                })
//                .doOnError(throwable -> Timber.e("Error doOnError level 0 %s", throwable.getMessage()))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe();
//        compositeDisposable.add(disposable);
//    }
//
//    private Completable TESTdownload2(GalerieEntity entitie) {
//        SystemClock.sleep(2000);
//        Single<ResponseBody> bodySingle = syncViewModel
//                .fetchImage(entitie.getImageUrl())
//                .doOnSuccess(new Consumer<ResponseBody>() {
//                    @Override
//                    public void accept(ResponseBody responseBody) throws Exception {
//                        Timber.e("size %s", responseBody.contentLength());
//                    }
//                });
//        return Completable.fromSingle(bodySingle);
//    }
//
//    private Completable TESTdownload(GalerieEntity entitie) {
//        SystemClock.sleep(2000);
//        Single<ResponseBody> bodySingle = syncViewModel
//                .fetchImage(entitie.getImageUrl())
//                .doOnError(throwable -> Timber.e("Error doOnError level 1 %s", throwable.getMessage()))
//                .doOnSuccess(new Consumer<ResponseBody>() {
//                    @Override
//                    public void accept(ResponseBody responseBody) throws Exception {
//                        Timber.e("size %s", responseBody.contentLength());
//                    }
//                });
//        return Completable.fromSingle(bodySingle).onErrorComplete(throwable -> {
//            Timber.e("Error doOnError level 2 %s", throwable.getMessage());
//            return throwable instanceof ConnectException;
//        }).doOnError(throwable -> Timber.e("Error doOnError level 3 %s", throwable.getMessage()));
//    }
//
//    private Single<String> TESTcreateFileFromResponse(String fileName, ResponseBody responseBody) {
//        File fileRoot = new File(getActivity().getFilesDir().getAbsolutePath() + File.separator + "galerie");
//        if (!fileRoot.exists()) {
//            fileRoot.mkdir();
//        }
//        File imageFile = new File(fileRoot.getAbsolutePath() + File.separator + fileName + ".png");
//        boolean saved = FileUtils.writeFileFromIS(imageFile, responseBody.byteStream());
//        if (saved) {
//            return Single.just(imageFile.getAbsolutePath());
//        } else {
//            return Single.error(NullPointerException::new);
//        }
//    }
//
//
//    //------------------------------------------------------------------//
//
//
//    private void saveDownloadLevel(int value) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt(PREF_DOWNLOAD_LEVEL, value);
//        editor.apply();
//    }
//
//
//}
