/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
