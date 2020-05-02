package com.lightteam.modpeide.ui.settings.fragments

import android.os.Bundle
import com.lightteam.modpeide.R
import com.lightteam.modpeide.ui.base.fragments.DaggerPreferenceFragmentCompat

class FilesFragment : DaggerPreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_files, rootKey)
    }
}