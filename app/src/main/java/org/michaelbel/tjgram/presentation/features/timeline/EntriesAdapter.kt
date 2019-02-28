package org.michaelbel.tjgram.presentation.features.timeline

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.view.View.*
import android.view.animation.OvershootInterpolator
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_entry.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.core.imageloader.ImageLoader
import org.michaelbel.tjgram.core.imageloader.transform.CircleTransform
import org.michaelbel.tjgram.core.time.TimeFormatter
import org.michaelbel.tjgram.core.views.ViewUtil
import org.michaelbel.tjgram.data.api.results.LikesResult
import org.michaelbel.tjgram.data.entities.Author
import org.michaelbel.tjgram.data.entities.Entry
import org.michaelbel.tjgram.data.entities.Likes
import org.michaelbel.tjgram.data.net.UserConfig
import java.util.*

class EntriesAdapter(
        private val entriesListener: Listener,
        private val imageLoader: ImageLoader
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        internal const val PAYLOAD_LIKE = "like"
        internal const val PAYLOAD_DATE = "date"
        internal const val PAYLOAD_INTRO = "intro"
        internal const val PAYLOAD_TITLE = "title"

        internal const val GIF_LOOPING_PLAY = true

        internal const val LIKE_ANIM_DURATION = 350L
        internal const val DOUBLE_TAP_LIKE_ANIM_DURATION = 450L

        internal val INTERPOLATOR = OvershootInterpolator()
    }

    interface Listener {
        fun onAuthorClick(authorId: Int)
        fun onAuthorLongClick(authorId: Int): Boolean
        fun popupItemClick(itemId: Int, entryId: Int): Boolean
        fun doLoginFirst()
        fun likeEntry(entry: Entry, sign: Int)
    }

    private var measuredWidth = 0
    private var ratio = 0.0F

    private val entries = ArrayList<Entry>()

    fun setEntries(results: List<Entry>) {
        entries.addAll(results)
        notifyItemRangeInserted(entries.size + 1, results.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entry, parent, false)
        measuredWidth = parent.measuredWidth
        return EntriesViewHolder(view, entriesListener, imageLoader)
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

                // fixme тестировать
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

    fun swapEntries(newEntries: List<Entry>) {
        val diffUtils = EntriesDiffUtils(entries, newEntries)
        val diffResult = DiffUtil.calculateDiff(diffUtils)
        diffResult.dispatchUpdatesTo(this)
        entries.clear()
        entries.addAll(newEntries)
    }

    fun changeLikes(entry: Entry?) {
        val pos = entries.indexOf(entry)
        val payload = Bundle()
        payload.putSerializable(PAYLOAD_LIKE, entry?.likes)
        notifyItemChanged(pos, payload)
    }

    private inner class EntriesDiffUtils internal constructor(
            private val oldList: List<Entry>, private val newList: List<Entry>
    ): DiffUtil.Callback() {

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

    inner class EntriesViewHolder (override val containerView: View,
            private val entriesListener: Listener, private val imageLoader: ImageLoader
    ): RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val context: Context get() = containerView.context
        private val detector: GestureDetector = GestureDetector(context, GestureListener())

        fun bind(entry: Entry) {
            val author = entry.author ?: return
            updateAuthor(author)

            editorialIcon.setImageDrawable(ViewUtil.getIcon(context, R.drawable.ic_check_decagram, R.color.md_amber_500))
            editorialIcon.visibility = if (entry.isEditorial) VISIBLE else GONE

            pinImage.setImageDrawable(ViewUtil.getIcon(context, R.drawable.ic_pin, R.color.icon_active_unfocused))
            pinImage.visibility = if (entry.isPinned) VISIBLE else GONE

            menuImage.setImageDrawable(ViewUtil.getIcon(context, R.drawable.ic_dots_vertical, R.color.icon_active_unfocused))
            menuImage.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    showPopupMenu(it, entry.id)
                }
            }

            // todo Дата должна будет отобразиться как 'Unknown Date'
            val date = entry.dateRFC ?: ""
            updateDate(date)

            val cover = entry.cover
            mediaLayout.visibility = if (cover != null) VISIBLE else GONE
            mediaLayout.setOnTouchListener {_, event -> detector.onTouchEvent(event) }
            cover?.let {
                val coverW = it.size.width.toFloat()
                val coverH = cover.size.height.toFloat()
                ratio = coverW / coverH

                val measuredHeight = (measuredWidth / ratio)
                mediaLayout.layoutParams.width = measuredWidth
                mediaLayout.layoutParams.height = measuredHeight.toInt()
                mediaLayout.requestLayout()

                imageLoader.load(cover.thumbnailUrl, coverImage, R.drawable.placeholder_rectangle, R.drawable.error_rectangle)
                gifBadge.visibility = if (cover.additionalData.isGif()) VISIBLE else GONE
            }

            updateTitle(entry.title)
            updateIntro(entry.intro)

            heartImage.setOnClickListener {
                likeEntry(entry)
            }

            doubleTapHeart.setImageDrawable(ViewUtil.getIcon(context, R.drawable.ic_heart, R.color.double_heart))

            val likes = entry.likes ?: return
            updateLikes(likes)
            tickerView.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        }

        /**
         * Set up author name and avatarUrl safety.
         */
        private fun updateAuthor(author: Author) {
            authorName.text = author.name

            /**
             * 1. Аватарка пользователя обрезается по размеру родительской вьюхи: 40x40 dp.
             * 2. Плейсхолдер во время загрузки [R.drawable.placeholder_circle]
             * 3. Плейсхолдер во время ошибки [R.drawable.error_circle]
             * 4. У пользователя может быть аватарка, совпадающая по цвету с карточкой записи
             * (белая или прозрачная для текущей темы приложения), для этого вокруг аватарки
             * отрисовывается рамка шириной 2px [R.drawable.avatar_frame]
             */
            imageLoader.load(
                    author.avatarUrl, authorIcon,
                    R.dimen.entry_avatar_size, R.dimen.entry_avatar_size,
                    R.drawable.placeholder_circle, R.drawable.error_circle,
                    CircleTransform()
            )

            with(authorCard) {
                setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        entriesListener.onAuthorClick(author.id)
                    }
                }
                setOnLongClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        entriesListener.onAuthorLongClick(author.id)
                        return@setOnLongClickListener true
                    }
                    return@setOnLongClickListener false
                }
            }
        }

        fun updateDate(dateRFC: String) {
            dateText.text = TimeFormatter.getTimeAgo(context, dateRFC)
        }

        fun updateTitle(title: String) {
            entryTitle.text = title
        }

        fun updateIntro(intro: String) {
            introText.visibility = if (intro.isEmpty()) GONE else VISIBLE
            introText.text = intro
        }

        // For unauthorized users.
        fun updateLikes(likes: Likes) {
            heartImage.setImageDrawable(ViewUtil.getIcon(context, R.drawable.ic_heart_outline, R.color.icon_active_unfocused))
            tickerView.textColor = ContextCompat.getColor(context, R.color.icon_active_unfocused)
            tickerView.setText(likes.summ.toString(), false)
            updateLikesAuth(likes)
        }

        // For authorized users.
        private fun updateLikesAuth(likes: Likes) {
            when {
                likes.isLiked == LikesResult.Status.LIKED -> {
                    heartImage.setImageDrawable(ViewUtil.getIcon(context, R.drawable.ic_heart, R.color.instagram_like))
                    tickerView.textColor = ContextCompat.getColor(context, if (likes.summ <= 0) R.color.icon_active_unfocused else R.color.instagram_like)
                }
                likes.isLiked == LikesResult.Status.NEUTRAL -> {
                    heartImage.setImageDrawable(ViewUtil.getIcon(context, R.drawable.ic_heart_outline, R.color.icon_active_unfocused))
                    tickerView.textColor = ContextCompat.getColor(context, R.color.icon_active_unfocused)
                }
                likes.isLiked == LikesResult.Status.DISLIKED -> {
                    heartImage.setImageDrawable(ViewUtil.getIcon(context, R.drawable.ic_heart_outline, R.color.icon_active_unfocused))
                    tickerView.textColor = ContextCompat.getColor(context, R.color.icon_active_unfocused)
                }
            }
        }

        private fun likeEntryRuntime(likes: Int, sign: Int) {
            val newLikes = likes + sign
            tickerView.setText(newLikes.toString(), true)

            when (sign) {
                -1 -> {
                    // Liked > Neutral
                    animHeartIcon(false)
                    tickerView.textColor = ContextCompat.getColor(context, R.color.icon_active_unfocused)
                }
                1 -> {
                    // Neutral > Liked
                    animHeartIcon(true)
                    tickerView.textColor = ContextCompat.getColor(context, if (newLikes <= 0) R.color.icon_active_unfocused else R.color.instagram_like)
                }
                2 -> {
                    // Disliked > Liked
                    animHeartIcon(true)
                    tickerView.textColor = ContextCompat.getColor(context, if (newLikes <= 0) R.color.icon_active_unfocused else R.color.instagram_like)
                }
            }
        }

        /*private fun dislikeEntryRuntime(likes: Int, sign: Int) {
            val newLikes = likes + sign
            tickerView.setText(newLikes.toString(), true)

            when (sign) {
                -1 -> {
                    // Disliked > Neutral
                    animHeartIcon(false)
                    tickerView.textColor = ContextCompat.getColor(context, R.color.icon_active_unfocused)
                }
                -2 -> {
                    // Disliked > Liked
                    animHeartIcon(true)
                    tickerView.textColor = ContextCompat.getColor(context, if (newLikes <= 0) R.color.icon_active_unfocused else R.color.instagram_like)
                }
                1 -> {
                    // Liked > Disliked
                    animHeartIcon(true)
                    tickerView.textColor = ContextCompat.getColor(context, if (newLikes <= 0) R.color.icon_active_unfocused else R.color.instagram_like)
                }
            }
        }*/

        private fun animHeartIcon(state: Boolean) {
            heartImage.setImageDrawable(ViewUtil.getIcon(context,
                    if (state) R.drawable.ic_heart else R.drawable.ic_heart_outline,
                    if (state) R.color.instagram_like else R.color.icon_active_unfocused)
            )
            val set = AnimatorSet()
            set.playTogether(
                    ObjectAnimator.ofFloat(heartImage, "scaleX", 0.2F, 1F),
                    ObjectAnimator.ofFloat(heartImage, "scaleY", 0.2F, 1F)
            )
            set.duration = LIKE_ANIM_DURATION
            set.interpolator = INTERPOLATOR
            set.start()
        }

        private fun animDoubleTapLike() {
            val set = AnimatorSet()
            set.playTogether(
                    ObjectAnimator.ofFloat(doubleTapHeart, "alpha", 0.5f, 1f),
                    ObjectAnimator.ofFloat(doubleTapHeart, "scaleX", 0.25f, 3f),
                    ObjectAnimator.ofFloat(doubleTapHeart, "scaleY", 0.25f, 3f)
            )
            set.duration = DOUBLE_TAP_LIKE_ANIM_DURATION
            set.interpolator = INTERPOLATOR
            set.addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator, isReverse: Boolean) {
                    doubleTapHeart.visibility = VISIBLE
                }

                override fun onAnimationEnd(animation: Animator) {
                    doubleTapHeart.visibility = INVISIBLE
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
                        likes.isLiked == LikesResult.Status.NEUTRAL -> likeEntryRuntime(likes.summ, 1)
                        likes.isLiked == LikesResult.Status.LIKED -> likeEntryRuntime(likes.summ, -1)
                        likes.isLiked == LikesResult.Status.DISLIKED -> likeEntryRuntime(likes.summ, 2)
                    }

                    entriesListener.likeEntry(entry, if (likes.isLiked == LikesResult.Status.LIKED) 0 else 1)
                }
            }
        }

        /*private fun dislikeEntry(entry: Entry) {
            if (!UserConfig.isAuthorized(context)) {
                entriesListener.doLoginFirst()
                return
            }

            if (adapterPosition != RecyclerView.NO_POSITION) {
                val likes = entry.likes
                if (likes != null) {
                    when {
                        likes.isLiked == LikesResult.Status.NEUTRAL -> likeEntry(likes.summ, -1)
                        likes.isLiked == LikesResult.Status.LIKED -> likeEntry(likes.summ, -2)
                        likes.isLiked == LikesResult.Status.DISLIKED -> likeEntry(likes.summ, 1)
                    }

                    entriesListener.likeEntry(entry, if (likes.isLiked == LikesResult.Status.DISLIKED) 0 else -1)
                }
            }
        }*/

        private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

            override fun onDown(e: MotionEvent): Boolean = true

            override fun onDoubleTap(e: MotionEvent): Boolean {
                heartImage.performClick()
                animDoubleTapLike()
                return true
            }
        }
    }
}