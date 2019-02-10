package org.michaelbel.tjgram

import android.app.Application
import android.content.Context
import com.singhajit.sherlock.core.Sherlock
import org.koin.android.ext.android.startKoin
import org.michaelbel.tjgram.data.di.appModule
import org.michaelbel.tjgram.data.di.networkModule
import timber.log.Timber

class App : Application() {

    companion object {
        const val TAG = "2580"

        operator fun get(context: Context): App {
            return context as App
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule, networkModule))

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.tag(TAG)

            Sherlock.init(this)
            //Traceur.enableLogging()
            //LeakCanary.install(this)
            //Stetho.initializeWithDefaults(this)
        }
    }
}