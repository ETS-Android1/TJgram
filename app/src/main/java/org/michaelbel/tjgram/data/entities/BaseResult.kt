package org.michaelbel.tjgram.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

open class BaseResult<T>: Serializable {
    @Expose @SerializedName("result") var result: T? = null
    @Expose @SerializedName("message") var message: String? = null
}