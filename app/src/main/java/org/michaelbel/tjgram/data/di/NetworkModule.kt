package org.michaelbel.tjgram.data.di

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module
import org.michaelbel.tjgram.BuildConfig
import org.michaelbel.tjgram.data.TjConfig
import org.michaelbel.tjgram.data.interceptors.UserInterceptor
import org.michaelbel.tjgram.data.remote.TjService
import org.michaelbel.tjgram.data.wss.TjWebSocket
import org.michaelbel.tjgram.data.wss.converter.WebSocketConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

val networkModule = module {
    single { okHttpClient(androidContext()) }
    single { (baseUrl: String) -> retrofit(androidContext(), baseUrl) }
    single { createService<TjService>(androidContext()) }
    single { webSocket(androidContext(), TjConfig.TJ_WEB_SOCKET)}
}

fun okHttpClient(context: Context): OkHttpClient {
    val okHttpClient = OkHttpClient().newBuilder()
    okHttpClient.networkInterceptors().add(userInterceptor(context))
    if (BuildConfig.DEBUG) {
        okHttpClient.interceptors().add(chuckInterceptor(context))
        okHttpClient.interceptors().add(httpLoggingInterceptor())
        okHttpClient.networkInterceptors().add(stethoInterceptor())
    }
    return okHttpClient.build()
}

fun userInterceptor(context: Context): UserInterceptor {
    return UserInterceptor(context)
}

fun chuckInterceptor(context: Context): ChuckInterceptor {
    return ChuckInterceptor(context)
}

fun httpLoggingInterceptor(): HttpLoggingInterceptor {
    val httpLoggingInterceptor = HttpLoggingInterceptor{message -> Timber.d(message)}
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return httpLoggingInterceptor
}

fun stethoInterceptor(): StethoInterceptor {
    return StethoInterceptor()
}

fun gson(): Gson {
    val gsonBuilder = GsonBuilder()
    gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    gsonBuilder.setDateFormat(TjConfig.GSON_DATE_FORMAT)
    return gsonBuilder.create()
}

fun retrofit(context: Context, baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(okHttpClient(context))
        .build()
}

inline fun <reified T> createService(context: Context): T {
    return retrofit(context, TjConfig.TJ_API_ENDPOINT).create(T::class.java)
}

fun webSocket(context: Context, url: String): TjWebSocket {
    return TjWebSocket.Builder()
        .addConverterFactory(WebSocketConverterFactory.create())
        .addReceiveInterceptor{data -> data}
        .addOkHttpClient(OkHttpClient().newBuilder().build())
        .build(url)
}