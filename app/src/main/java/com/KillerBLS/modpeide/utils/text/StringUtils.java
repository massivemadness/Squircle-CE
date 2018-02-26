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

package com.KillerBLS.modpeide.utils.text;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.KillerBLS.modpeide.utils.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringUtils {

    private static final String TAG = StringUtils.class.getSimpleName();

    /**
     * Проверка имени файла на правильность.
     * @param name - имя файла.
     * @return - вернет true, если имя не содержит запрещенных символов.
     */
    public static boolean isValidFileName(@Nullable String name) {
        return !TextUtils.isEmpty(name)
                && !name.contains("/")
                && !name.equals(".")
                && !name.equals("..");
    }

    /**
     * Чтение файла из папки "raw".
     * @param ctx - контекст.
     * @param resId - читаемый файл.
     * @return - возвращает текст из raw-файла.
     */
    public static String readRawTextFile(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader buffReader = new BufferedReader(inputReader);
        String line;
        StringBuilder text = new StringBuilder();
        try {
            while ((line = buffReader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            Logger.error(TAG, e);
            return null;
        }
        return text.toString();
    }

    /**
     * Чтение файла из папки "assets".
     * @param ctx - контекст.
     * @param path - путь к читаемому файлу.
     * @return - возвращает текст из файла.
     */
    public static String readAssetTextFile(Context ctx, String path) {
        InputStream inputStream = null;
        try {
            inputStream = ctx.getAssets().open(path);
        } catch (IOException e) {
            Logger.error(TAG, e);
        }

        assert inputStream != null;
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader buffReader = new BufferedReader(inputReader);
        String line;
        StringBuilder text = new StringBuilder();
        try {
            while ((line = buffReader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            Logger.error(TAG, e);
            return null;
        }
        return text.toString();
    }
}