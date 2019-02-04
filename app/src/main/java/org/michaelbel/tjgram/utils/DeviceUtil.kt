package org.michaelbel.tjgram.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.WindowManager
import timber.log.Timber

object DeviceUtil {

    @Suppress("unused")
    fun isLandscape(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    @Suppress("unused")
    fun statusBarHeight(context: Context): Int {
        var result = 0
        val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")

        if (resId > 0) {
            result = context.resources.getDimensionPixelSize(resId)
        }

        return result
    }

    fun dp(context: Context, value: Float): Int {
        return Math.ceil((context.resources.displayMetrics.density * value).toDouble()).toInt()
    }

    fun getScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun copyToClipboard(context: Context, text: CharSequence) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(text, text)
        clipboard.primaryClip = clipData
    }

    fun isAppInstalled(context: Context, uri: String): Boolean {
        val packageManager = context.packageManager

        try {
            packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e)
        }

        return false
    }

    fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else capitalize(manufacturer) + " " + model
    }

    private fun capitalize(str: String): String? {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        val arr = str.toCharArray()
        var capitalizeNext = true

        val phrase = StringBuilder()
        for (c in arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c))
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase.append(c)
        }

        return phrase.toString()
    }
}