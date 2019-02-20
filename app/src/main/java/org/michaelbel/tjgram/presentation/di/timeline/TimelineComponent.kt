package org.michaelbel.tjgram.presentation.di.timeline

import dagger.Subcomponent
import org.michaelbel.tjgram.presentation.features.timeline.TimelineFragment

@TimelineScope
@Subcomponent(modules = [TimelineModule::class])
interface TimelineComponent {
    fun inject(fragment: TimelineFragment)
}