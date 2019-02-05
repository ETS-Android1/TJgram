package org.michaelbel.tjgram.ui.main.adapter;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import org.jetbrains.annotations.NotNull;
import org.michaelbel.tjgram.R;
import org.michaelbel.tjgram.data.UserConfig;
import org.michaelbel.tjgram.data.entity.Author;
import org.michaelbel.tjgram.data.entity.Entry;
import org.michaelbel.tjgram.data.entity.Likes;
import org.michaelbel.tjgram.data.enums.LikesKt;

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

    public EntriesAdapter(EntriesListener entriesListener) {
        this.entriesListener = entriesListener;
    }

    private List<Entry> getEntries() {
        return entries;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*EntryView view = new EntryView(parent.getContext());
        EntriesViewHolder holder = new EntriesViewHolder(view);
        view.getAuthorLayout().setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();

            if (pos != RecyclerView.NO_POSITION) {
                Author author = getEntries().get(pos).author;
                if (author != null) {
                    entriesListener.onAuthorClick(author.getId());
                }
            }
        });
        view.getAuthorLayout().setOnLongClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Author author = getEntries().get(pos).author;
                if (author != null) {
                    entriesListener.onAuthorLongClick(author.getId());
                }
            }
            return true;
        });
        view.getMenuIcon().setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                showPopupMenu(v, getEntries().get(pos).id);
            }
        });
        view.getHeartIcon().setOnClickListener(v -> likeEntry(holder.getAdapterPosition(), view));
        return holder;*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry, parent, false);
        EntriesViewHolder holder = new EntriesViewHolder(view);
        holder.getAuthorLayout().setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();

            if (pos != RecyclerView.NO_POSITION) {
                Author author = getEntries().get(pos).author;
                if (author != null) {
                    entriesListener.onAuthorClick(author.getId());
                }
            }
        });
        holder.getAuthorLayout().setOnLongClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Author author = getEntries().get(pos).author;
                if (author != null) {
                    entriesListener.onAuthorLongClick(author.getId());
                }
            }
            return true;
        });
        holder.getMenuIcon().setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                showPopupMenu(v, getEntries().get(pos).id);
            }
        });
        holder.getHeartIcon().setOnClickListener(v -> likeEntry(holder));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        /*Entry entry = entries.get(position);
        EntryView view = (EntryView) holder.itemView;
        view.bind(entry);*/
        Entry entry = entries.get(position);
        EntriesViewHolder viewHolderOld = (EntriesViewHolder) holder;
        viewHolderOld.bind(entry);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        /*if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            //EntriesViewHolder holder = (EntriesViewHolder) viewHolder;
            EntryView view = (EntryView) holder.itemView;
            Bundle bundle = (Bundle) payloads.get(0);

            for (String key : bundle.keySet()) {
                if (key.equals(PAYLOAD_LIKE)) {
                    viewHolder.updateLikes((Likes) bundle.getSerializable(PAYLOAD_LIKE));
                }

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
        }*/

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

    private void showPopupMenu(View view, int entryId) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view, Gravity.END);
        popupMenu.inflate(R.menu.popup_entry);
        popupMenu.setOnMenuItemClickListener(item -> {
            entriesListener.popupItemClick(item.getItemId(), entryId);
            return true;
        });
        popupMenu.show();
    }

    /*private void likeEntry(int position, EntryView holder) {
        if (!UserConfig.isAuthorized(holder.getContext())) {
            entriesListener.doLoginFirst();
            return;
        }

        if (position != RecyclerView.NO_POSITION) {
            Likes likes = getEntries().get(position).likes;
            if (likes != null) {
                if (likes.isLiked == LikesKt.NEUTRAL) {
                    holder.likeEntry(likes.summ, 1);
                } else if (likes.isLiked == LikesKt.LIKED) {
                    holder.likeEntry(likes.summ, -1);
                } else if (likes.isLiked == LikesKt.DISLIKED) {
                    holder.likeEntry(likes.summ, 2);
                }

                entriesListener.likeEntry(getEntries().get(position), likes.isLiked == LikesKt.LIKED ? 0 : 1);
            }
        }
    }*/

    private void likeEntry(EntriesViewHolder holder) {
        if (!UserConfig.INSTANCE.isAuthorized(holder.getContext())) {
            entriesListener.doLoginFirst();
            return;
        }

        int pos = holder.getAdapterPosition();
        if (pos != RecyclerView.NO_POSITION) {
            Likes likes = getEntries().get(pos).likes;
            if (likes != null) {
                if (likes.isLiked == LikesKt.NEUTRAL) {
                    holder.likeEntry(likes.summ, 1);
                } else if (likes.isLiked == LikesKt.LIKED) {
                    holder.likeEntry(likes.summ, -1);
                } else if (likes.isLiked == LikesKt.DISLIKED) {
                    holder.likeEntry(likes.summ, 2);
                }

                entriesListener.likeEntry(getEntries().get(pos), likes.isLiked == LikesKt.LIKED ? 0 : 1);
            }
        }
    }

    /*private void dislikeEntry(EntriesViewHolder holder) {
        if (!UserConfig.isAuthorized(holder.getContext())) {
            entriesListener.doLoginFirst();
            return;
        }

        int pos = holder.getAdapterPosition();
        if (pos != RecyclerView.NO_POSITION) {
            Likes likes = getEntries().get(pos).likes;
            if (likes != null) {
                if (likes.isLiked == LikesKt.NEUTRAL) {
                    holder.dislikeEntry(likes.summ, -1);
                } else if (likes.isLiked == LikesKt.LIKED) {
                    holder.dislikeEntry(likes.summ, -2);
                } else if (likes.isLiked == LikesKt.DISLIKED) {
                    holder.dislikeEntry(likes.summ, 1);
                }

                entriesListener.likeEntry(getEntries().get(pos), likes.isLiked == LikesKt.DISLIKED ? 0 : -1);
            }
        }
    }*/

    // WSS
    /*public void swapEntry(@NotNull ArrayList<Entry> newEntries) {
        LikesDiffUtils diffUtils = new LikesDiffUtils(getEntries(), newEntries);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtils);
        diffResult.dispatchUpdatesTo(this);
    }*/
}