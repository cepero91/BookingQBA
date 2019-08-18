package com.infinitum.bookingqba.util.file;

import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class CopyCreator {
    private final Context mContext;
    private String oriPath, desPath;
    private CopyListener copyListener;

    CopyCreator(Context mContext) {
        this.mContext = mContext;
    }


    public CopyCreator from(String oriPath) {
        this.oriPath = oriPath;
        return this;
    }


    public CopyCreator to(String desPath) {
        this.desPath = desPath;
        return this;
    }


    public CopyCreator setListener(final CopyListener copyListener) {
        this.copyListener = copyListener;
        return this;
    }


    public boolean copy() {
        return this.copy(mContext, oriPath, desPath);
    }


    public boolean copy(Context context, String oriPath, String desPath) {

        oriPath = oriPath == null ? "" : oriPath;
        desPath = desPath == null ? context.getFilesDir().getAbsolutePath() : desPath;

        ArrayList<String> allAssetsFilePath = getAssetsFilePath(context, oriPath, null);

        Map<File, Boolean> results = new HashMap<>();

        int filesNum = allAssetsFilePath.size();
        for (int i = 0; i < filesNum; i++) {

            String path = allAssetsFilePath.get(i);
            File desFile = getFileByPath(desPath + "/" + path);
            if (desFile == null) return false;

            try {
                InputStream is = context.getAssets().open(path);
                boolean result = writeFileFromIS(desFile, is);
                if (!result) return false;

            } catch (IOException e) {
                if (copyListener != null) copyListener.error(this, e);
                return false;
            }

            results.put(desFile, true);

        }

        if (copyListener != null) {
            copyListener.completed(this, results);
        }
        return true;

    }


    private ArrayList<String> getAssetsFilePath(Context context, String oriPath, ArrayList<String> paths) {

        if (paths == null) paths = new ArrayList<>();

        try {
            String[] list = context.getAssets().list(oriPath);
            for (String l : list) {
                int length = context.getAssets().list(l).length;
                String desPath = oriPath.equals("") ? l : oriPath + "/" + l;
                if (length == 0) {
                    paths.add(desPath);
                } else {
                    getAssetsFilePath(context, desPath, paths);
                }
            }
            return paths;
        } catch (IOException e) {
            if (copyListener != null) copyListener.error(this, e);
            return paths;
        }

    }


    private File getFileByPath(String filePath) {
        return (filePath == null || filePath.trim().length() == 0) ? null : new File(filePath);
    }


    private boolean writeFileFromIS(File file, InputStream is) {
        if (file == null || is == null) return false;
        if (!this.createOrExistsFile(file)) return false;

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
            if (copyListener != null) copyListener.error(this, e);
            return false;
        } finally {
            this.closeIO(is, os);
        }
    }



    private boolean createOrExistsFile(File file) {
        if (file == null) return false;

        if (file.exists()) return file.isFile();
        if (!this.createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            if (copyListener != null) copyListener.error(this, e);
            return false;
        }
    }


    private boolean createOrExistsDir(File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }


    private void closeIO(Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    if (copyListener != null) copyListener.error(this, e);
                }
            }
        }
    }
}
