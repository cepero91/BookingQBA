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
        return new Resource<>(SUCCESS, data, "SUCCESS");
    }

    public static <T> Resource<T> downloaded(@Nullable T data) {
        return new Resource<>(DOWNLOADED, data, "DOWNLOADED");
    }

    public static <T> Resource<T> empty(@Nullable T data) {
        return new Resource<>(EMPTY, data, "EMPTY");
    }

    public static <T> Resource<T> error(Throwable throwable) {
        return new Resource<>(ERROR, null, throwable.getLocalizedMessage());
    }

    public static <T> Resource<T> error(String message) {
        return new Resource<>(ERROR, null, message);
    }

    public enum Status {
        SUCCESS,
        ERROR,
        DOWNLOADED,
        EMPTY
    }
}