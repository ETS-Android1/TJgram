package org.michaelbel.tjgram.ui.main.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.michaelbel.tjgram.R;
import org.michaelbel.tjgram.data.UserConfig;
import org.michaelbel.tjgram.data.constants.Liked;
import org.michaelbel.tjgram.data.entity.Author;
import org.michaelbel.tjgram.data.entity.Cover;
import org.michaelbel.tjgram.data.entity.Entry;
import org.michaelbel.tjgram.data.entity.Likes;
import org.michaelbel.tjgram.utils.DeviceUtil;
import org.michaelbel.tjgram.utils.FileUtil;
import org.michaelbel.tjgram.utils.ViewUtil;
import org.michaelbel.tjgram.utils.date.TimeFormatter;
import org.michaelbel.tjgram.utils.picasso.CircleTransform;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

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
                if (likes.isLiked == Liked.NEUTRAL) {
                    holder.likeEntry(likes.summ, 1);
                } else if (likes.isLiked == Liked.LIKED) {
                    holder.likeEntry(likes.summ, -1);
                } else if (likes.isLiked == Liked.DISLIKED) {
                    holder.likeEntry(likes.summ, 2);
                }

                entriesListener.likeEntry(getEntries().get(pos), likes.isLiked == Liked.LIKED ? 0 : 1);
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

    public class EntriesViewHolder extends RecyclerView.ViewHolder {

        private static final boolean GIF_LOOPING_PLAY = true;

        private CardView authorLayout;
        private AppCompatImageView authorIcon;
        private AppCompatTextView authorName;
        private AppCompatTextView dateText;

        private AppCompatImageView menuIcon;

        private AppCompatImageView pinIcon;

        private FrameLayout mediaLayout;
        private AppCompatImageView coverImage;
        private FrameLayout gifLayout;
        private VideoView videoView;

        private AppCompatTextView entryTitle;
        private AppCompatTextView introText;

        private AppCompatImageView heartIcon;
        private TextSwitcher likesValue;
        private TextView likesSwitcher1;
        private TextView likesSwitcher2;

        private AppCompatImageView doubleTapHeart;
        private GestureDetector detector;

        public class GestureListener extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                heartIcon.performClick();
                doubleTapLike();
                return true;
            }
        }

        CardView getAuthorLayout() {
            return authorLayout;
        }

        AppCompatImageView getMenuIcon() {
            return menuIcon;
        }

        AppCompatImageView getHeartIcon() {
            return heartIcon;
        }

        public Context getContext() {
            return itemView.getContext();
        }

        @SuppressLint("ClickableViewAccessibility")
        EntriesViewHolder(View view) {
            super(view);
            pinIcon = view.findViewById(R.id.pinIcon);
            pinIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), R.drawable.ic_pin, R.color.icon_active_unfocused));

            menuIcon = view.findViewById(R.id.menuIcon);
            menuIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), R.drawable.ic_dots_vertical, R.color.icon_active_unfocused));

            authorLayout = view.findViewById(R.id.authorLayout);
            authorIcon = view.findViewById(R.id.authorIcon);
            authorName = view.findViewById(R.id.authorName);
            dateText = view.findViewById(R.id.dateText);

            mediaLayout = view.findViewById(R.id.mediaLayout);
            coverImage = view.findViewById(R.id.entryCover);
            gifLayout = view.findViewById(R.id.gifLayout);
            videoView = view.findViewById(R.id.videoView);
            entryTitle = view.findViewById(R.id.titleText);
            introText = view.findViewById(R.id.introText);

            heartIcon = view.findViewById(R.id.likeIcon);
            likesValue = view.findViewById(R.id.likesText);
            likesSwitcher1 = view.findViewById(R.id.textLike1);
            likesSwitcher2 = view.findViewById(R.id.textLike2);

            doubleTapHeart = view.findViewById(R.id.likeImage);
            doubleTapHeart.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), R.drawable.ic_heart, R.color.double_heart));

            detector = new GestureDetector(getContext(), new EntriesViewHolder.GestureListener());
            mediaLayout.setOnTouchListener((v, event) -> detector.onTouchEvent(event));
        }

        void bind(@NonNull Entry entry) {
            Author author = entry.author;
            assert author != null;
            Picasso.get().load(author.getAvatarUrl()).placeholder(R.drawable.placeholder_circle).error(R.drawable.error_circle).transform(new CircleTransform()).into(authorIcon);
            authorName.setText(author.getName());

            pinIcon.setVisibility(entry.isPinned ? VISIBLE : GONE);

            updateDate(entry.dateRFC);

            Cover cover = entry.cover;
            mediaLayout.setVisibility(cover != null ? VISIBLE : GONE);

            if (cover != null) {
                if (cover.type == Cover.TYPE_IMAGE) {
                    if (cover.additionalData != null) {
                        String imageType = cover.additionalData.type;

                        if (FileUtil.INSTANCE.isGif(imageType)) {
                            gifLayout.setVisibility(VISIBLE);

                            videoView.getLayoutParams().height = DeviceUtil.INSTANCE.dp(getContext(), cover.size.getHeight());
                            videoView.setVideoPath(cover.url);
                            videoView.seekTo(1);
                            videoView.setOnPreparedListener(mp -> {
                                mp.setLooping(GIF_LOOPING_PLAY);
                                videoView.start();
                            });
                            videoView.setOnErrorListener((mp, what, extra) -> true);
                        } else if (FileUtil.INSTANCE.isImage(imageType)) {
                            gifLayout.setVisibility(GONE);

                            // FIXME высота медиа не должна превышать width * 1.5
                            Picasso.get().load(cover.thumbnailUrl)/*.resize(50, 50).centerCrop()*/
                                    .placeholder(R.drawable.placeholder_rectangle)
                                    .error(R.drawable.error_rectangle)
                                    .into(coverImage);
                        }
                    }
                }
            }

            updateTitle(entry.title);
            updateIntro(entry.intro);

            Likes likes = entry.likes;
            updateLikes(likes);
        }

        void updateDate(String dateRFC) {
            CharSequence date = TimeFormatter.getTimeAgo(getContext(), dateRFC);
            dateText.setText(date);
        }

        void updateTitle(String title) {
            entryTitle.setText(title);
        }

        void updateIntro(String intro) {
            introText.setVisibility(intro.isEmpty() ? GONE : VISIBLE);
            introText.setText(intro);
        }

        // For unauthorized users.
        void updateLikes(Likes likes) {
            likesValue.setCurrentText(String.valueOf(likes.summ));
            likesSwitcher1.setTextColor(ContextCompat.getColor(getContext(), R.color.icon_active_unfocused));
            likesSwitcher2.setTextColor(ContextCompat.getColor(getContext(), R.color.icon_active_unfocused));
            heartIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), R.drawable.ic_heart_outline, R.color.icon_active_unfocused));
            updateLikesAuth(likes);
        }

        // For authorized users.
        private void updateLikesAuth(Likes likes) {
            if (likes.isLiked == Liked.LIKED) {
                likesSwitcher1.setTextColor(ContextCompat.getColor(getContext(), likes.summ <= 0 ? R.color.icon_active_unfocused : R.color.instagram_like));
                likesSwitcher2.setTextColor(ContextCompat.getColor(getContext(), likes.summ <= 0 ? R.color.icon_active_unfocused : R.color.instagram_like));
                heartIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), R.drawable.ic_heart, R.color.instagram_like));
            } else if (likes.isLiked == Liked.NEUTRAL) {
                likesSwitcher1.setTextColor(ContextCompat.getColor(getContext(), R.color.icon_active_unfocused));
                likesSwitcher2.setTextColor(ContextCompat.getColor(getContext(), R.color.icon_active_unfocused));
                heartIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), R.drawable.ic_heart_outline, R.color.icon_active_unfocused));
            } else if (likes.isLiked == Liked.DISLIKED) {
                likesSwitcher1.setTextColor(ContextCompat.getColor(getContext(), R.color.icon_active_unfocused));
                likesSwitcher2.setTextColor(ContextCompat.getColor(getContext(), R.color.icon_active_unfocused));
                heartIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), R.drawable.ic_heart_outline, R.color.icon_active_unfocused));
            }
        }

        void likeEntry(int likes, int sign) {
            int newLikes = likes + sign;
            likesValue.setText(String.valueOf(newLikes));

            if (sign == -1) {
                // Liked > Neutral
                likesSwitcher1.setTextColor(ContextCompat.getColor(getContext(), R.color.icon_active_unfocused));
                likesSwitcher2.setTextColor(ContextCompat.getColor(getContext(), R.color.icon_active_unfocused));
                animateLike(false);
            } else if (sign == 1) {
                // Neutral > Liked
                likesSwitcher1.setTextColor(ContextCompat.getColor(getContext(), newLikes <= 0 ? R.color.icon_active_unfocused : R.color.instagram_like));
                likesSwitcher2.setTextColor(ContextCompat.getColor(getContext(), newLikes <= 0 ? R.color.icon_active_unfocused : R.color.instagram_like));
                animateLike(true);
            } else if (sign == 2) {
                // Disliked > Liked
                likesSwitcher1.setTextColor(ContextCompat.getColor(getContext(), newLikes <= 0 ? R.color.icon_active_unfocused : R.color.instagram_like));
                likesSwitcher2.setTextColor(ContextCompat.getColor(getContext(), newLikes <= 0 ? R.color.icon_active_unfocused : R.color.instagram_like));
                animateLike(true);
            }
        }

    /*private void dislikeEntry(int likes, int sign) {
        int newLikes = likes + sign;
        likesValue.setText(String.valueOf(newLikes));
    }*/

        private void animateLike(boolean state) {
            heartIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), state ? R.drawable.ic_heart : R.drawable.ic_heart_outline, state ? R.color.instagram_like : R.color.icon_active_unfocused));
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(heartIcon, "scaleX", 0.2f, 1f),
                    ObjectAnimator.ofFloat(heartIcon, "scaleY", 0.2f, 1f)
            );
            set.setDuration(350);
            set.setInterpolator(new OvershootInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                }
            });
            set.start();
        }

        private void doubleTapLike() {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(doubleTapHeart, "alpha", 0.5F, 1F),
                    ObjectAnimator.ofFloat(doubleTapHeart, "scaleX", 0.25F, 3F),
                    ObjectAnimator.ofFloat(doubleTapHeart, "scaleY", 0.25F, 3F)
            );
            set.setDuration(450);
            set.setInterpolator(new OvershootInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation, boolean isReverse) {
                    doubleTapHeart.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    doubleTapHeart.setVisibility(INVISIBLE);
                }
            });
            set.start();
        }

    /*public class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat -1f);
        }
    }*/
    }
}