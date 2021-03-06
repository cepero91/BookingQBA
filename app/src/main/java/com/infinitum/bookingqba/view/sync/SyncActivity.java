package com.infinitum.bookingqba.view.sync;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivitySyncBinding;
import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.DatabaseUpdateEntity;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.remote.pojo.SyncData;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.util.DateUtils;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.util.ThrowableUtil;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.viewmodel.SyncViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;


import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.ALTERNATIVE_SYNC;
import static com.infinitum.bookingqba.util.Constants.LEVEL_ENTITY;
import static com.infinitum.bookingqba.util.Constants.LEVEL_GALERY;
import static com.infinitum.bookingqba.util.Constants.LEVEL_GALERY_PAUSED;
import static com.infinitum.bookingqba.util.Constants.LEVEL_PROGRESS_ENTITY;
import static com.infinitum.bookingqba.util.Constants.LEVEL_PROGRESS_GALERY;
import static com.infinitum.bookingqba.util.Constants.PREF_ENTITY_DOWNLOAD_INDEX;
import static com.infinitum.bookingqba.util.Constants.PREF_DOWNLOAD_LEVEL;
import static com.infinitum.bookingqba.util.Constants.PREF_LAST_DOWNLOAD_DATE;
import static com.infinitum.bookingqba.util.Constants.PROGRESS_ERROR;
import static com.infinitum.bookingqba.util.Constants.PROGRESS_SUCCESS;
import static com.infinitum.bookingqba.util.Constants.ROOT_GALERY_FOLDER_NAME;

public class SyncActivity extends DaggerAppCompatActivity implements View.OnClickListener {
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


    private String apiTokenAuthorization;
    private int entityProgress = 0;
    private boolean uniqueDownloadErrorAlert = false;
    private int progressGalery = 0;
    private int sizeGalerieList = 0;
    private long galerySpaceInMb = 0;
    private boolean downloadImages = true;
    private String currentDateToSync = "";
    private boolean alternativeSync = false;
    private boolean updateIsNeeded = false;
    private float entityPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        syncBinding = DataBindingUtil.setContentView(this, R.layout.activity_sync);
        compositeDisposable = new CompositeDisposable();
        syncViewModel = ViewModelProviders.of(this, viewModelFactory).get(SyncViewModel.class);
        FileDownloader.setup(this);

        apiTokenAuthorization = getString(R.string.device);

        if (getIntent().hasExtra(ALTERNATIVE_SYNC)) {
            alternativeSync = getIntent().getExtras().getBoolean(ALTERNATIVE_SYNC, false);
        }

        initButtonComponent();

        checkRemoteUpdateData();

        syncBinding.fbDownload.setOnClickListener(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        syncBinding.ivUseCase.pauseAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncBinding.ivUseCase.resumeAnimation();
    }

    private void initButtonComponent() {
        String btnTag = sharedPreferences.getString(PREF_DOWNLOAD_LEVEL, LEVEL_ENTITY);
        syncBinding.fbDownload.setTag(btnTag);
        if (btnTag.equals(LEVEL_GALERY)) {
            syncBinding.pbEntities.setProgress(50);
        }
    }

    private void startSyncOnLevel(int downloadLevel) {
        syncBinding.pbEntities.setAlpha(1f);
        if (!currentDateToSync.equals("")) {
            switch (downloadLevel) {
                case 0:
                    syncronizeReferencesZone(currentDateToSync);
                    break;
                case 1:
                    syncronizeProvinces(currentDateToSync);
                    break;
                case 2:
                    syncronizeMunicipalities(currentDateToSync);
                    break;
                case 3:
                    syncronizeAmenities(currentDateToSync);
                    break;
                case 4:
                    syncronizePoiTypes(currentDateToSync);
                    break;
                case 5:
                    syncronizePois(currentDateToSync);
                    break;
                case 6:
                    syncronizeRentsMode(currentDateToSync);
                    break;
                case 7:
                    syncronizeDrawTypes(currentDateToSync);
                    break;
                case 8:
                    syncronizeRents(currentDateToSync);
                    break;
                case 9:
                    syncronizeRentAmenities(currentDateToSync);
                    break;
                case 10:
                    syncronizeRentPois(currentDateToSync);
                    break;
                case 11:
                    syncronizeRentDrawTypes(currentDateToSync);
                    break;
                case 12:
                    syncronizeOffers(currentDateToSync);
                    break;
                case 13:
                    syncronizeGaleries(currentDateToSync);
                    break;
                case 14:
                    syncronizeCommet(currentDateToSync);
                    break;
                case 15:
                    removedItems(currentDateToSync);
                    break;
                case 16:
                    completeDownload();
                    break;
            }
        }
    }

    private void checkRemoteUpdateData() {
        if (networkHelper.isNetworkAvailable()) {
            entityDisposable = Flowable.zip(syncViewModel.getDatabaseUpdateLocal(),
                    syncViewModel.getDatabaseUpdateRemote(), (localResource, remoteResource) -> {
                        boolean updateNeeded = checkIfUpdateNeeded(localResource, remoteResource);
                        String localDate = checkLocalDate(localResource);
                        return new Pair<>(updateNeeded, localDate);
                    })
                    .flatMap(booleanStringPair -> syncViewModel.syncData(booleanStringPair.second).subscribeOn(Schedulers.io()).map(syncData -> {
                        Map<String, Object> stringObjectMap = new HashMap<>();
                        stringObjectMap.put("up", booleanStringPair.first);
                        stringObjectMap.put("date", booleanStringPair.second);
                        stringObjectMap.put("sync", syncData);
                        return stringObjectMap;
                    }))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(stringObjectMap -> {
                        if (stringObjectMap.containsKey("up") && ((Boolean) stringObjectMap.get("up"))) {
                            String rents = String.valueOf(((SyncData) stringObjectMap.get("sync")).getRents());
                            String size = String.format(Locale.getDefault(), "%.1f", ((SyncData) stringObjectMap.get("sync")).getResourceSize());
                            syncBinding.tvRents.setText(rents);
                            syncBinding.tvResource.setText(size);
                            updateIsNeeded = true;
                            currentDateToSync = ((String) stringObjectMap.get("date"));
                        } else {
                            AlertUtils.showInfoAlertAndGoHome(this, getResources().getString(R.string.no_need_update));
                        }
                    }, throwable -> onDownloadError(ThrowableUtil.getMsgFromThrowable(throwable)));
            compositeDisposable.add(entityDisposable);
        } else {
            AlertUtils.showCFErrorNotificationWithAction(this, "Ooop!!",
                    Constants.CONNEXION_ERROR_MSG, (dialog, which) -> {
                        dialog.dismiss();
                        checkRemoteUpdateData();
                    }, (dialog, which) -> {
                        dialog.dismiss();
                        SyncActivity.this.finish();
                    }, "Volver a intentar", "Cerrar aplicación");
        }
    }

    /**
     * OJO CAMBIAR EL FORMATO DE SALIDA DE LA FECHA QUE DA 500 ERROR
     *
     * @param localResource
     * @return
     */
    private String checkLocalDate(Resource<DatabaseUpdateEntity> localResource) {
        if (localResource.status == Resource.Status.SUCCESS && localResource.data != null) {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es", "ES"));
            return parser.format(localResource.data.getLastDateUpdateEntity());
        } else {
            return getResources().getString(R.string.default_sync_date);
        }
    }

    @NonNull
    private Boolean checkIfUpdateNeeded(Resource<DatabaseUpdateEntity> resourceLocal, Resource<DatabaseUpdateEntity> resourceRemote) {
        boolean updateRequired = false;
        if (resourceLocal.status == Resource.Status.EMPTY) {
            updateRequired = true;
        } else if (resourceLocal.data != null && resourceRemote.data != null) {
            updateRequired = DateUtils.dateLocalIsLessThanRemote(resourceLocal.data.getLastDateUpdateEntity(), resourceRemote.data.getLastDateUpdateEntity());
        }
        return updateRequired;
    }

    private void syncronizeReferencesZone(String dateValue) {
        entityDisposable = syncViewModel.syncReferenceZone(apiTokenAuthorization, dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeProvinces(String dateValue) {
        entityDisposable = syncViewModel.syncProvinces(apiTokenAuthorization, dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeMunicipalities(String dateValue) {
        entityDisposable = syncViewModel.syncMunicipalities(apiTokenAuthorization, dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeAmenities(String dateValue) {
        entityDisposable = syncViewModel.syncAmenities(apiTokenAuthorization, dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizePoiTypes(String dateValue) {
        entityDisposable = syncViewModel.syncPoiTypes(apiTokenAuthorization, dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizePois(String dateValue) {
        entityDisposable = syncViewModel.syncPois(apiTokenAuthorization, dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeRentsMode(String dateValue) {
        entityDisposable = syncViewModel.syncRentsMode(apiTokenAuthorization, dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeDrawTypes(String dateValue) {
        entityDisposable = syncViewModel.syncDrawTypes(apiTokenAuthorization, dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeRents(String dateValue) {
        entityDisposable = syncViewModel.syncRents(apiTokenAuthorization, dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeRentAmenities(String dateValue) {
        entityDisposable = syncViewModel.syncRentAmenities(apiTokenAuthorization, dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeRentPois(String dateValue) {
        entityDisposable = syncViewModel.syncRentPois(apiTokenAuthorization, dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeRentDrawTypes(String dateValue) {
        entityDisposable = syncViewModel.syncRentDrawType(apiTokenAuthorization, dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeOffers(String dateValue) {
        entityDisposable = syncViewModel.syncOffers(apiTokenAuthorization, dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeGaleries(String dateValue) {
        entityDisposable = syncViewModel.syncGaleries(apiTokenAuthorization, dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void syncronizeCommet(String dateValue) {
        entityDisposable = syncViewModel.syncComment(apiTokenAuthorization, dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void removedItems(String dateValue) {
        entityDisposable = syncViewModel.removedsItem(dateValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void completeDownload() {
        entityDisposable = syncViewModel.insertDatabaseUpdate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkOperationResult, Timber::e);
        compositeDisposable.add(entityDisposable);
    }

    private void checkOperationResult(OperationResult operationResult) {
        if (operationResult.result == OperationResult.Result.SUCCESS) {
            updateEntityProgressAndContinue();
        } else if (operationResult.result == OperationResult.Result.OPERATIONAL_ERROR ||
                operationResult.result == OperationResult.Result.CONNEXION_ERROR) {
            onDownloadError(operationResult.message);
        }
    }

    private void updateEntityProgressAndContinue() {
        entityProgress++;
        entityPercent = ((float) entityProgress / 17) * 50;
        saveEntityDownloadIndex(entityProgress);
        syncBinding.pbEntities.setProgress((int) entityPercent);
        if (entityProgress < 17) {
            startSyncOnLevel(entityProgress);
        } else if (entityProgress == 17) {
            prepareForDownload();
        }
    }

    private void notifyDownloadEnds() {
        saveSuccessDownload();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        // Title and message
        builder.setTitle("Aviso!!");
        builder.setMessage(getString(R.string.sync_success));
        builder.setTextGravity(Gravity.CENTER);
        builder.addButton("Comenzar ya!!", -1, Color.parseColor("#00BFA5"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> dialog.dismiss());
        builder.onDismissListener(dialog -> {
            if(alternativeSync){
                SyncActivity.this.finish();
            }else{
                startActivity(new Intent(SyncActivity.this, HomeActivity.class));
            }
        });
        builder.setTextColor(Color.parseColor("#607D8B"));
        builder.show();
    }

    private void prepareForDownload() {
        galeryDisposable = syncViewModel
                .galeriesEntityVersionOne()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::prepareLinksForDownload)
                .subscribe(baseDownloadTasks -> {
                    saveDownloadLevel(LEVEL_GALERY);
                    startDownloadImages(baseDownloadTasks);
                }, throwable -> {
                    saveDownloadLevel(LEVEL_GALERY);
                    syncBinding.fbDownload.setTag(LEVEL_GALERY);
                });
    }


    private List<BaseDownloadTask> prepareLinksForDownload(List<GalerieEntity> galerieEntities) {
        sizeGalerieList = galerieEntities.size();
        List<BaseDownloadTask> tasks = new ArrayList<>();
        String fileRootName = getPathFilesRoot();
        for (GalerieEntity entity : galerieEntities) {
            String url = entity.getImageUrl();
            String imageName = fileRootName + File.separator + getFileNameFromUrl(url);
            tasks.add(FileDownloader.getImpl().create(url).setPath(imageName).setTag(entity.getId()));
            addGaleryUpdateUtil(entity.getId(), imageName);
        }
        return tasks;
    }

    @NonNull
    private String getPathFilesRoot() {
        String fileRootName = getFilesDir().getAbsolutePath() + File.separator + ROOT_GALERY_FOLDER_NAME;
        File fileRoot = new File(fileRootName);
        if (!fileRoot.exists()) {
            try {
                fileRoot.mkdir();
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return fileRootName;
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
        if (taskList.size() > 0) {
            if (queueSet != null) {
                queueSet.reuseAndStart();
            } else {
                queueSet = new FileDownloadQueueSet(getDownloaderListener());
                queueSet.disableCallbackProgressTimes();
                queueSet.downloadSequentially(taskList);
                queueSet.start();
            }
        } else {
            syncBinding.pbEntities.setProgress(100);
            notifyDownloadEnds();
        }

    }

    private void updateUIWithProgress() {
        final int progress = getDownloadProgress() + (int) entityPercent;
        syncBinding.pbEntities.setProgress(progress);
        if (sizeGalerieList == progressGalery) {
            disposeAllDisposable();
            updateAllGaleryDownloaded();
        }
    }

    private int getDownloadProgress() {
        progressGalery++;
        final int totalProgress = sizeGalerieList;
        return (int) (((double) progressGalery / (double) totalProgress) * 50);
    }

    private void addGaleryUpdateUtil(Object uuid, String targetFilePath) {
        syncViewModel.addGaleryUpdateUtil(uuid.toString(), targetFilePath);
    }

    private void updateAllGaleryDownloaded() {
        entityDisposable = syncViewModel.updateGaleryUpdateUtilList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::notifyDownloadEnds, throwable -> onDownloadError(ThrowableUtil.getMsgFromThrowable(throwable)));
        compositeDisposable.add(entityDisposable);
    }


    public void onDownloadError(String message) {
        Timber.e("Error on index ===> %s, with message ===> %s", entityProgress, message);
        AlertUtils.showCFErrorNotificationWithAction(this, "Ooop!!", message, (dialog, which) -> {
            dialog.dismiss();
        }, "Ok, lo entiendo");
        syncBinding.fbDownload.setEnabled(true);
        changeProgressColor(PROGRESS_ERROR);
    }

    private void changeProgressColor(int type) {
        switch (type) {
            case 2:
                syncBinding.pbEntities.setProgressTintList(ColorStateList.valueOf(Color.RED));
                break;
            case 4:
                syncBinding.pbEntities.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#009689")));
                break;
        }
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
        editor.putString(PREF_DOWNLOAD_LEVEL, LEVEL_ENTITY);
        editor.putInt(PREF_ENTITY_DOWNLOAD_INDEX, 0);
        editor.putString(PREF_LAST_DOWNLOAD_DATE, currentDateToSync);
        editor.apply();
        entityProgress = 0;
        uniqueDownloadErrorAlert = false;
        progressGalery = 0;
        sizeGalerieList = 0;
        galerySpaceInMb = 0;
        downloadImages = true;
        currentDateToSync = "";
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
                updateUIWithProgress();
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                syncBinding.fbDownload.setTag(LEVEL_GALERY_PAUSED);
                if (!uniqueDownloadErrorAlert) {
                    uniqueDownloadErrorAlert = true;
                    onDownloadError(ThrowableUtil.getMsgFromThrowable(e));
                }
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
                changeProgressColor(PROGRESS_SUCCESS);
                if (updateIsNeeded) {
                    startSyncOnLevel(entityProgress);
                }
            } else if (syncBinding.fbDownload.getTag().equals(LEVEL_GALERY)) {
                prepareForDownload();
                changeProgressColor(PROGRESS_SUCCESS);
            } else if (syncBinding.fbDownload.getTag().equals(LEVEL_GALERY_PAUSED)) {
                progressGalery = 0;
                uniqueDownloadErrorAlert = false;
                syncBinding.fbDownload.setEnabled(false);
                changeProgressColor(PROGRESS_SUCCESS);
                queueSet.reuseAndStart();
            }
        } else {
            syncBinding.fbDownload.setEnabled(true);
            AlertUtils.showCFErrorNotificationWithAction(this, "Ooop!!",
                    Constants.CONNEXION_ERROR_MSG, (dialog, which) -> {
                        dialog.dismiss();
                    },"Ok, lo entiendo");
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
