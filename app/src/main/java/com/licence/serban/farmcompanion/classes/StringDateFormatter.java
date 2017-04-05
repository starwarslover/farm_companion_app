package com.licence.serban.farmcompanion.classes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Serban on 15.03.2017.
 */

public class StringDateFormatter {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public static Date stringToDate(String date) throws ParseException {
        return dateFormat.parse(date);
    }

    public static String dateToString(Date date) {
        return dateFormat.format(date);
    }
}
