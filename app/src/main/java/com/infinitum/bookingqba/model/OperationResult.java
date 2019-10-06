package com.infinitum.bookingqba.model;

import android.support.annotation.NonNull;

import com.infinitum.bookingqba.model.remote.errors.ResponseResultException;
import com.infinitum.bookingqba.util.Constants;

import org.jetbrains.annotations.Nullable;

import java.net.ConnectException;
import java.net.SocketException;

public class OperationResult {

    @NonNull
    public final Result result;

    @Nullable
    public final String message;

    public OperationResult(@NonNull Result result, @Nullable String message) {
        this.result = result;
        this.message = message;
    }

    public static OperationResult success(String message) {
        return new OperationResult(Result.SUCCESS, message);
    }

    public static OperationResult success() {
        return new OperationResult(Result.SUCCESS, null);
    }

    public static OperationResult error(String message) {
        return new OperationResult(Result.OPERATIONAL_ERROR, message);
    }

    public static OperationResult error(Throwable throwable) {
        if (throwable instanceof ConnectException) {
            return new OperationResult(Result.CONNEXION_ERROR, Constants.CONNEXION_ERROR_MSG);
        } else if (throwable instanceof SocketException) {
            return new OperationResult(Result.CONNEXION_ERROR, Constants.CONNEXION_ERROR_MSG);
        } else if (throwable instanceof ResponseResultException) {
            return new OperationResult(Result.SERVER_ERROR, throwable.getMessage());
        } else {
            return new OperationResult(Result.OPERATIONAL_ERROR, Constants.OPERATIONAL_ERROR_MSG);
        }

    }

    public static OperationResult empty(String message) {
        return new OperationResult(Result.EMPTY, message);
    }

    @NonNull
    public Result getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return result == Result.SUCCESS ? "SUCCESS " + message : "ERROR " + message;
    }

    public enum Result {
        SUCCESS,
        OPERATIONAL_ERROR,
        CONNEXION_ERROR,
        SERVER_ERROR,
        EMPTY
    }
}
