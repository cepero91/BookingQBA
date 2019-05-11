package com.infinitum.bookingqba.model.repository.user;

import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.Oauth;
import com.infinitum.bookingqba.model.remote.pojo.User;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;
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
    public Single<Response<User>> login(Oauth oauth) {
        return retrofit.create(ApiInterface.class)
                .login(oauth.getParam1(), oauth.getParam2(),oauth.getParam3())
                .subscribeOn(Schedulers.io())
                .delay(2000,TimeUnit.MILLISECONDS);
    }

    //Fake Oauth
    @Override
    public Single<User> fakeLogin(String username, String password) {
        String[] rentsId = new String[]{
                "46768615-e60a-455c-9959-09058d6de9d9",
                "c5f36c43-a70c-45e1-a79a-e2a8b414fab1"
        };
        User user = new User(UUID.randomUUID().toString(), "Jose Manuel");
        return Single.just(user).delay(2000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> checkIfRentOwnerExist(List<String> uuids) {
        return Single.fromCallable(() -> uuids.size() > 0 && qbaDao.existRents(uuids))
                .subscribeOn(Schedulers.io());
    }

}
