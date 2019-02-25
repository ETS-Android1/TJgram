package org.michaelbel.tjgram.presentation.di.timeline

import dagger.Module
import dagger.Provides
import org.michaelbel.tjgram.domain.common.ASyncTransformer
import org.michaelbel.tjgram.data.repository.EntriesRemoteRepository
import org.michaelbel.tjgram.presentation.features.timeline.TimelineVMFactory
import org.michaelbel.tjgram.domain.usecases.LikeEntry
import org.michaelbel.tjgram.domain.usecases.RefreshTimeline
import org.michaelbel.tjgram.domain.usecases.SendReport

@Module
class TimelineModule {

    @Provides
    fun provideRefreshTimelineUseCase(repository: EntriesRemoteRepository): RefreshTimeline {
        return RefreshTimeline(ASyncTransformer(), repository)
    }

    @Provides
    fun provideSendReportUseCase(repository: EntriesRemoteRepository): SendReport {
        return SendReport(ASyncTransformer(), repository)
    }

    @Provides
    fun provideLikeEntryUseCase(repository: EntriesRemoteRepository): LikeEntry {
        return LikeEntry(ASyncTransformer(), repository)
    }

    @Provides
    fun provideTimelineVMFactory(
            refreshTimeline: RefreshTimeline, sendReport: SendReport, likeEntry: LikeEntry
    ): TimelineVMFactory = TimelineVMFactory(refreshTimeline, sendReport, likeEntry)
}