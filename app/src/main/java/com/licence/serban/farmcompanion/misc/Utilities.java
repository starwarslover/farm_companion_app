package com.licence.serban.farmcompanion.misc;

import android.content.Context;
import android.content.res.Resources;
import android.util.Patterns;

import com.google.android.gms.location.DetectedActivity;
import com.licence.serban.farmcompanion.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Serban on 14/02/2017.
 */

public final class Utilities {
  private Utilities() {

  }

  public static boolean isValidUrl(String url) {
    Pattern p = Patterns.WEB_URL;
    Matcher m = p.matcher(url.toLowerCase());
    return m.matches();
  }

  public static boolean isValidEmail(String email) {
    Pattern p = Patterns.EMAIL_ADDRESS;
    Matcher m = p.matcher(email.toLowerCase());
    return m.matches();
  }

  public static class Constants {
    public static final String DB_USERS = "users";
    public static final String DB_USER_COUNT = "user_number";
    public static final String USER_ID = "user_id";
    public static final String INTENT_USER = "new_user";
    public static final String BUNDLE_INPUT_TYPE = "input_type";
    public static final String DB_COMPANY = "company";
    public static final String DB_EMPLOYEES = "employees";
    public static final String DB_FIELDS = "fields";
    public static final String EMPLOYEE_ID = "employee_id";
    public static final String DATE_EDIT = "edit_date";
    public static final String EMPLOYEE_TAG = "employee_tag_fragment";
    public static final java.lang.String FIELD_ID = "edit_field_id";
    public static final String DB_LIVE_TRACKING = "live_tracking";
    public static final int REQUEST_FINE_LOCATION = 55;
    public static final String DB_EMPLOYER_ID = "employerID";
    public static final String DB_ACTIVITIES = "activities";
    public static final java.lang.String TASK_ID_EXTRA = "task_id";
    public static final String GPS_COORDINATES = "live_coordinates";
    public static final String DB_ACTIVE_TASKS = "active_tasks";
    public static final String DB_SPEED = "speed";
    public static final String DB_ELAPSED_TIME = "elapsed_time";
    public static final String DB_START_DATE = "startDate";
    public static final String DB_CAN_TRACK = "canTrack";
    public static final String DB_STATE = "currentState";
    public static final String DATE_FRAGMENT = "date_fragment";
    public static final String EQUIP_ID_EXTRA = "equip_id";
    public static String EMAIL = "email";
    public static String PASSWORD = "password";
    public static int SPLASH_TIMEOUT = 1000;

    public static String getActivityString(Context context, int detectedActivityType) {
      Resources resources = context.getResources();
      switch (detectedActivityType) {
        case DetectedActivity.IN_VEHICLE:
          return resources.getString(R.string.in_vehicle);
        case DetectedActivity.ON_BICYCLE:
          return resources.getString(R.string.on_bicycle);
        case DetectedActivity.ON_FOOT:
          return resources.getString(R.string.on_foot);
        case DetectedActivity.RUNNING:
          return resources.getString(R.string.running);
        case DetectedActivity.STILL:
          return resources.getString(R.string.still);
        case DetectedActivity.TILTING:
          return resources.getString(R.string.tilting);
        case DetectedActivity.UNKNOWN:
          return resources.getString(R.string.unknown);
        case DetectedActivity.WALKING:
          return resources.getString(R.string.walking);
        default:
          return resources.getString(R.string.unidentifiable_activity);
      }
    }

    public static String getTaskStatus(Context context, WorkState workState) {
      Resources resources = context.getResources();
      switch (workState) {
        case IN_DESFASURARE:
          return resources.getString(R.string.ongoing);
        case INCHEIATA:
          return resources.getString(R.string.finished);
        case NEINCEPUTA:
          return resources.getString(R.string.not_started);
        case OPRITA:
          return resources.getString(R.string.stopped);
        default:
          return resources.getString(R.string.not_available);
      }
    }
  }
}
