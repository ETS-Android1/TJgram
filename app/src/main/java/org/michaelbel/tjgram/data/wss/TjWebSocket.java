package org.michaelbel.tjgram.data.wss;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class TjWebSocket {

    private List<WebSocketInterceptor> receiveInterceptors = new ArrayList<>();
    private List<WebSocketConverter.Factory> converterFactories = new ArrayList<>();

    @Nullable private WebSocket webSocket;

    private Request request;
    private OkHttpClient okHttpClient;

    private boolean userRequestedClose = false;

    public interface Event {
        TjWebSocket client();
    }

    public class Open implements Event {

        private final Maybe<Response> response;

        Open(Response response) {
            this.response = Maybe.just(response);
        }

        Open() {
            this.response = Maybe.empty();
        }

        @Nullable
        public Response response() {
            return response.blockingGet();
        }

        @Override
        public TjWebSocket client() {
            return TjWebSocket.this;
        }
    }

    public class Message implements Event {

        private final String message;
        private final ByteString messageBytes;

        Message(String message) {
            this.message = message;
            this.messageBytes = null;
        }

        Message(ByteString messageBytes) {
            this.messageBytes = messageBytes;
            this.message = null;
        }

        @Nullable
        public String data() {
            String interceptedMessage = message;
            for (WebSocketInterceptor interceptor : receiveInterceptors) {
                interceptedMessage = interceptor.intercept(interceptedMessage);
            }
            return interceptedMessage;
        }

        @Nullable
        ByteString dataBytes() {
            return messageBytes;
        }

        @NonNull
        private String dataOrDataBytesAsString() {
            if (data() == null && dataBytes() == null) {
                return "";
            }

            if (dataBytes() == null) {
                return data();
            }

            if (data() == null) {
                return dataBytes() == null ? "" : Objects.requireNonNull(dataBytes()).utf8();
            }

            return "";
        }

        public <T> T data(Class<? extends T> type) throws Throwable {
            WebSocketConverter<String, T> converter = responseConverter(type);
            if (converter != null) {
                return converter.convert(dataOrDataBytesAsString());
            } else {
                throw new Exception("No converters available to convert the enqueued object");
            }
        }

        @Override
        public TjWebSocket client() {
            return TjWebSocket.this;
        }
    }

    public class QueuedMessage<T> implements Event {

        private final T message;

        QueuedMessage(T message) {
            this.message = message;
        }

        @Nullable
        public T message() {
            return message;
        }

        @Override
        public TjWebSocket client() {
            return TjWebSocket.this;
        }
    }

    public class Closed extends Throwable implements Event {

        public static final int INTERNAL_ERROR = 500;

        private final String reason;
        private final int code;

        Closed(int code, String reason) {
            this.code = code;
            this.reason = reason;
        }

        public int code() {
            return code;
        }

        String reason() {
            return reason;
        }

        @Override
        public String getMessage() {
            return reason();
        }

        @Override
        public TjWebSocket client() {
            return TjWebSocket.this;
        }
    }

    private PublishProcessor<Event> eventStream = PublishProcessor.create();

    public Single<Open> connect() {
        return eventStream()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(d -> doConnect())
            .ofType(Open.class)
            .firstOrError();
    }

    public Flowable<Message> listen() {
        return eventStream()
            .subscribeOn(Schedulers.io())
            .ofType(Message.class);
    }

    public Single<QueuedMessage> send(byte[] message) {
        return eventStream()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(d -> doQueueMessage(message))
            .ofType(QueuedMessage.class)
            .firstOrError();
    }

    public <T> Single<QueuedMessage> send(final T message) {
        return eventStream()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(d -> doQueueMessage(message))
            .ofType(QueuedMessage.class)
            .firstOrError();
    }

    public Single<Closed> disconnect(int code, String reason) {
        return eventStream()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(d -> doDisconnect(code, reason))
            .ofType(Closed.class)
            .firstOrError();
    }

    public Flowable<Event> eventStream() {
        return eventStream;
    }

    private void doConnect() {
        if (webSocket != null) {
            if (eventStream.hasSubscribers()) {
                eventStream.onNext(new Open());
            }
            return;
        }

        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder().build();
        }

        okHttpClient.newWebSocket(request, webSocketListener());
    }

    private void doDisconnect(int code, String reason) {
        requireNotNull(webSocket, "Expected an open websocket");
        userRequestedClose = true;
        if (webSocket != null) {
            webSocket.close(code, reason);
        }
    }

    private void doQueueMessage(byte[] message) {
        requireNotNull(webSocket, "Expected an open websocket");
        requireNotNull(message, "Expected a non null message");
        if (webSocket.send(ByteString.of(message))) {
            if (eventStream.hasSubscribers()) {
                eventStream.onNext(new QueuedMessage(ByteString.of(message)));
            }
        }
    }

    private <T> void doQueueMessage(T message) {
        requireNotNull(webSocket, "Expected an open websocket");
        requireNotNull(message, "Expected a non null message");

        WebSocketConverter<T, String> converter = requestConverter(message.getClass());
        if (converter != null) {
            try {
                if (webSocket.send(converter.convert(message))) {
                    if (eventStream.hasSubscribers()) {
                        eventStream.onNext(new QueuedMessage(message));
                    }
                }
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        } else if (message instanceof String) {
            if (webSocket.send((String) message)) {
                if (eventStream.hasSubscribers()) {
                    eventStream.onNext(new QueuedMessage(message));
                }
            }
        }
    }

    private void setClient(okhttp3.WebSocket originalWebsocket) {
        this.webSocket = originalWebsocket;
        userRequestedClose = false;
    }

    private WebSocketListener webSocketListener() {
        return new WebSocketListener() {

            @Override
            public void onOpen(okhttp3.WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);

                setClient(webSocket);

                if (eventStream.hasSubscribers()) {
                    eventStream.onNext(new Open(response));
                }
            }

            @Override
            public void onMessage(okhttp3.WebSocket webSocket, String message) {
                super.onMessage(webSocket, message);
                if (eventStream.hasSubscribers()) {
                    eventStream.onNext(new Message(message));
                }
            }

            @Override
            public void onMessage(okhttp3.WebSocket webSocket, ByteString messageBytes) {
                super.onMessage(webSocket, messageBytes);
                if (eventStream.hasSubscribers()) {
                    eventStream.onNext(new Message(messageBytes));
                }
            }

            @Override
            public void onClosed(okhttp3.WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                if (userRequestedClose) {
                    if (eventStream.hasSubscribers()) {
                        eventStream.onNext(new Closed(code, reason));
                        eventStream.onComplete();
                    }
                } else {
                    if (eventStream.hasSubscribers()) {
                        eventStream.onError(new Closed(code, reason));
                    }
                }
                setClient(null);
            }

            @Override
            public void onFailure(okhttp3.WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                if (eventStream.hasSubscribers()) {
                    eventStream.onError(t);
                }
                setClient(null);
            }
        };
    }

    private <T> WebSocketConverter<String, T> responseConverter(final Type type) {
        for (WebSocketConverter.Factory converterFactory : converterFactories) {
            WebSocketConverter<String, ?> converter = converterFactory.responseBodyConverter(type);
            if (converter != null) {
                return (WebSocketConverter<String, T>) converter;
            }
        }
        return null;
    }

    private <T> WebSocketConverter<T, String> requestConverter(final Type type) {
        for (WebSocketConverter.Factory converterFactory : converterFactories) {
            WebSocketConverter<?, String> converter = converterFactory.requestBodyConverter(type);
            if (converter != null) {
                return (WebSocketConverter<T, String>) converter;
            }
        }
        return null;
    }

    private static <T> void requireNotNull(T object, String message) {
        if (object == null) {
            throw new IllegalStateException(message);
        }
    }

    public static class Builder {

        private List<WebSocketConverter.Factory> converterFactories = new ArrayList<>();
        private List<WebSocketInterceptor> receiveInterceptors = new ArrayList<>();
        private Request request;
        private OkHttpClient okHttpClient;

        @NonNull
        public Builder request(Request request) {
            this.request = request;
            return this;
        }

        @NonNull
        public Builder addConverterFactory(WebSocketConverter.Factory factory) {
            if (factory != null) {
                converterFactories.add(factory);
            }
            return this;
        }

        @NonNull
        public Builder addReceiveInterceptor(WebSocketInterceptor receiveInterceptor) {
            receiveInterceptors.add(receiveInterceptor);
            return this;
        }

        @NonNull
        public Builder addOkHttpClient(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        @NonNull
        public TjWebSocket build() throws IllegalStateException {
            if (request == null) {
                throw new IllegalStateException("Request cannot be null");
            }

            TjWebSocket rxWebsocket = new TjWebSocket();
            rxWebsocket.request = request;
            rxWebsocket.converterFactories = converterFactories;
            rxWebsocket.receiveInterceptors = receiveInterceptors;
            rxWebsocket.okHttpClient = okHttpClient;
            return rxWebsocket;
        }

        @NonNull
        public TjWebSocket build(@NonNull String wssUrl) {
            if (wssUrl == null || wssUrl.isEmpty()) {
                throw new IllegalStateException("Websocket address cannot be null or empty");
            }

            request = new Request.Builder().url(wssUrl).get().build();

            TjWebSocket rxWebsocket = new TjWebSocket();
            rxWebsocket.converterFactories = converterFactories;
            rxWebsocket.receiveInterceptors = receiveInterceptors;
            rxWebsocket.request = request;
            rxWebsocket.okHttpClient = okHttpClient;
            return rxWebsocket;
        }
    }
}
