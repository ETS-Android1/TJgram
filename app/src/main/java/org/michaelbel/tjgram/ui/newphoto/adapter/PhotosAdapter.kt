package org.michaelbel.tjgram.ui.newphoto.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.michaelbel.tjgram.ui.newphoto.view.PhotoView
import org.michaelbel.tjgram.utils.recycler.DebouncingOnClickListener
import java.io.File
import java.util.*

class PhotosAdapter(
    private val photoListener: PhotoClickListener) : RecyclerView.Adapter<PhotoViewHolder>() {

    companion object {
        const val SPAN_COUNT = 4
        const val PHOTO_SIZE = 320
    }

    private val photos = ArrayList<File>()

    fun addPhotos(photos: List<File>) {
        this.photos.addAll(photos)
        notifyDataSetChanged()
        //notifyItemRangeInserted(this.photos.size + 1, photos.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = PhotoView(parent.context)
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        val holder = PhotoViewHolder(view)
        view.setOnClickListener(object : DebouncingOnClickListener() {
            override fun doClick(v: View) {
                val position = holder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    photoListener.onPhotoClick(photos[position])
                }
            }
        })
        return holder
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val view = holder.itemView as PhotoView
        view.setPhoto(photos[position])
        /*Picasso.get().load(Uri.fromFile(photos[position]))
            .placeholder(R.drawable.placeholder_rectangle)
            .error(R.drawable.error_rectangle)
            .resize(PHOTO_SIZE, PHOTO_SIZE).centerCrop()
            .into(holder.itemView.photo_view)*/
    }

    override fun getItemCount(): Int {
        return photos.size
    }
}