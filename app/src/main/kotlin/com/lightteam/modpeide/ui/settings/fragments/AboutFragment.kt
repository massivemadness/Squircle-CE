package com.lightteam.modpeide.ui.settings.fragments

import android.os.Bundle
import android.widget.TextView
import androidx.preference.Preference
import com.afollestad.materialdialogs.MaterialDialog
import com.lightteam.modpeide.BuildConfig
import com.lightteam.modpeide.R
import com.lightteam.modpeide.ui.base.fragments.DaggerPreferenceFragmentCompat
import com.lightteam.modpeide.utils.extensions.asHtml
import com.lightteam.modpeide.utils.extensions.getRawFileText
import com.lightteam.modpeide.utils.extensions.isUltimate

class AboutFragment : DaggerPreferenceFragmentCompat() {

    companion object {
        private const val KEY_ABOUT_AND_CHANGELOG = "ABOUT_AND_CHANGELOG"
        private const val KEY_PRIVACY_POLICY = "PRIVACY_POLICY"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_about, rootKey)

        val isUltimate = requireContext().isUltimate()

        val changelog = findPreference<Preference>(KEY_ABOUT_AND_CHANGELOG)
        changelog?.setOnPreferenceClickListener { showChangelogDialog() }
        changelog?.summary = String.format(
            getString(R.string.pref_about_summary),
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )
        if (isUltimate) {
            changelog?.setTitle(R.string.pref_about_ultimate_title)
        }

        val privacy = findPreference<Preference>(KEY_PRIVACY_POLICY)
        privacy?.setOnPreferenceClickListener { showPrivacyPolicyDialog() }
    }

    // region DIALOGS

    private fun showChangelogDialog(): Boolean {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_changelog)
            message(text = context.getRawFileText(R.raw.changelog).asHtml())
            findViewById<TextView>(R.id.md_text_message).textSize = 14f
            negativeButton(R.string.action_close)
        }
        return true
    }

    private fun showPrivacyPolicyDialog(): Boolean {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_privacy_policy)
            message(text = context.getRawFileText(R.raw.privacy_policy).asHtml())
            findViewById<TextView>(R.id.md_text_message).textSize = 14f
            negativeButton(R.string.action_close)
        }
        return true
    }

    // endregion DIALOGS
}