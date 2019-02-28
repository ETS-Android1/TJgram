package org.michaelbel.tjgram.presentation.features.addpost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.michaelbel.tjgram.data.api.remote.TjApi
import org.michaelbel.tjgram.domain.usecases.CreatePost

@Suppress("unchecked_cast")
class PostVMFactory(
        private val service: TjApi, private val createPost: CreatePost
): ViewModelProvider.Factory {

    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        return PostVM(service, createPost) as T
    }
}