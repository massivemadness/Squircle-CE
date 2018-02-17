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

import android.text.Editable;

import com.KillerBLS.modpeide.document.commons.FileObject;
import com.KillerBLS.modpeide.utils.logger.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class Properties {

    private static final String TAG = Properties.class.getSimpleName();

    public static class PropertiesResult {
        public String mName;
        public String mPath;
        public String mLastModified;
        public String mSize;
        public String mWords;
        public String mCharacters;
        public String mLines;

        public boolean mRead;
        public boolean mWrite;
        public boolean mExecute;
    }

    /**
     * Получение информации о файле.
     * @param file - файл, информацию о котором хотим получить.
     * @return - возвращает {@link PropertiesResult} наполненный данными.
     */
    public static PropertiesResult analyze(FileObject file) {
        PropertiesResult result = new PropertiesResult();

        result.mName = file.getName();
        result.mPath = file.getAbsolutePath();
        result.mLastModified = file.getLastModified();
        result.mSize = file.getReadableSize();
        result.mWords = getWordsCount(file) + "";
        result.mCharacters = getCharacterCount(file) + "";
        result.mLines = getLineCount(file) + 1 + ""; //+1 because first line is 0

        result.mRead = file.canRead();
        result.mWrite = file.canWrite();
        result.mExecute = file.canExecute();

        return result;
    }

    /**
     * Получение количества слов в файле.
     * @param file - файл для операции.
     * @return - возвращает количество слов в файле.
     */
    private static int getWordsCount(FileObject file) {
        return getStringFromFile(file).split(" ").length;
    }

    /**
     * Получение количества символов в файле.
     * @param file - файл для операции.
     * @return - возвращает количество символов в файле.
     */
    private static int getCharacterCount(FileObject file) {
        return new Editable.Factory().newEditable(getStringFromFile(file)).length();
    }

    /**
     * Получение количества строк в файле.
     * @param file - файл для операции.
     * @return - возвращает количество строк в файле.
     */
    private static int getLineCount(FileObject file) {
        FileReader fileReader;
        LineNumberReader lineNumberReader = null;
        try {
            fileReader = new FileReader(file);
            lineNumberReader = new LineNumberReader(fileReader);
        } catch (FileNotFoundException e) {
            Logger.error(TAG, e);
        }
        if(lineNumberReader != null) {
            try {
                while(lineNumberReader.skip(Long.MAX_VALUE) > 0) {
                    // Loop just in case the file is > Long.MAX_VALUE or skip() decides to not read the entire file
                }
            } catch (IOException e) {
                Logger.error(TAG, e);
            }
        }
        assert lineNumberReader != null;
        return lineNumberReader.getLineNumber();
    }

    /**
     * Получение текста из файла.
     * @param file - файл для получения текста.
     * @return - возвращает полученный текст.
     */
    private static String getStringFromFile(FileObject file) {
        StringBuilder result = new StringBuilder();
        if (file.canRead()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(fileInputStream));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line).append("\n");
                }
                in.close();
            } catch (IOException e) {
                Logger.error(TAG, e);
            }
        }
        return result.toString();
    }
}
