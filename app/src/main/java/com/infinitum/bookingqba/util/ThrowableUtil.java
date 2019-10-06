package com.infinitum.bookingqba.util;

import java.net.ConnectException;
import java.net.SocketException;

public class ThrowableUtil {
    public static String getMsgFromThrowable(Throwable throwable) {
        if (throwable instanceof ConnectException) {
            return Constants.CONNEXION_ERROR_MSG;
        } else if (throwable instanceof SocketException) {
            return Constants.CONNEXION_ERROR_MSG;
        } else {
            return Constants.OPERATIONAL_ERROR_MSG;
        }
    }
}
