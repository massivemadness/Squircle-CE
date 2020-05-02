package com.lightteam.modpeide.ui.settings.fragments

import android.os.Bundle
import androidx.preference.Preference
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.ui.base.fragments.DaggerPreferenceFragmentCompat
import com.lightteam.modpeide.utils.extensions.isUltimate

class ApplicationFragment : DaggerPreferenceFragmentCompat() {

    companion object {
        private const val KEY_THEME = PreferenceHandler.KEY_THEME
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_application, rootKey)

        val isUltimate = requireContext().isUltimate()
        findPreference<Preference>(KEY_THEME)?.isEnabled = isUltimate
    }
}