package com.infinitum.bookingqba.model.repository.user;



import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.remote.Oauth;
import com.infinitum.bookingqba.model.remote.ReservationType;
import com.infinitum.bookingqba.model.remote.pojo.BookRequestInfo;
import com.infinitum.bookingqba.model.remote.pojo.Reservation;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.model.remote.pojo.UserEsentialData;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import retrofit2.Response;

public interface UserRepository {

    Single<Resource<ResponseResult>> register(Map<String, String> map);

    Single<Response<User>> login(Oauth oauth);

    Single<User> fakeLogin(String username, String password);

    Single<Boolean> checkIfRentOwnerExist(List<String> uuids);

    Single<Response<User>> login(Map<String, String> map);

    Single<Resource<ResponseResult>> activationUser(Map<String, String> map);

    Single<Resource<ResponseResult>> resendActivationUser(Map<String, String> map);

    Single<Resource<List<Reservation>>> allPendingReservationByUser(String token, String userid);

    Single<Resource<List<Reservation>>> allCheckedReservationByUser(String token, String userid);

    Single<Resource<List<Reservation>>> allReservationUserByType(String token, String userid, ReservationType type);

    Single<Resource<List<Reservation>>> allUserBookRequestInfo(String token, String userid);

    Single<Resource<List<Reservation>>> allAcceptedReservationByUser(String token, String userid);

    Single<Resource<ResponseResult>> acceptReservation(String token, String resUuid);

    Single<Resource<ResponseResult>> deniedReservationByHost(String token, String resUuid);

    Single<Resource<ResponseResult>> deniedReservationByUser(String token, String resUuid);

    Single<Resource<UserEsentialData>> userBookEsentialData(String token, String userBookOwnerId, String rentId, String reservationId);

}
