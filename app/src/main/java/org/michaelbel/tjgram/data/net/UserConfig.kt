package org.michaelbel.tjgram.data.net

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import androidx.core.content.edit
import org.michaelbel.tjgram.BuildConfig
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.core.persistence.SharedPrefs
import java.util.*

object UserConfig {

    fun isAuthorized(context: Context): Boolean = getToken(context).isNotEmpty()

    fun getToken(context: Context): String {
        val preferences = context.getSharedPreferences(SharedPrefs.SP_NAME, MODE_PRIVATE)
        return preferences.getString(SharedPrefs.KEY_X_DEVICE_TOKEN, "")!!
    }

    fun getUserId(context: Context): Int {
        val preferences = context.getSharedPreferences(SharedPrefs.SP_NAME, MODE_PRIVATE)
        return preferences.getInt(SharedPrefs.KEY_LOCAL_USER_ID, 0)
    }

    fun userLogout(context: Context) {
        val preferences = context.getSharedPreferences(SharedPrefs.SP_NAME, MODE_PRIVATE)
        preferences.edit {
            putString(SharedPrefs.KEY_X_DEVICE_TOKEN, "")
            putInt(SharedPrefs.KEY_LOCAL_USER_ID, 0)
        }
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
        return String.format(Locale.getDefault(), "%s-app/%s (%s; %s; %s; %s)",
            context.getString(R.string.app_name),
            BuildConfig.VERSION_NAME,
            Build.MODEL,
            "Android/${Build.VERSION.RELEASE}",
            Locale.getDefault().language,
            getScreenSize(context))
    }

    private fun getScreenSize(context: Context): String {
        var size = ""
        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val displays = displayManager.displays
        for (display in displays) {
            if (display.displayId == Display.DEFAULT_DISPLAY) {
                val displayMetrics = DisplayMetrics()
                display.getMetrics(displayMetrics)
                val width = displayMetrics.widthPixels
                val height = displayMetrics.heightPixels
                size = "${width}x$height"
            }
        }

        return size
    }
}