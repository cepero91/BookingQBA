package com.infinitum.bookingqba.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.widget.Toast;

import com.infinitum.bookingqba.model.remote.Oauth;
import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.model.repository.user.UserRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
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

}
