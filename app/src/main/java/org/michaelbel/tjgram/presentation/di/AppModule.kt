package org.michaelbel.tjgram.presentation.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import org.michaelbel.tjgram.core.imageload.ImageLoader
import org.michaelbel.tjgram.core.imageload.PicassoImageLoader
import org.michaelbel.tjgram.core.persistense.SharedPrefs.SP_NAME
import javax.inject.Singleton

@Module
class AppModule constructor(context: Context) {

    private val appContext = context.applicationContext

    @Singleton
    @Provides
    fun provideAppContext(): Context = appContext

    @Singleton
    @Provides
    fun sharedPreferences(): SharedPreferences = appContext.getSharedPreferences(SP_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideImageLoader(): ImageLoader = PicassoImageLoader(Picasso.get())
}