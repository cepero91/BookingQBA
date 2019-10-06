package com.infinitum.bookingqba.view.map;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;

import com.graphhopper.GraphHopper;
import com.infinitum.bookingqba.util.file.CopyAssets;
import com.infinitum.bookingqba.util.file.CopyCreator;
import com.infinitum.bookingqba.util.file.CopyListener;

import java.io.File;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class GraphHelper {
    public static GraphCreator with(Context context){
        return new GraphCreator(context);
    }
}
