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
