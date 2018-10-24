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