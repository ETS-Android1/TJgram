package org.michaelbel.tjgram.presentation.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.data.db.AppDatabase
import org.michaelbel.tjgram.data.db.dao.EntryDao
import org.michaelbel.tjgram.data.db.dao.UserDao
import org.michaelbel.tjgram.data.repository.EntriesRemoteRepository
import org.michaelbel.tjgram.data.repository.UsersRemoteRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "tjgram_db")
                // todo Пока без миграций.
                .fallbackToDestructiveMigration()
                .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun provideEntryDao(db: AppDatabase): EntryDao = db.entryDao()

    @Singleton
    @Provides
    fun provideTjApi(retrofit: Retrofit): TjApi = retrofit.create(TjApi::class.java)

    @Provides
    @Singleton
    fun provideUsersRepository(service: TjApi): UsersRemoteRepository =
            UsersRemoteRepository(service)

    @Provides
    @Singleton
    fun provideEntriesRepository(service: TjApi): EntriesRemoteRepository =
            EntriesRemoteRepository(service)
}