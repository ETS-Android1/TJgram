package org.michaelbel.tjgram.utils.customtabs

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import androidx.core.content.ContextCompat
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.utils.ViewUtil

object Browser {

    fun openUrl(context: Context, url: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val inApp = preferences.getBoolean("key_browser", true)

        if (inApp) {
            openInAppUrl(context, url)
        } else {
            openBrowserUrl(context, url)
        }
    }

    private fun openInAppUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.putExtra("android.support.customtabs.extra.SESSION", null as Parcelable?)
            if (Build.VERSION.SDK_INT >= 21) {
                intent.putExtra("android.support.customtabs.extra.TOOLBAR_COLOR", ViewUtil.getAttrColor(context, android.R.attr.colorPrimary))
            } else {
                intent.putExtra("android.support.customtabs.extra.TOOLBAR_COLOR", ContextCompat.getColor(context, R.color.primary))
            }
            intent.putExtra("android.support.customtabs.extra.TITLE_VISIBILITY", 1)
            val actionIntent = Intent(Intent.ACTION_SEND)
            actionIntent.type = "text/plain"
            actionIntent.putExtra(Intent.EXTRA_TEXT, Uri.parse(url).toString())
            actionIntent.putExtra(Intent.EXTRA_SUBJECT, "")
            val pendingIntent = PendingIntent.getActivity(context, 0, actionIntent, PendingIntent.FLAG_ONE_SHOT)
            val bundle = Bundle()
            bundle.putInt("android.support.customtabs.customaction.ID", 0)
            bundle.putParcelable("android.support.customtabs.customaction.ICON", BitmapFactory.decodeResource(context.resources, R.drawable.ic_share))
            bundle.putString("android.support.customtabs.customaction.DESCRIPTION", context.getString(R.string.menu_share_link))
            bundle.putParcelable("android.support.customtabs.customaction.PENDING_INTENT", pendingIntent)
            intent.putExtra("android.support.customtabs.extra.ACTION_BUTTON_BUNDLE", bundle)
            intent.putExtra("android.support.customtabs.extra.TINT_ACTION_BUTTON", false)
            intent.putExtra(android.provider.Browser.EXTRA_APPLICATION_ID, context.packageName)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openBrowserUrl(context: Context, url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}