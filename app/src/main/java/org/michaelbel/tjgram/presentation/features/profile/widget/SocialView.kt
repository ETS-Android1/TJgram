package org.michaelbel.tjgram.presentation.features.profile.widget

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_social.view.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.data.entities.SocialAccount
import org.michaelbel.tjgram.presentation.utils.ViewUtil

class SocialView: FrameLayout {

    private val icons = intArrayOf(
            R.drawable.ic_social_vk,
            R.drawable.ic_social_twitter,
            R.drawable.ic_social_facebook,
            R.drawable.ic_social_google_plus
    )

    private val colors = intArrayOf(
            R.color.social_vk,
            R.color.social_tw,
            R.color.social_fb,
            R.color.social_gp
    )

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(org.michaelbel.tjgram.R.layout.item_social, this)
    }

    fun setAccount(account: SocialAccount) {
        val type = account.type - 1
        socialIcon.setImageDrawable(ViewUtil.getIcon(context, icons[type], org.michaelbel.tjgram.R.color.card))
        socialText.text = account.username
        setViewBackground(colors[type])
    }

    private fun setViewBackground(color: Int) {
        if (socialLayout.background is GradientDrawable) {
            val background = socialLayout.background.current as GradientDrawable
            background.setColor(ContextCompat.getColor(context, color))
        }
    }
}