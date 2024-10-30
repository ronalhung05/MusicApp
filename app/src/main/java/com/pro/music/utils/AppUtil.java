package com.pro.music.utils;

import android.annotation.SuppressLint;

public class AppUtil {

    @SuppressLint("DefaultLocale")
    public static String getTime(int millis) {
        long second = (millis / 1000) % 60;
        long minute = millis / (1000 * 60);
        return String.format("%02d:%02d", minute, second);
    }
}
