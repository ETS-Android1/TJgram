package org.michaelbel.tjgram

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.singhajit.sherlock.core.Sherlock
import com.squareup.leakcanary.LeakCanary
import com.tspoon.traceur.Traceur
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.michaelbel.tjgram.data.di.appModule
import org.michaelbel.tjgram.data.di.networkModule
import timber.log.Timber

@Suppress("unused")
class App : Application() {

    companion object {
        const val TAG = "2580"

        operator fun get(context: Context): App {
            return context as App
        }
    }

    override fun onCreate() {
        super.onCreate()
        initLogger()
        initKoin()
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.tag(TAG)
        }
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            modules(appModule, networkModule)
        }
    }

    private fun initTraceur() {
        if (BuildConfig.DEBUG) {
            Traceur.enableLogging()
        }
    }

    private fun initSherlock() {
        if (BuildConfig.DEBUG) {
            Sherlock.init(this)
        }
    }

    private fun initLeakCanary() {
        if (BuildConfig.DEBUG) {
            LeakCanary.install(this)
        }
    }

    private fun initStetho() {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }
}