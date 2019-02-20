package org.michaelbel.tjgram.presentation.common

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.squareup.picasso.Transformation

interface ImageLoader {
    fun load(url: Uri, imageView: ImageView, callback: (Boolean) -> Unit)
    fun load(url: String, imageView: ImageView, callback: (Boolean) -> Unit)
    fun load(url: String, imageView: ImageView, fadeEffect: Boolean = true)
    fun load(url: String, imageView: ImageView, @DrawableRes placeholder: Int, @DrawableRes error: Int)
    fun load(url: String, imageView: ImageView, @DrawableRes placeholder: Int, @DrawableRes error: Int, transform: Transformation)
    fun load(url: Uri, imageView: ImageView, @DrawableRes placeholder: Int, @DrawableRes error: Int, callback: (Boolean) -> Unit)
}