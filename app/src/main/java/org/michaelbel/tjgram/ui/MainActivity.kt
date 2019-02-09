package org.michaelbel.tjgram.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appbar.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.data.UserConfig
import org.michaelbel.tjgram.data.consts.Sorting
import org.michaelbel.tjgram.ui.common.bottombar.BottomNavigationBar
import org.michaelbel.tjgram.ui.common.bottombar.BottomNavigationItem
import org.michaelbel.tjgram.ui.main.MainFragment
import org.michaelbel.tjgram.ui.profile.ProfileFragment
import org.michaelbel.tjgram.utils.DeviceUtil
import org.michaelbel.tjgram.utils.ViewUtil

class MainActivity : AppCompatActivity(), MainFragment.Listener {

    companion object {
        const val REQUEST_CODE_NEW_ENTRY = 201
        const val NEW_ENTRY_RESULT = "new_entry_created"

        const val MAIN_FRAGMENT = 0
        const val POST_FRAGMENT = 1
        const val USER_FRAGMENT = 2
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_NEW_ENTRY) {
            if (data == null) {
                return
            }

            val isEntryCreated = data.getBooleanExtra(NEW_ENTRY_RESULT, false)
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
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= 21) {
            appBarLayout.stateListAnimator = null
        }
        ViewCompat.setElevation(appBarLayout, DeviceUtil.dp(this, 1.5F).toFloat())
        setSupportActionBar(toolbar)

        val params = fullToolbar.layoutParams as FrameLayout.LayoutParams
        params.topMargin = DeviceUtil.statusBarHeight(this)

        fullToolbar.navigationIcon = ViewUtil.getIcon(this, R.drawable.ic_arrow_back, R.color.primary)
        fullToolbar.setNavigationOnClickListener{onBackPressed()}

        bottomBar.setBarBackgroundColor(R.color.primary)
        bottomBar.activeColor = R.color.accent
        bottomBar.setMode(BottomNavigationBar.MODE_FIXED)
        bottomBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_DEFAULT)

        val itemMain = BottomNavigationItem(ViewUtil.getIcon(this, R.drawable.ic_view_dashboard, R.color.accent), "")
                .setInactiveIcon(ViewUtil.getIcon(this, R.drawable.ic_view_dashboard_outline, R.color.icon_active_unfocused))

        val itemPost = BottomNavigationItem(ViewUtil.getIcon(this, R.drawable.ic_add_circle, R.color.icon_active_unfocused), "")
                .setInactiveIcon(ViewUtil.getIcon(this, R.drawable.ic_add_circle, R.color.icon_active_unfocused))

        val itemNotify = BottomNavigationItem(ViewUtil.getIcon(this, R.drawable.ic_bell, R.color.accent), "")
                .setInactiveIcon(ViewUtil.getIcon(this, R.drawable.ic_bell_outline, R.color.icon_active_unfocused))

        val itemProfile = BottomNavigationItem(ViewUtil.getIcon(this, R.drawable.ic_account, R.color.accent), "")
                .setInactiveIcon(ViewUtil.getIcon(this, R.drawable.ic_account_outline, R.color.icon_active_unfocused))

        var prevPosition = MAIN_FRAGMENT

        bottomBar.setFirstSelectedPosition(MAIN_FRAGMENT)
        bottomBar.addItems(itemMain, itemPost, /*itemNotify, */itemProfile)
        bottomBar.initialise()
        bottomBar.setTabSelectedListener(object : BottomNavigationBar.OnNavigationItemSelectedListener {
            override fun onItemSelected(position: Int) {
                when (position) {
                    MAIN_FRAGMENT -> {
                        setActionBar(R.string.app_name, SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS)
                        replaceFragment(MainFragment.newInstance(Sorting.NEW))
                        prevPosition = MAIN_FRAGMENT
                    }
                    POST_FRAGMENT -> {
                        bottomBar.selectTab(prevPosition, false)
                        if (!UserConfig.isAuthorized(this@MainActivity)) {
                            showLoginSnack()
                        } else {
                            startActivityForResult(Intent(this@MainActivity, NewPostActivity::class.java), REQUEST_CODE_NEW_ENTRY)
                        }
                    }
                    USER_FRAGMENT -> {
                        setActionBar(R.string.profile, 0)
                        replaceFragment(ProfileFragment.newInstance())
                        prevPosition = USER_FRAGMENT
                    }
                }
            }

            override fun onItemUnselected(position: Int) {}

            override fun onItemReselected(position: Int) {}
        })

        if (savedInstanceState == null) {
            bottomBar.selectTab(MAIN_FRAGMENT)
        }
    }

    override fun showLoginSnack() {
        val snack = Snackbar.make(coordinatorLayout, R.string.msg_login_first, Snackbar.LENGTH_LONG)

        val params = snack.view.layoutParams as CoordinatorLayout.LayoutParams
        val margin = DeviceUtil.dp(this, 4F)
        params.setMargins(margin, 0, margin, bottomBar.height + margin)
        snack.view.layoutParams = params

        snack.setAction(R.string.action_go) { bottomBar.selectTab(2) }
        snack.show()
    }

    private fun setActionBar(textRes: Int, flags: Int) {
        supportActionBar!!.setTitle(textRes)
        ViewUtil.setScrollFlags(toolbar, flags)
    }

    fun showSystemStatusBar(state: Boolean) {
        val flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE
        window.decorView.systemUiVisibility = if (state) 0 else flags
    }

    /*public static void setLightStatusBar(View view,Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            activity.getWindow().setStatusBarColor(Color.WHITE);
        }
    }*/

    /*public static void clearLightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            window.setStatusBarColor(ContextCompat
                 .getColor(activity,R.color.colorPrimaryDark));
        }
    }*/

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.replace(R.id.fragmentView, fragment)
        transaction.commit()
    }
}