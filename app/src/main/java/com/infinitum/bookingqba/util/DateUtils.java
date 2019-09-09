package com.infinitum.bookingqba.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class DateUtils {

    public static Date dateStringToDate(String dateString) {
        dateString = dateString.replace("T", " ");
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es", "ES"));
        try {
            return parser.parse(dateString);
        } catch (ParseException e) {
            Timber.e(String.format("Error parsing date: " + dateString));
            return null;
        }
    }

    public static String currentDateToString() {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es", "ES"));
        return parser.format(Calendar.getInstance().getTime());
    }

    public static String parseDateToString(Date date) {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es", "ES"));
        return parser.format(date);
    }

    public static boolean dateLocalIsLessThanRemote(Date dateLocal, Date dateRemote) {
        boolean syncRequired = false;
        if (dateLocal.before(dateRemote)) {
            syncRequired = true;
        }
        return syncRequired;
    }

    public static String changeFormatDate(String formatIn, String formatOut, String date) {
        Date tempDate = null;
        SimpleDateFormat simpleDateFormatIn = new SimpleDateFormat(formatIn);
        SimpleDateFormat simpleDateFormatOut = new SimpleDateFormat(formatOut);
        try {
            tempDate = simpleDateFormatIn.parse(date);
        } catch (Exception e) {
            Timber.e(e);
        }
        return simpleDateFormatOut.format(tempDate);
    }
}
