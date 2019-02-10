package org.michaelbel.tjgram.ui.main.adapter

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import org.michaelbel.tjgram.data.entity.Entry
import org.michaelbel.tjgram.ui.main.adapter.EntriesAdapter.Companion.PAYLOAD_LIKE

class LikesDiffUtils internal constructor(private val oldList: List<Entry>, private val newList: List<Entry>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEntry = oldList[oldItemPosition]
        val newEntry = newList[newItemPosition]
        return oldEntry.id == newEntry.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEntry = oldList[oldItemPosition]
        val newEntry = newList[newItemPosition]
        return oldEntry.likes?.summ == newEntry.likes?.summ
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldEntry = newList[newItemPosition]
        val newEntry = oldList[oldItemPosition]

        val isLikesSame = oldEntry.likes?.summ == newEntry.likes?.summ

        val bundle = Bundle()

        if (!isLikesSame) {
            bundle.putSerializable(PAYLOAD_LIKE, newEntry.likes)
        }

        return if (bundle.size() == 0) null else bundle
    }
}