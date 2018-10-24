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