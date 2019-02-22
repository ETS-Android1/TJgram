package com.alexvasilkov.events.cache

import com.alexvasilkov.events.Event
import com.alexvasilkov.events.EventResult

interface CacheProvider {

    @Throws(Exception::class)
    fun loadFromCache(event: Event): EventResult

    @Throws(Exception::class)
    fun saveToCache(event: Event, result: EventResult)
}