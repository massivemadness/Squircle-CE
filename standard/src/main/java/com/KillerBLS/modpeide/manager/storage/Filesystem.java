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

package com.KillerBLS.modpeide.manager.storage;

import com.KillerBLS.modpeide.adapter.model.FileModel;

import java.util.Comparator;
import java.util.List;

public abstract class Filesystem {

    private final String[] mUnopenableExtensions = { //Неоткрываемые разрешения файлов
            "apk", "mp3", "mp4", "wav", "pdf", "avi", "wmv", "m4a", "png", "jpg", "jpeg", "zip",
            "7z", "rar", "gif", "xls", "doc", "dat", "jar", "tar", "torrent", "xd", "docx", "temp"
    };

    /**
     * @return - Возвращает список неоткрываемых в программе форматов.
     */
    public final String[] getUnopenableExtensions() {
        return mUnopenableExtensions;
    }

    // region METHODS

    /**
     * @return - Возвращает стандартную локацию.
     */
    public abstract FileModel getDefaultLocation();

    /**
     * @return - Возвращает родительскую папку данной локации.
     */
    public abstract FileModel getParentFolder(FileModel fileModel);

    /**
     * Создает список из всех файлов в данной локации.
     * @param fileModel - локация из которой нужно получить список файлов.
     * @param comparator - Comparator для сортировки файлов.
     * @param showHidden - значение, отвечающее за отображение скрытых файлов.
     * @return - возвращает список из файлов.
     */
    public abstract List<FileModel> makeList(FileModel fileModel,
                                             Comparator<? super FileModel> comparator, boolean showHidden);

    // endregion METHODS
}
