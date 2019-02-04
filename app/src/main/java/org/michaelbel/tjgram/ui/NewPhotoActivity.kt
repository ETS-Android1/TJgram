package org.michaelbel.tjgram.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.appbar.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.ui.newphoto.GalleryFragment
import org.michaelbel.tjgram.utils.DeviceUtil

class NewPhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_photo)

        if (Build.VERSION.SDK_INT >= 21) {
            appbar.stateListAnimator = null
        }
        ViewCompat.setElevation(appbar, DeviceUtil.dp(this, 1.5F).toFloat())

        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            startFragment(GalleryFragment.newInstance())
        }
    }

    private fun startFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_view, fragment).commit()
    }

    fun startFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_view, fragment).addToBackStack(tag).commit()
    }

    fun finishFragment() {
        supportFragmentManager.popBackStack()
    }
}