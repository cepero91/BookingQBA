package com.infinitum.bookingqba.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.infinitum.bookingqba.model.Resource.Status.*;


/**
 * Clase encargada de mapear los distintos tipos de estados por los que puede pasar el objecto
 * observable
 *
 * @param <T>
 */
public class Resource<T> {

    @NonNull
    public final Status status;

    @Nullable
    public final String message;

    @Nullable
    public final T data;

    public Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(SUCCESS_LOCAL, data, "");
    }

    public static <T> Resource<T> success_local(@Nullable T data) {
        return new Resource<>(SUCCESS_LOCAL, data, "Error remote");
    }

    public static <T> Resource<T> success_remote(@Nullable T data) {
        return new Resource<>(SUCCESS_REMOTE, data, null);
    }

    public static <T> Resource<T> error(Throwable throwable) {
        return new Resource<>(ERROR_LOCAL, null, throwable.getLocalizedMessage());
    }

    public static <T> Resource<T> error_local(Throwable throwable) {
        return new Resource<>(ERROR_LOCAL, null, throwable.getLocalizedMessage());
    }

    public static <T> Resource<T> error_remote(Throwable throwable) {
        return new Resource<>(ERROR_REMOTE, null, throwable.getLocalizedMessage());
    }

    public static <T> Resource<T> error(String message) {
        return new Resource<>(ERROR, null, message);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(LOADING, data, null);
    }

    public enum Status {
        SUCCESS,
        SUCCESS_LOCAL,
        SUCCESS_REMOTE,
        ERROR,
        ERROR_LOCAL,
        ERROR_REMOTE,
        LOADING
    }
}