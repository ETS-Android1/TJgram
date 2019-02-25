package org.michaelbel.tjgram.core.imageloader

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
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

    /**
     * centerCrop пока не указан в параметрах.
     * todo добавить его в Builder
     */
    override fun load(url: Uri, imageView: ImageView, width: Int, height: Int, placeholder: Int, error: Int) {
        picasso.load(url).resizeDimen(width, height).centerCrop().placeholder(placeholder).error(error).into(imageView)
    }

    override fun load(url: String, imageView: ImageView, width: Int, height: Int, placeholder: Int, error: Int, transform: Transformation) {
        picasso.load(url).resizeDimen(width, height).placeholder(placeholder).error(error).transform(transform).into(imageView)
    }

    override fun load(url: String, imageView: ImageView, transform: Transformation) {
        picasso.load(url).transform(transform).into(imageView)
    }

    override fun load(url: String, imageView: ImageView, placeholder: Int, error: Int, transform: Transformation) {
        picasso.load(url).placeholder(placeholder).error(error).transform(transform).into(imageView)
    }

    override fun load(url: Uri, imageView: ImageView, placeholder: Int, error: Int, callback: (Boolean) -> Unit) {
        picasso.load(url).placeholder(placeholder).error(error).into(imageView, FetchCallback(callback))
    }

    override fun load(url: String, placeholder: Int, error: Int, callback: Target) {
        picasso.load(url).placeholder(placeholder).error(error).into(callback)
    }

    private class FetchCallback(val delegate: (Boolean) -> Unit): Callback {

        override fun onError(e: Exception?) {
            delegate(false)
        }

        override fun onSuccess() {
            delegate(true)
        }
    }

    private class FetchTarget: Target {

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

        }

        override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {

        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

        }
    }
}