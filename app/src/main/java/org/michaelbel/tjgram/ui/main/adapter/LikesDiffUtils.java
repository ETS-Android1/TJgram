package org.michaelbel.tjgram.ui.main.adapter;

import android.os.Bundle;

import org.michaelbel.tjgram.data.entity.Entry;

import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import static org.michaelbel.tjgram.ui.main.adapter.EntriesAdapter.PAYLOAD_LIKE;

@SuppressWarnings("unused")
public class LikesDiffUtils extends DiffUtil.Callback {

    private List<Entry> oldList;
    private List<Entry> newList;

    LikesDiffUtils(List<Entry> oldList, List<Entry> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Entry oldEntry = oldList.get(oldItemPosition);
        Entry newEntry = newList.get(newItemPosition);
        return oldEntry.id == newEntry.id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Entry oldEntry = oldList.get(oldItemPosition);
        Entry newEntry = newList.get(newItemPosition);
        return Objects.requireNonNull(oldEntry.likes).summ == Objects.requireNonNull(newEntry.likes).summ;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Entry newEntry = newList.get(newItemPosition);
        Entry oldEntry = oldList.get(oldItemPosition);

        boolean isLikesSame = Objects.requireNonNull(oldEntry.likes).summ == Objects.requireNonNull(newEntry.likes).summ;

        Bundle bundle = new Bundle();

        if (!isLikesSame) {
            bundle.putSerializable(PAYLOAD_LIKE, newEntry.likes);
        }

        return bundle.size() == 0 ? null : bundle;
    }
}