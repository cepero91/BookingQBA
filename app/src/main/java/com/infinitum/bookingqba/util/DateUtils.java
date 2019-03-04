package com.infinitum.bookingqba.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class DateUtils {

    public static Date dateStringToDate(String dateString){
        dateString = dateString.replace("T"," ");
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", new Locale("es", "ES"));
        try {
            return parser.parse(dateString);
        } catch (ParseException e) {
            Timber.e(String.format("Error parsing date: " + dateString));
            return null;
        }
    }
}
