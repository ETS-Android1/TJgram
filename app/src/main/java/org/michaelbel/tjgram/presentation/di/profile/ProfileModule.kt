package org.michaelbel.tjgram.presentation.di.profile

import dagger.Module
import dagger.Provides
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.data.db.dao.UserDao
import org.michaelbel.tjgram.presentation.features.profile.ProfileVMFactory

@Module
class ProfileModule {

    @Provides
    fun provideProfileVMFactory(service: TjApi, dataSource: UserDao): ProfileVMFactory = ProfileVMFactory(service, dataSource)
}