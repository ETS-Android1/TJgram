package org.michaelbel.tjgram.presentation.di.profile

import dagger.Module
import dagger.Provides
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.domain.UserRepository
import org.michaelbel.tjgram.presentation.common.ASyncTransformer
import org.michaelbel.tjgram.presentation.features.profile.AuthUser
import org.michaelbel.tjgram.presentation.features.profile.ProfileContract
import org.michaelbel.tjgram.presentation.features.profile.ProfilePresenter
import org.michaelbel.tjgram.presentation.features.profile.ProfileViewModelFactory

@Module
class ProfileModule {

    @Provides
    fun provideProfilePresenter(service: TjApi): ProfileContract.Presenter {
        return ProfilePresenter(service)
    }

    @Provides
    fun provideAuthUserUseCase(userRepository: UserRepository): AuthUser {
        return AuthUser(ASyncTransformer(), userRepository)
    }

    @Provides
    fun provideProfileVMFactory(useCase: AuthUser): ProfileViewModelFactory {
        return ProfileViewModelFactory(useCase)
    }
}