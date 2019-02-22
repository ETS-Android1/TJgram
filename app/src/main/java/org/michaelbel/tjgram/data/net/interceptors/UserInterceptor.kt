package org.michaelbel.tjgram.data.net.interceptors

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.michaelbel.tjgram.data.net.UserConfig

// fixme deprecated code
/*class UserInterceptor(private val context: Context): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("User-agent", UserConfig.getConfiguration(context))
            .addHeader("X-Device-Token", UserConfig.getToken(context))
            .build()
        return chain.proceed(request)
    }
}*/

class UserInterceptor(private val context: Context): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain.proceed(request(chain))

    fun request(chain: Interceptor.Chain): Request =
        chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("User-agent", UserConfig.getConfiguration(context))
            .addHeader("X-Device-Token", UserConfig.getToken(context))
            .build()
}