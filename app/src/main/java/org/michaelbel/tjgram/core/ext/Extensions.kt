package org.michaelbel.tjgram.core.ext

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.appbar.AppBarLayout

/*fun Context.getColorCompat(@ColorRes resId: Int) = ContextCompat.getColor(this, resId)*/

fun Context.showToast(message: Int, length: Int) = Toast.makeText(this, message, length).show()

/*inline fun FragmentActivity.replaceFragment(containerViewId: Int, f: () -> Fragment): Fragment? {
    return f().apply {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.replace(containerViewId, this)
        transaction.commitNow()
    }
}*/

fun FragmentActivity.startFragment(containerViewId: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction().replace(containerViewId, fragment).commit()
}

fun FragmentActivity.replaceFragment(containerViewId: Int, fragment: Fragment) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    transaction.replace(containerViewId, fragment)
    transaction.commit()
}

/*fun FragmentActivity.replaceFragment(containerViewId: Int, fragment: Fragment, tag: String) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    transaction.addToBackStack(tag)
    transaction.replace(containerViewId, fragment)
    transaction.commitNow()
}*/

/**
 * Программно установить тень для вьюшки.
 */
fun Context.setViewElevation(view: View, @DimenRes value: Int) =
        ViewCompat.setElevation(view, resources.getDimension(value))

/**
 * Уменьшить стандартную тень ActionBarLayout
 */
fun setStateListAnimatorNull(appBar: AppBarLayout) {
    if (Build.VERSION.SDK_INT >= 21) {
        appBar.stateListAnimator = null
    }
}