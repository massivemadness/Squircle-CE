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

package com.KillerBLS.modpeide.manager.theming;

import android.app.Activity;
import android.content.Context;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.utils.Wrapper;

/**
 * Простой менеджер тем.
 */
public class ThemeManager {

    /**
     * Запускает процесс установки темы на выбранное активити.
     * @param activity - активити для темы.
     */
    public void start(Activity activity) {
        fill(activity);
    }

    /**
     * Устанавливает тему на выбранный {@link Context}.
     * @param context - контекст для установки темы.
     */
    private void fill(Context context) {
        switch(getCurrentTheme(context)) {
            case ThemeIdentificator.DARCULA:
                context.setTheme(R.style.Theme_Darcula);
                break;
            default: //Если установлена несуществующая для приложения тема, ставим Darcula
                context.setTheme(R.style.Theme_Darcula);
                break;
        }
    }

    /**
     * Метод для получения текущей выбранной темы из настроек приложения.
     * @param context - контекст для вызова {@link Wrapper}.
     * @return - возвращает идентификатор текущей темы.
     */
    private String getCurrentTheme(Context context) {
        return new Wrapper(context).getCurrentTheme();
    }
}
