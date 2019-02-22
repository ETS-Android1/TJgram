package org.michaelbel.tjgram.presentation.di.timeline

import dagger.Module
import dagger.Provides
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.presentation.features.timeline.TimelineVMFactory

@Module
class TimelineModule {

    @Provides
    fun provideTimelineVMFactory(service: TjApi): TimelineVMFactory = TimelineVMFactory(service)
}