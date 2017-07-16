package com.licence.serban.farmcompanion.misc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by Serban on 15.03.2017.
 */

public class StringDateFormatter {
  private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

  public static Date stringToDate(String date) throws ParseException {
    return dateFormat.parse(date);
  }

  public static String formatDate(Date date, String format) {
    return new SimpleDateFormat(format).format(date);
  }

  public static String dateToString(Date date) {
    return dateFormat.format(date);
  }

  public static String millisToString(Long millis, String dateFormat) {
    SimpleDateFormat format = new SimpleDateFormat(dateFormat);
    return format.format(new Date(millis));
  }

  public static String millisToTime(Long millis) {
    int hrs = (int) (MILLISECONDS.toHours(millis) % 24);
    int min = (int) (MILLISECONDS.toMinutes(millis) % 60);
    int sec = (int) (MILLISECONDS.toSeconds(millis) % 60);
    return String.format("%02d:%02d:%02d", hrs, min, sec);
  }
}
