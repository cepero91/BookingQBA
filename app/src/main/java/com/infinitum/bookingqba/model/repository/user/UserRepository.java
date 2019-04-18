package com.infinitum.bookingqba.model.repository.user;



import com.infinitum.bookingqba.model.remote.Login;
import com.infinitum.bookingqba.model.remote.pojo.User;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;

public interface UserRepository {

    Call<User> login(String username, String password);

    Single<User> fakeLogin(String username, String password);

    Single<Boolean> checkIfRentOwnerExist(List<String> uuids);
}
