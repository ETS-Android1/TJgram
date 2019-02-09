package org.michaelbel.tjgram

import android.app.Application
import android.content.Context
import com.singhajit.sherlock.core.Sherlock
import org.koin.android.ext.android.startKoin
import org.michaelbel.tjgram.data.di.appModule
import org.michaelbel.tjgram.data.di.networkModule
import timber.log.Timber

@Suppress("unused")
class App : Application() {

    companion object {
        operator fun get(context: Context): App {
            return context as App
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule, networkModule))

        if (BuildConfig.DEBUG) {
            //Traceur.enableLogging()
            Sherlock.init(this)
            //LeakCanary.install(this)
            Timber.plant(Timber.DebugTree())
            //Stetho.initializeWithDefaults(this)
        }
    }
}