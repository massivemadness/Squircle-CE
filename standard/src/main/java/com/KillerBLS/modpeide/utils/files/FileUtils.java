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

package com.KillerBLS.modpeide.utils.files;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FileUtils {

    /**
     * Проверка расширения файла.
     * @param fileName - имя файла, расширение которого хотим проверить.
     * @param extensions - массив разрешений для проверки.
     * @return - вернет true, если расширение файла совпало хоть с одним значением из массива.
     */
    public static boolean isExtension(final String fileName, final String... extensions) {
        for(String extension : extensions) {
            if(fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Получение расширения файла.
     * @param filename - имя файла.
     * @return - возвращает расширение файла.
     */
    public static String getExtension(String filename) {
        int strLength = filename.lastIndexOf(".");
        if(strLength > 0) {
            return filename.substring(strLength).toLowerCase();
        }
        return null;
    }

    /**
     * Получаем читабельный размер файла для пользователя.
     * @return - возвращает нормальный размер файла, например "1.5 KB".
     */
    public static String getReadableSize(long length) {
        if(length <= 0)
            return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(length)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(length/Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }

    /**
     * Метод для получения читабельной даты последнего редактирования.
     * @return - возвращает читабельную для пользователя дату последнего редактирования файла.
     */
    public static String getLastModified(long lastModified) {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd/MM/yyyy EEE HH:mm", Locale.getDefault());
        return dateFormat.format(lastModified);
    }
}
