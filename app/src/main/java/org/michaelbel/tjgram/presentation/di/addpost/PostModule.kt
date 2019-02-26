package org.michaelbel.tjgram.presentation.di.addpost

import dagger.Module
import dagger.Provides
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.data.repository.EntriesRemoteRepository
import org.michaelbel.tjgram.data.repository.UsersRemoteRepository
import org.michaelbel.tjgram.domain.common.ASyncTransformer
import org.michaelbel.tjgram.domain.usecases.AuthQr
import org.michaelbel.tjgram.domain.usecases.CreatePost
import org.michaelbel.tjgram.presentation.features.addpost.PostVMFactory

@Module
class PostModule {

    @Provides
    fun provideCreatePostUseCase(repository: EntriesRemoteRepository): CreatePost {
        return CreatePost(ASyncTransformer(), repository)
    }

    @Provides
    fun providePostVMFactory(service: TjApi, createPost: CreatePost): PostVMFactory =
            PostVMFactory(service, createPost)
}