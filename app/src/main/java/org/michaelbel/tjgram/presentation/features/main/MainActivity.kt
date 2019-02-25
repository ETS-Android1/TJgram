package org.michaelbel.tjgram.presentation.features.main

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.core.imageloader.ImageLoader
import org.michaelbel.tjgram.core.imageloader.transform.CircleTransform
import org.michaelbel.tjgram.core.persistense.SharedPrefs
import org.michaelbel.tjgram.core.views.ViewUtil
import org.michaelbel.tjgram.data.api.results.EntriesResult
import org.michaelbel.tjgram.data.net.UserConfig
import org.michaelbel.tjgram.presentation.App
import org.michaelbel.tjgram.presentation.features.addpost.PostActivity
import org.michaelbel.tjgram.presentation.features.auth.AuthFragment
import org.michaelbel.tjgram.presentation.features.profile.ProfileFragment
import org.michaelbel.tjgram.presentation.features.timeline.TimelineFragment
import timber.log.Timber
import javax.inject.Inject

class MainActivity: AppCompatActivity() {

    companion object {
        const val EXTRA_NEW_ENTRY = "new_entry_created"

        private const val REQUEST_CODE_NEW_ENTRY = 201

        private const val MAIN_TAB = 0
        private const val POST_TAB = 1
        private const val USER_TAB = 2
    }

    private var snackBar: Snackbar? = null

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var factory: MainVMFactory

    private lateinit var mainVM: MainVM

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_NEW_ENTRY) {
            if (data == null) {
                return
            }

            val isEntryCreated = data.getBooleanExtra(EXTRA_NEW_ENTRY, false)
            if (isEntryCreated) {
                Toast.makeText(this, R.string.msg_posted_successfully, Toast.LENGTH_LONG).show()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun setTheme(resid: Int) {
        super.setTheme(R.style.AppTheme)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        App[application].createMainComponent().inject(this)

        initToolbar()
        initBottomBar()

        mainVM = ViewModelProviders.of(this, factory)[MainVM::class.java]
        mainVM.snackBarMessage.observe(this, Observer {
            showSnackbar(it.peekContent())
        })
        mainVM.toolbarTitle.observe(this, Observer {
            supportActionBar!!.setTitle(it.peekContent())
        })
        mainVM.userAvatar.observe(this, Observer {
            updateUserAvatar(it)
        })

        if (savedInstanceState == null) {
            bottomBar.selectTab(MAIN_TAB)
        }
    }

    override fun onPause() {
        super.onPause()
        Timber.d("MainActivity onPause")
        snackBar?.dismiss()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)

        if (Build.VERSION.SDK_INT >= 21) {
            appBarLayout.stateListAnimator = null
        }

        ViewCompat.setElevation(appBarLayout, resources.getDimension(R.dimen.toolbar_elevation))
    }

    private fun initBottomBar() {
        bottomBar.setBarBackgroundColor(R.color.primary)
        bottomBar.activeColor = R.color.accent
        bottomBar.setMode(BottomNavigationBar.MODE_FIXED)
        bottomBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_DEFAULT)

        val itemMain = BottomNavigationItem(ViewUtil.getIcon(this, R.drawable.ic_view_dashboard, R.color.accent)!!, "")
                .setInactiveIcon(ViewUtil.getIcon(this, R.drawable.ic_view_dashboard_outline, R.color.icon_active_unfocused))

        val itemPost = BottomNavigationItem(ViewUtil.getIcon(this, R.drawable.ic_add_circle, R.color.icon_active_unfocused)!!, "")
                .setInactiveIcon(ViewUtil.getIcon(this, R.drawable.ic_add_circle, R.color.icon_active_unfocused))

        /*val itemNotify = BottomNavigationItem(ViewUtil.getIcon(this, R.drawable.ic_bell, R.color.accent)!!, "")
                .setInactiveIcon(ViewUtil.getIcon(this, R.drawable.ic_bell_outline, R.color.icon_active_unfocused))*/

        val itemProfile = BottomNavigationItem(ViewUtil.getIcon(this, R.drawable.ic_account, R.color.accent)!!, "")
                .setInactiveIcon(ViewUtil.getIcon(this, R.drawable.ic_account_outline, R.color.icon_active_unfocused))

        var prevPosition = MAIN_TAB

        bottomBar.setFirstSelectedPosition(MAIN_TAB)
        bottomBar.addItems(itemMain, itemPost, /*itemNotify, */itemProfile)
        bottomBar.initialise()
        bottomBar.setTabSelectedListener(object : BottomNavigationBar.OnNavigationItemSelectedListener {
            override fun onItemSelected(position: Int) {
                when (position) {
                    MAIN_TAB -> {
                        prevPosition = MAIN_TAB
                        setActionBarBehavior(SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS)
                        supportFragmentManager.beginTransaction()
                                .replace(container.id, TimelineFragment.newInstance(EntriesResult.Sorting.NEW))
                                .commit()
                    }
                    POST_TAB -> {
                        bottomBar.selectTab(prevPosition, false)
                        if (UserConfig.isAuthorized(this@MainActivity)) {
                            startActivityForResult(Intent(this@MainActivity, PostActivity::class.java), REQUEST_CODE_NEW_ENTRY)
                        } else {
                            showSnackbar(R.string.msg_login_first)
                        }
                    }
                    USER_TAB -> {
                        setActionBarBehavior(0)
                        if (UserConfig.isAuthorized(this@MainActivity)) {
                            supportFragmentManager
                                    .beginTransaction()
                                    .replace(container.id, ProfileFragment.newInstance(), "auth")
                                    .commit()
                        } else {
                            supportFragmentManager
                                    .beginTransaction()
                                    .replace(container.id, AuthFragment.newInstance(), "profile")
                                    .commit()
                        }

                        prevPosition = USER_TAB
                    }
                }
            }

            override fun onItemUnselected(position: Int) {}

            override fun onItemReselected(position: Int) {}
        })

        if (UserConfig.isAuthorized(this@MainActivity)) {
            updateUserAvatar(preferences.getString(SharedPrefs.KEY_LOCAL_USER_AVATAR, "")!!)
        }
    }

    fun showSnackbar(@StringRes message: Int) {
        val snack = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)

        val params = snack.view.layoutParams as CoordinatorLayout.LayoutParams
        val margin = resources.getDimension(R.dimen.snackbar_margin).toInt()
        params.setMargins(margin, 0, margin, bottomBar.height + margin)
        snack.view.layoutParams = params

        snack.setAction(R.string.action_go) { bottomBar.selectTab(2) }
        snack.show()

        this.snackBar = snack
    }

    private fun setActionBarBehavior(flags: Int) {
        ViewUtil.setScrollFlags(toolbar, flags)
    }

    private fun updateUserAvatar(avatar: String) {
        if (avatar == "") {
            bottomBar.getImageViewByTabItemPosition(USER_TAB).setImageDrawable(null)
            return
        }

        imageLoader.load(avatar, bottomBar.getImageViewByTabItemPosition(USER_TAB), CircleTransform())
    }
}