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

package com.KillerBLS.modpeide.utils.files;

import android.content.Context;

import com.KillerBLS.modpeide.document.commons.FileObject;
import com.KillerBLS.modpeide.manager.DatabaseManager;

import java.util.ArrayList;

/**
 * @author Trần Lê Duy
 */
public class TabFileUtils {

    /**
     * Метод возвращает {@link ArrayList} со всеми файлами из базы данных.
     * @param context - контекст приложения.
     */
    public static ArrayList<FileObject> getTabFiles(Context context) {
        ArrayList<FileObject> files = new ArrayList<>();
        DatabaseManager database = new DatabaseManager(context);
        files.addAll(database.getListFile());
        database.close(); //Закрываем базу данный во избежание утечек
        return files;
    }
}
