package org.michaelbel.tjgram.presentation.di.main

import dagger.Module
import dagger.Provides
import org.michaelbel.tjgram.data.db.dao.UserDao
import org.michaelbel.tjgram.presentation.features.main.MainVMFactory

@Module
class MainModule {

    @Provides
    fun provideMainVMFactory(dao: UserDao): MainVMFactory = MainVMFactory(dao)
}