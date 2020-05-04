package com.lightteam.modpeide.ui.settings.fragments

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.ui.base.fragments.DaggerPreferenceFragmentCompat

class ApplicationFragment : DaggerPreferenceFragmentCompat() {

    companion object {
        private const val KEY_COLOR_SCHEME = PreferenceHandler.KEY_COLOR_SCHEME
    }

    private lateinit var navController: NavController

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_application, rootKey)

        navController = findNavController()
        findPreference<Preference>(KEY_COLOR_SCHEME)?.setOnPreferenceClickListener {
            navController.navigate(R.id.themesFragment)
            true
        }
    }
}