package org.michaelbel.tjgram.presentation.di.profile

import dagger.Subcomponent
import org.michaelbel.tjgram.presentation.features.profile.ProfileFragment

@ProfileScope
@Subcomponent(modules = [ProfileModule::class])
interface ProfileComponent {
    fun inject(target: ProfileFragment)
}