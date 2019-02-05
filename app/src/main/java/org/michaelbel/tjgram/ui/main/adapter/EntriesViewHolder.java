package org.michaelbel.tjgram.ui.main.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import org.michaelbel.tjgram.R;
import org.michaelbel.tjgram.data.entity.Author;
import org.michaelbel.tjgram.data.entity.Cover;
import org.michaelbel.tjgram.data.entity.Entry;
import org.michaelbel.tjgram.data.entity.Likes;
import org.michaelbel.tjgram.data.enums.LikesKt;
import org.michaelbel.tjgram.utils.date.TimeFormatter;
import org.michaelbel.tjgram.utils.DeviceUtil;
import org.michaelbel.tjgram.utils.FileUtil;
import org.michaelbel.tjgram.utils.ViewUtil;
import org.michaelbel.tjgram.utils.picasso.CircleTransform;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

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
        pinIcon = view.findViewById(R.id.pin_icon);
        pinIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), R.drawable.ic_pin, R.color.icon_active_unfocused));

        menuIcon = view.findViewById(R.id.menu_icon);
        menuIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), R.drawable.ic_dots_vertical, R.color.icon_active_unfocused));

        authorLayout = view.findViewById(R.id.author_layout);
        authorIcon = view.findViewById(R.id.author_icon);
        authorName = view.findViewById(R.id.author_name);
        dateText = view.findViewById(R.id.date_text);

        mediaLayout = view.findViewById(R.id.media_layout);
        coverImage = view.findViewById(R.id.entry_cover);
        gifLayout = view.findViewById(R.id.gif_layout);
        videoView = view.findViewById(R.id.video_view);
        entryTitle = view.findViewById(R.id.title_text);
        introText = view.findViewById(R.id.intro_text);

        heartIcon = view.findViewById(R.id.like_icon);
        likesValue = view.findViewById(R.id.likes_text);
        likesSwitcher1 = view.findViewById(R.id.text_like1);
        likesSwitcher2 = view.findViewById(R.id.text_like2);

        doubleTapHeart = view.findViewById(R.id.like_image);
        doubleTapHeart.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), R.drawable.ic_heart, R.color.double_heart));

        detector = new GestureDetector(getContext(), new GestureListener());
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
        if (likes.isLiked == LikesKt.LIKED) {
            likesSwitcher1.setTextColor(ContextCompat.getColor(getContext(), likes.summ <= 0 ? R.color.icon_active_unfocused : R.color.instagram_like));
            likesSwitcher2.setTextColor(ContextCompat.getColor(getContext(), likes.summ <= 0 ? R.color.icon_active_unfocused : R.color.instagram_like));
            heartIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), R.drawable.ic_heart, R.color.instagram_like));
        } else if (likes.isLiked == LikesKt.NEUTRAL) {
            likesSwitcher1.setTextColor(ContextCompat.getColor(getContext(), R.color.icon_active_unfocused));
            likesSwitcher2.setTextColor(ContextCompat.getColor(getContext(), R.color.icon_active_unfocused));
            heartIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), R.drawable.ic_heart_outline, R.color.icon_active_unfocused));
        } else if (likes.isLiked == LikesKt.DISLIKED) {
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