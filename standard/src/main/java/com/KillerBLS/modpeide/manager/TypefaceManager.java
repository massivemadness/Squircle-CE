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

package com.KillerBLS.modpeide.manager;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.HashMap;

public class TypefaceManager {

    private static final String TAG = TypefaceManager.class.getSimpleName();

    public static final String ROBOTO = "Roboto";
    public static final String ROBOTO_LIGHT = "Roboto Light";
    public static final String SOURCE_CODE_PRO = "Source Code Pro";
    public static final String DROID_SANS_MONO = "Droid Sans Mono";

    private static HashMap<String, String> fontMap = new HashMap<>();

    static {
        fontMap.put(ROBOTO, "fonts/roboto.ttf");
        fontMap.put(ROBOTO_LIGHT, "fonts/roboto_light.ttf");
        fontMap.put(SOURCE_CODE_PRO, "fonts/source_code_pro.ttf");
        fontMap.put(DROID_SANS_MONO, null); // monospace
    }

    /**
     * Загрузка шрифта из assets.
     * @param context - контекст приложения, откуда будут загружаться шрифты.
     * @param fontType - шрифт для загрузки, существующий в fontMap.
     * @return - возвращает выбранный Typeface.
     */
    public static Typeface get(Context context, String fontType) {
        if (fontType.equals(DROID_SANS_MONO)) {
            return Typeface.MONOSPACE;
        }
        String file = fontMap.get(fontType);
        if (file == null) {
            return Typeface.MONOSPACE;
        }
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), file);
        if (typeface == null) {
            Log.d(TAG, "typeface not found");
            return Typeface.MONOSPACE;
        }
        return typeface;
    }
}