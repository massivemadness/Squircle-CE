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

package com.KillerBLS.modpeide.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.widget.CodeViewer;

import java.io.InputStream;

public class ButtonWidgetFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_button_widget_display, container, false);
        showCode(view);
        return view;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void showCode(View view) {
        CodeViewer ui_text = (CodeViewer) view.findViewById(R.id.ui_text);
        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.button_widget);
            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            ui_text.setHighlightedText(new String(b));
        } catch (Exception e) {
            ui_text.setHighlightedText(e.toString());
        }
    }
}
