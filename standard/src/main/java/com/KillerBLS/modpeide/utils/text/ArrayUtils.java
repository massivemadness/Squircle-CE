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

package com.KillerBLS.modpeide.utils.text;

import android.support.annotation.NonNull;

import java.lang.reflect.Array;

public class ArrayUtils {

    @SafeVarargs
    public static <T> T[] join(Class<T> c, @NonNull T[]... objects) {
        int size = 0;
        for (T[] object : objects) {
            size += object.length;
        }
        T[] result = (T[]) Array.newInstance(c, size);
        int index = 0;
        for (T[] object : objects) {
            for (T t : object) {
                Array.set(result, index, t);
                index++;
            }
        }
        return result;
    }
}