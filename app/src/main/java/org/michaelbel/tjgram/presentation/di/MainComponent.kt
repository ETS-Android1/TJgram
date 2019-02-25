package org.michaelbel.tjgram.presentation.di

import dagger.Component
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
import javax.inject.Singleton

@Singleton
@Component(modules = [
    (AppModule::class),
    (NetworkModule::class),
    (DataModule::class)
])
interface MainComponent {
    fun plus(target: TimelineModule): TimelineComponent
    fun plus(target: ProfileModule): ProfileComponent
    fun plus(target: AuthModule): AuthComponent
    fun plus(target: PostModule): PostComponent
    fun plus(target: MainModule): MainSubComponent
}