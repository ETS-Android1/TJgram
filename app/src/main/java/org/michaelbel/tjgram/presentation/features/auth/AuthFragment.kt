package org.michaelbel.tjgram.presentation.features.auth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blikoon.qrcodescanner.QrCodeActivity
import kotlinx.android.synthetic.main.fragment_auth.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.core.persistence.SharedPrefs
import org.michaelbel.tjgram.core.views.ViewUtil
import org.michaelbel.tjgram.presentation.App
import org.michaelbel.tjgram.presentation.features.main.MainVM
import org.michaelbel.tjgram.presentation.features.profile.ProfileFragment
import org.michaelbel.tjgram.presentation.features.settings.SettingsActivity
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import javax.inject.Inject

class AuthFragment: Fragment(), EasyPermissions.PermissionCallbacks {

    companion object {
        private const val REQUEST_CODE_SCAN_QR = 100

        /**
         * Значение не должно превышать 128.
         */
        private const val REQUEST_PERMISSION_CAMERA = 1

        fun newInstance() = AuthFragment()
    }

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var factory: AuthVMFactory

    private lateinit var mainVM: MainVM
    private lateinit var authVM: AuthVM

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK &&  requestCode == REQUEST_CODE_SCAN_QR) {
            val tokenStr = data?.getStringExtra(QrCodeActivity.EXTRA_QR_SCAN_RESULT)

            if (tokenStr == null || TextUtils.isEmpty(tokenStr)) {
                Toast.makeText(requireContext(), R.string.err_invalid_token, Toast.LENGTH_SHORT).show()
                return
            }

            val separated = tokenStr.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (separated.size != 2) {
                Toast.makeText(requireContext(), R.string.err_invalid_token, Toast.LENGTH_SHORT).show()
                return
            }

            val token = separated[1]
            authVM.authQr(token)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                    .setRationale(R.string.rationale_camera)
                    .setPositiveButton(R.string.dialog_action_ok)
                    .setNegativeButton(R.string.dialog_action_cancel)
                    .build()
                    .show()
        } else {
            startScan()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Timber.d("onPermissionsGranted")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile, menu)
        menu.findItem(R.id.item_settings).icon = ViewUtil.getIcon(requireContext(), R.drawable.ic_settings_outline, R.color.icon_active)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_settings) {
            startActivity(Intent(requireActivity(), SettingsActivity::class.java))
            return true
        }

        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        App[requireActivity().application].createAuthComponent().inject(this)
        authVM = ViewModelProviders.of(requireActivity(), factory)[AuthVM::class.java]

        authVM.token.observe(this, Observer {
            preferences.edit {
                putString(SharedPrefs.KEY_X_DEVICE_TOKEN, it)
            }
        })
        authVM.user.observe(this, Observer {
            preferences.edit {
                putInt(SharedPrefs.KEY_LOCAL_USER_ID, it.id)
                putString(SharedPrefs.KEY_LOCAL_USER_AVATAR, it.avatarUrl)
            }

            mainVM.changeUserAvatar(it.avatarUrl)

            requireFragmentManager().beginTransaction().replace(R.id.container, ProfileFragment.newInstance()).commit()
            requireFragmentManager().beginTransaction().remove(requireFragmentManager().findFragmentByTag("auth")!!)

            val removeAuthFragment = requireActivity().supportFragmentManager.fragments.remove(AuthFragment.newInstance())
            App.d("remove auth fragment from fragmentManager: $removeAuthFragment")
        })
        authVM.error.observe(this, Observer {
            if (it != null) {
                Toast.makeText(requireContext(), R.string.err_auth, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_auth, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainVM = ViewModelProviders.of(requireActivity())[MainVM::class.java]
        mainVM.changeToolbarTitle(R.string.login)

        qrIcon.setImageDrawable(ViewUtil.getIcon(requireContext(), R.drawable.ic_qr, R.color.icon_active))
        loginButton.setOnClickListener {
            startScan()
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_CAMERA)
    private fun startScan() {
        val permission = Manifest.permission.CAMERA
        if (EasyPermissions.hasPermissions(requireContext(), permission)) {
            startActivityForResult(Intent(requireContext(), QrCodeActivity::class.java), REQUEST_CODE_SCAN_QR)
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera), REQUEST_PERMISSION_CAMERA, permission)
        }
    }
}