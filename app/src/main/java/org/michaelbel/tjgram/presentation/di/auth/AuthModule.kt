package org.michaelbel.tjgram.presentation.di.auth

import dagger.Module
import dagger.Provides
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.data.db.dao.UserDao
import org.michaelbel.tjgram.presentation.features.auth.AuthVMFactory

@Module
class AuthModule {

    @Provides
    fun provideAuthVMFactory(service: TjApi, dataSource: UserDao): AuthVMFactory = AuthVMFactory(service, dataSource)
}