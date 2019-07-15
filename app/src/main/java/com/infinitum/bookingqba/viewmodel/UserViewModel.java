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
import okhttp3.MediaType;
import okhttp3.ResponseBody;
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
//        User user = new User("qwertyuiop", "cepero91");
        return userRepository.login(map);
//        return Single.just(Response.success(201, user));
    }

    public Single<Resource<ResponseResult>> register(Map<String, String> map){
        return userRepository.register(map);
//        return Single.just(Resource.success(new ResponseResult(200,"Un codigo de activacion ha sido enviado a su correo")));
    }

    public Single<Resource<ResponseResult>> activate(Map<String, String> map){
        return userRepository.activationUser(map);
//        return Single.just(Resource.success(new ResponseResult(200,"Usuario activado con exito")));
    }

    public Single<Resource<ResponseResult>> resendCode(Map<String, String> map){
        return userRepository.resendActivationUser(map);
    }

}
