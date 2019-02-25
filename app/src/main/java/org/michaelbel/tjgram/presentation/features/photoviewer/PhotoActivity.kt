package org.michaelbel.tjgram.presentation.features.photoviewer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.appcompat.app.AppCompatActivity
import com.alexvasilkov.events.Events
import com.alexvasilkov.gestures.GestureController
import com.alexvasilkov.gestures.animation.ViewPosition
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_photo.*
import org.michaelbel.tjgram.R

class PhotoActivity: AppCompatActivity() {

    companion object {
        private const val EXTRA_POSITION = "position"
        private const val EXTRA_IMAGE_URL = "image_url"

        private const val EXIT_WITH_ANIM = true

        fun show(from: Activity, position: ViewPosition, imageUrl: String) {
            val intent = Intent(from, PhotoActivity::class.java)
            intent.putExtra(EXTRA_POSITION, position.pack())
            intent.putExtra(EXTRA_IMAGE_URL, imageUrl)
            from.startActivity(intent)
            from.overridePendingTransition(0, 0)
        }
    }

    private val isSystemUiShown: Boolean
        get() = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN != 0

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_photo, menu)
        return super.onCreateOptionsMenu(menu)
    }*/

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when {
            item.itemId == R.id.item_save -> Toast.makeText(this, "Saving photo to downloads", Toast.LENGTH_SHORT).show()
            item.itemId == R.id.item_share -> Toast.makeText(this, "Sharing photo", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this, "Copy photo link", Toast.LENGTH_SHORT).show()
        }

        return super.onOptionsItemSelected(item)
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        //initToolbar()

        photoView.setOnClickListener {
            showSystemUI(isSystemUiShown)
        }

        photoView.controller.setOnGesturesListener(object: GestureController.OnGestureListener {
            override fun onDown(event: MotionEvent) {
                // Зажали фото пальцем и двигаем вверх-вниз
            }

            override fun onUpOrCancel(event: MotionEvent) {
                // Отпустили фото и при этом оно может остаться в просмотре или закрыться
            }

            override fun onSingleTapUp(event: MotionEvent): Boolean = false

            override fun onSingleTapConfirmed(event: MotionEvent): Boolean = false

            override fun onLongPress(event: MotionEvent) {}

            override fun onDoubleTap(event: MotionEvent): Boolean = false
        })

        photoView.visibility = View.INVISIBLE
        backgroundView.visibility = View.INVISIBLE

        val avatarUrl = intent.getStringExtra(EXTRA_IMAGE_URL)
        Picasso.get().load(avatarUrl).into(photoView)

        photoView.positionAnimator.addPositionUpdateListener { position, isLeaving ->
            this@PhotoActivity.applyImageAnimationState(position, isLeaving) }

        runAfterImageDraw(Runnable {
            this@PhotoActivity.enterFullImage(savedInstanceState == null)
            Events.create("show_image").param(false).post()
        })
    }

    /*private fun initToolbar() {
        toolbar.navigationIcon = ViewUtil.getIcon(this, R.drawable.ic_arrow_back, R.color.primary)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        setSupportActionBar(toolbar)

        supportActionBar?.setTitle(getString(R.string.photo_of, 1, 1))

        appBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        val params: CoordinatorLayout.LayoutParams = appBarLayout.layoutParams
        params.topMargin = DeviceUtil.statusBarHeight(this)

        ViewCompat.setElevation(appBarLayout, DeviceUtil.dp(this, 0F).toFloat())
    }*/

    override fun onBackPressed() {
        if (!photoView!!.positionAnimator.isLeaving) {
            photoView!!.positionAnimator.exit(EXIT_WITH_ANIM)
        }
    }

    private fun enterFullImage(animate: Boolean) {
        val position = ViewPosition.unpack(intent.getStringExtra(EXTRA_POSITION))
        photoView.positionAnimator.enter(position, animate)
    }

    private fun applyImageAnimationState(position: Float, isLeaving: Boolean) {
        // Exit animation is finished.
        val isFinished = position == 0F && isLeaving

        backgroundView.alpha = position
        backgroundView.visibility = if (isFinished) View.INVISIBLE else View.VISIBLE
        photoView.visibility = if (isFinished) View.INVISIBLE else View.VISIBLE

        if (isFinished) {
            Events.create("show_image").param(true).post()

            photoView!!.controller.settings.disableBounds()
            photoView!!.positionAnimator.setState(0f, false, false)

            runOnNextFrame(Runnable {
                finish()
                overridePendingTransition(0, 0)
            })
        }
    }

    private fun runAfterImageDraw(action: Runnable) {
        photoView.viewTreeObserver.addOnPreDrawListener(object: OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                photoView.viewTreeObserver.removeOnPreDrawListener(this)
                runOnNextFrame(action)
                return true
            }
        })
        photoView.invalidate()
    }

    private fun runOnNextFrame(action: Runnable) {
        val frameLength = 17L
        photoView.postDelayed(action, frameLength)
    }

    /**
     * При клике на фотографию скрывать статус бар и тулбар.
     */
    private fun showSystemUI(show: Boolean) {
        window.decorView.systemUiVisibility = if (show) 0 else View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE
    }
}