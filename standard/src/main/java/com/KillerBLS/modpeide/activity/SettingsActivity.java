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

package com.KillerBLS.modpeide.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.activity.compat.AppCompatPreferenceActivity;
import com.KillerBLS.modpeide.fragment.FragmentPreference;
import com.KillerBLS.modpeide.utils.Wrapper;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Inject
    Wrapper mWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        init();
    }

    /**
     * Установка Toolbar'а в активность настроек.
     */
    private void init() {
        ViewGroup root = findViewById(android.R.id.content);
        if (root != null) {
            LinearLayout content = (LinearLayout) root.getChildAt(0);
            LinearLayout toolbarContainer =
                    (LinearLayout) View.inflate(this, R.layout.activity_settings, null);
            root.removeAllViews();
            toolbarContainer.addView(content);
            root.addView(toolbarContainer);

            Toolbar toolbar = toolbarContainer.findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mWrapper.getFullscreenMode()) { //Fullscreen
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return FragmentPreference.class.getName().equals(fragmentName);
    }
}
