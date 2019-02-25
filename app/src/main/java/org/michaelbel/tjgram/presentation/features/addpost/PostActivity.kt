package org.michaelbel.tjgram.presentation.features.addpost

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_post.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.core.ext.setStateListAnimatorNull
import org.michaelbel.tjgram.core.ext.setViewElevation

class PostActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        initToolbar()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(container.id, PostFragment.newInstance())
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar() {
        setStateListAnimatorNull(appBarLayout)
        setViewElevation(appBarLayout, R.dimen.toolbar_elevation)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}