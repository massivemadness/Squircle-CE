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

package com.KillerBLS.modpeide.utils.text.language;

import android.support.annotation.Nullable;

import com.KillerBLS.modpeide.utils.files.FileUtils;

import java.io.File;

public class LanguageProvider {

    /**
     * Метод для определения языка программирования (нет).
     * @param file - файл, язык которого хотим получить.
     * @return - возвращает язык, в зависимости от расширения файла.
     */
    @Nullable
    public static Language getLanguage(File file) {
        if(FileUtils.isExtension(file.getName(), "js")) { //Если это JavaScript (ModPE Script)
            return new ModPELanguage(); //ставим соответствующий язык
        }
        return null; //Не загружаем язык
    }
}