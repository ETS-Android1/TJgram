package org.michaelbel.tjgram.presentation.features.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
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
import kotlinx.android.synthetic.main.activity_timeline.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.core.ext.replaceFragment
import org.michaelbel.tjgram.core.ext.toast
import org.michaelbel.tjgram.core.views.DeviceUtil
import org.michaelbel.tjgram.core.views.ViewUtil
import org.michaelbel.tjgram.data.api.results.EntriesResult
import org.michaelbel.tjgram.data.net.UserConfig
import org.michaelbel.tjgram.presentation.App
import org.michaelbel.tjgram.presentation.features.addpost.PostActivity
import org.michaelbel.tjgram.presentation.features.auth.AuthFragment
import org.michaelbel.tjgram.presentation.features.profile.ProfileFragment
import org.michaelbel.tjgram.presentation.features.timeline.TimelineFragment
import javax.inject.Inject

class MainActivity: AppCompatActivity() {

    companion object {
        const val NEW_ENTRY_RESULT = "new_entry_created"

        private const val REQUEST_CODE_NEW_ENTRY = 201

        private const val MAIN_TAB = 0
        private const val POST_TAB = 1
        private const val USER_TAB = 2
    }

    private var snackbar: Snackbar? = null

    @Inject
    lateinit var factory: MainVMFactory

    private lateinit var viewModel: MainVM

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_NEW_ENTRY) {
            if (data == null) {
                return
            }

            val isEntryCreated = data.getBooleanExtra(NEW_ENTRY_RESULT, false)
            if (isEntryCreated) {
                //Toast.makeText(this, R.string.msg_posted_successfully, Toast.LENGTH_LONG).show()
                toast(R.string.msg_posted_successfully, Toast.LENGTH_LONG)
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun setTheme(resid: Int) {
        super.setTheme(R.style.AppTheme)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        App[application].createMainComponent().inject(this)

        initToolbar()
        initBottomBar()

        viewModel = ViewModelProviders.of(this, factory)[MainVM::class.java]
        viewModel.snackbarMessage.observe(this, Observer {
            showSnackbar(it)
        })
        viewModel.toolbarTitle.observe(this, Observer {
            supportActionBar!!.title = it
        })

        if (savedInstanceState == null) {
            bottomBar.selectTab(MAIN_TAB)
        }
    }

    override fun onPause() {
        super.onPause()
        snackbar?.dismiss()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)

        val params = fullToolbar.layoutParams as FrameLayout.LayoutParams
        params.topMargin = DeviceUtil.statusBarHeight(this)
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
                        setActionBarBehavior(SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS, 1.5F)
                        replaceFragment(R.id.fragmentView, TimelineFragment.newInstance(EntriesResult.Sorting.NEW))
                        prevPosition = MAIN_TAB
                    }
                    POST_TAB -> {
                        bottomBar.selectTab(prevPosition, false)

                        if (UserConfig.isAuthorized(this@MainActivity)) {
                            startActivityForResult(Intent(this@MainActivity, PostActivity::class.java), REQUEST_CODE_NEW_ENTRY)
                        } else {
                            showSnackbar("")
                        }
                    }
                    USER_TAB -> {
                        setActionBarBehavior(0, 1.5F)
                        if (UserConfig.isAuthorized(this@MainActivity)) {
                            replaceFragment(R.id.fragmentView, ProfileFragment.newInstance())
                        } else {
                            replaceFragment(R.id.fragmentView, AuthFragment.newInstance())
                        }

                        prevPosition = USER_TAB
                    }
                }
            }

            override fun onItemUnselected(position: Int) {}

            override fun onItemReselected(position: Int) {}
        })

        updateUserAvatar()
    }

    fun showSnackbar(message: String) {
        val snack = Snackbar.make(coordinatorLayout, R.string.msg_login_first, Snackbar.LENGTH_LONG)

        val params = snack.view.layoutParams as CoordinatorLayout.LayoutParams
        val margin = DeviceUtil.dp(this, 4F)
        params.setMargins(margin, 0, margin, bottomBar.height + margin)
        snack.view.layoutParams = params

        snack.setAction(R.string.action_go) { bottomBar.selectTab(2) }
        snack.show()

        this.snackbar = snack
    }

    private fun setActionBarBehavior(flags: Int, elevation: Float) {
        ViewUtil.setScrollFlags(toolbar, flags)

        if (Build.VERSION.SDK_INT >= 21) {
            appBarLayout.stateListAnimator = null
        }

        ViewCompat.setElevation(appBarLayout, DeviceUtil.dp(this, elevation).toFloat())
    }

    @SuppressLint("LogNotTimber")
    private fun updateUserAvatar() {
        if (UserConfig.isAuthorized(this)) {
            val userId = UserConfig.getUserId(this)
            Log.e("2580", "User ID: $userId")
        }

        val userId = UserConfig.getUserId(this)

        /*disposable.add(viewModel.localUser(userId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Picasso.get().load(it.avatarUrl).transform(CircleTransform()).into(bottomBar.getImageViewByTabItemPosition(USER_TAB))
            }, { error -> Timber.e(error) }))*/
    }

    fun clearBottomAvatar() {
        bottomBar.getImageViewByTabItemPosition(USER_TAB).setImageDrawable(null)
    }
}