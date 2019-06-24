package com.infinitum.bookingqba.model.repository.user;



import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.remote.Oauth;
import com.infinitum.bookingqba.model.remote.pojo.User;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Response;

public interface UserRepository {

    Single<Response<User>> login(Oauth oauth);

    Single<User> fakeLogin(String username, String password);

    Single<Boolean> checkIfRentOwnerExist(List<String> uuids);

    Single<Response<User>> login(Map<String, String> map);

    Single<OperationResult> register(Map<String, String> map);

    Single<OperationResult> activationUser(Map<String, String> map);

}
