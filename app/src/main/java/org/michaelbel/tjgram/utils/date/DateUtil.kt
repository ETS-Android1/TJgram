package org.michaelbel.tjgram.utils.date

import android.text.format.Time

object DateUtil {

    fun isYearNow(then: Long): Boolean {
        val time = Time()
        time.set(then)

        val thenYear = time.year

        time.set(System.currentTimeMillis())
        return thenYear == time.year
    }
}