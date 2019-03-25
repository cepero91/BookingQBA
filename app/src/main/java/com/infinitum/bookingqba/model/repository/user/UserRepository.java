package com.infinitum.bookingqba.model.repository.user;



import com.infinitum.bookingqba.model.remote.Login;
import com.infinitum.bookingqba.model.remote.pojo.User;

import retrofit2.Call;

public interface UserRepository {

    Call<User> login(String username, String password);
}
