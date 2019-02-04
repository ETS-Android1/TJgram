package org.michaelbel.tjgram.ui.main.adapter

import org.michaelbel.tjgram.data.entity.Entry

interface SwapListener {
    fun setEntries(results: List<Entry>)
    fun swapEntries(newEntries: ArrayList<Entry>)
    fun changeLikes(entry: Entry)
}