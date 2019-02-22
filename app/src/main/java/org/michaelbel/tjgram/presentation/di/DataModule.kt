package org.michaelbel.tjgram.presentation.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import org.michaelbel.tjgram.data.db.AppDatabase
import org.michaelbel.tjgram.data.db.dao.UserDao
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
    fun providesUserDataSource(db: AppDatabase): UserDao = db.userDao()
}