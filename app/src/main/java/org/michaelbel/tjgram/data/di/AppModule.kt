package org.michaelbel.tjgram.data.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module
import org.michaelbel.tjgram.data.TjConfig
import org.michaelbel.tjgram.data.room.AppDatabase
import org.michaelbel.tjgram.data.room.UserDao
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
    factory<MainContract.Presenter> { MainPresenter(MainRepository(createService(androidContext()), webSocket(androidContext(), TjConfig.TJ_WEB_SOCKET))) }
    factory<ProfileContract.Presenter> { ProfilePresenter(ProfileRepository(createService(androidContext()))) }
    factory<PostContract.Presenter> { PostPresenter(PostRepository(createService(androidContext()))) }

    factory { appDatabase(androidContext()) }
    factory { userDao(appDatabase(androidContext())) }
}

fun appDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, "tjgram.db")
        .fallbackToDestructiveMigration()
        .allowMainThreadQueries()
        .build()
}

fun userDao(db: AppDatabase): UserDao {
    return db.userDao()
}