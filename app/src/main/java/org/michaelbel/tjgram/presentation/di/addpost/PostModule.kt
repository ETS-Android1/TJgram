package org.michaelbel.tjgram.presentation.di.addpost

import dagger.Module
import dagger.Provides
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.presentation.features.addpost.PostVMFactory

@Module
class PostModule {

    @Provides
    fun providePostVMFactory(service: TjApi): PostVMFactory = PostVMFactory(service)
}