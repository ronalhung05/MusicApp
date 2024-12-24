package com.pro.music.utils;

import static android.provider.Settings.System.getString;

import android.widget.Toast;

import com.pro.music.R;

public class StringUtil {

    public static boolean isEmpty(String input) {
        return input == null || input.isEmpty() || input.trim().isEmpty();
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isContainNumber(String input){
        for (char ch : input.toCharArray()){
            if (Character.isDigit(ch)) return true;
        }

        return false;
    }

    public static boolean isContainsSpecialCharacter(String input) {
        for (char c : input.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c)) return true;
        }

        return false;
    }
}
