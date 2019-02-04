package org.michaelbel.tjgram.data.wss.converter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import org.michaelbel.tjgram.data.wss.WebSocketConverter;

import java.lang.reflect.Type;

public class WebSocketConverterFactory extends WebSocketConverter.Factory {

    public static WebSocketConverterFactory create() {
        return create(new Gson());
    }

    private static WebSocketConverterFactory create(Gson gson) {
        return new WebSocketConverterFactory(gson);
    }

    private final Gson gson;

    private WebSocketConverterFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public GsonResponseConverter responseBodyConverter(Type type) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonResponseConverter(gson, adapter);
    }

    @Override
    public WebSocketConverter<?, String> requestBodyConverter(Type type) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonRequestConverter<>(gson, adapter);
    }
}