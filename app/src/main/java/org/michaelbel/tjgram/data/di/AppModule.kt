package org.michaelbel.tjgram.data.di

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.michaelbel.tjgram.modules.timeline.TimelineContract
import org.michaelbel.tjgram.modules.timeline.TimelinePresenter
import org.michaelbel.tjgram.modules.addpost.PostContract
import org.michaelbel.tjgram.modules.addpost.PostPresenter
import org.michaelbel.tjgram.modules.profile.ProfileContract
import org.michaelbel.tjgram.modules.profile.ProfilePresenter
import org.michaelbel.tjgram.utils.consts.SharedPrefs

val appModule = module {
    single<SharedPreferences> {androidContext().getSharedPreferences(SharedPrefs.SP_NAME, MODE_PRIVATE)}
    factory<TimelineContract.Presenter> {TimelinePresenter(createService(androidContext()))}
    factory<ProfileContract.Presenter> {ProfilePresenter(createService(androidContext()))}
    factory<PostContract.Presenter> {PostPresenter(createService(androidContext()))}
}