package com.infinitum.bookingqba.util.file;


import java.io.File;
import java.util.Map;

public interface CopyListener {
    void completed(final CopyCreator copyCreator, final Map<File, Boolean> results);

    void error(final CopyCreator copyCreator, Throwable e);
}
