package org.michaelbel.tjgram.presentation.features.addpost

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.appbar.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.presentation.utils.ViewUtil
import org.michaelbel.tjgram.presentation.utils.ext.replaceFragment
import org.michaelbel.tjgram.presentation.utils.ext.setAppBarElevation

class PostActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        initToolbar()

        if (savedInstanceState == null) {
            replaceFragment(R.id.fragmentView, PostFragment.newInstance())
        }
    }

    private fun initToolbar() {
        if (Build.VERSION.SDK_INT >= 21) {
            appBarLayout.stateListAnimator = null
        }

        setAppBarElevation(appBarLayout, R.dimen.toolbar_elevation)

        setSupportActionBar(toolbar)
        toolbar.navigationIcon = ViewUtil.getIcon(this, R.drawable.ic_arrow_back, R.color.icon_active)
        toolbar.setNavigationOnClickListener{finish()}

        supportActionBar?.setTitle(R.string.post_entry)
    }
}