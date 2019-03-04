package com.infinitum.bookingqba.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
//import com.infinitum.bookingqba.model.local.synchelper.ProvinceSync;
import com.infinitum.bookingqba.model.local.entity.DrawTypeEntity;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.entity.MunicipalityEntity;
import com.infinitum.bookingqba.model.local.entity.PoiEntity;
import com.infinitum.bookingqba.model.local.entity.PoiTypeEntity;
import com.infinitum.bookingqba.model.local.entity.ProvinceEntity;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.CombinedRequest;
import com.infinitum.bookingqba.model.remote.pojo.Amenities;
import com.infinitum.bookingqba.model.remote.pojo.Galerie;
import com.infinitum.bookingqba.model.remote.pojo.Municipality;
import com.infinitum.bookingqba.model.remote.pojo.Rent;
import com.infinitum.bookingqba.model.repository.amenities.AmenitiesRepository;
import com.infinitum.bookingqba.model.repository.drawtype.DrawTypeRepository;
import com.infinitum.bookingqba.model.repository.galerie.GalerieRepository;
import com.infinitum.bookingqba.model.repository.municipality.MunicipalityRepository;
import com.infinitum.bookingqba.model.repository.poi.PoiRepository;
import com.infinitum.bookingqba.model.repository.poitype.PoiTypeRepository;
import com.infinitum.bookingqba.model.repository.province.ProvinceRepository;
import com.infinitum.bookingqba.model.repository.referencezone.ReferenceZoneRepository;
import com.infinitum.bookingqba.model.repository.rent.RentRepository;
import com.infinitum.bookingqba.model.repository.rentmode.RentModeRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;


import static com.infinitum.bookingqba.util.Constants.BASIC_URL_API;


public class MainActivity extends AppCompatActivity {

    @Inject
    Retrofit retrofit;

    @Inject
    BookingQBADao dao;

    @Inject
    ProvinceRepository provinceRepository;

    @Inject
    MunicipalityRepository municipalityRepository;

    @Inject
    AmenitiesRepository amenitiesRepository;

    @Inject
    PoiTypeRepository poiTypeRepository;

    @Inject
    PoiRepository poiRepository;

    @Inject
    RentRepository rentRepository;

    @Inject
    RentModeRepository rentModeRepository;

    @Inject
    ReferenceZoneRepository referenceZoneRepository;

    @Inject
    DrawTypeRepository drawTypeRepository;

    @Inject
    GalerieRepository galerieRepository;

    String dateTest = "2019-02-20 20:00:26";

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    private ArrayList<Galerie> galerieList;

    private ArrayList<Observable<ResponseBody>> galerieListZip;

    private ProgressBar progressBar;

    private TextView textView;

    private Button mButton;

    private String msg = "";

    private LinearLayout linearLayout;

    private boolean isSync = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidInjection.inject(this);

        compositeDisposable = new CompositeDisposable();

        linearLayout = findViewById(R.id.linearLayout);

        textView = findViewById(R.id.tv_sample);
        mButton = findViewById(R.id.button);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);

        galerieList = new ArrayList<>();
        galerieListZip = new ArrayList<>();

        progressBar.setIndeterminate(false);
        progressBar.setMax(20);

        mButton.setOnClickListener(v -> {
            if(!isSync) {
                isSync = true;
                msg += "Sincronicing.....\n";
                textView.setText(msg);
                syncProvinces();
            }
        });

    }

    private void syncProvinces() {
        disposable = provinceRepository.fetchRemoteAndTransform()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<ProvinceEntity>>() {
                    @Override
                    public void onSuccess(List<ProvinceEntity> provinceEntityList) {
                        msg += "provinces fetch and transform success, list size: " + provinceEntityList.size() + "\n";
                        textView.setText(msg);
                        progressBar.setProgress(1);
                        addProvinces(provinceEntityList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "error fetch and transform, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void addProvinces(List<ProvinceEntity> provinceEntityList) {
        disposable = provinceRepository.insertProvinces(provinceEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        msg += "provinces insertion complete, congratulations\n";
                        textView.setText(msg);
                        progressBar.setProgress(2);
                        syncMunicipalities();
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "provinces error saving on database, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncMunicipalities() {
        municipalityRepository.fetchRemoteAndTransform()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<MunicipalityEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<MunicipalityEntity> municipalityEntities) {
                        msg += "municipalities fetch and transform success, list size: " + municipalityEntities.size() + "\n";
                        textView.setText(msg);
                        progressBar.setProgress(3);
                        addMunicipalities(municipalityEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "error fetch and transform, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });

    }

    private void addMunicipalities(List<MunicipalityEntity> municipalityEntityList) {
        disposable = municipalityRepository.insertMunicipalities(municipalityEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        msg += "municipalities insertion complete, congratulations\n";
                        textView.setText(msg);
                        progressBar.setProgress(4);
                        syncAmenities();
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "municipalities error saving on database, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncAmenities() {
        disposable = amenitiesRepository.fetchRemoteAndTransform()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<AmenitiesEntity>>() {
                    @Override
                    public void onSuccess(List<AmenitiesEntity> amenitiesEntities) {
                        msg += "amenities fetch and transform success, list size: " + amenitiesEntities.size() + "\n";
                        textView.setText(msg);
                        progressBar.setProgress(5);
                        saveAmenities(amenitiesEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "amenities error fetch and transform, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);

    }

    private void saveAmenities(List<AmenitiesEntity> amenitiesEntityList) {
        disposable = amenitiesRepository.insertAmenities(amenitiesEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        msg += "amenities insertion complete, congratulations\n";
                        textView.setText(msg);
                        progressBar.setProgress(6);
                        syncPoiTypes();
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "amenities error saving on database, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncPoiTypes() {
        disposable = poiTypeRepository.fetchRemoteAndTransform()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<PoiTypeEntity>>() {
                    @Override
                    public void onSuccess(List<PoiTypeEntity> poiTypeEntities) {
                        msg += "poiTypes fetch and transform success, list size: " + poiTypeEntities.size() + "\n";
                        textView.setText(msg);
                        progressBar.setProgress(7);
                        savePoiTypes(poiTypeEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "poiTypes error fetch and transform, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);

    }

    private void savePoiTypes(List<PoiTypeEntity> poiTypeEntityList) {
        disposable = poiTypeRepository.insertPoiTypes(poiTypeEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        msg += "poiTypes insertion complete, congratulations\n";
                        textView.setText(msg);
                        progressBar.setProgress(8);
                        syncPois();
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "poiTypes error saving on database, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncPois() {
        disposable = poiRepository.fetchRemoteAndTransform()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<PoiEntity>>() {
                    @Override
                    public void onSuccess(List<PoiEntity> poiEntities) {
                        msg += "poi fetch and transform success, list size: " + poiEntities.size() + "\n";
                        textView.setText(msg);
                        progressBar.setProgress(9);
                        savePois(poiEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "poi error fetch and transform, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);

    }

    private void savePois(List<PoiEntity> poiEntityList) {
        disposable = poiRepository.insertPois(poiEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        msg += "poi insertion complete, congratulations\n";
                        textView.setText(msg);
                        progressBar.setProgress(10);
                        syncRentsMode();
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "poi error saving on database, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncRentsMode() {
        disposable = rentModeRepository.fetchRemoteAndTransform()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<RentModeEntity>>() {
                    @Override
                    public void onSuccess(List<RentModeEntity> rentModeEntities) {
                        msg += "rentsMode fetch and transform success, list size: " + rentModeEntities.size() + "\n";
                        textView.setText(msg);
                        progressBar.setProgress(11);
                        saveRentsMode(rentModeEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "rentsMode error fetch and transform, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);

    }

    private void saveRentsMode(List<RentModeEntity> rentModeEntityList) {
        disposable = rentModeRepository.insertRentsMode(rentModeEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        msg += "rentsMode insertion complete, congratulations\n";
                        textView.setText(msg);
                        progressBar.setProgress(12);
                        syncReferencesZone();
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "rentsMode error saving on database, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncReferencesZone() {
        disposable = referenceZoneRepository.fetchRemoteAndTransform()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<ReferenceZoneEntity>>() {
                    @Override
                    public void onSuccess(List<ReferenceZoneEntity> referenceZoneEntities) {
                        msg += "referenceZone fetch and transform success, list size: " + referenceZoneEntities.size() + "\n";
                        textView.setText(msg);
                        progressBar.setProgress(13);
                        saveReferencesZone(referenceZoneEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "referenceZone error fetch and transform, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);

    }

    private void saveReferencesZone(List<ReferenceZoneEntity> referenceZoneEntityList) {
        disposable = referenceZoneRepository.insertReferencesMode(referenceZoneEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        msg += "referenceZone insertion complete, congratulations\n";
                        textView.setText(msg);
                        progressBar.setProgress(14);
                        syncRents();
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "referenceZone error saving on database, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncRents() {
        disposable = rentRepository.fetchRemoteAndTransform()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<RentEntity>>() {
                    @Override
                    public void onSuccess(List<RentEntity> rentEntities) {
                        msg += "rents fetch and transform success, list size: " + rentEntities.size() + "\n";
                        textView.setText(msg);
                        progressBar.setProgress(15);
                        saveRents(rentEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "rents error fetch and transform, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);

    }

    private void saveRents(List<RentEntity> rentEntityList) {
        disposable = rentRepository.insertRents(rentEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        msg += "rent insertion complete, congratulations\n";
                        textView.setText(msg);
                        progressBar.setProgress(16);
                        syncDrawsType();
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "rent error saving on database, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncDrawsType() {
        disposable = drawTypeRepository.fetchRemoteAndTransform()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<DrawTypeEntity>>() {
                    @Override
                    public void onSuccess(List<DrawTypeEntity> drawTypeEntities) {
                        msg += "drawType fetch and transform success, list size: " + drawTypeEntities.size() + "\n";
                        textView.setText(msg);
                        progressBar.setProgress(17);
                        saveDrawsType(drawTypeEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "drawType error fetch and transform, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);

    }

    private void saveDrawsType(List<DrawTypeEntity> drawTypeEntitiesList) {
        disposable = drawTypeRepository.insertDrawType(drawTypeEntitiesList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        msg += "drawType insertion complete, congratulations\n";
                        textView.setText(msg);
                        progressBar.setProgress(18);
                        syncGaleries();
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "drawType error saving on database, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void syncGaleries() {
        disposable = galerieRepository.fetchRemoteAndTransform()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<GalerieEntity>>() {
                    @Override
                    public void onSuccess(List<GalerieEntity> galerieEntities) {
                        msg += "galerie fetch and transform success, list size: " + galerieEntities.size() + "\n";
                        textView.setText(msg);
                        progressBar.setProgress(19);
                        saveGaleries(galerieEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "galerie error fetch and transform, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void saveGaleries(List<GalerieEntity> galerieEntityList) {
        disposable = galerieRepository.insertGalerie(galerieEntityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        msg += "galerie insertion complete, congratulations\n";
                        textView.setText(msg);
                        progressBar.setProgress(20);
                        isSync = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg += "galerie error saving on database, error: " + e.getMessage() + "\n";
                        textView.setText(msg);
                    }
                });
        compositeDisposable.add(disposable);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }


    /**
     * PRIMER METODO PROBADO PERO CREA VARIOS HILOS SIMULTANEOS PARA LAS PETICIONES
     * Y EL PROGRESO NO ES MUY FIABLE
     *
     * @param galerieList
     */
    private void putFilesToDownload(ArrayList<Galerie> galerieList) {
        Call<ResponseBody> imageCalls;
        for (int i = 0; i < galerieList.size(); i++) {
            final int index = i;
            imageCalls = retrofit.create(ApiInterface.class).getImage(BASIC_URL_API + "/" + galerieList.get(i).getImagen());
            imageCalls.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        writeResponseToDisk(response.body(), "galerie" + index);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Timber.i("onError " + t.getMessage());
                }
            });
        }
    }


    /**
     * OTRA PRUEBA QUE SE HIZO PARA LAS PETICIONES SECUENCIALES PERO SIN MUCHO RESULTADO
     *
     * @param galerieList
     */
    private void putFilesToDownload2(ArrayList<Galerie> galerieList) {
        Observable<ResponseBody> imageCalls;
        for (int i = 0; i < galerieList.size(); i++) {
            final int index = i;
            final int p = i + 1;
            imageCalls = retrofit.create(ApiInterface.class).getImageThread(BASIC_URL_API + "/" + galerieList.get(i).getImagen());
            imageCalls.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(v -> {
                        Timber.d(String.valueOf(v.contentLength()));
                        progressBar.setProgress(p);
                    });
        }
    }

    /**
     * METODO QUE PREPARA LOS REQUEST OBSERVABLES
     *
     * @param galerieList
     */
    private void putFilesToZip(ArrayList<Galerie> galerieList) {
        Observable<ResponseBody> imageCalls;
        for (int i = 0; i < galerieList.size(); i++) {
            final int index = i;
            imageCalls = retrofit.create(ApiInterface.class).getImageZip(BASIC_URL_API + "/" + galerieList.get(i).getImagen());
            galerieListZip.add(imageCalls);
        }
        downloadAndSave(galerieListZip);
    }

    /**
     * ESTE ES UNO DE LOS METODOS PROBADOS
     * EL METODO APPLY SE EJECUTA CUANDO RECIBE TODAS LAS PETICIONES RESUELTAS
     * Y EL ACCEPT ES A CONSECUENCIA DE LO RETORNADO EN EL PROPIO METODO APPLY
     *
     * @param listZip
     */
    private void downloadAndSave(ArrayList<Observable<ResponseBody>> listZip) {

        disposable = Observable.zip(listZip, this::getCollectionResponse)
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Timber.d("Entro");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        compositeDisposable.add(disposable);
    }

    private Object getCollectionResponse(Object[] object) {
        long size = 0;
        Map<Long, Object[]> map = new HashMap<>();
        for (int i = 0; i < object.length; i++) {
            size += ((ResponseBody) object[i]).contentLength();
        }
        map.put(size, object);
        return map;
    }


    /**
     * METODO PARA ESCRIBIR FICHERO EN ALM INTERNO
     *
     * @param body
     * @param fileName
     * @return
     */
    private boolean writeResponseToDisk(ResponseBody body, String fileName) {
        try {

            String destineFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/destine";
            File folder = new File(destineFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }
            File image = new File(destineFolder + "/" + fileName + ".jpeg");
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {

                byte[] fileReader = new byte[4084];
                long fileSize = body.contentLength();
                long fileSizeDownload = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(image);

                while (true) {

                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownload += read;

                    Timber.d("File downloaded " + fileSizeDownload + " of " + fileSize);
                }

                outputStream.flush();
                return true;

            } catch (Exception e) {
                return false;
            } finally {

                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }


        } catch (Exception e) {
            return false;
        }
    }
}
