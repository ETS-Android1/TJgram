package org.michaelbel.tjgram.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.layout_auth.*
import kotlinx.android.synthetic.main.layout_profile.*
import org.koin.android.ext.android.inject
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.data.UserConfig
import org.michaelbel.tjgram.data.entity.SocialAccount
import org.michaelbel.tjgram.data.entity.User
import org.michaelbel.tjgram.data.viewmodel.Injection
import org.michaelbel.tjgram.data.viewmodel.UserViewModel
import org.michaelbel.tjgram.data.viewmodel.ViewModelFactory
import org.michaelbel.tjgram.ui.QrCodeActivity
import org.michaelbel.tjgram.ui.profile.view.SocialView
import org.michaelbel.tjgram.utils.DeviceUtil
import org.michaelbel.tjgram.utils.ViewUtil
import org.michaelbel.tjgram.utils.consts.SharedPrefs
import org.michaelbel.tjgram.utils.date.TimeFormatter
import timber.log.Timber

class ProfileFragment : Fragment(), ProfileContract.View {

    companion object {
        private const val REQUEST_CODE_QR_SCAN = 101
        private const val REQUEST_PERMISSION_CAMERA = 102

        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    private val preferences: SharedPreferences by inject()
    private val presenter: ProfileContract.Presenter by inject()



    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: UserViewModel
    private val disposable = CompositeDisposable()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_QR_SCAN) {
                if (data == null) return

                val tokenStr = data.getStringExtra(QrCodeActivity.QR_SCAN_RESULT)
                if (TextUtils.isEmpty(tokenStr)) {
                    Toast.makeText(requireContext(), R.string.err_invalid_token, Toast.LENGTH_SHORT).show()
                    return
                }

                val separated = tokenStr.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (separated.size != 2) {
                    Toast.makeText(requireContext(), R.string.err_invalid_token, Toast.LENGTH_SHORT).show()
                    return
                }

                val token = separated[1]
                presenter.authQr(token)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_CAMERA && grantResults.isNotEmpty()) {
            if (permissions[0] == Manifest.permission.CAMERA) {
                val cameraGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (cameraGranted) {
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



        viewModelFactory = Injection.provideViewModelFactory(requireContext())
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile, menu)
        menu.findItem(R.id.item_logout).icon = ViewUtil.getIcon(requireContext(), R.drawable.ic_logout, R.color.icon_active)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_logout) {
            preferences.edit {putString(SharedPrefs.KEY_X_DEVICE_TOKEN, "")}
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
        qrIcon.setImageDrawable(ViewUtil.getIcon(requireContext(), R.drawable.ic_qr, R.color.icon_active))

        if (Build.VERSION.SDK_INT >= 21) {
            ViewCompat.setElevation(profileLayout, DeviceUtil.dp(requireContext(), 1F).toFloat())
        }
        contactsLayout.setTitle(R.string.contacts_info)
        paidIcon.setImageDrawable(ViewUtil.getIcon(requireContext(), R.drawable.ic_check_decagram, R.color.accent))
        setFragmentUI()

        // FIXME open full image
        /*avatar_image.setOnClickListener {
            val position = ViewPosition.from(avatar_image)
            PhotoActivity.show(requireActivity(), position, preferences.getString(KEY_AVATAR_URL, ""))
        }*/
    }

    private fun setFragmentUI() {
        val isUserAuth = UserConfig.isAuthorized(requireContext())
        setHasOptionsMenu(isUserAuth)

        if (isUserAuth) {
            authLayout.visibility = GONE
            profileLayout.visibility = VISIBLE
            setProfile()
            presenter.userMe()
        } else {
            profileLayout.visibility = GONE
            authLayout.visibility = VISIBLE
        }
    }

    override fun setUser(user: User, xToken: String) {
        if (xToken != "x") {
            setHasOptionsMenu(true)
            UserConfig.setLocalUser(requireContext(), xToken, user.id)
        }

        addUserToRoom(user)

        val accounts = user.socialAccounts
        for (acc in accounts) {
            addSocialAccount(acc)
        }

        setProfile()
        authLayout.visibility = GONE
        profileLayout.visibility = VISIBLE
    }

    override fun setAuthError(throwable: Throwable) {
        Toast.makeText(requireContext(), R.string.err_auth, Toast.LENGTH_SHORT).show()
    }

    private fun setProfile() {
        val userId = UserConfig.getUserId(requireContext())
        disposable.add(viewModel.localUser(userId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                nameText.text = it.name
                setAvatar(it.avatarUrl)
                setKarma(it.karma)
                setDate(it.createdDateRFC)
                setPaidIcon(it.tjSubscriptionActive)
            }, { error -> Timber.e(error) }))
    }

    private fun setAvatar(url: String) {
        Timber.i("set avatar: $url")
        Picasso.get().load(url).placeholder(R.drawable.placeholder_circle).error(R.drawable.error_circle)
            .into(object : com.squareup.picasso.Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    Timber.i("avatar prepare load")
                    avatarImage.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.placeholder_circle))
                }
                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    Timber.i("avatar bitmap load failed")
                    avatarImage.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.error_circle))
                }
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    Timber.i("avatar bitmap loaded")
                    avatarImage.setImageBitmap(bitmap)
                }
            })
    }

    private fun setKarma(karma: Long) {
        Timber.i("set karma: $karma")
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

    private fun setDate(date: String) {
        Timber.i("set date: $date")
        regDate.text = getString(R.string.sign_up_date, TimeFormatter.convertRegDate(context, date))
    }

    private fun setPaidIcon(paid: Boolean) {
        Timber.i("set paid icon: $paid")
        paidIcon.visibility = if (paid) VISIBLE else GONE
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

    private fun addUserToRoom(user: User) {
        Timber.e("user added to room")
        disposable.add(
            viewModel.updateUser(user).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Timber.e("user addedOrupdated successful") },
                        { error -> Timber.e("user addedOrupdated failure") }))
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}