package org.michaelbel.tjgram.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.appbar.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.ui.post.PostFragment
import org.michaelbel.tjgram.utils.DeviceUtil
import org.michaelbel.tjgram.utils.ViewUtil

class NewPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_photo)

        if (Build.VERSION.SDK_INT >= 21) {
            appbar.stateListAnimator = null
        }
        ViewCompat.setElevation(appbar, DeviceUtil.dp(this, 1.5F).toFloat())

        toolbar.navigationIcon = ViewUtil.getIcon(this, R.drawable.ic_arrow_back, R.color.icon_active)
        toolbar.setNavigationOnClickListener{finish()}
        setSupportActionBar(toolbar)

        supportActionBar!!.setTitle(R.string.post_entry)

        if (savedInstanceState == null) {
            replaceFragment(PostFragment(), "")
        }
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        if (tag.isNotEmpty()) {
            transaction.addToBackStack(tag)
        }
        transaction.replace(R.id.fragment_view, fragment)
        transaction.commit()
    }
}