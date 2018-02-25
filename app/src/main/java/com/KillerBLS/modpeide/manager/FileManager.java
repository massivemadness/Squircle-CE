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

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.widget.Toast;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.document.Document;
import com.KillerBLS.modpeide.document.commons.FileObject;
import com.KillerBLS.modpeide.document.commons.LinesCollection;
import com.KillerBLS.modpeide.processor.language.LanguageProvider;
import com.KillerBLS.modpeide.utils.logger.Logger;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import es.dmoral.toasty.Toasty;

public class FileManager {

    private static final String TAG = FileManager.class.getSimpleName();

    private Context mContext;
    private DatabaseManager mDatabase;

    public FileManager(Context context) {
        mContext = context;
        mDatabase = new DatabaseManager(context);
    }

    /**
     * Загружаем файл в редактор со всеми данными.
     * @param document - фрагмент в котором загружен файл.
     * @param file - читаемый файл.
     */
    @WorkerThread
    public void loadFile(Document document, FileObject file) throws IOException {
        if(file.canRead()) {
            LinesCollection lines = new LinesCollection();
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            int line = 0;
            int currentLineStart = 0;
            while(true) {
                String text = bufferedReader.readLine();
                if(text == null) {
                    break;
                }
                lines.add(line, currentLineStart);
                stringBuilder.append(text);
                stringBuilder.append('\n');
                currentLineStart += text.length() + 1;
                line++;
            }
            if(lines.getLineCount() == 0) {
                lines.add(0, 0);
            }
            if(stringBuilder.length() >= 1) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            bufferedReader.close();
            document.setLanguage(LanguageProvider.getLanguage(file)); //ставим язык
            document.setText(stringBuilder.toString(), 1); //заполняем поле текстом
            document.setLineStartsList(lines); //подгружаем линии
        }
    }

    /**
     * Сохранение выбранного файла.
     * @param file - файл.
     * @param text - текст для записи.
     */
    @WorkerThread
    public void saveFile(FileObject file, String text) {
        try {
            FileUtils.writeStringToFile(file, text, "UTF-8");
            Toasty.success(mContext,
                    mContext.getString(R.string.save_file_success), Toast.LENGTH_SHORT, true).show();
        } catch (Exception e) {
            Logger.error(TAG, e);
            Toasty.error(mContext,
                    mContext.getString(R.string.save_file_error), Toast.LENGTH_SHORT, true).show();
        }
    }

    /**
     * Создаем файл.
     * @param path - путь к создаваемому файлу.
     * @return - путь к созданному файлу при успехе.
     */
    String createNewFile(String path) {
        FileObject file = new FileObject(path);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            return path;
        } catch (IOException e) {
            Toasty.error(mContext,
                    mContext.getString(R.string.error) + e.getMessage(), Toast.LENGTH_SHORT, true).show();
            Logger.error(TAG, e);
            return "";
        }
    }

    /**
     * Добавляем файл в базу данных.
     * @param path - путь к файлу.
     */
    void addNewPath(String path) {
        mDatabase.addNewFile(new FileObject(path));
    }

    /**
     * Удаляем файл из базы данных.
     * @param path - путь к файлу.
     */
    void removeTabFile(String path) {
        mDatabase.removeFile(path);
    }

    public void closeDatabase() {
        mDatabase.close();
    }
}
