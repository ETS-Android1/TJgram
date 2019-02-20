package org.michaelbel.tjgram.presentation.utils

import android.content.Context
import android.net.ConnectivityManager
import retrofit2.HttpException

object NetworkUtil {

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    @Suppress("unused")
    fun getHttpCode(throwable: Throwable): Int = (throwable as HttpException).code()

    fun isHttpStatusCode(throwable: Throwable, statusCode: Int): Boolean = throwable is HttpException && throwable.code() == statusCode
}