package org.michaelbel.tjgram.ui.main.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.data.entity.Entry
import org.michaelbel.tjgram.data.entity.Likes
import java.util.*

class EntriesAdapter(private val entriesListener: EntriesListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), SwapListener {

    companion object {
        internal const val PAYLOAD_LIKE = "like"
        internal const val PAYLOAD_DATE = "date"
        internal const val PAYLOAD_INTRO = "intro"
        internal const val PAYLOAD_TITLE = "title"
    }

    private val entries = ArrayList<Entry>()

    override fun setEntries(results: List<Entry>) {
        this.entries.addAll(results)
        notifyItemRangeInserted(entries.size + 1, results.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entry, parent, false)
        return EntriesViewHolder(view, entriesListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as EntriesViewHolder
        viewHolder.bind(entries[position])
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val viewHolder = holder as EntriesViewHolder
            val bundle = payloads[0] as Bundle

            for (key in bundle.keySet()) {
                if (key == PAYLOAD_LIKE) {
                    viewHolder.updateLikes(bundle.getSerializable(PAYLOAD_LIKE) as Likes)
                }

                // FIXME Работают ли методы ниже, я хз, не тестировал пока.
                if (key == PAYLOAD_DATE) {
                    viewHolder.updateDate(bundle.getString(PAYLOAD_DATE)!!)
                }

                if (key == PAYLOAD_TITLE) {
                    viewHolder.updateTitle(bundle.getString(PAYLOAD_TITLE)!!)
                }

                if (key == PAYLOAD_INTRO) {
                    viewHolder.updateIntro(bundle.getString(PAYLOAD_INTRO)!!)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return entries.size
    }

    override fun swapEntries(newEntries: ArrayList<Entry>) {
        val diffUtils = EntriesDiffUtils(entries, newEntries)
        val diffResult = DiffUtil.calculateDiff(diffUtils)
        diffResult.dispatchUpdatesTo(this)
        entries.clear()
        entries.addAll(newEntries)
    }

    override fun changeLikes(entry: Entry) {
        val pos = entries.indexOf(entry)
        val payload = Bundle()
        payload.putSerializable(PAYLOAD_LIKE, entry.likes)
        notifyItemChanged(pos, payload)
    }
}