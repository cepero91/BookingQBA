package com.infinitum.bookingqba.model.repository.province;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.ProvinceEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.Province;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

public class ProvinceRepoImpl implements ProvinceRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public ProvinceRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }


    /**
     * Prepara la peticion al API
     * @return
     */
    private Single<List<Province>> fetchProvinces() {
        return retrofit.create(ApiInterface.class).getProvinces();
    }

    /**
     * Tranforma entidad JSON en entidad de Base de Datos
     * @param gsonList
     * @return
     */
    private List<ProvinceEntity> parseGsonToEntity(List<Province> gsonList) {
        ArrayList<ProvinceEntity> listEntity = new ArrayList<>();
        ProvinceEntity entity;
        for (Province item : gsonList) {
            entity = new ProvinceEntity(item.getId(), item.getNombre());
            listEntity.add(entity);
        }
        return listEntity;
    }


    /**
     * Ejecuta la peticion y tranforma los datos
     * @return
     */
    @Override
    public Single<List<ProvinceEntity>> fetchRemoteAndTransform() {
        return fetchProvinces().flatMap((Function<List<Province>, SingleSource<? extends List<ProvinceEntity>>>) provinces -> {
            ArrayList<ProvinceEntity> listEntity = new ArrayList<>(parseGsonToEntity(provinces));
            return Single.just(listEntity);
        }).subscribeOn(Schedulers.io());
    }


    /**
     * Inserta una coleccion de provincias
     * @param provinceEntityList
     * @return
     */
    @Override
    public Completable insertProvinces(List<ProvinceEntity> provinceEntityList) {
        return Completable.fromAction(() -> qbaDao.upsertProvince(provinceEntityList)).subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<Resource<List<ProvinceEntity>>> getAllProvinces() {
        return qbaDao.getAllProvinces().map(Resource::success).onErrorReturn(Resource::error).subscribeOn(Schedulers.io());
    }
}
