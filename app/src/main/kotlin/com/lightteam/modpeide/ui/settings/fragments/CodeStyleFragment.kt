package com.lightteam.modpeide.ui.settings.fragments

import android.os.Bundle
import androidx.preference.Preference
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.ui.base.fragments.DaggerPreferenceFragmentCompat
import com.lightteam.modpeide.utils.extensions.isUltimate

class CodeStyleFragment : DaggerPreferenceFragmentCompat() {

    companion object {
        private const val KEY_AUTOCLOSE_QUOTES = PreferenceHandler.KEY_AUTOCLOSE_QUOTES
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_code_style, rootKey)

        val isUltimate = requireContext().isUltimate()
        findPreference<Preference>(KEY_AUTOCLOSE_QUOTES)?.isEnabled = isUltimate
    }
}