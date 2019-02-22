package org.michaelbel.tjgram.core.time

import android.content.Context
import android.text.TextUtils
import android.text.format.DateUtils
import org.michaelbel.tjgram.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object TimeFormatter {

    fun getTimeAgo(context: Context, startTime: String?): CharSequence {
        val startFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH)
        startFormat.timeZone = TimeZone.getDefault()

        val startDate = startFormat.parse(startTime)

        val then = startDate.time
        val now = System.currentTimeMillis()

        val diff = now - then

        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        val isToday = DateUtils.isToday(then)
        val isYesterday = DateUtils.isToday(then + DateUtils.DAY_IN_MILLIS)
        val isYearNow = DateUtil.isYearNow(then)

        val formatDate = formatDate("d MMM", startDate)
        val formatDateWithYear = formatDate("d MMM yyyy", startDate)
        val formatTime = formatDate("HH:mm", startDate)

        return if (seconds < 60) {
            context.getString(R.string.just_now)
        } else if (minutes < 2) {
            context.getString(R.string.minute_ago)
        } else if (minutes < 60) {
            context.resources.getQuantityString(R.plurals.minutes, minutes.toInt(), minutes.toInt())
        } else if (hours < 2) {
            context.getString(R.string.hour_ago)
        } else if (hours < 13) {
            context.resources.getQuantityString(R.plurals.hours, hours.toInt(), hours.toInt())
        } else if (isToday) {
            context.getString(R.string.today_at, formatTime)
        } else if (isYesterday) {
            context.getString(R.string.yesterday_at, formatTime)
        } else if (days > 2) {
            if (isYearNow) {
                context.getString(R.string.default_date, formatDate, formatTime)
            } else {
                context.getString(R.string.default_date, formatDateWithYear, formatTime)
            }
        } else {
            context.getString(R.string.unknown_date)
        }
    }

    private fun formatDate(format: String, date: Date): String {
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        return simpleDateFormat.format(date)
    }

    fun convertRegDate(context: Context?, startDate: String?): String {
        if (startDate == null || TextUtils.isEmpty(startDate)) {
            return context!!.getString(R.string.unknown_date)
        }

        val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH)
        val newFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())

        val date = format.parse(startDate)
        return newFormat.format(date)
    }
}