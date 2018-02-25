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

package com.KillerBLS.modpeide.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.KillerBLS.modpeide.document.commons.FileObject;
import com.KillerBLS.modpeide.utils.logger.Logger;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Trần Lê Duy
 */
public class DatabaseManager extends SQLiteOpenHelper implements Serializable {

    private static final String TAG = DatabaseManager.class.getSimpleName();

    private static final String DATABASE_NAME = "db_manager";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_FILE_TAB = "tbl_file_history";
    private static final String KEY_FILE_PATH = "path";

    private static final String CREATE_TABLE_FILE_HISTORY =
            "create table " + TABLE_FILE_TAB +
                    "(" +
                    KEY_FILE_PATH + " TEXT PRIMARY KEY" +
                    ")";

    public DatabaseManager(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FILE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILE_TAB);
        onCreate(db);
    }

    @WorkerThread
    public ArrayList<FileObject> getListFile() {
        ArrayList<FileObject> files = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_FILE_TAB;
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    String result = cursor.getString(cursor.getColumnIndex(KEY_FILE_PATH));
                    FileObject file = new FileObject(result);
                    if (file.isFile())
                        files.add(file);
                    else
                        removeFile(result);
                } while (cursor.moveToNext());
            }
            cursor.close();
            sqLiteDatabase.close();
        } catch (Exception e) {
            Logger.error(TAG, e);
        }
        return files;
    }

    @WorkerThread
    void addNewFile(FileObject file) {
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_FILE_PATH, file.getPath());
            sqLiteDatabase.insert(TABLE_FILE_TAB, null, contentValues);
            sqLiteDatabase.close();
        } catch (Exception e) {
            Logger.error(TAG, e);
        }
    }

    @WorkerThread
    boolean removeFile(String path) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        boolean returnValue = sqLiteDatabase.delete(TABLE_FILE_TAB,
                KEY_FILE_PATH + "=?", new String[]{path}) > 0;
        sqLiteDatabase.close();
        return returnValue;
    }
}
