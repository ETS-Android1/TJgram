package org.michaelbel.tjgram.data.wss.wrapper

import com.google.gson.Gson
import okhttp3.Response
import org.michaelbel.tjgram.data.wss.model.SocketResponse

@Suppress("unused")
class DataWrapper {

    companion object {
        fun fromJson(response: Response): SocketResponse = Gson().fromJson(response.toString(), SocketResponse::class.java)
    }

    override fun toString(): String = Gson().toJson(this)
}