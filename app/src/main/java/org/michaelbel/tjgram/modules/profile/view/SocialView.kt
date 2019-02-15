package org.michaelbel.tjgram.modules.profile.view

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_social.view.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.data.entities.SocialAccount
import org.michaelbel.tjgram.utils.ViewUtil

class SocialView : FrameLayout {

    /*interface AccountClickListener {
        fun onAccountClick(acc: SocialAccount)
    }*/

    private val colors = intArrayOf(R.color.social_vk, R.color.social_tw, R.color.social_fb, R.color.social_gp)
    private val icons = intArrayOf(R.drawable.ic_social_vk, R.drawable.ic_social_twitter, R.drawable.ic_social_facebook, R.drawable.ic_social_google_plus)

    //private var accountClickListener: AccountClickListener? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    /*fun addListener(listener: AccountClickListener) {
        accountClickListener = listener
    }*/

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.item_social, this)
    }

    fun setAccount(account: SocialAccount) {
        val type = account.type - 1
        socialIcon.setImageDrawable(ViewUtil.getIcon(context, icons[type], R.color.card))
        socialText.text = account.username
        setViewBackground(colors[type])
        //setOnClickListener { accountClickListener?.onAccountClick(account) }
    }

    private fun setViewBackground(color: Int) {
        if (socialLayout.background is ShapeDrawable) {
            (background as ShapeDrawable).paint.color = ContextCompat.getColor(context, color)
        }

        socialLayout.setBackgroundColor(ContextCompat.getColor(context, color))
    }
}