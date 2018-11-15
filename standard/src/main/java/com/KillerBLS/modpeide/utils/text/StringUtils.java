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

package com.KillerBLS.modpeide.utils.text;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

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
     * @param context - контекст.
     * @param resId - читаемый файл.
     * @return - возвращает текст из raw-файла.
     */
    public static String getRawFileText(Context context, int resId) {
        InputStream inputStream = context.getResources().openRawResource(resId);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        StringBuilder text = new StringBuilder();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
        return text.toString();
    }
}