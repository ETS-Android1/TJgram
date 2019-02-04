package org.michaelbel.tjgram.data.interceptors

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import org.michaelbel.tjgram.data.UserConfig

class UserInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("User-agent", UserConfig.getConfiguration(context))
            .addHeader("X-Device-Token", UserConfig.getToken(context))
            .build()
        return chain.proceed(request)
    }
}