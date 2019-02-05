package org.michaelbel.tjgram.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
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
            replaceFragment(GalleryFragment.newInstance(), "")
        }
    }

    fun startFragment(fragment: Fragment, tag: String) {
        replaceFragment(fragment, tag)
    }

    fun finishFragment() {
        supportFragmentManager.popBackStack()
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (tag.isNotEmpty()) {
            transaction.addToBackStack(tag)
        }
        transaction.replace(R.id.fragment_view, fragment)
        transaction.commit()
    }
}