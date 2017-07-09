package com.licence.serban.farmcompanion.misc;

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

  public static String formatDate(Date date, String format) {
    return new SimpleDateFormat(format).format(date);
  }

  public static String dateToString(Date date) {
    return dateFormat.format(date);
  }

  public static String milisToString(Long milis, String dateFormat) {
    SimpleDateFormat format = new SimpleDateFormat(dateFormat);
    return format.format(new Date(milis));
  }
}
