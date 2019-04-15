package com.infinitum.bookingqba.di.worker;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.work.RxWorker;
import dagger.MapKey;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@MapKey
public @interface WorkerKey {
    Class<? extends RxWorker> value();
}
