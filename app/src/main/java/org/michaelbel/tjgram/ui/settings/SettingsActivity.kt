package org.michaelbel.tjgram.ui.settings

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.activity_settings.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.utils.DeviceUtil
import org.michaelbel.tjgram.utils.ViewUtil

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (Build.VERSION.SDK_INT >= 21) {
            appBarLayout.stateListAnimator = null
        }
        ViewCompat.setElevation(appBarLayout, DeviceUtil.dp(this, 1.5F).toFloat())

        setSupportActionBar(toolbar)
        toolbar.navigationIcon = ViewUtil.getIcon(this, R.drawable.ic_arrow_back, R.color.icon_active)
        toolbar.setNavigationOnClickListener{finish()}

        supportActionBar?.setTitle(R.string.settings)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentView, SettingsFragment())
                .commit()
        }
    }
}