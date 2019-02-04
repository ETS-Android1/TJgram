package org.michaelbel.tjgram.ui.newphoto.view

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_photo.view.*
import org.michaelbel.tjgram.R
import java.io.File

class PhotoView : FrameLayout {

    companion object {
        const val PHOTO_SIZE = 320
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.item_photo, this)
    }

    fun setPhoto(file: File) {
        val thumb = Uri.fromFile(file)
        Picasso.get().load(thumb).placeholder(R.drawable.placeholder_rectangle).error(R.drawable.error_rectangle).resize(PHOTO_SIZE, PHOTO_SIZE).centerCrop().into(photo_view)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    /*fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            reduceImage(true)
        } else if (event.action == MotionEvent.ACTION_UP) {
            reduceImage(false)
        }

        return false
    }*/

    /*private fun reduceImage(event: Boolean) {
        val set = AnimatorSet()
        set.playTogether(
            ObjectAnimator.ofFloat(photoView, "scaleX", if (event) 1f else 0.9f, if (event) 0.9f else 1),
            ObjectAnimator.ofFloat(photoView, "scaleY", if (event) 1f else 0.9f, if (event) 0.9f else 1)
        )
        set.duration = 250
        set.start()
    }*/
}