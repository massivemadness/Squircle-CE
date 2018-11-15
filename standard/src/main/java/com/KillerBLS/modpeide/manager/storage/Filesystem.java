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
