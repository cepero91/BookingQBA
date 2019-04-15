package com.infinitum.bookingqba.di;



import com.infinitum.bookingqba.BookingQBApp;
import com.infinitum.bookingqba.di.module.ActivityModule;
import com.infinitum.bookingqba.di.module.AppModule;
import com.infinitum.bookingqba.di.module.DatabaseModule;
import com.infinitum.bookingqba.di.module.FragmentModule;
import com.infinitum.bookingqba.di.module.NetModule;
import com.infinitum.bookingqba.di.module.RepositoryModule;
import com.infinitum.bookingqba.di.module.SharedPreferencesModule;
import com.infinitum.bookingqba.di.module.ViewModelModule;
import com.infinitum.bookingqba.di.worker.DaggerWorkerFactory;
import com.infinitum.bookingqba.di.worker.WorkerSubcomponent;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        ActivityModule.class,
        FragmentModule.class,
        AppModule.class,
        NetModule.class,
        DatabaseModule.class,
        SharedPreferencesModule.class,
        RepositoryModule.class,
        ViewModelModule.class
})
public interface AppComponent extends AndroidInjector<BookingQBApp> {

    DaggerWorkerFactory daggerWorkerFactory();

    WorkerSubcomponent.Builder workerSubcomponentBuilder();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(BookingQBApp application);

        AppComponent build();
    }

    void inject(BookingQBApp application);
}

