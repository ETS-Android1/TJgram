package org.michaelbel.tjgram.presentation.features.profile

import org.michaelbel.tjgram.data.entities.User

data class ProfileViewState(
     var showLoading: Boolean = true,
     var user: User? = null
)
