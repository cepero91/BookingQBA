package com.infinitum.bookingqba.model.repository.user;

import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.Login;
import com.infinitum.bookingqba.model.remote.pojo.User;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Retrofit;

public class UserRepoImp implements UserRepository{

    private Retrofit retrofit;

    @Inject
    public UserRepoImp(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    @Override
    public Call<User> login(String username, String password) {
        return retrofit.create(ApiInterface.class).login(username, password);
    }

}
