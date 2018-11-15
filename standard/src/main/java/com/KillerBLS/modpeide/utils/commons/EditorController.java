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

package com.KillerBLS.modpeide.utils.commons;

import android.text.Editable;

import com.KillerBLS.modpeide.utils.text.LinesCollection;
import com.KillerBLS.modpeide.utils.text.UndoStack;
import com.KillerBLS.modpeide.utils.text.language.Language;

public interface EditorController {

    Language getLanguage(); //Получение текущего языка
    void setLanguage(Language language); //Установка языка
    void setLanguage(String language); //Установка языка по расширению

    UndoStack getUndoStack(); //Получение текущего стэка для undo-операций
    UndoStack getRedoStack(); //Получение текущего стэка для redo-операций
    void setUndoStack(UndoStack undoStack); //Установка стэка для undo-операций
    void setRedoStack(UndoStack redoStack); //Установка стэка для redo-операций

    LinesCollection getLinesCollection(); //Получение массива строк текста
    void setLinesCollection(LinesCollection linesCollection); //Установка массива строк

    int getLineCount(); //Получение общего количества строк в тексте
    int getLineForIndex(int index); //Получение строки по её индексу в массиве
    int getIndexForStartOfLine(int line); //Получение индекса по началу строки
    int getIndexForEndOfLine(int line); //Получение индекса по окончанию строки

    String getText(); //Получение текста
    void setText(String text, int flag); //Установка текста
    void setText(Editable text, int flag); //Установка текста
    void replaceText(int start, int end, CharSequence text); //Редактирование участков текста
}