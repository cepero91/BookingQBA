package com.infinitum.bookingqba.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.DatabaseUpdateEntity;
import com.infinitum.bookingqba.model.remote.pojo.RentAnalitics;
import com.infinitum.bookingqba.model.repository.dbcommonsop.DBCommonOperationRepository;
import com.infinitum.bookingqba.model.repository.rentanalitics.RentAnaliticsRepository;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class InformationViewModel extends ViewModel {

    private DBCommonOperationRepository dbCommonOperationRepository;

    @Inject
    public InformationViewModel(DBCommonOperationRepository dbCommonOperationRepository) {
        this.dbCommonOperationRepository = dbCommonOperationRepository;
    }

    public Flowable<String> lastLocalDatabaseUpdate(){
        return dbCommonOperationRepository.lastDatabaseUpdateLocal()
                .subscribeOn(Schedulers.io())
                .map(databaseUpdateEntityResource -> {
                    if(databaseUpdateEntityResource.data!=null){
                        TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(new Locale("es", "ES")).build();
                        String dateRelative = TimeAgo.using(databaseUpdateEntityResource.data.getLastDateUpdateEntity().getTime(), messages);
                        return dateRelative;
                    }
                    return "No disponible";
                });
    }

    public Flowable<String> lastRemoteDatabaseUpdate(){
        return dbCommonOperationRepository.lastDatabaseUpdateRemote()
                .subscribeOn(Schedulers.io())
                .map(databaseUpdateEntityResource -> {
                    if(databaseUpdateEntityResource.data!=null){
                        TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(new Locale("es", "ES")).build();
                        String dateRelative = TimeAgo.using(databaseUpdateEntityResource.data.getLastDateUpdateEntity().getTime(), messages);
                        return dateRelative;
                    }
                    return "No disponible";
                });
    }
}
