package org.michaelbel.tjgram.ui.main.adapter

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import org.michaelbel.tjgram.data.entity.Entry
import org.michaelbel.tjgram.ui.main.adapter.EntriesAdapter.Companion.PAYLOAD_DATE
import org.michaelbel.tjgram.ui.main.adapter.EntriesAdapter.Companion.PAYLOAD_INTRO
import org.michaelbel.tjgram.ui.main.adapter.EntriesAdapter.Companion.PAYLOAD_LIKE
import org.michaelbel.tjgram.ui.main.adapter.EntriesAdapter.Companion.PAYLOAD_TITLE

class EntriesDiffUtils internal constructor(private val oldList: List<Entry>, private val newList: List<Entry>) : DiffUtil.Callback() {

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

        val isDateSame = oldEntry.date == newEntry.date
        val isTitleSame = oldEntry.title == newEntry.title
        val isIntroSame = oldEntry.intro == newEntry.intro
        val isLikesSame = oldEntry.likes?.count == newEntry.likes?.count
        val isLikesSame2 = oldEntry.likes?.summ == newEntry.likes?.summ

        return isTitleSame && isIntroSame && isDateSame && isLikesSame && isLikesSame2
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldEntry = newList[newItemPosition]
        val newEntry = oldList[oldItemPosition]

        val isDateSame = oldEntry.date == newEntry.date
        val isTitleSame = oldEntry.title == newEntry.title
        val isIntroSame = oldEntry.intro == newEntry.intro
        val isLikesSame = oldEntry.likes?.count == newEntry.likes?.count
        val isLikesSame2 = oldEntry.likes?.summ == newEntry.likes?.summ

        val bundle = Bundle()

        if (!isDateSame) {
            bundle.putString(PAYLOAD_DATE, newEntry.dateRFC)
        }

        if (!isTitleSame) {
            bundle.putString(PAYLOAD_TITLE, newEntry.title)
        }

        if (!isIntroSame) {
            bundle.putString(PAYLOAD_INTRO, newEntry.intro)
        }

        if (!isLikesSame || !isLikesSame2) {
            bundle.putSerializable(PAYLOAD_LIKE, newEntry.likes)
        }

        return if (bundle.size() == 0) null else bundle
    }
}