package org.michaelbel.tjgram.core.imageload

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.squareup.picasso.Target
import com.squareup.picasso.Transformation

// fixme вместо этой каши использовать Builder
interface ImageLoader {
    fun load(url: Uri, imageView: ImageView, callback: (Boolean) -> Unit)
    fun load(url: String, imageView: ImageView, callback: (Boolean) -> Unit)
    fun load(url: String, imageView: ImageView, fadeEffect: Boolean = true)
    fun load(url: String, imageView: ImageView, @DrawableRes placeholder: Int, @DrawableRes error: Int)
    fun load(url: String, imageView: ImageView, @DrawableRes placeholder: Int, @DrawableRes error: Int, transform: Transformation)
    fun load(url: Uri, imageView: ImageView, @DrawableRes placeholder: Int, @DrawableRes error: Int, callback: (Boolean) -> Unit)
    fun load(url: String, @DrawableRes placeholder: Int, @DrawableRes error: Int, callback: Target)
}