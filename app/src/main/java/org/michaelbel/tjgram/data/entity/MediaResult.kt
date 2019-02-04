package org.michaelbel.tjgram.data.entity

import java.io.Serializable

data class MediaResult(
    val items: List<TweetMedia>
) : Serializable