package org.michaelbel.tjgram.presentation.di.profile

import dagger.Module
import dagger.Provides
import org.michaelbel.tjgram.data.repository.UsersRemoteRepository
import org.michaelbel.tjgram.presentation.features.profile.ProfileVMFactory

@Module
class ProfileModule {

    @Provides
    fun provideProfileVMFactory(repository: UsersRemoteRepository): ProfileVMFactory = ProfileVMFactory(repository)
}