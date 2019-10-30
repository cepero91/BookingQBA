package com.infinitum.bookingqba.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.remote.Oauth;
import com.infinitum.bookingqba.model.remote.ReservationType;
import com.infinitum.bookingqba.model.remote.pojo.BookRequestInfo;
import com.infinitum.bookingqba.model.remote.pojo.Reservation;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.model.remote.pojo.UserEsentialData;
import com.infinitum.bookingqba.model.repository.user.UserRepository;
import com.infinitum.bookingqba.util.DateUtils;
import com.infinitum.bookingqba.view.adapters.items.reservation.BookInfoItem;
import com.infinitum.bookingqba.view.adapters.items.reservation.ReservationItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class UserViewModel extends ViewModel {

    private UserRepository userRepository;

    @Inject
    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Single<Resource<ResponseResult>> register(Map<String, String> map) {
        return userRepository.register(map);
    }

    public Single<Response<User>> userLogin(Oauth oauth) {
        return userRepository.login(oauth);
    }

    public Single<User> fakeLogin(String username, String password) {
        return userRepository.fakeLogin(username, password);
    }

    public Single<Boolean> checkIfRentExists(List<String> uuids) {
        return userRepository.checkIfRentOwnerExist(uuids).subscribeOn(Schedulers.io());
    }

    public Single<Response<User>> login(Map<String, String> map) {
        return userRepository.login(map);
    }

    public Single<Resource<ResponseResult>> activate(Map<String, String> map) {
        return userRepository.activationUser(map);
    }

    public Single<Resource<ResponseResult>> resendCode(Map<String, String> map) {
        return userRepository.resendActivationUser(map);
    }


    public Single<Resource<ResponseResult>> acceptReservation(String token, String uuid) {
        return userRepository.acceptReservation(token, uuid);
    }

    public Single<Resource<ResponseResult>> deniedReservation(String token, String uuid) {
        return userRepository.deniedReservationByHost(token, uuid);
    }

    public Single<Resource<ResponseResult>> deniedReservationByUser(String token, String uuid) {
        return userRepository.deniedReservationByUser(token, uuid);
    }

    public Single<Resource<List<ReservationItem>>> getPendingReservationByUser(String token, String userId) {
        return userRepository.allPendingReservationByUser(token, userId)
                .map(this::transformToReservationItem)
                .onErrorReturn(Resource::error);
    }

    public Single<Resource<List<ReservationItem>>> getCheckedReservationByUser(String token, String userId) {
        return userRepository.allCheckedReservationByUser(token, userId)
                .map(this::transformToReservationItem)
                .onErrorReturn(Resource::error);
    }

    public Single<Resource<List<ReservationItem>>> getAcceptedReservationByUser(String token, String userId) {
        return userRepository.allAcceptedReservationByUser(token, userId)
                .map(this::transformToReservationItem)
                .onErrorReturn(Resource::error);
    }

    public Single<Resource<List<ReservationItem>>> getReservationUserByType(String token, String userId, ReservationType type) {
        return userRepository.allReservationUserByType(token, userId, type)
                .map(this::transformToReservationItem)
                .onErrorReturn(Resource::error);
    }

    public Single<Resource<List<BookInfoItem>>> getUserBookRequestInfo(String token, String userId) {
        return userRepository.allUserBookRequestInfo(token, userId).map(this::transformBookRequestToBookItem).map(Resource::success).onErrorReturn(Resource::error);
    }

    private List<BookInfoItem> transformBookRequestToBookItem(Resource<List<Reservation>> listResource) {
        List<BookInfoItem> bookInfoItems = new ArrayList<>();
        if (listResource.data != null) {
            BookInfoItem infoItem;
            for (Reservation requestInfo : listResource.data) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date startDate = null;
                Date endDate = null;
                Date created = null;
                try {
                    startDate = simpleDateFormat.parse(requestInfo.getStartDate());
                    endDate = simpleDateFormat.parse(requestInfo.getEndDate());
                    created = simpleDateFormat.parse(requestInfo.getCreated());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                infoItem = new BookInfoItem(requestInfo.getId());
                infoItem.setRentId(requestInfo.getRentId());
                infoItem.setUserId(requestInfo.getUserId());
                infoItem.setUserName(requestInfo.getUsername());
                infoItem.setRentName(requestInfo.getRentName());
                infoItem.setStartDate(startDate);
                infoItem.setEndDate(endDate);
                infoItem.setCreated(created);
                infoItem.setRawState(requestInfo.getState());
                infoItem.setAditional(requestInfo.getAditional());
                infoItem.setCapability(requestInfo.getCapability());
                infoItem.setHostCount(requestInfo.getHostCount());
                bookInfoItems.add(infoItem);
            }
        }
        return bookInfoItems;
    }

    public Single<Resource<UserEsentialData>> getUserBookRequestData(String token, String userBookOwner, String rentId, String reservationId) {
        return userRepository.userBookEsentialData(token, userBookOwner, rentId, reservationId);
    }

    private Resource<List<ReservationItem>> transformToReservationItem(Resource<List<Reservation>> listResource) {
        List<ReservationItem> resultItems = new ArrayList<>();
        if (listResource.data != null) {
            ReservationItem item;
            for (Reservation reservation : listResource.data) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date startDate = null;
                Date endDate = null;
                Date created = null;
                try {
                    startDate = simpleDateFormat.parse(reservation.getStartDate());
                    endDate = simpleDateFormat.parse(reservation.getEndDate());
                    created = simpleDateFormat.parse(reservation.getCreated());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                item = new ReservationItem();
                item.setId(reservation.getId());
                item.setUserName(reservation.getUsername());
                item.setRentId(reservation.getRentId());
                item.setRentName(reservation.getRentName());
                item.setUserId(reservation.getUserId());
                item.setStartDate(startDate);
                item.setEndDate(endDate);
                item.setCreated(created);
                item.setUserAvatar(reservation.getAvatar());
                item.setCapability(reservation.getCapability());
                item.setHostCount(reservation.getHostCount());
                item.setAditionalNote(reservation.getAditional());
                resultItems.add(item);
            }
            return Resource.success(resultItems);
        } else {
            return Resource.error(listResource.message);
        }
    }

}
