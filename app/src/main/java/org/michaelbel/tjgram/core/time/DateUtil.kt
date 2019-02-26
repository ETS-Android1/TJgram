package org.michaelbel.tjgram.core.time

import android.text.format.Time

@Suppress("deprecation")
object DateUtil {

    fun isYearNow(then: Long): Boolean {
        val time = Time()
        time.set(then)

        val thenYear = time.year

        time.set(System.currentTimeMillis())
        return thenYear == time.year
    }
}