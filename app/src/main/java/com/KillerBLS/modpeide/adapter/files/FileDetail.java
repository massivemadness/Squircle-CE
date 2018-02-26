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

package com.KillerBLS.modpeide.adapter.files;

/**
 * Thanks Vlad Mihalachi
 */
public class FileDetail {

    private String name; //Имя файла
    private String size; //Размер файла
    private String dateModified; //Дата изменения файла
    private boolean isFolder; //Является ли файл папкой?

    public FileDetail(String name, String size, String dateModified, boolean isFolder) {
        this.name = name;
        this.size = size;
        this.dateModified = dateModified;
        this.isFolder = isFolder;
    }

    String getDateModified() {
        return dateModified;
    }

    String getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    boolean isFolder() {
        return isFolder;
    }
}