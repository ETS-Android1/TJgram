package org.michaelbel.tjgram.presentation.di.main

import dagger.Subcomponent
import org.michaelbel.tjgram.presentation.features.main.MainActivity

@MainScope
@Subcomponent(modules = [MainModule::class])
interface MainSubComponent {
    fun inject(target: MainActivity)
}