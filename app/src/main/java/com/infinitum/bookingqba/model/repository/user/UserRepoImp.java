package com.infinitum.bookingqba.model.repository.user;

import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.Login;
import com.infinitum.bookingqba.model.remote.pojo.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Retrofit;

public class UserRepoImp implements UserRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public UserRepoImp(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    @Override
    public Call<User> login(String username, String password) {
        return retrofit.create(ApiInterface.class).login(username, password);
    }

    //Fake Login
    @Override
    public Single<User> fakeLogin(String username, String password) {
        String[] rentsId = new String[]{
                "9a526e5f-d289-4470-a521-23de426158f3",
                "84ea7dfd-fd0a-4cfd-b022-38d3be1577be",
                "edadb3fd-c27a-46de-83ec-bb24f8a0d51c"
        };
        User user = new User(UUID.randomUUID().toString(), "Jose Manuel", "", Arrays.asList(rentsId));
        return Single.just(user).delay(2000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> checkIfRentOwnerExist(List<String> uuids) {
        return Single.fromCallable(() -> uuids.size() > 0 && qbaDao.existRents(uuids))
                .subscribeOn(Schedulers.io());
    }

}
