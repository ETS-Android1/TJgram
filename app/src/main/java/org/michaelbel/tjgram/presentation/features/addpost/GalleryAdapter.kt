package org.michaelbel.tjgram.presentation.features.addpost

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_camera.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.core.imageloader.ImageLoader
import org.michaelbel.tjgram.core.views.ViewUtil
import java.io.File
import java.util.*

class GalleryAdapter(
        private val listener: Listener,
        private val imageLoader: ImageLoader
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_MEDIA = 1
        private const val VIEW_TYPE_CAMERA = 2
        private const val VIEW_TYPE_GALLERY = 3
    }

    interface Listener {
        fun onPhotoClick(photo: File)
        fun onCameraClick()
        fun onGalleryClick()
    }

    private val photos = ArrayList<File>()

    fun swapData(list: List<File>) {
        photos.addAll(list)
        photos.add(0, File(""))
        photos.add(photos.size - 1, File(""))
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = photos.size

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_CAMERA
            photos.size - 1 -> VIEW_TYPE_GALLERY
            else -> VIEW_TYPE_MEDIA
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_MEDIA) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false)
            MediaViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_camera, parent, false)
            BucketViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = photos[position].toUri()

        when {
            getItemViewType(position) == VIEW_TYPE_CAMERA -> {
                val viewHolder = holder as BucketViewHolder
                viewHolder.bind(R.string.item_camera, R.drawable.ic_camera)
            }
            getItemViewType(position) == VIEW_TYPE_GALLERY -> {
                val viewHolder = holder as BucketViewHolder
                viewHolder.bind(R.string.item_gallery, R.drawable.ic_image_outline)
            }
            else -> {
                val viewHolder = holder as MediaViewHolder
                viewHolder.bind(data)
            }
        }
    }

    private inner class BucketViewHolder(override val containerView: View):
            RecyclerView.ViewHolder(containerView), View.OnClickListener, LayoutContainer {

        fun bind(@StringRes textId: Int, @DrawableRes iconId: Int) {
            containerView.setOnClickListener(this)
            textView.text = containerView.context.getString(textId)
            imageView.setImageDrawable(ViewUtil.getIcon(containerView.context, iconId, R.color.icon_active))
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (adapterPosition == 0) {
                    listener.onCameraClick()
                } else {
                    listener.onGalleryClick()
                }
            }
        }
    }

    private inner class MediaViewHolder(override val containerView: View):
            RecyclerView.ViewHolder(containerView), View.OnClickListener, LayoutContainer {

        fun bind(image: Uri) {
            containerView.setOnClickListener(this)
            imageLoader.load(
                    image, imageView,
                    R.dimen.post_photo_view_size, R.dimen.post_photo_view_size,
                    R.drawable.placeholder_rectangle, R.drawable.error_rectangle
            )
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onPhotoClick(photos[adapterPosition])
            }
        }
    }
}