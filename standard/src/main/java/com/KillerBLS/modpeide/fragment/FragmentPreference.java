/*
 * Copyright (C) 2018 Light Team Software
 *
 * This file is part of ModPE IDE.
 *
 * ModPE IDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ModPE IDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.KillerBLS.modpeide.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.dialog.commons.DialogChangeLog;
import com.KillerBLS.modpeide.dialog.commons.DialogPrivacy;
import com.KillerBLS.modpeide.dialog.commons.DialogShop;

public class FragmentPreference extends PreferenceFragment {

    private static final String ACTION_APPLICATION = "application";
    private static final String ACTION_EDITOR = "editor";
    private static final String ACTION_CODE_STYLE = "codestyle";
    private static final String ACTION_FILES = "files";
    private static final String ACTION_ABOUT = "about";

    private String mSelectedHeader;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        int selectedHeader = -1;
        mSelectedHeader = getArguments().getString("settings");

        if (mSelectedHeader != null) {
            switch (mSelectedHeader) {
                case ACTION_APPLICATION:
                    selectedHeader = R.xml.header_application;
                    break;
                case ACTION_EDITOR:
                    selectedHeader = R.xml.header_editor;
                    break;
                case ACTION_CODE_STYLE:
                    selectedHeader = R.xml.header_code_style;
                    break;
                case ACTION_FILES:
                    selectedHeader = R.xml.header_files;
                    break;
                case ACTION_ABOUT:
                    selectedHeader = R.xml.header_about;
                    break;
            }
            addPreferencesFromResource(selectedHeader);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        switch (mSelectedHeader) {
            case ACTION_APPLICATION:
                Preference preference1 = findPreference("THEME_RESOURCE");
                preference1.setOnPreferenceClickListener(preference -> {
                    new DialogShop.Builder(getActivity()).show();
                    return false;
                });
                break;
            //case ACTION_EDITOR:
            //    break;
            case ACTION_CODE_STYLE:
                SwitchPreference preference2 = (SwitchPreference) findPreference("INSERT_QUOTE");
                preference2.setOnPreferenceClickListener(preference -> {
                    new DialogShop.Builder(getActivity()).show();
                    preference2.setChecked(false);
                    return false;
                });
                break;
            //case ACTION_FILES:
            //    break;
            case ACTION_ABOUT:
                //ChangeLog
                Preference mAboutPreference = findPreference("ABOUT_AND_CHANGELOG");
                mAboutPreference.setOnPreferenceClickListener(preference -> {
                    new DialogChangeLog.Builder(getActivity()).show();
                    return false;
                });

                //Privacy Policy
                Preference mPrivacyPreference = findPreference("PRIVACY_POLICY");
                mPrivacyPreference.setOnPreferenceClickListener(preference -> {
                    new DialogPrivacy.Builder(getActivity()).show();
                    return false;
                });
                break;
        }
    }
}
