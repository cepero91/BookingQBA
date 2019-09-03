package com.infinitum.bookingqba.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.remote.Oauth;
import com.infinitum.bookingqba.model.remote.pojo.Reservation;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.model.repository.user.UserRepository;
import com.infinitum.bookingqba.view.adapters.items.reservation.ReservationItem;

import java.util.ArrayList;
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


    public Single<Resource<ResponseResult>> acceptReservation(String token, String uuid){
        return userRepository.acceptReservation(token, uuid);
    }

    public Single<Resource<ResponseResult>> deniedReservation(String token, String uuid){
        return userRepository.deniedReservation(token, uuid);
    }

    public Single<Resource<List<ReservationItem>>> getPendingReservationByUser(String token, String userId){
        return userRepository.allPendingReservationByUser(token, userId)
                .map(this::transformToReservationItem)
                .onErrorReturn(Resource::error);
    }

    public Single<Resource<List<ReservationItem>>> getAcceptedReservationByUser(String token, String userId){
        return userRepository.allAcceptedReservationByUser(token, userId)
                .map(this::transformToReservationItem)
                .onErrorReturn(Resource::error);
    }

    private Resource<List<ReservationItem>> transformToReservationItem(Resource<List<Reservation>> listResource) {
        List<ReservationItem> resultItems = new ArrayList<>();
        if(listResource.data!=null){
            ReservationItem item;
            for(Reservation reservation: listResource.data){
                item = new ReservationItem();
                item.setId(reservation.getId());
                item.setUserName(reservation.getUsername());
                item.setStartDate(reservation.getStartDate());
                item.setEndDate(reservation.getEndDate());
                item.setUserAvatar(reservation.getAvatar());
                item.setCapability(String.valueOf(reservation.getCapability()));
                resultItems.add(item);
            }
            return Resource.success(resultItems);
        }else{
            return Resource.error(listResource.message);
        }
    }

}
