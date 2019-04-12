package com.infinitum.bookingqba.service;

import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.Worker;
import timber.log.Timber;

public class SendTrackToServer extends Worker{

    @NonNull
    @Override
    public Result doWork() {

        Timber.e("SendTrackToServer in BackGround");

        return Result.SUCCESS;
    }
}
