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

import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;

import com.KillerBLS.modpeide.R;

public class ApplicationFragment extends PreferenceFragment
        /*implements SharedPreferences.OnSharedPreferenceChangeListener*/ {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.fragment_application);
        //getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

   /* @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("THEME_RESOURCE")) { //Если тема была изменена
            new MaterialDialog.Builder(getActivity())
                    .title("Restart")
                    .content("Do you want to restart application now?")
                    .positiveText(R.string.restart)
                    .negativeText(R.string.no)
                    .onPositive(((dialog, which) -> QuantumInstance.doRestart(getActivity())))
                    .build().show();
        }
    }*/
}
