package org.michaelbel.tjgram.data.entities

import java.io.Serializable

data class MediaResult(
    val items: List<TweetMedia>
) : Serializable