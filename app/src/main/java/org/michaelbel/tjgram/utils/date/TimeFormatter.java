package org.michaelbel.tjgram.utils.date;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;

import org.michaelbel.tjgram.Logg;
import org.michaelbel.tjgram.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class TimeFormatter {

    //private String[] russianMonths = {"Янв", "Фев", "Мар", "Апр", "Мая", "Июня", "Июля", "Авг", "Сен", "Окт", "Ноя", "Дек"};

    //private static final long MILLISECONDS_IN_SECOND = 1000;
    //private static final long MILLISECONDS_IN_MINUTE = 1000 * 60;
    //private static final long MILLISECONDS_IN_HOUR = 1000 * 60 * 60;
    //private static final long MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;

    public static CharSequence getTimeAgo(Context context, String startTime) {
        SimpleDateFormat startFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
        startFormat.setTimeZone(TimeZone.getDefault());

        try {
            Date startDate = startFormat.parse(startTime);

            long when = startDate.getTime();
            long now = System.currentTimeMillis();
            long diff = now - when;

            long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            long days = TimeUnit.MILLISECONDS.toDays(diff);

            boolean isToday = DateUtils.isToday(when);
            boolean isYesterday = DateUtils.isToday(when + DateUtils.DAY_IN_MILLIS);
            boolean isYearNow = DateUtil.isYearNow(when);

            String formatDate = formatDate("d MMM", startDate);
            String formatDateWithYear = formatDate("d MMM yyyy", startDate);
            String formatTime = formatDate("HH:mm", startDate);

            if (seconds < 60) {
                return context.getString(R.string.just_now);
            } else if (minutes < 2) {
                return context.getString(R.string.minute_ago);
            } else if (minutes < 60) {
                return context.getResources().getQuantityString(R.plurals.minutes, (int) minutes, (int) minutes);
            } else if (hours < 2) {
                return context.getString(R.string.hour_ago);
            } else if (hours < 13) {
                return context.getResources().getQuantityString(R.plurals.hours, (int) hours, (int) hours);
            } else if (isToday) {
                return context.getString(R.string.today_at, formatTime);
            } else if (isYesterday) {
                return context.getString(R.string.yesterday_at, formatTime);
            } else if (days > 2) {
                if (isYearNow) {
                    return context.getString(R.string.default_date, formatDate, formatTime);
                } else {
                    return context.getString(R.string.default_date, formatDateWithYear, formatTime);
                }
            } else {
                return context.getString(R.string.default_date, formatDateWithYear, formatTime);
            }
        } catch (ParseException e) {
            Timber.e(e);
        }

        return context.getString(R.string.unknown_date);
    }

    private static String formatDate(String format, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return simpleDateFormat.format(date != null ? date : new Date());
    }

    public static String convertSignDate(Context context, String startDate) {
        if (startDate == null || TextUtils.isEmpty(startDate)) {
            return context.getString(R.string.unknown_date);
        }

        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat newFormat = new SimpleDateFormat(/*"d MMM yyyy"*/"d MMM yyyy", Locale.getDefault());

        try {
            Date date = format.parse(startDate);
            return newFormat.format(date);
        } catch (ParseException e) {
            Timber.e(e);
        }

        return context.getString(R.string.unknown_date);
    }
}