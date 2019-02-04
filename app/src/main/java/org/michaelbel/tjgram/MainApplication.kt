package org.michaelbel.tjgram

import android.app.Application
import android.content.Context
import com.singhajit.sherlock.core.Sherlock
import org.koin.android.ext.android.startKoin
import org.michaelbel.tjgram.data.injection.appModule
import org.michaelbel.tjgram.data.injection.networkModule
import timber.log.Timber

@Suppress("unused")
class MainApplication : Application() {

    companion object {
        operator fun get(context: Context): MainApplication {
            return context as MainApplication
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