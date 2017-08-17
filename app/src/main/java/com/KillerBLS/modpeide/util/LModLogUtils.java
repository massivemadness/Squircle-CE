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

package com.KillerBLS.modpeide.util;

import android.util.Log;

public class LModLogUtils {
    private static final String TAG = "LMod";
    private static boolean isLogsEnabled = true;

    public static void i(String string) {
        if (isLogsEnabled) Log.i(TAG, string);
    }
    public static void e(String string) {
        if (isLogsEnabled) Log.e(TAG, string);
    }
    public static void d(String string) {
        if (isLogsEnabled) Log.d(TAG, string);
    }
    public static void v(String string) {
        if (isLogsEnabled) Log.v(TAG, string);
    }
    public static void w(String string) {
        if (isLogsEnabled) Log.w(TAG, string);
    }
}
