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

package com.KillerBLS.modpeide.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.KillerBLS.modpeide.adapter.KeyboardAdapter;

/**
 * @author Trần Lê Duy
 */
public class ExtendedKeyboard extends RecyclerView {

    public interface OnKeyListener {
        void onKeyClick(View view, String text);
    }

    // region CONSTRUCTOR

    public ExtendedKeyboard(Context context) {
        super(context);
    }

    public ExtendedKeyboard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public ExtendedKeyboard(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // endregion CONSTRUCTOR

    public void init(OnKeyListener listener) {
        KeyboardAdapter mAdapter = new KeyboardAdapter(listener);
        mAdapter.setListKey(new String[]{"{", "}", "(", ")", ";", ",", ".", "=", "\"", "|", "&",
                "!", "[", "]", "<", ">", "+", "-", "/", "*", "?", ":", "_"});
        setAdapter(mAdapter);
    }
}