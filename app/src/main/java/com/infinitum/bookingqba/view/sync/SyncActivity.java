package com.infinitum.bookingqba.view.sync;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivitySyncBinding;
import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.DatabaseUpdateEntity;
import com.infinitum.bookingqba.model.local.entity.DrawTypeEntity;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.entity.RentAmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentPoiEntity;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.DateUtils;
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
import io.reactivex.Flowable;
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


    private int entityProgress = 0;
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
        String dateValue = getResources().getString(R.string.fecha_prueba);
        switch (downloadLevel) {
            case 0:
                syncronizeReferencesZone(dateValue);
                break;
            case 1:
                syncronizeProvinces(dateValue);
                break;
            case 2:
                syncronizeMunicipalities(dateValue);
                break;
            case 3:
                syncronizeAmenities(dateValue);
                break;
            case 4:
                syncronizePoiTypes(dateValue);
                break;
            case 5:
                syncronizePois(dateValue);
                break;
            case 6:
                syncronizeRentsMode(dateValue);
                break;
            case 7:
                syncronizeDrawTypes(dateValue);
                break;
            case 8:
                syncronizeRents(dateValue);
                break;
            case 9:
                syncronizeRentAmenities(dateValue);
                break;
            case 10:
                syncronizeRentPois(dateValue);
                break;
            case 11:
                syncronizeRentDrawTypes(dateValue);
                break;
            case 12:
                syncronizeOffers(dateValue);
                break;
//            case 11:
//                syncGaleries();
//                break;
        }
    }

    private void startSyncronitation(int downloadIndex) {
        Flowable<Resource<DatabaseUpdateEntity>> flowLocal = syncViewModel.getLastDatabaseUpdate();
        Flowable<Resource<DatabaseUpdateEntity>> flowRemote = syncViewModel.getDatabaseUpdateRemote();
        entityDisposable = Flowable.zip(flowLocal, flowRemote, this::checkIfUpdateNeeded)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(aBoolean -> {
                    if (aBoolean) {
                        startSyncOnLevel(downloadIndex);
                    } else {
                        AlertUtils.showInfoAlertAndGoHome(this,getResources().getString(R.string.no_need_update));
                    }
                }).subscribe();
        compositeDisposable.add(entityDisposable);
    }

    @NonNull
    private Boolean checkIfUpdateNeeded(Resource<DatabaseUpdateEntity> resourceLocal, Resource<DatabaseUpdateEntity> resourceRemote) {
        boolean updateRequired = false;
        if (resourceLocal.status == Resource.Status.EMPTY) {
            updateRequired = true;
        } else if (resourceLocal.status == Resource.Status.SUCCESS && resourceRemote.data != null) {
            updateRequired = DateUtils.dateLocalIsLessThanRemote(resourceLocal.data.getLastDatabaseUpdate(), resourceRemote.data.getLastDatabaseUpdate());
        }
        return updateRequired;
    }

    private void syncronizeReferencesZone(String dateValue) {
        entityDisposable = syncViewModel.syncReferenceZone(dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeProvinces(String dateValue) {
        entityDisposable = syncViewModel.syncProvinces(dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeMunicipalities(String dateValue) {
        entityDisposable = syncViewModel.syncMunicipalities(dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeAmenities(String dateValue) {
        entityDisposable = syncViewModel.syncAmenities(dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizePoiTypes(String dateValue) {
        entityDisposable = syncViewModel.syncPoiTypes(dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizePois(String dateValue) {
        entityDisposable = syncViewModel.syncPois(dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeRentsMode(String dateValue) {
        entityDisposable = syncViewModel.syncRentsMode(dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeDrawTypes(String dateValue) {
        entityDisposable = syncViewModel.syncDrawTypes(dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeRents(String dateValue) {
        entityDisposable = syncViewModel.syncRents(dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeRentAmenities(String dateValue) {
        entityDisposable = syncViewModel.syncRentAmenities(dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeRentPois(String dateValue) {
        entityDisposable = syncViewModel.syncRentPois(dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeRentDrawTypes(String dateValue) {
        entityDisposable = syncViewModel.syncRentDrawType(dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeOffers(String dateValue) {
        entityDisposable = syncViewModel.syncOffers(dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void checkOperationResult(OperationResult operationResult) {
        if (operationResult.result == OperationResult.Result.SUCCESS) {
            updateEntityProgressAndContinue();
        } else if (operationResult.result == OperationResult.Result.ERROR) {
            onDownloadError(operationResult.message);
        }
    }

    private void updateEntityProgressAndContinue() {
        entityProgress++;
        float percent = ((float) entityProgress / 11) * 100;
        saveEntityDownloadIndex(entityProgress);
        syncBinding.setPercent(percent);
        if (entityProgress < 11) {
            startSyncronitation(entityProgress);
        } else if (entityProgress == 11 && !syncBinding.cbImages.isChecked()) {
            AlertUtils.showSuccessAlertAndGoHome(this);
        }
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
        AlertUtils.showErrorAlert(this);
        syncBinding.fbDownload.setEnabled(true);
    }

    public void onDownloadError(String message) {
        Timber.e("Error on index ===> %s, with message ===> %s", entityProgress, message);
        AlertUtils.showErrorAlert(this);
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
                entityProgress = sharedPreferences.getInt(PREF_ENTITY_DOWNLOAD_INDEX, 0);
                syncBinding.fbDownload.setEnabled(false);
                if (isFirstDownload) {
                    isFirstDownload = false;
                    preDownloadAnimation();
                    new Handler().postDelayed(() -> startSyncronitation(entityProgress), 500);
                } else {
                    startSyncOnLevel(entityProgress);
                }
            } else if (syncBinding.fbDownload.getTag().equals(LEVEL_GALERY)) {
                prepareForDownload();
            } else if (syncBinding.fbDownload.getTag().equals(LEVEL_GALERY_PAUSED)) {
                progressGalery = 0;
                syncBinding.fbDownload.setEnabled(false);
                queueSet.reuseAndStart();
            }
        } else {
            AlertUtils.showErrorAlert(this);
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
