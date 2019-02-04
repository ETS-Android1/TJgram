package org.michaelbel.tjgram.ui.main.adapter

import org.michaelbel.tjgram.data.entity.Entry

interface EntriesListener {
    fun onAuthorClick(authorId: Int)
    fun onAuthorLongClick(authorId: Int): Boolean
    fun popupItemClick(itemId: Int, entryId: Int): Boolean
    fun doLoginFirst()
    fun likeEntry(entry: Entry, sign: Int)
}