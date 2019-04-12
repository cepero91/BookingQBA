package com.infinitum.bookingqba.di.module;

import android.app.Application;
import android.content.Context;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2core.Downloader;
import com.tonyodev.fetch2okhttp.OkHttpDownloader;
import com.tonyodev.fetch2rx.RxFetch;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.infinitum.bookingqba.util.Constants.BASIC_URL_API;


@Module
public class NetModule {

    @Provides
    @Singleton
    Cache provideHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024;
        return new Cache(application.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkhttpClient(Cache cache) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.cache(cache);
        return client.build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASIC_URL_API)
                .client(okHttpClient)
                .build();
    }

    @Provides
    NetworkHelper provideNetworkHelper(Context context) {
        return new NetworkHelper(context);
    }


    //--------- CONFIGURACIONES DE FILE DOWNLOADER -----------//

    @Provides
    FileDownloader provideFileDownloader() {
        return FileDownloader.getImpl();
    }


}
