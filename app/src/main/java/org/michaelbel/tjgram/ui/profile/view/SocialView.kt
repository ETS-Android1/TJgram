package org.michaelbel.tjgram.ui.profile.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_social.view.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.data.entity.SocialAccount
import org.michaelbel.tjgram.utils.ViewUtil

class SocialView : CardView {

    private val colors = intArrayOf(R.color.social_vk, R.color.social_tw, R.color.social_fb, R.color.social_gp)
    private val icons = intArrayOf(R.drawable.ic_social_vk, R.drawable.ic_social_twitter, R.drawable.ic_social_facebook, R.drawable.ic_social_google_plus)

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.item_social, this)
    }

    fun setAccount(account: SocialAccount) {
        val type = account.type - 1
        socialIcon.setImageDrawable(ViewUtil.getIcon(context, icons[type], R.color.foreground))
        socialText.text = account.username
        socialLayout.setCardBackgroundColor(ContextCompat.getColor(context, colors[type]))
    }
}