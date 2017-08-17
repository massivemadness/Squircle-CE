/*
 * Copyright (C) 2017 Light Team Software
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

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.adapter.ViewPagerAdapter;
import com.KillerBLS.modpeide.fragment.AlertDialogFragment;
import com.KillerBLS.modpeide.fragment.ButtonFragment;
import com.KillerBLS.modpeide.fragment.ButtonTouchListenerFragment;
import com.KillerBLS.modpeide.fragment.ButtonWidgetFragment;
import com.KillerBLS.modpeide.fragment.CheckboxWidgetFragment;
import com.KillerBLS.modpeide.fragment.ImageViewFragment;
import com.KillerBLS.modpeide.fragment.MenuFragment;
import com.KillerBLS.modpeide.fragment.SwitchWidgetFragment;
import com.KillerBLS.modpeide.fragment.ToggleWidgetFragment;
import com.KillerBLS.modpeide.widget.CodeViewer;

public class LModDisplayActivity extends AppCompatActivity {

    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_display);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton copy_action = findViewById(R.id.copy_action);
        copy_action.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings({"ConstantConditions", "deprecation"})
            @Override
            public void onClick(View view) {
                try {
                    CodeViewer cd = adapter.getItem(viewPager.getCurrentItem())
                            .getView().findViewById(R.id.ui_text);
                    ((ClipboardManager)
                            getApplicationContext()
                                    .getSystemService(Context.CLIPBOARD_SERVICE))
                            .setText(cd.getText().toString());
                    Toast.makeText(LModDisplayActivity.this,
                            R.string.copied, Toast.LENGTH_SHORT).show();
                } catch (NullPointerException ex) {
                    Toast.makeText(LModDisplayActivity.this,
                            "Error: " + ex.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        if(Build.VERSION.SDK_INT >= 21) { //Lollipop
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDarkPrefs));
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ButtonFragment(), "Button");
        adapter.addFragment(new MenuFragment(), "Menu");
        adapter.addFragment(new ButtonWidgetFragment(), "Button Widget");
        adapter.addFragment(new ButtonTouchListenerFragment(), "Button (TouchListener)");
        adapter.addFragment(new CheckboxWidgetFragment(), "Checkbox Widget");
        adapter.addFragment(new ToggleWidgetFragment(), "Toggle Button Widget");
        adapter.addFragment(new ImageViewFragment(), "ImageView Widget");
        adapter.addFragment(new SwitchWidgetFragment(), "Switch Widget");
        adapter.addFragment(new AlertDialogFragment(), "AlertDialog.Builder");
        viewPager.setAdapter(adapter);
    }
}
