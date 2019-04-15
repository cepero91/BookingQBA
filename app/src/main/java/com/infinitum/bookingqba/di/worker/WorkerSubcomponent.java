package com.infinitum.bookingqba.di.worker;

import com.infinitum.bookingqba.service.SendDataWorker;

import java.util.Map;

import javax.inject.Provider;

import androidx.work.RxWorker;
import androidx.work.WorkerParameters;
import dagger.BindsInstance;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent(modules = WorkerModule.class)
public interface WorkerSubcomponent extends AndroidInjector<SendDataWorker>{

    Map<Class<? extends RxWorker>, Provider<RxWorker>> workers();

    @Subcomponent.Builder
    interface Builder{

        @BindsInstance
        Builder workerParameters(WorkerParameters parameters);

        WorkerSubcomponent build();
    }

}
