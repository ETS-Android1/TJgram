package org.michaelbel.tjgram.ui.main.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import org.michaelbel.tjgram.utils.FileUtil;
import org.michaelbel.tjgram.utils.ViewUtil;
import org.michaelbel.tjgram.utils.picasso.CircleTransform;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

@SuppressWarnings("unused")
public class EntryView extends LinearLayoutCompat {

    private static final boolean GIF_LOOPING_PLAY = true;

    private CardView authorLayout;
    private AppCompatImageView authorIcon;
    private AppCompatTextView authorName;
    private AppCompatTextView dateText;

    private AppCompatImageView pinIcon;
    private AppCompatImageView menuIcon;

    private FrameLayout mediaLayout;
    private AppCompatImageView coverImage;
    private FrameLayout gifLayout;
    private VideoView videoView;

    private AppCompatTextView titleText;
    private AppCompatTextView introText;

    private AppCompatImageView heartIcon;
    private TextSwitcher likesValue;
    private TextView likesSwitcher1;
    private TextView likesSwitcher2;

    public CardView getAuthorLayout() {
        return authorLayout;
    }

    public AppCompatImageView getMenuIcon() {
        return menuIcon;
    }

    public ImageView getCoverImage() {
        return coverImage;
    }

    public AppCompatImageView getHeartIcon() {
        return heartIcon;
    }

    public EntryView(@NonNull Context context) {
        super(context);
        initialize(context);
    }

    public EntryView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_entry, this);

        pinIcon = findViewById(R.id.pin_icon);
        pinIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), R.drawable.ic_pin, R.color.icon_active_unfocused));

        menuIcon = findViewById(R.id.menu_icon);
        menuIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), R.drawable.ic_dots_vertical, R.color.icon_active_unfocused));

        authorLayout = findViewById(R.id.author_layout);
        authorIcon = findViewById(R.id.author_icon);
        authorName = findViewById(R.id.author_name);
        dateText = findViewById(R.id.date_text);

        mediaLayout = findViewById(R.id.media_layout);
        coverImage = findViewById(R.id.entry_cover);
        gifLayout = findViewById(R.id.gif_layout);
        videoView = findViewById(R.id.video_view);

        titleText = findViewById(R.id.title_text);
        introText = findViewById(R.id.intro_text);

        heartIcon = findViewById(R.id.like_icon);
        likesValue = findViewById(R.id.likes_text);
        likesSwitcher1 = findViewById(R.id.text_like1);
        likesSwitcher2 = findViewById(R.id.text_like2);
    }

    public void bind(@NonNull Entry entry) {
        Author author = entry.author;
        setAuthor(author);

        pinIcon.setVisibility(entry.isPinned ? VISIBLE : GONE);

        setDate(entry.dateRFC);

        Integer date = entry.date;

        Cover cover = entry.cover;
        mediaLayout.setVisibility(cover != null ? VISIBLE : GONE);

        if (cover != null) {
            if (cover.type == Cover.TYPE_IMAGE) {
                if (cover.additionalData != null) {
                    String imageType = cover.additionalData.type;

                    if (FileUtil.INSTANCE.isGif(imageType)) {
                        setGif(cover.url);
                    } else if (FileUtil.INSTANCE.isImage(imageType)) {
                        setImage(cover.thumbnailUrl);
                    }
                } // else additional data -> null
            } // else type -> video
        }

        setTitle(entry.title);
        setIntro(entry.intro);

        Likes likes = entry.likes;
        setLikes(likes);
    }

    private void setAuthor(Author author) {
        Picasso.get().load(author.getAvatarUrl()).placeholder(R.drawable.placeholder_circle).error(R.drawable.error_circle).transform(new CircleTransform()).into(authorIcon);
        authorName.setText(author.getName());
    }

    public void setDate(String dateRFC) {
        //String date = DateUtil.getTimeAgo(getContext(), dateRFC);
        //dateText.setText(date);
    }

    private void setGif(String gifUrl) {
        gifLayout.setVisibility(VISIBLE);
        coverImage.setVisibility(GONE);

        //videoView.getLayoutParams().height = DeviceUtil.INSTANCE.dp(getContext(), cover.size.getHeight());
        videoView.setVideoPath(gifUrl);
        videoView.seekTo(1);
        videoView.setOnErrorListener((mp, what, extra) -> true);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(GIF_LOOPING_PLAY);
            videoView.start();
        });
    }

    private void setImage(String imageUrl) {
        //coverImage.setVisibility(VISIBLE);
        //gifLayout.setVisibility(GONE);

        /*boolean centerCrop = cover.size.getHeight() > cover.size.getWidth();

                        if (centerCrop) {
                            Picasso.get().load(cover.thumbnailUrl)
                                    //.resize((int) cover.size.width, (int) cover.size.width)
                                    .fit().centerCrop()
                                    .placeholder(R.color.placeholder)
                                    .into(coverImage);
                        } else {
                            Picasso.get().load(cover.thumbnailUrl).placeholder(R.color.placeholder).error(R.color.error).into(coverImage);
                        }*/

        Picasso.get().load(imageUrl).placeholder(R.drawable.placeholder_rectangle).error(R.drawable.error_rectangle).into(coverImage);
    }

    public void setTitle(String title) {
        titleText.setText(title);
    }

    public void setIntro(String intro) {
        introText.setVisibility(intro.isEmpty() ? GONE : VISIBLE);
        introText.setText(intro);
    }

    // For unauthorized users.
    public void setLikes(Likes likes) {
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

    public void likeEntry(int likes, int sign) {
        int newLikes = likes + sign;
        //likesValue.setCurrentText(String.valueOf(newLikes));
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

    public void dislikeEntry(int likes, int sign) {
        int newLikes = likes + sign;
        //likesValue.setCurrentText(String.valueOf(newLikes));
        likesValue.setText(String.valueOf(newLikes));

        if (sign == -1) {
            // Neutral > Disliked
            heartIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), R.drawable.ic_heart_outline, R.color.icon_active_unfocused));
            likesSwitcher1.setTextColor(ContextCompat.getColor(getContext(), R.color.icon_active_unfocused));
            likesSwitcher2.setTextColor(ContextCompat.getColor(getContext(), R.color.icon_active_unfocused));
        } else if (sign == 1) {
            // Disliked > Neutral
            heartIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), R.drawable.ic_heart_outline, R.color.icon_active_unfocused));
            likesSwitcher1.setTextColor(ContextCompat.getColor(getContext(), R.color.icon_active_unfocused));
            likesSwitcher2.setTextColor(ContextCompat.getColor(getContext(), R.color.icon_active_unfocused));
        } else if (sign == -2) {
            // Liked > Disliked
            heartIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), R.drawable.ic_heart_outline, R.color.icon_active_unfocused));
            likesSwitcher1.setTextColor(ContextCompat.getColor(getContext(), R.color.icon_active_unfocused));
            likesSwitcher2.setTextColor(ContextCompat.getColor(getContext(), R.color.icon_active_unfocused));
        }
    }

    private void animateLike(boolean state) {
        heartIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(getContext(), state ? R.drawable.ic_heart : R.drawable.ic_heart_outline, state ? R.color.instagram_like : R.color.icon_active_unfocused));

        ObjectAnimator progressAnimX = ObjectAnimator.ofFloat(heartIcon, "scaleX", 0.2f, 1f);
        ObjectAnimator progressAnimY = ObjectAnimator.ofFloat(heartIcon, "scaleY", 0.2f, 1f);

        AnimatorSet set = new AnimatorSet();
        set.play(progressAnimX).with(progressAnimY);
        set.setDuration(350);
        set.setInterpolator(new OvershootInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });
        set.start();
    }
}