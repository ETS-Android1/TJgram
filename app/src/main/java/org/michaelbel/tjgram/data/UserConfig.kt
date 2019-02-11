package org.michaelbel.tjgram.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Build
import org.michaelbel.tjgram.BuildConfig
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.utils.DeviceUtil
import org.michaelbel.tjgram.utils.consts.SharedPrefs
import java.util.*

object UserConfig {

    fun isAuthorized(context: Context): Boolean {
        return !getToken(context).isEmpty()
    }

    fun getToken(context: Context): String {
        val preferences = context.getSharedPreferences(SharedPrefs.SP_NAME, MODE_PRIVATE)
        return preferences.getString(SharedPrefs.KEY_X_DEVICE_TOKEN, "")!!
    }

    fun formatKarma(value: Long): String {
        val builder = StringBuilder()
        if (value > 0L) {
            builder.append("+")
            builder.append(String.format(Locale.getDefault(), "%,d", value))
        } else {
            builder.append(String.format(Locale.getDefault(), "%,d", value))
        }
        return builder.toString()
    }

    fun getConfiguration(context: Context): String {
        return String.format(Locale.getDefault(), "%s-app/%s (%s; %s/%s; %s; %sx%s)",
            context.getString(R.string.app_name),
            BuildConfig.VERSION_NAME,
            DeviceUtil.getDeviceName(),
            "Android",
            Build.VERSION.RELEASE,
            context.getString(R.string.language_code),
            DeviceUtil.getScreenHeight(context),
            DeviceUtil.getScreenWidth(context))
    }
}