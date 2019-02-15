package com.alexvasilkov.events.cache;

import com.alexvasilkov.events.Event;
import com.alexvasilkov.events.EventResult;

import androidx.annotation.NonNull;

public interface CacheProvider {

    EventResult loadFromCache(@NonNull Event event) throws Exception;

    void saveToCache(@NonNull Event event, EventResult result) throws Exception;
}