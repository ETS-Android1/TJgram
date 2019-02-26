package org.michaelbel.tjgram.presentation.di.auth

import dagger.Module
import dagger.Provides
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.data.db.dao.UserDao
import org.michaelbel.tjgram.data.repository.EntriesRemoteRepository
import org.michaelbel.tjgram.data.repository.UsersRemoteRepository
import org.michaelbel.tjgram.domain.common.ASyncTransformer
import org.michaelbel.tjgram.domain.usecases.AuthQr
import org.michaelbel.tjgram.domain.usecases.LikeEntry
import org.michaelbel.tjgram.presentation.features.auth.AuthVMFactory

@Module
class AuthModule {

    @Provides
    fun provideAuthQrUseCase(repository: UsersRemoteRepository): AuthQr {
        return AuthQr(ASyncTransformer(), repository)
    }

    @Provides
    fun provideAuthVMFactory(authQr: AuthQr): AuthVMFactory = AuthVMFactory(authQr)
}