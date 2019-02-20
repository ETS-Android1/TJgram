package org.michaelbel.tjgram.presentation.utils

import android.content.Context
import org.michaelbel.tjgram.R
import java.util.*

@Suppress("unused")
object LocaleUtil {

    fun Context.getLocale(): Locale {
        val languageCode = getString(R.string.language_code)

        return when (languageCode) {
            "en" -> Locale.ENGLISH
            "ru" -> Locale("ru", "RU")
            else -> Locale.ENGLISH
        }
    }
}