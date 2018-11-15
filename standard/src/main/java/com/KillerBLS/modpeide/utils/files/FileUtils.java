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
