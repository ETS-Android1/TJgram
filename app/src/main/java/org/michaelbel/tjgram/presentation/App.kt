package org.michaelbel.tjgram.presentation

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.singhajit.sherlock.core.Sherlock
import com.squareup.leakcanary.LeakCanary
import com.tspoon.traceur.Traceur
import org.michaelbel.tjgram.BuildConfig.DEBUG
import org.michaelbel.tjgram.data.net.TjConfig
import org.michaelbel.tjgram.presentation.di.*
import org.michaelbel.tjgram.presentation.di.addpost.PostComponent
import org.michaelbel.tjgram.presentation.di.addpost.PostModule
import org.michaelbel.tjgram.presentation.di.auth.AuthComponent
import org.michaelbel.tjgram.presentation.di.auth.AuthModule
import org.michaelbel.tjgram.presentation.di.main.MainModule
import org.michaelbel.tjgram.presentation.di.main.MainSubComponent
import org.michaelbel.tjgram.presentation.di.profile.ProfileComponent
import org.michaelbel.tjgram.presentation.di.profile.ProfileModule
import org.michaelbel.tjgram.presentation.di.timeline.TimelineComponent
import org.michaelbel.tjgram.presentation.di.timeline.TimelineModule
import timber.log.Timber

class App: Application() {

    companion object {
        /**
         * Local tag for logging.
         */
        const val TAG = "2580"

        operator fun get(context: Context): App {
            return context as App
        }
    }

    private lateinit var mainComponent: MainComponent

    override fun onCreate() {
        super.onCreate()
        initLogger()
        initDI()
        initTraceur()
        initSherlock()
        initLeakCanary()
        initStetho()
    }

    private fun initDI() {
        mainComponent = DaggerMainComponent.builder()
            .appModule(AppModule(applicationContext))
            .networkModule(NetworkModule(TjConfig.TJ_API_ENDPOINT, applicationContext))
            .dataModule(DataModule())
            .build()
    }

    fun createTimelineComponent(): TimelineComponent = mainComponent.plus(TimelineModule())
    fun createProfileComponent(): ProfileComponent = mainComponent.plus(ProfileModule())
    fun createPostComponent(): PostComponent = mainComponent.plus(PostModule())
    fun createMainComponent(): MainSubComponent = mainComponent.plus(MainModule())
    fun createAuthComponent(): AuthComponent = mainComponent.plus(AuthModule())

    private fun initLogger() {
        if (DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.tag(TAG)
        }
    }

    private fun initTraceur() {
        if (DEBUG) {
            Traceur.enableLogging()
        }
    }

    private fun initSherlock() {
        if (DEBUG) {
            Sherlock.init(this)
        }
    }

    private fun initLeakCanary() {
        if (DEBUG) {
            LeakCanary.install(this)
        }
    }

    private fun initStetho() {
        if (DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }
}