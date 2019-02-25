package org.michaelbel.tjgram.presentation.features.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.michaelbel.tjgram.domain.usecases.LikeEntry
import org.michaelbel.tjgram.domain.usecases.RefreshTimeline
import org.michaelbel.tjgram.domain.usecases.SendReport

@Suppress("unchecked_cast")
class TimelineVMFactory(
        private val refreshTimeline: RefreshTimeline,
        private val sendReport: SendReport,
        private val likeEntry: LikeEntry
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TimelineVM(refreshTimeline, sendReport, likeEntry) as T
    }
}