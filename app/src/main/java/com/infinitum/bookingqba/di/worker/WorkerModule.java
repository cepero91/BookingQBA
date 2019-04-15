package com.infinitum.bookingqba.di.worker;

import com.infinitum.bookingqba.service.SendDataWorker;

import androidx.work.RxWorker;
import androidx.work.Worker;
import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class WorkerModule {
    @Binds
    @IntoMap
    @WorkerKey(SendDataWorker.class)
    abstract RxWorker bindSendDataWorker(SendDataWorker sendDataWorker);
}
