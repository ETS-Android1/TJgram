package org.michaelbel.tjgram.presentation.features.profile

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fragment_auth.*
import kotlinx.android.synthetic.main.layout_profile.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.core.imageload.ImageLoader
import org.michaelbel.tjgram.core.time.TimeFormatter
import org.michaelbel.tjgram.core.views.ViewUtil
import org.michaelbel.tjgram.data.entities.SocialAccount
import org.michaelbel.tjgram.data.net.UserConfig
import org.michaelbel.tjgram.presentation.App
import org.michaelbel.tjgram.presentation.features.main.MainVM
import org.michaelbel.tjgram.presentation.features.profile.widget.SocialView
import org.michaelbel.tjgram.presentation.features.settings.SettingsActivity
import javax.inject.Inject

class ProfileFragment: Fragment() {

    companion object {
        private const val REQUEST_CODE_LOGOUT = 102

        const val LOGOUT_RESULT = "logout"

        fun newInstance() = ProfileFragment()
    }

    private lateinit var mainVM: MainVM
    private lateinit var profileVM: ProfileVM

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var factory: ProfileVMFactory

    private var accountsView: LinearLayoutCompat? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_LOGOUT) {
                // Открыть AuthFragment
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile, menu)
        menu.findItem(R.id.item_settings).icon = ViewUtil.getIcon(requireContext(), R.drawable.ic_settings_outline, R.color.icon_active)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_settings) {
            startActivityForResult(Intent(requireActivity(), SettingsActivity::class.java), REQUEST_CODE_LOGOUT)
            return true
        }

        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        App[requireActivity().application].createProfileComponent().inject(this)

        mainVM = ViewModelProviders.of(requireActivity())[MainVM::class.java]

        profileVM = ViewModelProviders.of(requireActivity(), factory)[ProfileVM::class.java]
        profileVM.getUser(UserConfig.getUserId(requireContext()))
        profileVM.user.observe(this, Observer {
            nameText.text = it.name
            updateAvatar(it.avatarUrl)
            updateKarma(it.karma)
            regDate.text = getString(R.string.reg_date, TimeFormatter.convertRegDate(context, it.createdDateRFC))
            updatePaidIcon(it.tjSubscriptionActive)
            //updateAccounts(it.socialAccounts)

            //commentsCountText.text = it.counters.comments.toString()
            //favoritesCountText.text = it.counters.favorites.toString()

            authLayout.visibility = GONE
            profileLayout.visibility = VISIBLE
        })
        profileVM.error.observe(this, Observer {

        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainVM.setToolbarTitle(getString(R.string.profile))

        avatarImage.setOnClickListener {
            //val position = ViewPosition.from(avatarImage)
            //PhotoActivity.show(requireActivity(), position, preferences.getString(KEY_AVATAR_URL, ""))
        }
        paidIcon.setImageDrawable(ViewUtil.getIcon(requireContext(), R.drawable.ic_check_decagram, R.color.accent))
        contactsLayout.setTitle(R.string.contacts_info)

        accountsView = LinearLayoutCompat(requireContext())
        accountsView?.orientation = LinearLayoutCompat.VERTICAL
        contactsLayout.addChildView(accountsView)

        commentsLayout.setOnClickListener {
            Toast.makeText(requireContext(), "Open comments", Toast.LENGTH_SHORT).show()
        }
        favoritesLayout.setOnClickListener {
            Toast.makeText(requireContext(), "Open favorites", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateAvatar(url: String) {
        imageLoader.load(url, R.drawable.placeholder_circle, R.drawable.error_circle, object: Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                avatarImage.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.placeholder_circle))
            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                avatarImage.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.error_circle))
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                avatarImage.setImageBitmap(bitmap)
            }
        })
    }

    private fun updateKarma(karma: Long) {
        val karmaTextColor = intArrayOf(R.color.karma_value, R.color.karma_value_pos, R.color.karma_value_neg)
        val karmaBackground = intArrayOf(R.color.karma_background, R.color.karma_background_pos, R.color.karma_background_neg)

        karmaValue.text = UserConfig.formatKarma(karma)

        when {
            karma == 0L -> {
                karmaValue.setTextColor(ContextCompat.getColor(requireContext(), karmaTextColor[0]))
                karmaCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), karmaBackground[0]))
            }
            karma > 0L -> {
                karmaValue.setTextColor(ContextCompat.getColor(requireContext(), karmaTextColor[1]))
                karmaCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), karmaBackground[1]))
            }
            else -> {
                karmaValue.setTextColor(ContextCompat.getColor(requireContext(), karmaTextColor[2]))
                karmaCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), karmaBackground[2]))
            }
        }
    }

    private fun updatePaidIcon(paid: Boolean) {
        paidIcon.visibility = if (paid) VISIBLE else GONE
    }

    private fun updateAccounts(accounts: List<SocialAccount>) {
        for (acc in accounts) {
            addSocialAccount(acc)
        }
    }

    private fun addSocialAccount(account: SocialAccount) {
        val socialView = SocialView(requireContext())
        socialView.setAccount(account)
        accountsView?.addView(socialView)
    }

    /*private fun showRemaining() {
        val milliseconds = preferences.getLong(KEY_UNTIL, 0L)
        val days = TimeUnit.MILLISECONDS.toDays(milliseconds).toInt()
        Toast.makeText(requireContext(), resources.getQuantityString(R.plurals.paid_days_until, days, days), Toast.LENGTH_SHORT).show()
    }*/
}