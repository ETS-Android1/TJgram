package org.michaelbel.tjgram.modules.timeline.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.animation.OvershootInterpolator
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_entry.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.data.UserConfig
import org.michaelbel.tjgram.data.consts.Liked
import org.michaelbel.tjgram.data.entities.Entry
import org.michaelbel.tjgram.data.entities.Likes
import org.michaelbel.tjgram.utils.DeviceUtil
import org.michaelbel.tjgram.utils.ViewUtil
import org.michaelbel.tjgram.utils.date.TimeFormatter
import org.michaelbel.tjgram.utils.picasso.CircleTransform
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

    override fun getItemCount(): Int = entries.size

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

    class EntriesDiffUtils internal constructor(private val oldList: List<Entry>, private val newList: List<Entry>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

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

    class EntriesViewHolder (override val containerView: View, listener: EntriesListener) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        companion object {
            private const val GIF_LOOPING_PLAY = true

            private const val LIKE_ANIM_DURATION = 350L
            private const val DOUBLE_TAP_LIKE_ANIM_DURATION = 450L

            private val INTERPOLATOR = OvershootInterpolator()
        }

        private val context: Context get() = containerView.context

        private var entriesListener: EntriesListener = listener
        private val detector: GestureDetector = GestureDetector(context, GestureListener())

        fun bind(entry: Entry) {
            val author = entry.author!!
            Picasso.get().load(author.avatarUrl).placeholder(R.drawable.placeholder_circle).error(R.drawable.error_circle).transform(CircleTransform()).into(authorIcon)
            authorName.text = author.name

            authorCard.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    entriesListener.onAuthorClick(author.id)
                }
            }

            authorCard.setOnLongClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    entriesListener.onAuthorLongClick(author.id)
                    return@setOnLongClickListener true
                }
                return@setOnLongClickListener false
            }

            pinImage.setImageDrawable(ViewUtil.getIcon(context, R.drawable.ic_pin, R.color.icon_active_unfocused))
            pinImage.visibility = if (entry.isPinned) View.VISIBLE else View.GONE

            menuImage.setImageDrawable(ViewUtil.getIcon(context, R.drawable.ic_dots_vertical, R.color.icon_active_unfocused))
            menuImage.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    showPopupMenu(it, entry.id)
                }
            }

            updateDate(entry.dateRFC)

            val cover = entry.cover
            mediaLayout.visibility = if (cover != null) View.VISIBLE else View.GONE
            mediaLayout.setOnTouchListener {_, event -> detector.onTouchEvent(event) }

            if (cover != null) {
                if (cover.isImage()) {
                    if (cover.additionalData.isGif()) {
                        gifLayout.visibility = View.VISIBLE

                        videoView.layoutParams.height = DeviceUtil.dp(context, cover.size.height.toFloat())
                        videoView.setVideoPath(cover.url)
                        videoView.seekTo(1)
                        videoView.setOnPreparedListener { mp ->
                            mp.isLooping = GIF_LOOPING_PLAY
                            videoView.start()
                        }
                        videoView.setOnErrorListener {_,_,_ -> true }
                    } else if (cover.additionalData.isImage()) {
                        gifLayout.visibility = View.GONE

                        // fixme высота медиа не должна превышать width * 1.5
                        Picasso.get().load(cover.thumbnailUrl)/*.resize(50, 50).centerCrop()*/
                                .placeholder(R.drawable.placeholder_rectangle)
                                .error(R.drawable.error_rectangle)
                                .into(coverImage)
                    }
                }
            }

            updateTitle(entry.title)
            updateIntro(entry.intro)

            heartImage.setOnClickListener {
                likeEntry(entry)
            }

            doubleTapHeart.setImageDrawable(ViewUtil.getIcon(context, R.drawable.ic_heart, R.color.double_heart))

            val likes = entry.likes
            updateLikes(likes)
        }

        fun updateDate(dateRFC: String?) {
            val date = TimeFormatter.getTimeAgo(context, dateRFC)
            dateText.text = date
        }

        fun updateTitle(title: String) {
            entryTitle.text = title
        }

        fun updateIntro(intro: String) {
            introText.visibility = if (intro.isEmpty()) View.GONE else View.VISIBLE
            introText.text = intro
        }

        // For unauthorized users.
        fun updateLikes(likes: Likes?) {
            likesCount.setCurrentText(likes?.summ.toString())
            textLike1.setTextColor(ContextCompat.getColor(context, R.color.icon_active_unfocused))
            textLike2.setTextColor(ContextCompat.getColor(context, R.color.icon_active_unfocused))
            heartImage.setImageDrawable(ViewUtil.getIcon(context, R.drawable.ic_heart_outline, R.color.icon_active_unfocused))
            updateLikesAuth(likes)
        }

        // For authorized users.
        private fun updateLikesAuth(likes: Likes?) {
            when {
                likes?.isLiked == Liked.LIKED -> {
                    textLike1.setTextColor(ContextCompat.getColor(context, if (likes.summ <= 0) R.color.icon_active_unfocused else R.color.instagram_like))
                    textLike2.setTextColor(ContextCompat.getColor(context, if (likes.summ <= 0) R.color.icon_active_unfocused else R.color.instagram_like))
                    heartImage.setImageDrawable(ViewUtil.getIcon(context, R.drawable.ic_heart, R.color.instagram_like))
                }
                likes?.isLiked == Liked.NEUTRAL -> {
                    textLike1.setTextColor(ContextCompat.getColor(context, R.color.icon_active_unfocused))
                    textLike2.setTextColor(ContextCompat.getColor(context, R.color.icon_active_unfocused))
                    heartImage.setImageDrawable(ViewUtil.getIcon(context, R.drawable.ic_heart_outline, R.color.icon_active_unfocused))
                }
                likes?.isLiked == Liked.DISLIKED -> {
                    textLike1.setTextColor(ContextCompat.getColor(context, R.color.icon_active_unfocused))
                    textLike2.setTextColor(ContextCompat.getColor(context, R.color.icon_active_unfocused))
                    heartImage.setImageDrawable(ViewUtil.getIcon(context, R.drawable.ic_heart_outline, R.color.icon_active_unfocused))
                }
            }
        }

        private fun likeEntry(likes: Int, sign: Int) {
            val newLikes = likes + sign
            likesCount.setText(newLikes.toString())

            when (sign) {
                -1 -> {
                    // Liked > Neutral
                    textLike1.setTextColor(ContextCompat.getColor(context, R.color.icon_active_unfocused))
                    textLike2.setTextColor(ContextCompat.getColor(context, R.color.icon_active_unfocused))
                    animateLike(false)
                }
                1 -> {
                    // Neutral > Liked
                    textLike1.setTextColor(ContextCompat.getColor(context, if (newLikes <= 0) R.color.icon_active_unfocused else R.color.instagram_like))
                    textLike2.setTextColor(ContextCompat.getColor(context, if (newLikes <= 0) R.color.icon_active_unfocused else R.color.instagram_like))
                    animateLike(true)
                }
                2 -> {
                    // Disliked > Liked
                    textLike1.setTextColor(ContextCompat.getColor(context, if (newLikes <= 0) R.color.icon_active_unfocused else R.color.instagram_like))
                    textLike2.setTextColor(ContextCompat.getColor(context, if (newLikes <= 0) R.color.icon_active_unfocused else R.color.instagram_like))
                    animateLike(true)
                }
            }
        }

        /*private void dislikeEntry(int likes, int sign) {
            int newLikes = likes + sign;
            likesValue.setText(String.valueOf(newLikes));
        }*/

        private fun animateLike(state: Boolean) {
            heartImage.setImageDrawable(ViewUtil.getIcon(context, if (state) R.drawable.ic_heart else R.drawable.ic_heart_outline, if (state) R.color.instagram_like else R.color.icon_active_unfocused))
            val set = AnimatorSet()
            set.playTogether(
                    ObjectAnimator.ofFloat(heartImage, "scaleX", 0.2F, 1F),
                    ObjectAnimator.ofFloat(heartImage, "scaleY", 0.2F, 1F)
            )
            set.duration = LIKE_ANIM_DURATION
            set.interpolator = INTERPOLATOR
            set.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {}
            })
            set.start()
        }

        private fun doubleTapLike() {
            val set = AnimatorSet()
            set.playTogether(
                    ObjectAnimator.ofFloat(doubleTapHeart, "alpha", 0.5f, 1f),
                    ObjectAnimator.ofFloat(doubleTapHeart, "scaleX", 0.25f, 3f),
                    ObjectAnimator.ofFloat(doubleTapHeart, "scaleY", 0.25f, 3f)
            )
            set.duration = DOUBLE_TAP_LIKE_ANIM_DURATION
            set.interpolator = INTERPOLATOR
            set.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator, isReverse: Boolean) {
                    doubleTapHeart.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animator) {
                    doubleTapHeart.visibility = View.INVISIBLE
                }
            })
            set.start()
        }

        private fun showPopupMenu(view: View, entryId: Int) {
            val popupMenu = PopupMenu(view.context, view, Gravity.END)
            popupMenu.inflate(R.menu.popup_entry)
            popupMenu.setOnMenuItemClickListener { item ->
                entriesListener.popupItemClick(item.itemId, entryId)
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }

        private fun likeEntry(entry: Entry) {
            if (!UserConfig.isAuthorized(context)) {
                entriesListener.doLoginFirst()
                return
            }

            if (adapterPosition != RecyclerView.NO_POSITION) {
                val likes = entry.likes
                if (likes != null) {
                    when {
                        likes.isLiked == Liked.NEUTRAL -> likeEntry(likes.summ, 1)
                        likes.isLiked == Liked.LIKED -> likeEntry(likes.summ, -1)
                        likes.isLiked == Liked.DISLIKED -> likeEntry(likes.summ, 2)
                    }

                    entriesListener.likeEntry(entry, if (likes.isLiked == Liked.LIKED) 0 else 1)
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

        private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

            override fun onDown(e: MotionEvent): Boolean = true

            override fun onDoubleTap(e: MotionEvent): Boolean {
                heartImage.performClick()
                doubleTapLike()
                return true
            }
        }
    }
}