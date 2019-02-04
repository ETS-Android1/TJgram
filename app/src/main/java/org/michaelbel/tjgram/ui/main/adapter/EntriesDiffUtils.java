package org.michaelbel.tjgram.ui.main.adapter;

import android.os.Bundle;

import org.michaelbel.tjgram.data.entity.Entry;

import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import static org.michaelbel.tjgram.ui.main.adapter.EntriesAdapter.PAYLOAD_DATE;
import static org.michaelbel.tjgram.ui.main.adapter.EntriesAdapter.PAYLOAD_INTRO;
import static org.michaelbel.tjgram.ui.main.adapter.EntriesAdapter.PAYLOAD_LIKE;
import static org.michaelbel.tjgram.ui.main.adapter.EntriesAdapter.PAYLOAD_TITLE;

public class EntriesDiffUtils extends DiffUtil.Callback {

    private List<Entry> oldList;
    private List<Entry> newList;

    EntriesDiffUtils(List<Entry> oldList, List<Entry> newList) {
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

        boolean isDateSame = oldEntry.date.equals(newEntry.date);
        boolean isTitleSame = oldEntry.title.equals(newEntry.title);
        boolean isIntroSame = oldEntry.intro.equals(newEntry.intro);
        boolean isLikesSame = Objects.requireNonNull(oldEntry.likes).count == Objects.requireNonNull(newEntry.likes).count;
        boolean isLikesSame2 = Objects.requireNonNull(oldEntry.likes).summ == Objects.requireNonNull(newEntry.likes).summ;

        return isTitleSame && isIntroSame && isDateSame && isLikesSame && isLikesSame2;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Entry newEntry = newList.get(newItemPosition);
        Entry oldEntry = oldList.get(oldItemPosition);

        boolean isDateSame = oldEntry.date.equals(newEntry.date);
        boolean isTitleSame = oldEntry.title.equals(newEntry.title);
        boolean isIntroSame = oldEntry.intro.equals(newEntry.intro);
        boolean isLikesSame = Objects.requireNonNull(oldEntry.likes).count == Objects.requireNonNull(newEntry.likes).count;
        boolean isLikesSame2 = Objects.requireNonNull(oldEntry.likes).summ == Objects.requireNonNull(newEntry.likes).summ;

        Bundle bundle = new Bundle();

        if (!isDateSame) {
            bundle.putString(PAYLOAD_DATE, newEntry.dateRFC);
        }

        if (!isTitleSame) {
            bundle.putString(PAYLOAD_TITLE, newEntry.title);
        }

        if (!isIntroSame) {
            bundle.putString(PAYLOAD_INTRO, newEntry.intro);
        }

        if (!isLikesSame || !isLikesSame2) {
            bundle.putSerializable(PAYLOAD_LIKE, newEntry.likes);
        }

        return bundle.size() == 0 ? null : bundle;
    }
}