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

package com.KillerBLS.modpeide.utils;

import android.util.TypedValue;
import android.view.View;

import com.KillerBLS.modpeide.utils.files.SortMode;

public class Converter {

    public static int dpAsPixels(View view, int dp) {
        return (int) TypedValue.applyDimension(1, dp, view.getResources().getDisplayMetrics());
    }

    public static int toSortMode(String sortMode) {
        switch (sortMode) {
            case "SORT_BY_NAME":
                return SortMode.SORT_BY_NAME;
            case "SORT_BY_SIZE":
                return SortMode.SORT_BY_SIZE;
            default: //"SORT_BY_DATE"
                return SortMode.SORT_BY_DATE;
        }
    }
}
