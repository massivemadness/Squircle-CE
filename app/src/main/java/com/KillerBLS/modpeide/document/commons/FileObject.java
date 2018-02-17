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

package com.KillerBLS.modpeide.document.commons;

import android.support.annotation.NonNull;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Класс сделан из-за надобности более простого способа получения данных о файле.
 */
public class FileObject extends File {

    public FileObject(@NonNull String pathname) {
        super(pathname);
    }

    public FileObject(String parent, @NonNull String child) {
        super(parent, child);
    }

    public FileObject(File parent, @NonNull String child) {
        super(parent, child);
    }

    public FileObject(@NonNull URI uri) {
        super(uri);
    }

    @Override
    public FileObject[] listFiles() {
        return filenamesToFiles(this.list());
    }

    private FileObject[] filenamesToFiles(String[] filenames) {
        if (filenames == null) {
            return null;
        }
        int count = filenames.length;
        FileObject[] result = new FileObject[count];
        for (int i = 0; i < count; ++i) {
            result[i] = new FileObject(this, filenames[i]);
        }
        return result;
    }

    /**
     * Получаем читабельный размер файла для пользователя.
     * @return - возвращает нормальный размер файла, например "1.5 KB".
     */
    public String getReadableSize() {
        long size = this.length();
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }

    /**
     * Метод для получения читабельной даты редактирования.
     * @return - возвращает читабельную для пользователя дату последнего редактирования файла.
     */
    public String getLastModified() {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd/MM/yyyy EEE HH:mm", Locale.getDefault());
        return dateFormat.format(this.lastModified());
    }

    /**
     * @return - возвращает разрешение файла, например "js", "html", без точки.
     */
    public String getExtension() {
        return FilenameUtils.getExtension(getName());
    }

    /**
     * Проверка файл на скрытность.
     * @return - вернет true, если файл скрыт (Если перед именем стоит точка).
     */
    public boolean isHidden() {
        return getName().startsWith(".");
    }

    /**
     * Удаление файла, включая все дочерние папки и файлы.
     */
    public void deleteRecursive() {
        if (isDirectory()) {
            FileObject[] files = listFiles();
            if (files != null) {
                for (FileObject child : files) {
                    child.deleteRecursive();
                }
            }
        }
        delete();
    }
}
