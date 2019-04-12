package com.infinitum.bookingqba.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
}
