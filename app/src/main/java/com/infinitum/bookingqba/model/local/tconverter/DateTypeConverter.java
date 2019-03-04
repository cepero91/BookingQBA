package com.infinitum.bookingqba.model.local.tconverter;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateTypeConverter {

    @TypeConverter
    public static Long fromDate(Date date){
        if(date==null){
            return null;
        }
        return date.getTime();
    }

    @TypeConverter
    public static Date toDate(Long millis){
        if(millis==null){
            return null;
        }
        return new Date(millis);
    }
}
