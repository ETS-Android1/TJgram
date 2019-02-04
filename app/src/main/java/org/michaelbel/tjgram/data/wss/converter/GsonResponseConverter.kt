package org.michaelbel.tjgram.data.wss.converter

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import org.michaelbel.tjgram.data.wss.WebSocketConverter
import java.io.StringReader

class GsonResponseConverter<T>(
    private val gson: Gson, private val adapter: TypeAdapter<T>)
: WebSocketConverter<String, T> {

    override fun convert(value: String): T {
        gson.newJsonReader(StringReader(value)).use { jsonReader -> return adapter.read(jsonReader) }
    }
}