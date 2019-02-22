package org.michaelbel.tjgram.data.api.results

import org.michaelbel.tjgram.data.entities.TweetMedia
import java.io.Serializable

data class MediaResult(val items: List<TweetMedia>): Serializable