package org.michaelbel.tjgram.presentation.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import org.michaelbel.tjgram.presentation.common.ImageLoader
import org.michaelbel.tjgram.presentation.common.PicassoImageLoader
import org.michaelbel.tjgram.presentation.utils.consts.SharedPrefs.SP_NAME
import javax.inject.Singleton

@Module
class AppModule constructor(context: Context) {

    private val appContext = context.applicationContext

    @Singleton
    @Provides
    fun provideAppContext(): Context {
        return appContext
    }

    @Singleton
    @Provides
    fun sharedPreferences(): SharedPreferences {
        return appContext.getSharedPreferences(SP_NAME, MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideImageLoader(): ImageLoader {
        return PicassoImageLoader(Picasso.get())
    }
}