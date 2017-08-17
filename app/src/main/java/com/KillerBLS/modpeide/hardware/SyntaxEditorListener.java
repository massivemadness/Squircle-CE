/*
 * Copyright (C) 2017 Light Team Software
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

package com.KillerBLS.modpeide.hardware;

import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.widget.EditText;

import com.KillerBLS.modpeide.widget.LModEditor;

import java.util.regex.Pattern;

public interface SyntaxEditorListener {

    String getString();
    void setCurrentText(String text);
    void setCurrentTypeface(Typeface typeface);
    void setCodeColor(@ColorInt int color);
    void setCursorColor(EditText view, @ColorInt int color);
    LModEditor getEditor();
    void setLineNumbersColor(@ColorInt int color);
    void setCurrentLineHighlightColor(@ColorInt int color);
    void setFixedTextSize(float textSize);
    void setLineNumbersEnabled(boolean enabled);
    void setHighlightCurrentLine(boolean enabled);
    void setSelectionHighlightColor(@ColorInt int color);
    void addSyntaxPattern(Pattern pattern, int syntaxType, @ColorInt int color);
    void setSyntaxHighlightEnabled(boolean enabled);
    void setSyntaxUpdateDelay(int delay);
    void setBracketMatchingEnabled(boolean enabled);
    void setMatchedBracketsColor(@ColorInt int color);
    void setReadOnly(boolean value);
    void setBracketsAutoClosing(boolean autoClosing);
    void findText(String searchText, boolean ignoreCase);
    void findPreviousText(String searchText, boolean ignoreCase);
    void deleteLine();
    void selectLine();
    void selectAll();
    void goToLine(int toLine);
    void toBegin();
    void toEnd();
    void replaceAll(String first, String last);
    void highlightKeywords(boolean highlight);
    void highlightKeywords2(boolean highlight);
    void highlightComments(boolean highlight);
    void highlightStrings(boolean highlight);
    void highlightSymbols(boolean highlight);
    void highlightNumbers(boolean highlight);
    void highlightClasses(boolean highlight);
    void undo();
    void redo();
    boolean canUndo();
    boolean canRedo();
    void clearHistory();
    void setMaxHistorySize(int maxSize);
    void cut();
    void copy();
    void paste();
    void setAutoCompleteEnabled(boolean enabled);
    void setAutoIndentationEnabled(boolean enabled);
    void setPinchZoomEnabled(boolean enabled);
}