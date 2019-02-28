package org.michaelbel.tjgram.core.customtabs

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.preference.PreferenceManager
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import org.michaelbel.tjgram.R

object Browser {

    private const val REQUEST_CODE = 100

    fun openUrl(context: Context, url: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val inApp = preferences.getBoolean("key_browser", true)

        if (inApp) {
            openCustomTab(context, url)
        } else {
            openBrowser(context, url)
        }
    }

    private fun openCustomTab(context: Context, url: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, url)

        val pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val shareIcon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_share)

        val builder = CustomTabsIntent.Builder()
        builder.addDefaultShareMenuItem()
        builder.setShowTitle(true)
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.primary))
        builder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.primary))
        builder.setActionButton(shareIcon, context.getString(R.string.menu_share_link), pendingIntent, true)

        val intent = builder.build()
        intent.launchUrl(context, url.toUri())
    }

    private fun openBrowser(context: Context, url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }
}