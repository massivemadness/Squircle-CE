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

import android.text.Editable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class Properties {

    private static final String TAG = Properties.class.getSimpleName();

    public static class Result {
        public String name;
        public String path;
        public String lastModified;
        public String size;
        public String words;
        public String chars;
        public String lines;

        public boolean read;
        public boolean write;
        public boolean execute;
    }

    /**
     * Получение информации о файле.
     * @param file - файл, информацию о котором хотим получить.
     * @return - возвращает {@link Result} наполненный данными.
     */
    public static Result analyze(File file) {
        Result result = new Result();

        result.name = file.getName();
        result.path = file.getPath();
        result.lastModified = FileUtils.getLastModified(file.lastModified());
        if(!file.isDirectory() && file.canRead()) {
            result.size = FileUtils.getReadableSize(file.length());
            result.lines = getLineCount(file) + 1 + ""; //+1 потому что первая строка - 0
            result.words = getWordCount(file) + "";
            result.chars = getCharacterCount(file) + "";
        }
        result.read = file.canRead();
        result.write = file.canWrite();
        result.execute = file.canExecute();

        return result;
    }

    /**
     * Получение количества строк в файле.
     * @param file - файл для операции.
     * @return - возвращает количество строк в файле.
     */
    private static int getLineCount(File file) {
        FileReader fileReader;
        LineNumberReader lineNumberReader = null;
        try {
            fileReader = new FileReader(file);
            lineNumberReader = new LineNumberReader(fileReader);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        if(lineNumberReader != null) {
            try {
                while(lineNumberReader.skip(Long.MAX_VALUE) > 0) {
                    // Loop just in case the file is > Long.MAX_VALUE or skip() decides to not read the entire file
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        assert lineNumberReader != null;
        return lineNumberReader.getLineNumber();
    }

    /**
     * Получение количества слов в файле.
     * @param file - файл для операции.
     * @return - возвращает количество слов в файле.
     */
    private static int getWordCount(File file) {
        return getStringFromFile(file).split(" ").length;
    }

    /**
     * Получение количества символов в файле.
     * @param file - файл для операции.
     * @return - возвращает количество символов в файле.
     */
    private static int getCharacterCount(File file) {
        return new Editable.Factory().newEditable(getStringFromFile(file)).length();
    }

    /**
     * Получение текста из файла.
     * @param file - файл для получения текста.
     * @return - возвращает полученный текст.
     */
    private static String getStringFromFile(File file) {
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
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return result.toString();
    }
}
