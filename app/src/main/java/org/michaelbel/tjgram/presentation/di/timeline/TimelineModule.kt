package org.michaelbel.tjgram.presentation.di.timeline

import dagger.Module
import dagger.Provides
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.presentation.features.timeline.TimelineContract
import org.michaelbel.tjgram.presentation.features.timeline.TimelinePresenter

@Module
class TimelineModule {

    @Provides
    fun provideTimelinePresenter(service: TjApi): TimelineContract.Presenter {
        return TimelinePresenter(service)
    }
}