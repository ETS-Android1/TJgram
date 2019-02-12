package org.michaelbel.tjgram.data.di

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.michaelbel.tjgram.ui.main.MainContract
import org.michaelbel.tjgram.ui.main.MainPresenter
import org.michaelbel.tjgram.ui.main.MainRepository
import org.michaelbel.tjgram.ui.post.PostContract
import org.michaelbel.tjgram.ui.post.PostPresenter
import org.michaelbel.tjgram.ui.post.PostRepository
import org.michaelbel.tjgram.ui.profile.ProfileContract
import org.michaelbel.tjgram.ui.profile.ProfilePresenter
import org.michaelbel.tjgram.ui.profile.ProfileRepository
import org.michaelbel.tjgram.utils.consts.SharedPrefs

val appModule = module {
    single<SharedPreferences> { androidContext().getSharedPreferences(SharedPrefs.SP_NAME, MODE_PRIVATE) }
    factory<MainContract.Presenter> { MainPresenter(MainRepository(createService(androidContext()))) }
    factory<ProfileContract.Presenter> { ProfilePresenter(ProfileRepository(createService(androidContext()))) }
    factory<PostContract.Presenter> { PostPresenter(PostRepository(createService(androidContext()))) }
}