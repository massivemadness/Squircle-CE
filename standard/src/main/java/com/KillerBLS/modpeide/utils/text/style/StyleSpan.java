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

package com.KillerBLS.modpeide.utils.text.style;

import android.support.annotation.ColorInt;

/**
 * @author Henry Thompson
 */
public class StyleSpan {

    private boolean mBold;
    private boolean mItalic;
    private int mColor;

    public StyleSpan(@ColorInt int color, boolean bold, boolean italic) {
        mColor = color;
        mBold = bold;
        mItalic = italic;
    }

    void setColor(@ColorInt int color) {
        mColor = color;
    }

    public int getColor() {
        return mColor;
    }

    void setBold(boolean bold) {
        mBold = bold;
    }

    public boolean getIsBold() {
        return mBold;
    }

    void setItalic(boolean italic) {
        mItalic = italic;
    }

    public boolean getIsItalic() {
        return mItalic;
    }
}