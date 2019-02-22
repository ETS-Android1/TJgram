package org.michaelbel.tjgram.presentation.di.auth

import dagger.Subcomponent
import org.michaelbel.tjgram.presentation.features.auth.AuthFragment

@AuthScope
@Subcomponent(modules = [AuthModule::class])
interface AuthComponent {
    fun inject(fragment: AuthFragment)
}