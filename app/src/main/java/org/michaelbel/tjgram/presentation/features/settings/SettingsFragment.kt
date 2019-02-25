package org.michaelbel.tjgram.presentation.features.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.data.net.UserConfig
import org.michaelbel.tjgram.presentation.features.profile.ProfileFragment

class SettingsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference?.key == "key_logout") {
            return logout()
        }

        return super.onPreferenceTreeClick(preference)
    }

    private fun logout(): Boolean {
        if (UserConfig.isAuthorized(requireContext()).not()) {
            return true
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.app_name)
            .setMessage(R.string.logout_dialog_message)
            .setPositiveButton(R.string.dialog_action_ok) { _, _ ->
                UserConfig.userLogout(requireContext())
                val intent = Intent()
                intent.putExtra(ProfileFragment.EXTRA_LOGOUT_RESULT, true)
                requireActivity().setResult(Activity.RESULT_OK, intent)
                requireActivity().finish()
            }
            .setNegativeButton(R.string.dialog_action_cancel, null)
            .show()

        return true
    }
}