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

package com.KillerBLS.modpeide.utils.logger;

import android.support.annotation.NonNull;
import android.util.Log;

import com.KillerBLS.modpeide.EditorInstance;

/**
 * Упрощение работы с логгированием.
 */
public class Logger {

    public static String ERROR_OCCURRED = "An error occurred: ";
    private static String mStartsWith = "[" + EditorInstance.APPLICATION_NAME + "] ";

    public static void debug(@NonNull String tag, String message) {
        Log.d(tag, mStartsWith + message);
    }

    public static void error(@NonNull String tag, String message, Exception exception) {
        Log.e(tag, mStartsWith + message, exception);
    }

    public static void error(@NonNull String tag, Exception exception) {
        Log.e(tag, mStartsWith + ERROR_OCCURRED, exception);
    }
}
