package org.michaelbel.tjgram.presentation.common

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.singhajit.sherlock.core.Sherlock
import com.squareup.leakcanary.LeakCanary
import com.tspoon.traceur.Traceur
import org.michaelbel.tjgram.BuildConfig
import org.michaelbel.tjgram.data.net.TjConfig
import org.michaelbel.tjgram.presentation.di.*
import org.michaelbel.tjgram.presentation.di.addpost.PostComponent
import org.michaelbel.tjgram.presentation.di.addpost.PostModule
import org.michaelbel.tjgram.presentation.di.main.MainModule
import org.michaelbel.tjgram.presentation.di.main.MainSubComponent
import org.michaelbel.tjgram.presentation.di.profile.ProfileComponent
import org.michaelbel.tjgram.presentation.di.profile.ProfileModule
import org.michaelbel.tjgram.presentation.di.timeline.TimelineComponent
import org.michaelbel.tjgram.presentation.di.timeline.TimelineModule
import timber.log.Timber

@Suppress("unused")
class App: Application() {

    companion object {
        const val TAG = "2580"

        operator fun get(context: Context): App {
            return context as App
        }
    }

    private lateinit var mainComponent: MainComponent

    private var timelineComponent: TimelineComponent? = null
    private var profileComponent: ProfileComponent? = null
    private var postComponent: PostComponent? = null
    private var mainSubComponent: MainSubComponent? = null

    override fun onCreate() {
        super.onCreate()
        initLogger()
        initDI()
    }

    private fun initDI() {
        mainComponent = DaggerMainComponent.builder()
            .appModule(AppModule(applicationContext))
            .networkModule(NetworkModule(TjConfig.TJ_API_ENDPOINT, applicationContext))
            .dataModule(DataModule())
            .build()
    }

    fun createTimelineComponent(): TimelineComponent {
        timelineComponent = mainComponent.plus(TimelineModule())
        return timelineComponent!!
    }

    fun removeTimelineComponent() {
        timelineComponent = null
    }

    fun createProfileComponent(): ProfileComponent {
        profileComponent = mainComponent.plus(ProfileModule())
        return profileComponent!!
    }

    fun removeProfileComponent() {
        profileComponent = null
    }

    fun createPostComponent(): PostComponent {
        postComponent = mainComponent.plus(PostModule())
        return postComponent!!
    }

    fun removePostComponent() {
        postComponent = null
    }

    fun createMainComponent(): MainSubComponent {
        mainSubComponent = mainComponent.plus(MainModule())
        return mainSubComponent!!
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.tag(TAG)
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