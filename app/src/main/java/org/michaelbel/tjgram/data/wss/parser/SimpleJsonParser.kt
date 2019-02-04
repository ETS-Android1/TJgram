package org.michaelbel.tjgram.data.wss.parser

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.michaelbel.tjgram.data.wss.model.SocketResponse

@Suppress("unused")
class SimpleJsonParser {

    companion object {
        const val TYPE_CONTENT_VOTED = "content voted"
        const val TYPE_COMMENT_VOTED = "comment voted"
    }

    fun getSocketData(json: String): SocketResponse {
        val parser = JSONParser()
        val socket = parser.parse(json) as JSONObject

        val type = socket["type"] as String
        val contentId = socket["content_id"] as Long
        val count = socket["count"] as Long
        val id = socket["id"] as Long
        val state = socket["state"] as Long
        val userHash = socket["user_hash"] as String

        return SocketResponse(type, contentId, count, id, state, userHash)
    }
}