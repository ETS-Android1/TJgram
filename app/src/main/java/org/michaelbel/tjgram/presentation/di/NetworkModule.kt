package org.michaelbel.tjgram.presentation.di

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.michaelbel.tjgram.BuildConfig
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.data.net.TjConfig
import org.michaelbel.tjgram.data.net.interceptors.UserInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module
class NetworkModule(private val baseUrl: String, val context: Context) {

    @Singleton
    @Provides
    fun retrofit(): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient())
                .baseUrl(baseUrl)
                .build()
    }

    @Singleton
    @Provides
    fun provideTjApi(retrofit: Retrofit): TjApi {
        return retrofit.create(TjApi::class.java)
    }

    private fun okHttpClient(): OkHttpClient {
        val okHttpClient = OkHttpClient().newBuilder()
        okHttpClient.networkInterceptors().add(UserInterceptor(context))
        if (BuildConfig.DEBUG) {
            okHttpClient.interceptors().add(ChuckInterceptor(context))
            okHttpClient.interceptors().add(httpLoggingInterceptor())
            okHttpClient.networkInterceptors().add(StethoInterceptor())
        }
        return okHttpClient.build()
    }

    private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor{message -> Timber.d(message)}
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    private fun gson(): Gson {
        return GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat(TjConfig.GSON_DATE_FORMAT)
                .create()
    }

    inline fun <reified T> createService(): T {
        return retrofit().create(T::class.java)
    }
}