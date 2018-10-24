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

package com.KillerBLS.modpeide.adapter.model;

import android.support.annotation.Nullable;

import java.io.File;

public class FileModel {

    private String name; //Имя файла
    private String path; //Путь к файлу
    private long length; //Размер файла
    private long lastModified; //Дата последнего изменения файла
    private boolean isFolder; //Является ли файл папкой
    private boolean isHidden; //Является ли файл скрытым
    private boolean isUp; //Является ли файл переходом на уровень выше

    public FileModel(File file) {
        setName(file.getName());
        setPath(file.getPath());
        setLength(file.length());
        setLastModified(file.lastModified());
        setIsFolder(file.isDirectory());
        setIsHidden(file.isHidden());
    }

    // region GETTER

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public long getLength() {
        return length;
    }

    public long getLastModified() {
        return lastModified;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public boolean isUp() {
        return isUp;
    }

    // endregion GETTER

    // region SETTER

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public void setIsFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }

    public void setIsHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public void setIsUp(boolean isUp) {
        this.isUp = isUp;
        this.name = "..";
    }

    // endregion SETTER


    @Override
    public boolean equals(@Nullable Object object) {
        if(object instanceof FileModel) {
            return this.getPath().equals(((FileModel) object).getPath());
        }
        return super.equals(object);
    }
}
