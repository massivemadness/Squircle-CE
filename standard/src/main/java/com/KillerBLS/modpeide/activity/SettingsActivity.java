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
            toolbar.setNavigationOnClickListener(view -> super.onBackPressed());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
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
