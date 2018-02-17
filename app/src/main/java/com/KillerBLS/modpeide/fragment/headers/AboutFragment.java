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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.KillerBLS.modpeide.fragment.headers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.Preference;

import com.KillerBLS.modpeide.EditorInstance;
import com.KillerBLS.modpeide.preference.ChangeLogDialogPreference;
import com.afollestad.materialdialogs.MaterialDialog;
import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.preference.PrivacyDialogPreference;
import com.KillerBLS.modpeide.utils.text.StringUtils;

public class AboutFragment extends PreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.fragment_about);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if(preference instanceof PrivacyDialogPreference) {
            showPrivacyDialog();
        } else if(preference instanceof ChangeLogDialogPreference) {
            showChangeLogDialog();
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    private void showChangeLogDialog() {
        String rawText = StringUtils.readRawTextFile(getActivity(), R.raw.changelog);
        assert rawText != null;
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.pref_changelog_title))
                .content(rawText)
                .positiveText(R.string.close)
                .negativeText(R.string.dialog_open_source_code)
                .onNegative((dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(EditorInstance.APP_URL_OPEN_SOURCE_CODE))))
                .show();
    }

    private void showPrivacyDialog() {
        String rawText = StringUtils.readRawTextFile(getActivity(), R.raw.privacy_policy);
        assert rawText != null;
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.pref_privacy_policy_title))
                .content(rawText)
                .positiveText(R.string.close)
                .show();
    }
}
