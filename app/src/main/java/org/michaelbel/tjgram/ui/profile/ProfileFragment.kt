package org.michaelbel.tjgram.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_auth.*
import kotlinx.android.synthetic.main.layout_profile.*
import org.koin.android.ext.android.inject
import org.michaelbel.tjgram.Logg
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.data.UserConfig
import org.michaelbel.tjgram.data.entity.SocialAccount
import org.michaelbel.tjgram.data.entity.User
import org.michaelbel.tjgram.data.room.AppDatabase
import org.michaelbel.tjgram.data.room.UserDao
import org.michaelbel.tjgram.ui.QrCodeActivity
import org.michaelbel.tjgram.ui.profile.view.SocialView
import org.michaelbel.tjgram.utils.date.TimeFormatter
import org.michaelbel.tjgram.utils.DeviceUtil
import org.michaelbel.tjgram.utils.ViewUtil
import org.michaelbel.tjgram.utils.consts.*
import java.lang.Exception

class ProfileFragment : Fragment(), ProfileContract.View {

    companion object {
        private const val REQUEST_CODE_QR_SCAN = 101
        private const val REQUEST_PERMISSION_CAMERA = 102

        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    private val karmaTextColor = intArrayOf(R.color.karma_value, R.color.karma_value_pos, R.color.karma_value_neg)
    private val karmaBackground = intArrayOf(R.color.karma_background, R.color.karma_background_pos, R.color.karma_background_neg)

    val preferences: SharedPreferences by inject()
    val presenter: ProfileContract.Presenter by inject()

    val database: AppDatabase by inject()
    val userDao: UserDao by inject()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            if (data == null) {
                return
            }

            Toast.makeText(requireContext(), R.string.err_invalid_token, Toast.LENGTH_SHORT).show()
            return
        }

        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null) {
                return
            }

            val token = data.getStringExtra(QrCodeActivity.QR_SCAN_RESULT)
            if (TextUtils.isEmpty(token)) {
                Toast.makeText(requireContext(), R.string.err_invalid_token, Toast.LENGTH_SHORT).show()
                return
            }

            val separated = token.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (separated.size != 2) {
                Toast.makeText(requireContext(), R.string.err_invalid_token, Toast.LENGTH_SHORT).show()
                return
            }

            val newToken = separated[1]
            presenter.authQr(newToken)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_CAMERA && grantResults.isNotEmpty()) {
            if (permissions[0] == Manifest.permission.CAMERA) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScan()
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.view = this
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile, menu)
        menu.findItem(R.id.item_logout).icon = ViewUtil.getIcon(requireContext(), R.drawable.ic_logout, R.color.icon_active)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.item_logout) {
            preferences.edit().putString(KEY_X_DEVICE_TOKEN, "").apply()
            contactsLayout.clearChildren()
            setFragmentUI()
            return true
        }

        return false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginButton.setOnClickListener { startScan() }
        qrIcon.setImageDrawable(ViewUtil.getIcon(requireContext(), R.drawable.ic_qrcode, R.color.icon_active))

        if (Build.VERSION.SDK_INT >= 21) {
            ViewCompat.setElevation(profile_layout, DeviceUtil.dp(requireContext(), 1F).toFloat())
        }
        contactsLayout.setTitle(R.string.contacts_info)

        setFragmentUI()

        // FIXME open full image
        /*avatar_image.setOnClickListener {
            val position = ViewPosition.from(avatar_image)
            PhotoActivity.show(requireActivity(), position, preferences.getString(KEY_AVATAR_URL, ""))
        }*/
    }

    private fun setFragmentUI() {
        val isAuth = UserConfig.isAuthorized(requireContext())
        setHasOptionsMenu(isAuth)

        if (isAuth) {
            auth_layout.visibility = GONE
            profile_layout.visibility = VISIBLE
            setProfile()
            presenter.userMe()
        } else {
            profile_layout.visibility = GONE
            auth_layout.visibility = VISIBLE
        }
    }

    override fun setUser(user: User, xToken: String) {
        /*val userDb = org.michaelbel.tjgram.data.room.User(
                user.id,
                user.name,
                user.karma,
                user.createdRFC,
                user.created,
                user.avatarUrl,
                user.pushTopic,
                user.url,
                user.userHash
        )
        userDao.insert(userDb)

        val u = userDao.getById(54438)
        Logg.e("user from db: " + u.name)*/

        if (xToken != "x") {
            preferences.edit {
                putString(KEY_X_DEVICE_TOKEN, xToken)
            }

            setHasOptionsMenu(true)
        }
        preferences.edit {
            putString(KEY_AVATAR_URL, user.avatarUrl)
            putString(KEY_CREATED_DATE, user.createdRFC)
            putLong(KEY_KARMA, user.karma)
            putString(KEY_NAME, user.name)
            putBoolean(KEY_PAID, user.advancedAccess.tjSubscription.isActive)
            putLong(KEY_UNTIL, user.advancedAccess.tjSubscription.activeUntil)
        }

        val accounts = user.socialAccounts
        for (acc in accounts) {
            addSocialAccount(acc)
        }

        setProfile()
        auth_layout.visibility = GONE
        profile_layout.visibility = VISIBLE
    }

    override fun setError(throwable: Throwable) {
        Toast.makeText(requireContext(), R.string.err_auth, Toast.LENGTH_SHORT).show()
    }

    private fun setProfile() {
        val avatarUrl = preferences.getString(KEY_AVATAR_URL, "")
        Logg.e(avatarUrl)

        Picasso.get().load(avatarUrl).placeholder(R.drawable.placeholder_circle).error(R.drawable.error_circle)
               .into(object : com.squareup.picasso.Target {
                   override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                   override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
                   override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                       avatar_image.setImageBitmap(bitmap)
                   }
               })

        val karma = preferences.getLong(KEY_KARMA, 0L)
        setKarma(karma)

        val name = preferences.getString(KEY_NAME, "")
        user_name.text = name

        val checkPaid = preferences.getBoolean(KEY_PAID, false)
        if (checkPaid) {
            paid_icon.setImageDrawable(ViewUtil.getIcon(requireContext(), R.drawable.ic_check_decagram, R.color.accent))
        }

        val date = preferences.getString(KEY_CREATED_DATE, "")
        signUpDate.text = getString(R.string.sign_up_date, TimeFormatter.convertSignDate(context, date))
    }

    private fun setKarma(karma: Long) {
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

    private fun addSocialAccount(account: SocialAccount) {
        val socialView = SocialView(requireContext())
        socialView.setAccount(account)
        contactsLayout.addChildView(socialView)
    }

    private fun startScan() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
                return
            }
        }

        /*if (Build.VERSION.SDK_INT >= 23) {
            if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    AlertDialog.Builder(requireContext())
                            .setTitle(R.string.permission_denied)
                            .setMessage(R.string.msg_permission_camera)
                            .setPositiveButton(R.string.settings) { _, _ ->
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                intent.data = Uri.fromParts("package", requireContext().packageName, null)
                                startActivity(intent)
                            }
                            .setNegativeButton(R.string.action_cancel, null)
                            .show()
                    return
                }

                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
                return
            }
        }*/

        startActivityForResult(Intent(requireContext(), QrCodeActivity::class.java), REQUEST_CODE_QR_SCAN)
    }

    /*private fun showRemaining() {
        val milliseconds = preferences.getLong(KEY_UNTIL, 0L)
        val days = TimeUnit.MILLISECONDS.toDays(milliseconds).toInt()
        Toast.makeText(requireContext(), resources.getQuantityString(R.plurals.paid_days_until, days, days), Toast.LENGTH_SHORT).show()
    }*/

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}