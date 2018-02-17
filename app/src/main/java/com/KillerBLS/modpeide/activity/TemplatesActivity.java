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

package com.KillerBLS.modpeide.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.Toast;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.adapter.LockableViewPager;
import com.KillerBLS.modpeide.adapter.TemplateAdapter;
import com.KillerBLS.modpeide.fragment.templates.TemplateFragment;
import com.KillerBLS.modpeide.manager.theming.ThemeManager;
import com.KillerBLS.modpeide.utils.Wrapper;
import com.KillerBLS.modpeide.utils.logger.Logger;
import com.KillerBLS.modpeide.widget.CodeTemplateView;

import es.dmoral.toasty.Toasty;

public class TemplatesActivity extends AppCompatActivity {

    private static final String TAG = TemplatesActivity.class.getSimpleName();

    private TemplateAdapter mAdapter;
    private LockableViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new ThemeManager().start(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_templates);
        initToolbar();
        initTabs();
        initFab();
    }

    private void initToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar_without_tabs);
        setSupportActionBar(mToolbar);
        if(new Wrapper(this).getFullScreenMode()) { //Fullscreen
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void initTabs() {
        mViewPager = findViewById(R.id.viewpager);
        mAdapter = new TemplateAdapter(getSupportFragmentManager());

        //region BUNDLES

        Bundle mButton = new Bundle(); //Button
        mButton.putString("asset_path", "templates/Button.txt");
        Fragment mButtonFragment = new TemplateFragment();
        mButtonFragment.setArguments(mButton);

        Bundle mMenu = new Bundle(); //Menu
        mMenu.putString("asset_path", "templates/Menu.txt");
        Fragment mMenuFragment = new TemplateFragment();
        mMenuFragment.setArguments(mMenu);

        Bundle mButtonWidget = new Bundle(); //Button (Widget)
        mButtonWidget.putString("asset_path", "templates/ButtonWidget.txt");
        Fragment mButtonWidgetFragment = new TemplateFragment();
        mButtonWidgetFragment.setArguments(mButtonWidget);

        Bundle mButtonWithTouchListener = new Bundle(); //Button (TouchListener)
        mButtonWithTouchListener.putString("asset_path", "templates/ButtonWithTouchListener.txt");
        Fragment mButtonWithTouchListenerFragment = new TemplateFragment();
        mButtonWithTouchListenerFragment.setArguments(mButtonWithTouchListener);

        Bundle mCheckBoxWidget = new Bundle(); //CheckBox (Widget)
        mCheckBoxWidget.putString("asset_path", "templates/CheckBoxWidget.txt");
        Fragment mCheckBoxWidgetFragment = new TemplateFragment();
        mCheckBoxWidgetFragment.setArguments(mCheckBoxWidget);

        Bundle mToggleWidget = new Bundle(); //Toggle Button (Widget)
        mToggleWidget.putString("asset_path", "templates/ToggleWidget.txt");
        Fragment mToggleWidgetFragment = new TemplateFragment();
        mToggleWidgetFragment.setArguments(mToggleWidget);

        Bundle mImageViewWidget = new Bundle(); //ImageView (Widget)
        mImageViewWidget.putString("asset_path", "templates/ImageWidget.txt");
        Fragment mImageViewWidgetFragment = new TemplateFragment();
        mImageViewWidgetFragment.setArguments(mImageViewWidget);

        Bundle mSwitchWidget = new Bundle(); //Switch (Widget)
        mSwitchWidget.putString("asset_path", "templates/SwitchWidget.txt");
        Fragment mSwitchWidgetFragment = new TemplateFragment();
        mSwitchWidgetFragment.setArguments(mSwitchWidget);

        Bundle mAlertDialog = new Bundle(); //AlertDialog.Builder
        mAlertDialog.putString("asset_path", "templates/AlertDialog.txt");
        Fragment mAlertDialogFragment = new TemplateFragment();
        mAlertDialogFragment.setArguments(mAlertDialog);

        //endregion BUNDLES

        mAdapter.addFragment(mButtonFragment, "Button");
        mAdapter.addFragment(mMenuFragment, "Menu");
        mAdapter.addFragment(mButtonWidgetFragment, "Button (Widget)");
        mAdapter.addFragment(mButtonWithTouchListenerFragment, "Button (TouchListener)");
        mAdapter.addFragment(mCheckBoxWidgetFragment, "CheckBox (Widget)");
        mAdapter.addFragment(mToggleWidgetFragment, "Toggle Button (Widget)");
        mAdapter.addFragment(mImageViewWidgetFragment, "Image View (Widget)");
        mAdapter.addFragment(mSwitchWidgetFragment, "Switch (Widget)");
        mAdapter.addFragment(mAlertDialogFragment, "AlertDialog.Builder");

        mViewPager.setAdapter(mAdapter);

        TabLayout mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initFab() {
        FloatingActionButton mCopyButton = findViewById(R.id.copy_action);
        mCopyButton.setOnClickListener(view -> {
            try {
                CodeTemplateView textField = mAdapter.getItem(mViewPager.getCurrentItem())
                        .getView().findViewById(R.id.code_view);
                ClipboardManager clipboardManager =
                        (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(
                            ClipData.newPlainText("CopiedText", textField.getText().toString()));
                    Toasty.success(this, getString(R.string.copied), Toast.LENGTH_SHORT).show();
                } else {
                    Toasty.error(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            } catch (NullPointerException e) {
                Logger.error(TAG, e);
                Toasty.error(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}