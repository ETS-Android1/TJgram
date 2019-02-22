package org.michaelbel.tjgram.data.wss.converter

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import org.michaelbel.tjgram.data.wss.WebSocketConverter
import java.nio.charset.Charset

@Suppress("unused")
class GsonRequestConverter<T>(
        private val gson: Gson, private val adapter: TypeAdapter<T>
): WebSocketConverter<T, String> {

    companion object {
        private val UTF8 = Charset.forName("UTF-8")
    }

    override fun convert(value: T): String = adapter.toJson(value)
}