package org.michaelbel.tjgram.presentation.common

import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

class PicassoImageLoader(private val picasso: Picasso): ImageLoader {

    override fun load(url: Uri, imageView: ImageView, callback: (Boolean) -> Unit) {
        picasso.load(url).into(imageView, FetchCallback(callback))
    }

    override fun load(url: String, imageView: ImageView, callback: (Boolean) -> Unit) {
        picasso.load(url).into(imageView, FetchCallback(callback))
    }

    override fun load(url: String, imageView: ImageView, fadeEffect: Boolean) {
        if (fadeEffect) {
            picasso.load(url).into(imageView)
        } else {
            picasso.load(url).noFade().into(imageView)
        }
    }

    override fun load(url: String, imageView: ImageView, placeholder: Int, error: Int) {
        picasso.load(url).placeholder(placeholder).error(error).into(imageView)
    }

    override fun load(url: String, imageView: ImageView, placeholder: Int, error: Int, transform: Transformation) {
        picasso.load(url).placeholder(placeholder).error(error).transform(transform).into(imageView)
    }

    override fun load(url: Uri, imageView: ImageView, placeholder: Int, error: Int, callback: (Boolean) -> Unit) {
        picasso.load(url).placeholder(placeholder).error(error).into(imageView, FetchCallback(callback))
    }

    private class FetchCallback(val delegate: (Boolean) -> Unit): Callback {

        override fun onError(e: Exception?) {
            delegate(false)
        }

        override fun onSuccess() {
            delegate(true)
        }
    }
}