package com.infinitum.bookingqba.view.interaction;

import com.infinitum.bookingqba.model.remote.pojo.User;

public interface LoginInteraction {

    void onLogin(User user);

    void showNotificationToUpdate(String msg);

    void showGroupMenuProfile(boolean show);

}
