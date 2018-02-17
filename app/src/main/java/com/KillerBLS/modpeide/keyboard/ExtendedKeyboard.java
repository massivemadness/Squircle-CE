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

package com.KillerBLS.modpeide.keyboard;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.KillerBLS.modpeide.adapter.SymbolAdapter;

/**
 * @author Trần Lê Duy
 */
public class ExtendedKeyboard extends RecyclerView {

    private OnKeyListener mListener;
    private SymbolAdapter mAdapter;

    public ExtendedKeyboard(Context context) {
        super(context);
        setup(context);
    }

    public ExtendedKeyboard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(context);

    }

    public ExtendedKeyboard(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context);

    }

    public OnKeyListener getListener() {
        return mListener;
    }

    public void setListener(OnKeyListener listener) {
        mListener = listener;
        mAdapter.setListener(mListener);
    }

    private void setup(Context context) {
        mAdapter = new SymbolAdapter();
        mAdapter.setListKey(new String[]{"{", "}", "(", ")", ";", ",", ".", "=", "\"", "|", "&",
                "!", "[", "]", "<", ">", "+", "-", "/", "*", "?", ":", "_"});
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(linearLayoutManager);
        setHasFixedSize(true);
        setAdapter(mAdapter);
    }

    public interface OnKeyListener {
        void onKeyClick(View view, String text);
    }
}
