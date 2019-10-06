package com.infinitum.bookingqba.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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

    public static List<Calendar> transformStringDatesToCalendars(List<String> disabledStrDates) {
        List<Calendar> calendarList = new ArrayList<>();
        if (disabledStrDates != null && disabledStrDates.size() > 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            Calendar calendarItem;
            for (String dates : disabledStrDates) {
                try {
                    date = simpleDateFormat.parse(dates);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendarItem = dateToCalendar(date);
                calendarList.add(calendarItem);
            }
        }
        return calendarList;
    }

    public static Set<Long> transformStringDatesToCalendarsSet(List<String> disabledStrDates) {
        Set<Long> calendarList = new HashSet<>();
        if (disabledStrDates != null && disabledStrDates.size() > 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            Calendar calendarItem;
            for (String dates : disabledStrDates) {
                try {
                    date = simpleDateFormat.parse(dates);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendarItem = dateToCalendar(date);
                calendarList.add(calendarItem.getTimeInMillis());
            }
        }
        return calendarList;
    }

    public static List<String> transformCalendarsToStringDates(List<Calendar> disabledStrDates) {
        List<String> calendarList = new ArrayList<>();
        if (disabledStrDates != null && disabledStrDates.size() > 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String date;
            for (Calendar dates : disabledStrDates) {
                date = simpleDateFormat.format(dates.getTime());
                calendarList.add(date);
            }
        }
        return calendarList;
    }

    private static Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

}
