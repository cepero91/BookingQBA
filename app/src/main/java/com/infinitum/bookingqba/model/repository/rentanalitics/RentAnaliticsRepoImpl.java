package com.infinitum.bookingqba.model.repository.rentanalitics;

import android.arch.persistence.db.SimpleSQLiteQuery;

import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.AnaliticsGroup;
import com.infinitum.bookingqba.model.remote.pojo.RentAnalitics;
import com.infinitum.bookingqba.util.StringUtils;
import com.infinitum.bookingqba.view.adapters.items.spinneritem.CommonSpinnerItem;
import com.infinitum.bookingqba.view.adapters.items.spinneritem.CommonSpinnerList;
import com.infinitum.bookingqba.view.profile.DataGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class RentAnaliticsRepoImpl implements RentAnaliticsRepository{

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public RentAnaliticsRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    @Override
    public Single<List<RentAnalitics>> getRentAnalitics(List<String> uuids) {
        return Single.just(DataGenerator.getRentAnalitic()).subscribeOn(Schedulers.io()).delay(2000, TimeUnit.MILLISECONDS);
    }

    @Override
    public Single<AnaliticsGroup> rentAnalitics(String uuid) {
        return retrofit.create(ApiInterface.class)
                .rentAnalitics(uuid)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<CommonSpinnerList> rentByUuidCommaSeparate(List<String> uuids) {
        String comma = StringUtils.convertListToCommaSeparated(uuids);
        String testSqlStatement = "SELECT Rent.id,Rent.name,Rent.address,Rent.price, Rent.rating, Rent.latitude, Rent.longitude, Rent.rentMode, Rent.isWished FROM Rent " +
                "LEFT JOIN Galerie ON Galerie.id = (SELECT Galerie.id FROM Galerie WHERE rent = Rent.id LIMIT 1) " +
                "WHERE Rent.id IN(%s)";
        SimpleSQLiteQuery simpleSQLiteQuery = new SimpleSQLiteQuery(String.format("SELECT * FROM Rent WHERE Rent.id IN(%s)",comma));
        return qbaDao.getRentByStringUuidCommaSeparate(simpleSQLiteQuery)
                .subscribeOn(Schedulers.io())
                .map(this::transformToSpinnerList);
    }

    @Override
    public Single<List<RentAndGalery>> rentByUuidList(List<String> uuids) {
        String comma = StringUtils.convertListToCommaSeparated(uuids);
        String testSqlStatement = "SELECT Rent.id,Rent.name,Rent.address,Rent.price, Rent.rating, Rent.latitude, Rent.longitude, Rent.rentMode, Rent.isWished FROM Rent " +
                "WHERE Rent.id IN(%s)";
        SimpleSQLiteQuery simpleSQLiteQuery = new SimpleSQLiteQuery(String.format(testSqlStatement,comma));
        return qbaDao.getRentByUuidList(simpleSQLiteQuery)
                .subscribeOn(Schedulers.io());
    }

    private CommonSpinnerList transformToSpinnerList(List<RentEntity> rentEntityList) {
        List<CommonSpinnerItem> commonSpinnerItemList = new ArrayList<>();
        for(RentEntity rentEntity: rentEntityList){
            commonSpinnerItemList.add(new CommonSpinnerItem(rentEntity.getId(),rentEntity.getName()));
        }
        return new CommonSpinnerList(commonSpinnerItemList);
    }

}
