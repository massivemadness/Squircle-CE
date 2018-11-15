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