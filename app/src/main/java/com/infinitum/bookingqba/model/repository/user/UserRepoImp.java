package com.infinitum.bookingqba.model.repository.user;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.Oauth;
import com.infinitum.bookingqba.model.remote.pojo.BookRequestInfo;
import com.infinitum.bookingqba.model.remote.pojo.Reservation;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.model.remote.pojo.UserEsentialData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
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
                .login(oauth.getParam1(), oauth.getParam2(), oauth.getParam3())
                .subscribeOn(Schedulers.io());
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

    @Override
    public Single<Response<User>> login(Map<String, String> map) {
        return retrofit.create(ApiInterface.class)
                .login(map)
                .subscribeOn(Schedulers.io());

    }

    @Override
    public Single<Resource<ResponseResult>> register(Map<String, String> map) {
        return retrofit.create(ApiInterface.class)
                .register(map)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Resource<ResponseResult>> activationUser(Map<String, String> map) {
        return retrofit.create(ApiInterface.class)
                .activeUser(map)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Resource<ResponseResult>> resendActivationUser(Map<String, String> map) {
        return retrofit.create(ApiInterface.class)
                .resendActivationCode(map)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Resource<List<Reservation>>> allPendingReservationByUser(String token, String userid) {
        return retrofit.create(ApiInterface.class)
                .pendingReservation(token, userid)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Resource<List<BookRequestInfo>>> allUserBookRequestInfo(String token, String userid) {
        return retrofit.create(ApiInterface.class)
                .bookRequestInfo(token, userid)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Resource<List<Reservation>>> allAcceptedReservationByUser(String token, String userid) {
        return retrofit.create(ApiInterface.class)
                .acceptedReservation(token, userid)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Resource<ResponseResult>> acceptReservation(String token ,String uuid) {
        return retrofit.create(ApiInterface.class)
                .acceptReservation(token, uuid)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Resource<ResponseResult>> deniedReservationByHost(String token , String uuid) {
        return retrofit.create(ApiInterface.class)
                .deniedReservation(token, uuid)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Resource<ResponseResult>> deniedReservationByUser(String token , String uuid) {
        return retrofit.create(ApiInterface.class)
                .deniedReservationByUser(token, uuid)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Resource<UserEsentialData>> userBookEsentialData(String token, String userBookOwnerId, String rentId) {
        return  retrofit.create(ApiInterface.class)
                .userBookOwnerData(token, userBookOwnerId,rentId)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

}
