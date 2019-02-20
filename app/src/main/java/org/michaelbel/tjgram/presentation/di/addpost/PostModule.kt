package org.michaelbel.tjgram.presentation.di.addpost

import dagger.Module
import dagger.Provides
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.presentation.features.addpost.PostContract
import org.michaelbel.tjgram.presentation.features.addpost.PostPresenter

@Module
class PostModule {

    @Provides
    fun providePostPresenter(service: TjApi): PostContract.Presenter {
        return PostPresenter(service)
    }
}