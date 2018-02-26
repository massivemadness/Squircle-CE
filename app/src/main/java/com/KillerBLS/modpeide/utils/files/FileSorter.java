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

import com.KillerBLS.modpeide.document.commons.FileObject;

import java.util.Comparator;

/**
 * Класс с различными Comparator'ами для сортировки файлов.
 */
public class FileSorter {

    /**
     * Сортировка по имени. (В алфавитном порядке).
     */
    public static Comparator<? super FileObject> getFileNameComparator() {
        return (Comparator<FileObject>) (o1, o2) -> o1.getName().compareTo(o2.getName());
    }

    /**
     * Сортировка по размеру. От большого к малому.
     */
    public static Comparator<? super FileObject> getFileSizeComparator() {
        return (Comparator<FileObject>) (o1, o2) -> {
            if (o1.length() == o2.length()) {
                return 0;
            }
            if (o1.length() > o2.length()) {
                return -1;
            } else {
                return 1;
            }
        };
    }

    /**
     * Сортировка по дате. От новых к старым.
     */
    public static Comparator<? super FileObject> getFileDateComparator() {
        return (Comparator<FileObject>) (o1, o2) -> {
            if (o1.lastModified() == o2.lastModified()) {
                return 0;
            }
            if (o1.lastModified() > o2.lastModified()) {
                return -1;
            } else {
                return 1;
            }
        };
    }
}
