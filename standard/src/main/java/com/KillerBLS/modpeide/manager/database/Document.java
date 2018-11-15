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

package com.KillerBLS.modpeide.manager.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.UUID;

@Entity(tableName = "tbl_file_history")
public class Document implements Parcelable {

    @NonNull
    @PrimaryKey
    private String uuid; //Индентификатор документа
    private String name; //Имя документа (отображается во вкладке)
    private String path; //Путь к файлу вместе с именем
    private String language; //Язык подсветки
    private int scrollX; //Позиция скроллинга по оси X
    private int scrollY; //Позиция скроллинга по оси Y
    private int selectionStart; //Стартовая позиция выделения
    private int selectionEnd; //Конечная позиция выделения

    /**
     * Создание нового документа.
     */
    public Document() {
        uuid = UUID.randomUUID().toString();
    }

    /**
     * Получение готового документа с уже заполненными данными. Используется в адаптере ViewPager'а.
     * @param in - Parcel с готовыми данными.
     */
    public Document(Parcel in) {
        uuid = in.readString();
        name = in.readString();
        path = in.readString();
        language = in.readString();
        scrollX = in.readInt();
        scrollY = in.readInt();
        selectionStart = in.readInt();
        selectionEnd = in.readInt();
    }

    // region GETTER

    @NonNull
    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getLanguage() {
        return language;
    }

    public int getScrollX() {
        return scrollX;
    }

    public int getScrollY() {
        return scrollY;
    }

    public int getSelectionStart() {
        return selectionStart;
    }

    public int getSelectionEnd() {
        return selectionEnd;
    }

    // endregion GETTER

    // region SETTER

    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setScrollX(int scroll_x) {
        this.scrollX = scroll_x;
    }

    public void setScrollY(int scroll_y) {
        this.scrollY = scroll_y;
    }

    public void setSelectionStart(int sel_start) {
        this.selectionStart = sel_start;
    }

    public void setSelectionEnd(int sel_end) {
        this.selectionEnd = sel_end;
    }

    // endregion SETTER

    // region PARCELABLE

    public static final Parcelable.Creator<Document> CREATOR = new Parcelable.Creator<Document>() {

        public Document createFromParcel(Parcel in) {
            return new Document(in);
        }

        public Document[] newArray(int size) {
            return new Document[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(uuid);
        out.writeString(name);
        out.writeString(path);
        out.writeString(language);
        out.writeInt(scrollX);
        out.writeInt(scrollY);
        out.writeInt(selectionStart);
        out.writeInt(selectionEnd);
    }

    // endregion PARCELABLE
}
