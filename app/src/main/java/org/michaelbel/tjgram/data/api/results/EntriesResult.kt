package org.michaelbel.tjgram.data.api.results

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.michaelbel.tjgram.data.entities.Entry
import java.io.Serializable

@Suppress("unused")
data class EntriesResult(
        @Expose @SerializedName("result") val results: List<Entry>
): Serializable {

    object Category {
        const val INDEX = "index"
        const val GAMEDEV = "gamedev"
        const val MAINPAGE = "mainpage"
    }

    object Sorting {
        const val RECENT = "recent"
        const val POPULAR = "popular"
        const val WEEK = "week"
        const val MONTH = "month"
        const val NEW = "new"
        const val TOP_WEEK = "/top/week"
        const val TOP_MONTH = "/top/month"
        const val TOP_YEAR = "/top/year"
        const val TOP_ALL = "/top/all"
    }
}