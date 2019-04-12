package com.infinitum.bookingqba.service;

import android.support.annotation.NonNull;

import androidx.work.Worker;
import timber.log.Timber;

public class DeleteLocalTrack extends Worker{

    @NonNull
    @Override
    public Result doWork() {

        Timber.e("SendTrackToServer in BackGround");

        return Result.SUCCESS;
    }
}
