package com.infinitum.bookingqba.view.sync;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivitySyncBinding;
import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.DrawTypeEntity;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.entity.MunicipalityEntity;
import com.infinitum.bookingqba.model.local.entity.PoiEntity;
import com.infinitum.bookingqba.model.local.entity.PoiTypeEntity;
import com.infinitum.bookingqba.model.local.entity.ProvinceEntity;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.local.entity.RentAmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.local.entity.RentPoiEntity;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.viewmodel.SyncViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.muddzdev.styleabletoast.StyleableToast;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.PREF_ENTITY_DOWNLOAD_INDEX;
import static com.infinitum.bookingqba.util.Constants.PREF_DOWNLOAD_LEVEL;
import static com.infinitum.bookingqba.util.Constants.PREF_DOWNLOAD_SUCCESS;
import static com.infinitum.bookingqba.util.Constants.ROOT_GALERY_FOLDER_NAME;

public class SyncActivity extends DaggerAppCompatActivity implements View.OnClickListener {

    private static final String LEVEL_ENTITY = "entity";
    private static final String LEVEL_GALERY = "galery";
    private static final String LEVEL_GALERY_PAUSED = "paused";

    private ActivitySyncBinding syncBinding;

    private SyncViewModel syncViewModel;
    private Disposable entityDisposable;
    private CompositeDisposable compositeDisposable;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    NetworkHelper networkHelper;

    private FileDownloadListener fileDownloadListener;
    private FileDownloadQueueSet queueSet;

    @Nullable
    private Disposable galeryDisposable;


    private boolean isFirstDownload = true;
    private int progressGalery = 0;
    private int sizeGalerieList = 0;
    private long galerySpaceInMb = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        syncBinding = DataBindingUtil.setContentView(this, R.layout.activity_sync);

        FileDownloader.setup(this);

        compositeDisposable = new CompositeDisposable();

        String btnTag = sharedPreferences.getString(PREF_DOWNLOAD_LEVEL, LEVEL_ENTITY);
        syncBinding.fbDownload.setTag(btnTag);

        syncBinding.fbDownload.setOnClickListener(this);

        syncViewModel = ViewModelProviders.of(this, viewModelFactory).get(SyncViewModel.class);
    }

    private void preDownloadAnimation() {
        syncBinding.setDownloadImages(syncBinding.cbImages.isChecked());
        syncBinding.setIsDownloading(true);
        syncBinding.llContentDownload.setAlpha(0);
        syncBinding.llContentDownload.setTranslationY(30);
        ViewCompat.animate(syncBinding.llContentDownload)
                .setInterpolator(new AccelerateInterpolator())
                .alpha(1f)
                .translationY(0)
                .setDuration(300)
                .start();
    }

    private void startSyncOnLevel(int downloadLevel) {
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
                syncRentAmenities();
                break;
            case 10:
                syncRentPois();
                break;
            case 11:
                syncGaleries();
                break;
        }
    }

    private void syncReferencesZone() {
        entityDisposable = syncViewModel.referenceZoneList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<ReferenceZoneEntity>>() {
                    @Override
                    public void onSuccess(List<ReferenceZoneEntity> referenceZoneEntities) {
                        syncBinding.setPercent(4f);
                        saveReferencesZone(referenceZoneEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }

    private void saveReferencesZone(List<ReferenceZoneEntity> referenceZoneEntityList) {
        entityDisposable = syncViewModel.insertReferenceZone(referenceZoneEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        syncBinding.setPercent(8f);
                        saveEntityDownloadIndex(1);
                        syncProvinces();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }

    private void syncProvinces() {
        entityDisposable = syncViewModel.provinceList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<ProvinceEntity>>() {
                    @Override
                    public void onSuccess(List<ProvinceEntity> provinceEntityList) {
                        syncBinding.setPercent(12f);
                        addProvinces(provinceEntityList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }

    private void addProvinces(List<ProvinceEntity> provinceEntityList) {
        entityDisposable = syncViewModel.insertProvinces(provinceEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        syncBinding.setPercent(16f);
                        saveEntityDownloadIndex(2);
                        syncMunicipalities();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }

    private void syncMunicipalities() {
        entityDisposable = syncViewModel.municipalityList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<MunicipalityEntity>>() {
                    @Override
                    public void onSuccess(List<MunicipalityEntity> municipalityEntityList) {
                        syncBinding.setPercent(20f);
                        addMunicipalities(municipalityEntityList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }

    private void addMunicipalities(List<MunicipalityEntity> municipalityEntityList) {
        entityDisposable = syncViewModel.insertMunicipality(municipalityEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        syncBinding.setPercent(24f);
                        saveEntityDownloadIndex(3);
                        syncAmenities();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }

    private void syncAmenities() {
        entityDisposable = syncViewModel.amenitiesList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<AmenitiesEntity>>() {
                    @Override
                    public void onSuccess(List<AmenitiesEntity> amenitiesEntities) {
                        syncBinding.setPercent(28f);
                        saveAmenities(amenitiesEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);

    }

    private void saveAmenities(List<AmenitiesEntity> amenitiesEntityList) {
        entityDisposable = syncViewModel.insertAmenities(amenitiesEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        syncBinding.setPercent(32f);
                        saveEntityDownloadIndex(4);
                        syncPoiTypes();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }

    private void syncPoiTypes() {
        entityDisposable = syncViewModel.poiTypeList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<PoiTypeEntity>>() {
                    @Override
                    public void onSuccess(List<PoiTypeEntity> poiTypeEntities) {
                        syncBinding.setPercent(36f);
                        savePoiTypes(poiTypeEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);

    }

    private void savePoiTypes(List<PoiTypeEntity> poiTypeEntityList) {
        entityDisposable = syncViewModel.insertPoiType(poiTypeEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        syncBinding.setPercent(40f);
                        saveEntityDownloadIndex(5);
                        syncPois();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }

    private void syncPois() {
        entityDisposable = syncViewModel.poiList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<PoiEntity>>() {
                    @Override
                    public void onSuccess(List<PoiEntity> poiEntities) {
                        syncBinding.setPercent(44f);
                        savePois(poiEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);

    }

    private void savePois(List<PoiEntity> poiEntityList) {
        entityDisposable = syncViewModel.insertPoi(poiEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        syncBinding.setPercent(48f);
                        saveEntityDownloadIndex(6);
                        syncRentsMode();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }

    private void syncRentsMode() {
        entityDisposable = syncViewModel.rentModeList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<RentModeEntity>>() {
                    @Override
                    public void onSuccess(List<RentModeEntity> rentModeEntities) {
                        syncBinding.setPercent(52f);
                        saveRentsMode(rentModeEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);

    }

    private void saveRentsMode(List<RentModeEntity> rentModeEntityList) {
        entityDisposable = syncViewModel.insertRentMode(rentModeEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        syncBinding.setPercent(56f);
                        saveEntityDownloadIndex(7);
                        syncRents();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }

    private void syncRents() {
        entityDisposable = syncViewModel.rentList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<RentEntity>>() {
                    @Override
                    public void onSuccess(List<RentEntity> rentEntities) {
                        syncBinding.setPercent(60f);
                        saveRents(rentEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }

    private void saveRents(List<RentEntity> rentEntityList) {
        entityDisposable = syncViewModel.insertRent(rentEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        syncBinding.setPercent(64f);
                        saveEntityDownloadIndex(8);
                        syncDrawsType();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }

    private void syncDrawsType() {
        entityDisposable = syncViewModel.drawTypeList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<DrawTypeEntity>>() {
                    @Override
                    public void onSuccess(List<DrawTypeEntity> drawTypeEntities) {
                        syncBinding.setPercent(68f);
                        saveDrawsType(drawTypeEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);

    }

    private void saveDrawsType(List<DrawTypeEntity> drawTypeEntitiesList) {
        entityDisposable = syncViewModel.insertDrawType(drawTypeEntitiesList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        syncBinding.setPercent(72f);
                        saveEntityDownloadIndex(9);
                        syncRentAmenities();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }

    private void syncRentAmenities() {
        entityDisposable = syncViewModel.rentAmenitiesList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<RentAmenitiesEntity>>() {
                    @Override
                    public void onSuccess(List<RentAmenitiesEntity> rentAmenitiesEntityList) {
                        syncBinding.setPercent(76f);
                        saveRentAmenities(rentAmenitiesEntityList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);

    }

    private void saveRentAmenities(List<RentAmenitiesEntity> rentAmenitiesEntityList) {
        entityDisposable = syncViewModel.insertRentAmenities(rentAmenitiesEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        syncBinding.setPercent(80f);
                        saveEntityDownloadIndex(10);
                        syncRentPois();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }

    private void syncRentPois() {
        entityDisposable = syncViewModel.rentPoiList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<RentPoiEntity>>() {
                    @Override
                    public void onSuccess(List<RentPoiEntity> rentPoiEntityList) {
                        syncBinding.setPercent(84f);
                        saveRentPois(rentPoiEntityList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);

    }

    private void saveRentPois(List<RentPoiEntity> rentPoiEntityList) {
        entityDisposable = syncViewModel.insertRentPois(rentPoiEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        syncBinding.setPercent(88f);
                        saveEntityDownloadIndex(11);
                        syncGaleries();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }

    private void syncGaleries() {
        entityDisposable = syncViewModel.galerieList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<GalerieEntity>>() {
                    @Override
                    public void onSuccess(List<GalerieEntity> galerieEntities) {
                        syncBinding.setPercent(96f);
                        saveGaleries(galerieEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }

    private void saveGaleries(List<GalerieEntity> galerieEntityList) {
        entityDisposable = syncViewModel.insertGalerie(galerieEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        syncBinding.setPercent(100f);
                        saveEntityDownloadIndex(12);
                        if (syncBinding.cbImages.isChecked()) {
                            sizeGalerieList = galerieEntityList.size();
                            syncBinding.pbGalery.setEndProgress(sizeGalerieList);
                            syncBinding.pbGalery.setProgressTextVisibility(true);
                            prepareForDownload();
                        } else {
                            onDownloadSuccess();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        onDownloadError(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }


    private void prepareForDownload() {
        galeryDisposable = syncViewModel
                .galerieEntityList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::prepareLinksForDownload)
                .subscribe(this::startDownloadImages, throwable -> {
                    saveDownloadLevel(LEVEL_GALERY);
                    syncBinding.fbDownload.setTag(LEVEL_GALERY);
                });
    }


    private List<BaseDownloadTask> prepareLinksForDownload(List<GalerieEntity> galerieEntities) {
        List<BaseDownloadTask> tasks = new ArrayList<>();
        String fileRootName = getFilesDir().getAbsolutePath() + File.separator + ROOT_GALERY_FOLDER_NAME;
        File fileRoot = new File(fileRootName);
        if (!fileRoot.exists()) {
            try {
                fileRoot.mkdir();
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        for (GalerieEntity entity : galerieEntities) {
            String url = entity.getImageUrl();
            String imageName = fileRootName + File.separator + getFileNameFromUrl(url);
            tasks.add(FileDownloader.getImpl().create(url).setPath(imageName).setTag(entity.getId()));
            addGaleryUpdateUtil(entity.getId(), imageName);
        }
        return tasks;
    }

    private String getFileNameFromUrl(String url) {
        String[] splitUrl = url.split("/");
        String fileName = "";
        if (splitUrl.length > 0) {
            fileName = splitUrl[splitUrl.length - 1];
        }
        return fileName;
    }

    private void startDownloadImages(List<BaseDownloadTask> taskList) {
        if (queueSet != null) {
            queueSet.reuseAndStart();
        } else {
            queueSet = new FileDownloadQueueSet(getDownloaderListener());
            queueSet.disableCallbackProgressTimes();
            queueSet.downloadTogether(taskList);
            queueSet.start();
        }

    }

    private void updateUIWithProgress() {
        final int progress = getDownloadProgress();
        syncBinding.pbGalery.setProgress(progress);
        if (sizeGalerieList == progressGalery) {
            disposeAllDisposable();
            updateAllGaleryDownloaded();
        }
    }

    private int getDownloadProgress() {
        progressGalery++;
        final int totalProgress = sizeGalerieList;
        return (int) (((double) progressGalery / (double) totalProgress) * 100);
    }

    private void addGaleryUpdateUtil(Object uuid, String targetFilePath) {
        syncViewModel.addGaleryUpdateUtil(uuid.toString(), targetFilePath);
    }

    private void updateAllGaleryDownloaded() {
        entityDisposable = syncViewModel.updateGaleryUpdateUtilList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        onDownloadSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        showErrorToast();
                        Timber.e(e);
                    }
                });
        compositeDisposable.add(entityDisposable);
    }


    public void onDownloadSuccess() {
        saveSuccessDownload();
        showSuccessToast();
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SyncActivity.this, HomeActivity.class));
            SyncActivity.this.finish();
        }, 1000);
    }


    public void onDownloadError(Throwable throwable) {
        Timber.e(throwable);
        showErrorToast();
        syncBinding.fbDownload.setEnabled(true);
    }


    private void showErrorToast() {
        StyleableToast.makeText(SyncActivity.this, "Un error a ocurrido", Toast.LENGTH_LONG, R.style.myErrorToast).show();
    }

    private void showSuccessToast() {
        StyleableToast.makeText(SyncActivity.this, "Toda a ido bien", Toast.LENGTH_LONG, R.style.mySuccessToast).show();
    }


    //SAVE TO SHARED PREFERENCE
    private void saveEntityDownloadIndex(int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_ENTITY_DOWNLOAD_INDEX, value);
        editor.apply();
    }

    private void saveDownloadLevel(String level) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_DOWNLOAD_LEVEL, level);
        editor.apply();
    }

    private void saveSuccessDownload() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_DOWNLOAD_SUCCESS, true);
        editor.apply();
    }

    private void disposeAllDisposable() {
        if (galeryDisposable != null && !galeryDisposable.isDisposed()) {
            galeryDisposable.dispose();
        }
        if (entityDisposable != null && !entityDisposable.isDisposed()) {
            entityDisposable.dispose();
        }
    }

    //-------------  File Downloader ------------------- //

    private FileDownloadListener getDownloaderListener() {
        if (fileDownloadListener == null) {
            fileDownloadListener = createDownloadListener();
            return fileDownloadListener;
        } else {
            return fileDownloadListener;
        }
    }

    private FileDownloadListener createDownloadListener() {
        return new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            }

            @Override
            protected void completed(BaseDownloadTask task) {
                galerySpaceInMb = galerySpaceInMb + task.getLargeFileTotalBytes();
                syncBinding.setGalerieSize(galerySpaceInMb);
                updateUIWithProgress();
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                Timber.e("paused soFarBytes %s, totalBytes %s", soFarBytes, totalBytes);
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                FileDownloader.getImpl().pause(getDownloaderListener());
                syncBinding.fbDownload.setTag(LEVEL_GALERY_PAUSED);
                syncBinding.fbDownload.setEnabled(true);
                showErrorToast();
                saveDownloadLevel(LEVEL_GALERY);
            }

            @Override
            protected void warn(BaseDownloadTask task) {

            }
        };
    }

    @Override
    public void onClick(View v) {
        if (networkHelper.isNetworkAvailable()) {
            if (syncBinding.fbDownload.getTag().equals(LEVEL_ENTITY)) {
                int downLevel = sharedPreferences.getInt(PREF_ENTITY_DOWNLOAD_INDEX, 0);
                syncBinding.fbDownload.setEnabled(false);
                if (isFirstDownload) {
                    isFirstDownload = false;
                    preDownloadAnimation();
                    new Handler().postDelayed(() -> startSyncOnLevel(downLevel), 500);
                } else {
                    startSyncOnLevel(downLevel);
                }
            } else if (syncBinding.fbDownload.getTag().equals(LEVEL_GALERY)) {
                prepareForDownload();
            } else if (syncBinding.fbDownload.getTag().equals(LEVEL_GALERY_PAUSED)) {
                progressGalery = 0;
                syncBinding.fbDownload.setEnabled(false);
                queueSet.reuseAndStart();
            }
        } else {
            showErrorToast();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        disposeAllDisposable();
        FileDownloader.getImpl().clearAllTaskData();
    }
}
