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