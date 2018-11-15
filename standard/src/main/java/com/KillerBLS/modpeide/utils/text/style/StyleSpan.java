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