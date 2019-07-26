package com.infinitum.bookingqba.util;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.common.api.ApiException;

public class ParcelableException implements Parcelable {

    private ApiException apiException;

    public ParcelableException(ApiException apiException) {
        this.apiException = apiException;
    }

    public ApiException getApiException() {
        return apiException;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.apiException);
    }

    protected ParcelableException(Parcel in) {
        this.apiException = (ApiException) in.readSerializable();
    }

    public static final Parcelable.Creator<ParcelableException> CREATOR = new Parcelable.Creator<ParcelableException>() {
        @Override
        public ParcelableException createFromParcel(Parcel source) {
            return new ParcelableException(source);
        }

        @Override
        public ParcelableException[] newArray(int size) {
            return new ParcelableException[size];
        }
    };
}
