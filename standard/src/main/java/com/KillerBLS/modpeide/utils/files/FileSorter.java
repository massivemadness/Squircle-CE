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

import com.KillerBLS.modpeide.adapter.model.FileModel;

import java.util.Comparator;

/**
 * Класс с различными Comparator'ами для сортировки файлов.
 */
public class FileSorter {

    private static final int SORT_BY_NAME = 0; //Сортировка по имени
    private static final int SORT_BY_SIZE = 1; //Сортировка по размеру
    private static final int SORT_BY_DATE = 2; //Сортировка по дате

    public static Comparator<? super FileModel> getComparator(int sortMode) {
        switch (sortMode) {
            case SORT_BY_NAME:
                return getFileNameComparator();
            case SORT_BY_DATE:
                return getFileDateComparator();
            case SORT_BY_SIZE:
                return getFileSizeComparator();
            default:
                return getFileNameComparator();
        }
    }

    /**
     * Сортировка по имени. (В алфавитном порядке).
     */
    private static Comparator<? super FileModel> getFileNameComparator() {
        return (Comparator<FileModel>) (o1, o2) -> o1.getName().compareTo(o2.getName());
    }

    /**
     * Сортировка по размеру. От большого к малому.
     */
    private static Comparator<? super FileModel> getFileSizeComparator() {
        return (Comparator<FileModel>) (o1, o2) -> Long.compare(o2.getLength(), o1.getLength());
    }

    /**
     * Сортировка по дате. От новых к старым.
     */
    private static Comparator<? super FileModel> getFileDateComparator() {
        return (Comparator<FileModel>) (o1, o2) -> Long.compare(o2.getLastModified(), o1.getLastModified());
    }
}