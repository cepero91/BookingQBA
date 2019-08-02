package com.infinitum.bookingqba.util;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.wshunli.assets.CopyAssets;
import com.wshunli.assets.CopyCreator;
import com.wshunli.assets.CopyListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import timber.log.Timber;

public class FileUtils {

    public static boolean writeFileFromIS(File file, InputStream is) {
        if(is==null || file == null)return false;

        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            byte data[] = new byte[1024];
            int len;
            while ((len = is.read(data, 0, 1024)) != -1) {
                os.write(data, 0, len);
            }
            return true;
        } catch (IOException e) {
            Timber.e(e);
            return false;
        } finally {
            closeIO(is, os);
        }
    }

    private static void closeIO(InputStream is, OutputStream os) {
        if(is!= null && os!=null) {
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyFromAsset(Activity activity, CopyListener copyListener, String dirName){
        CopyAssets.with(activity)
                .from(dirName)
                .setListener(copyListener)
                .copy();
    }


}
