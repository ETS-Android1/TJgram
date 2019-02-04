package org.michaelbel.tjgram.data.wss;

import org.michaelbel.tjgram.data.wss.converter.GsonResponseConverter;

import java.lang.reflect.Type;

public interface WebSocketConverter<V, T> {

    T convert(V value) throws Throwable;

    abstract class Factory {

        public GsonResponseConverter responseBodyConverter(Type type) {
            return null;
        }

        public WebSocketConverter<?, String> requestBodyConverter(Type type) {
            return null;
        }
    }
}