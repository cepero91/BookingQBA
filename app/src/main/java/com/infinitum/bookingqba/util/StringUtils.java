package com.infinitum.bookingqba.util;

import java.util.List;

public class StringUtils {

    public static final String SEPARATOR = ",";

    public static String convertListToCommaSeparated(List<String> ids) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            stringBuilder.append("'").append(ids.get(i)).append("'");
            if (i < ids.size() - 1)
                stringBuilder.append(SEPARATOR);
        }
        return stringBuilder.toString();
    }
}
