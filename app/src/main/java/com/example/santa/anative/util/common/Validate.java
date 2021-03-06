package com.example.santa.anative.util.common;

import android.text.TextUtils;

/**
 * Created by santa on 11.03.17.
 */

public final class Validate {
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
    public static boolean isArrayNullOrEmpty(byte[] s) {
        return s == null || s.length == 0;
    }
    public static boolean isEmailValid(String s) {
        return s != null && s.length() > 6 && s.contains("@");
    }
}
