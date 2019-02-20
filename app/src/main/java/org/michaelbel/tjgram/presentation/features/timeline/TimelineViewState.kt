package org.michaelbel.tjgram.presentation.features.timeline

import org.michaelbel.tjgram.data.entities.Entry

data class TimelineViewState(
    var showLoading: Boolean = true,
    var entries: List<Entry>? = null
)