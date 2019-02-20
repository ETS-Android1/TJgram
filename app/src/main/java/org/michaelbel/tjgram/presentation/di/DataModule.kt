package org.michaelbel.tjgram.presentation.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.data.db.AppDatabase
import org.michaelbel.tjgram.data.db.dao.UserDao
import org.michaelbel.tjgram.data.repositories.UserDataStore
import org.michaelbel.tjgram.data.repositories.UserRepositoryImpl
import org.michaelbel.tjgram.domain.UserRepository
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "tjgram_db")
                .fallbackToDestructiveMigration()
                .build()
    }

    @Provides
    @Singleton
    fun providesUserDataSource(db: AppDatabase): UserDao {
        return db.userDao()
    }

    @Provides
    @Singleton
    fun provideUserRepository(api: TjApi): UserRepository {
        val userDataStore = UserDataStore(api)
        return UserRepositoryImpl(userDataStore)
    }
}