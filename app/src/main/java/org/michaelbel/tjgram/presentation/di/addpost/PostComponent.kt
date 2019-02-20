package org.michaelbel.tjgram.presentation.di.addpost

import dagger.Subcomponent
import org.michaelbel.tjgram.presentation.features.addpost.PostFragment

@PostScope
@Subcomponent(modules = [PostModule::class])
interface PostComponent {
    fun inject(fragment: PostFragment)
}