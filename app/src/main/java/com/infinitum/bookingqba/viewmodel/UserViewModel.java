package com.infinitum.bookingqba.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.widget.Toast;

import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.model.repository.user.UserRepository;

import javax.inject.Inject;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Callback;

public class UserViewModel extends ViewModel{

    private UserRepository userRepository;

    @Inject
    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Call<User> userLogin(String username, String password){
        return userRepository.login(username,password);
    }

}
