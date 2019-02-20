package org.michaelbel.tjgram.presentation.features.settings

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.presentation.utils.ViewUtil
import org.michaelbel.tjgram.presentation.utils.ext.setAppBarElevation
import org.michaelbel.tjgram.presentation.utils.ext.startFragment

class SettingsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initToolbar()

        if (savedInstanceState == null) {
            startFragment(R.id.fragmentView, SettingsFragment())
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

        supportActionBar?.setTitle(R.string.settings)
    }
}