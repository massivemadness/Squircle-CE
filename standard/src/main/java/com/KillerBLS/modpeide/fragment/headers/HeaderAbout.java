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

package com.KillerBLS.modpeide.fragment.headers;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.dialog.commons.DialogChangeLog;
import com.KillerBLS.modpeide.dialog.commons.DialogPrivacy;

public class HeaderAbout extends PreferenceFragment {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.header_about);

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
    }
}
