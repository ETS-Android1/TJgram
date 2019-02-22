package org.michaelbel.tjgram.core.locale

import android.content.Context
import org.michaelbel.tjgram.R
import java.util.*

@Suppress("unused")
object LocaleUtil {

    fun getLocale(context: Context): Locale {
        val languageCode = context.getString(R.string.language_code)

        return when (languageCode) {
            "en" -> Locale.ENGLISH
            "ru" -> Locale("ru", "RU")
            else -> Locale.ENGLISH
        }
    }
}