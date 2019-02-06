package org.michaelbel.tjgram.utils.date;

import android.text.format.Time;

@SuppressWarnings("all")
public class DateUtil {

    public static boolean isMonthNow(long when) {
        Time time = new Time();
        time.set(when);

        int thenYear = time.year;
        int thenMonth = time.month;

        time.set(System.currentTimeMillis());
        return (thenYear == time.year) && (thenMonth == time.month);
    }

    public static boolean isYearNow(long when) {
        Time time = new Time();
        time.set(when);

        int thenYear = time.year;

        time.set(System.currentTimeMillis());
        return thenYear == time.year;
    }

    public static boolean isYearLast(long when) {
        Time time = new Time();
        time.set(when);

        int thenYear = time.year;

        time.set(System.currentTimeMillis());
        return thenYear < time.year;
    }
}