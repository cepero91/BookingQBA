package com.infinitum.bookingqba.util.file;

import android.content.Context;

public final class CopyAssets {
    public static CopyCreator with(Context context) {
        return new CopyCreator(context);
    }
}
