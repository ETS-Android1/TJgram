package org.michaelbel.tjgram.ui.main.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;
import org.michaelbel.tjgram.R;
import org.michaelbel.tjgram.data.entity.Entry;
import org.michaelbel.tjgram.data.entity.Likes;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class EntriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SwapListener {

    static final String PAYLOAD_LIKE = "like";
    static final String PAYLOAD_DATE = "date";
    static final String PAYLOAD_INTRO = "intro";
    static final String PAYLOAD_TITLE = "title";

    private EntriesListener entriesListener;
    private List<Entry> entries = new ArrayList<>();

    public EntriesAdapter(EntriesListener listener) {
        entriesListener = listener;
    }

    private List<Entry> getEntries() {
        return entries;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry, parent, false);
        return new EntriesViewHolder(view, entriesListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Entry entry = entries.get(position);
        EntriesViewHolder viewHolderOld = (EntriesViewHolder) holder;
        viewHolderOld.bind(entry);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            EntriesViewHolder viewHolder = (EntriesViewHolder) holder;
            Bundle bundle = (Bundle) payloads.get(0);

            for (String key : bundle.keySet()) {
                if (key.equals(PAYLOAD_LIKE)) {
                    viewHolder.updateLikes((Likes) bundle.getSerializable(PAYLOAD_LIKE));
                }

                // Работают ли методы ниже, я хз, не тестировал пока.
                if (key.equals(PAYLOAD_DATE)) {
                    viewHolder.updateDate(bundle.getString(PAYLOAD_DATE));
                }

                if (key.equals(PAYLOAD_TITLE)) {
                    viewHolder.updateTitle(bundle.getString(PAYLOAD_TITLE));
                }

                if (key.equals(PAYLOAD_INTRO)) {
                    viewHolder.updateIntro(bundle.getString(PAYLOAD_INTRO));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return getEntries() != null ? entries.size() : 0;
    }

    @Override
    public void setEntries(@NotNull List<? extends Entry> results) {
        this.entries.addAll(results);
        notifyItemRangeInserted(entries.size() + 1, results.size());
    }

    @Override
    public void swapEntries(@NotNull ArrayList<Entry> newEntries) {
        EntriesDiffUtils diffUtils = new EntriesDiffUtils(getEntries(), newEntries);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtils);
        diffResult.dispatchUpdatesTo(this);
        getEntries().clear();
        getEntries().addAll(newEntries);
    }

    @Override
    public void changeLikes(@NotNull Entry entry) {
        int pos = getEntries().indexOf(entry);
        Bundle payload = new Bundle();
        payload.putSerializable(PAYLOAD_LIKE, entry.likes);
        notifyItemChanged(pos, payload);
    }

    // WSS
    /*public void swapEntry(@NotNull ArrayList<Entry> newEntries) {
        LikesDiffUtils diffUtils = new LikesDiffUtils(getEntries(), newEntries);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtils);
        diffResult.dispatchUpdatesTo(this);
    }*/
}