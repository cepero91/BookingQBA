package com.infinitum.bookingqba.di.worker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import androidx.work.ListenableWorker;
import androidx.work.RxWorker;
import androidx.work.WorkerFactory;
import androidx.work.WorkerParameters;

@Singleton
public class DaggerWorkerFactory extends WorkerFactory{

    private WorkerSubcomponent.Builder subcomponent;

    @Inject
    public DaggerWorkerFactory(WorkerSubcomponent.Builder subcomponent) {
        this.subcomponent = subcomponent;
    }

    @Nullable
    @Override
    public ListenableWorker createWorker(@NonNull Context appContext, @NonNull String workerClassName, @NonNull WorkerParameters workerParameters) {
        Map<Class<? extends RxWorker>, Provider<RxWorker>> workers = subcomponent.workerParameters(workerParameters).build().workers();
        return createListenable(workerClassName,workers);
    }

    private ListenableWorker createListenable(String workerClassName, Map<Class<? extends RxWorker>, Provider<RxWorker>> workers) {
        try {

            Class<? extends RxWorker> workerClass = Class.forName(workerClassName).asSubclass(RxWorker.class);
            Provider<RxWorker> provider = workers.get(workerClass);

            if(provider == null){
                for(Map.Entry<Class<? extends RxWorker>, Provider<RxWorker>> entry: workers.entrySet()){
                    if(workerClass.isAssignableFrom(entry.getKey())){
                        provider = entry.getValue();
                        break;
                    }
                }
            }
            if(provider == null){
                throw new IllegalArgumentException("Missing binding for $workerClassName");
            }

            return provider.get();

        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }
}
