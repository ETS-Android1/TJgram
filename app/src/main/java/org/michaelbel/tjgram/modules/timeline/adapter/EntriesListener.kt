package org.michaelbel.tjgram.modules.timeline.adapter

import org.michaelbel.tjgram.data.entities.Entry

interface EntriesListener {
    fun onAuthorClick(authorId: Int)
    fun onAuthorLongClick(authorId: Int): Boolean
    fun popupItemClick(itemId: Int, entryId: Int): Boolean
    fun doLoginFirst()
    fun likeEntry(entry: Entry, sign: Int)
}