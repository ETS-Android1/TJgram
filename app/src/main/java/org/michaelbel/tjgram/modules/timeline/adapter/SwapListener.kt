package org.michaelbel.tjgram.modules.timeline.adapter

import org.michaelbel.tjgram.data.entities.Entry

interface SwapListener {
    fun setEntries(results: List<Entry>)
    fun swapEntries(newEntries: ArrayList<Entry>)
    fun changeLikes(entry: Entry)
}