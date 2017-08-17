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

package com.KillerBLS.modpeide.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.hardware.SyntaxEditorListener;
import com.KillerBLS.modpeide.widget.LModEditor;

import java.util.regex.Pattern;

public class SyntaxEditor extends LinearLayout implements SyntaxEditorListener {

    private Context mContext;
    private LModEditor mEditor;

    public SyntaxEditor(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public SyntaxEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    @SuppressWarnings("deprecation")
    protected void init() {
        //Editor Setup
        mEditor = new LModEditor(mContext);
        mEditor.setGravity(Gravity.TOP | Gravity.START);
        mEditor.setScrollBarStyle(SCROLLBARS_INSIDE_INSET);
        mEditor.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mEditor.setDropDownAnchor(R.id.appBar);
        mEditor.setDropDownBackgroundResource(R.color.colorPrimary);
        mEditor.setCursorVisible(true);
        mEditor.setBackgroundColor(ContextCompat.getColor(mContext, R.color.ide_background));
        //bufferType="spannable"
        addView(mEditor);
    }

    @Override
    public void setCurrentText(String text) {
        mEditor.setCurrentText(text);
    }

    @Override
    public String getString() {
        return mEditor.getString();
    }

    @Override
    public void setCurrentTypeface(Typeface typeface) {
        mEditor.setCurrentTypeface(typeface);
    }

    @Override
    public void setCodeColor(@ColorInt int color) {
        mEditor.setCodeColor(color);
    }

    @Override
    public void setCursorColor(EditText view, @ColorInt int color) {
        mEditor.setCursorColor(view, color);
    }

    @Override
    public LModEditor getEditor() {
        return mEditor;
    }

    @Override
    public void selectAll() {
        mEditor.selectAll();
    }

    @Override
    public void setLineNumbersColor(@ColorInt int color) {
        mEditor.setLineNumbersColor(color);
    }

    @Override
    public void setCurrentLineHighlightColor(@ColorInt int color) {
        mEditor.setCurrentLineHighlightColor(color);
    }

    @Override
    public void setFixedTextSize(float textSize) {
        mEditor.setFixedTextSize(textSize);
    }

    @Override
    public void setLineNumbersEnabled(boolean enabled) {
        mEditor.setLineNumbersEnabled(enabled);
    }

    @Override
    public void setHighlightCurrentLine(boolean enabled) {
        mEditor.setHighlightCurrentLine(enabled);
    }

    @Override
    public void setSelectionHighlightColor(@ColorInt int color) {
        mEditor.setSelectionHighlightColor(color);
    }

    @Override
    public void addSyntaxPattern(Pattern pattern, int syntaxType, @ColorInt int color) {
        mEditor.addSyntaxPattern(pattern, syntaxType, color);
    }

    @Override
    public void setSyntaxHighlightEnabled(boolean enabled) {
        mEditor.setSyntaxHighlightEnabled(enabled);
    }

    @Override
    public void setSyntaxUpdateDelay(int delay) {
        mEditor.setSyntaxUpdateDelay(delay);
    }

    @Override
    public void setBracketMatchingEnabled(boolean enabled) {
        mEditor.setBracketMatchingEnabled(enabled);
    }

    @Override
    public void setMatchedBracketsColor(@ColorInt int color) {
        mEditor.setMatchedBracketsColor(color);
    }

    @Override
    public void setReadOnly(boolean value) {
        mEditor.setReadOnly(value);
    }

    @Override
    public void setBracketsAutoClosing(boolean autoClosing) {
        mEditor.setBracketsAutoClosing(autoClosing);
    }

    @Override
    public void findText(String searchText, boolean ignoreCase) {
        mEditor.findText(searchText, ignoreCase);
    }

    @Override
    public void findPreviousText(String searchText, boolean ignoreCase) {
        mEditor.findPreviousText(searchText, ignoreCase);
    }

    @Override
    public void deleteLine() {
        mEditor.deleteLine();
    }

    @Override
    public void selectLine() {
        mEditor.selectLine();
    }

    @Override
    public void goToLine(int toLine) {
        mEditor.goToLine(toLine);
    }

    @Override
    public void toBegin() {
        mEditor.toBegin();
    }

    @Override
    public void toEnd() {
        mEditor.toEnd();
    }

    @Override
    public void replaceAll(String first, String last) {
        mEditor.replaceAll(first, last);
    }

    @Override
    public void highlightKeywords(boolean highlight) {
        mEditor.highlightKeywords(highlight);
    }

    @Override
    public void highlightKeywords2(boolean highlight) {
        mEditor.highlightKeywords2(highlight);
    }

    @Override
    public void highlightComments(boolean highlight) {
        mEditor.highlightComments(highlight);
    }

    @Override
    public void highlightStrings(boolean highlight) {
        mEditor.highlightStrings(highlight);
    }

    @Override
    public void highlightSymbols(boolean highlight) {
        mEditor.highlightSymbols(highlight);
    }

    @Override
    public void highlightNumbers(boolean highlight) {
        mEditor.highlightNumbers(highlight);
    }

    @Override
    public void highlightClasses(boolean highlight) {
        mEditor.highlightClasses(highlight);
    }

    @Override
    public void undo() {
        mEditor.undo();
    }

    @Override
    public void redo() {
        mEditor.redo();
    }

    @Override
    public boolean canUndo() {
        return mEditor.canUndo();
    }

    @Override
    public boolean canRedo() {
        return mEditor.canRedo();
    }

    @Override
    public void clearHistory() {
        mEditor.clearHistory();
    }

    @Override
    public void setMaxHistorySize(int maxSize) {
        mEditor.setMaxHistorySize(maxSize);
    }

    @Override
    public void cut() {
        mEditor.cut();
    }

    @Override
    public void copy() {
        mEditor.copy();
    }

    @Override
    public void paste() {
        mEditor.paste();
    }

    @Override
    public void setAutoCompleteEnabled(boolean enabled) {
        mEditor.setAutoCompleteEnabled(enabled);
    }

    @Override
    public void setAutoIndentationEnabled(boolean enabled) {
        mEditor.setAutoIndentationEnabled(enabled);
    }

    @Override
    public void setPinchZoomEnabled(boolean enabled) {
        mEditor.setPinchZoomEnabled(enabled);
    }
}