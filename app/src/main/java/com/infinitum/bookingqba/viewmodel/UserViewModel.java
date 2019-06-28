package com.infinitum.bookingqba.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.remote.Oauth;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.model.repository.user.UserRepository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class UserViewModel extends ViewModel{

    private UserRepository userRepository;

    @Inject
    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Single<Response<User>> userLogin(Oauth oauth){
        return userRepository.login(oauth);
    }

    public Single<User> fakeLogin(String username, String password){
        return userRepository.fakeLogin(username,password);
    }

    public Single<Boolean> checkIfRentExists(List<String> uuids){
        return userRepository.checkIfRentOwnerExist(uuids).subscribeOn(Schedulers.io());
    }

    public Single<Response<User>> login(Map<String, String> map){
        return userRepository.login(map);
    }

    public Single<Resource<ResponseResult>> register(Map<String, String> map){
        return userRepository.register(map);
    }

    public Single<Resource<ResponseResult>> activate(Map<String, String> map){
        return userRepository.activationUser(map);
    }

    public Single<Resource<ResponseResult>> resendCode(Map<String, String> map){
        return userRepository.resendActivationUser(map);
    }

}
