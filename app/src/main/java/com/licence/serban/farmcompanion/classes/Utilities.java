package com.licence.serban.farmcompanion.classes;

import android.util.Patterns;

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
        public static String EMAIL = "email";
        public static String PASSWORD = "password";
        public static int SPLASH_TIMEOUT = 1500;
    }
}
