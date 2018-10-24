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
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.KillerBLS.modpeide.utils.commons.EditorController;

/**
 * @author Henry Thompson
 */
public class Gutter extends View {

    private EditorController mController;
    private TextProcessor mEditor;

    // region CONSTRUCTOR

    public Gutter(Context context) {
        super(context);
    }

    public Gutter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Gutter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Gutter(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // endregion CONSTRUCTOR

    public void link(TextProcessor editor, EditorController controller) {
        if (editor != null) {
            mEditor = editor;
            mController = controller;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mEditor == null) {
            super.onDraw(canvas);
            return;
        }
        if (mController != null) {
            mEditor.updateGutter();
        }
    }
}