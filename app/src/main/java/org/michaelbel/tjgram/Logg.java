package org.michaelbel.tjgram;

import android.annotation.SuppressLint;
import android.util.Log;

public class Logg {

    @SuppressLint("LogNotTimber")
    public static void e(String s) {
        if (BuildConfig.DEBUG) {
            Log.e("2580", s);
        }
    }
}