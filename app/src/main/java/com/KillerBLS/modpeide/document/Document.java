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

package com.KillerBLS.modpeide.document;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.document.commons.FileObject;
import com.KillerBLS.modpeide.document.commons.LinesCollection;
import com.KillerBLS.modpeide.manager.FileManager;
import com.KillerBLS.modpeide.processor.TextProcessor;
import com.KillerBLS.modpeide.processor.language.Language;
import com.KillerBLS.modpeide.utils.Wrapper;
import com.KillerBLS.modpeide.utils.logger.Logger;
import com.KillerBLS.modpeide.widget.FastScrollerView;
import com.KillerBLS.modpeide.widget.GutterView;

import java.io.IOException;
import java.io.Serializable;

import es.dmoral.toasty.Toasty;

public class Document extends Fragment implements Serializable {

    private static final String TAG = Document.class.getSimpleName();

    private FileManager mFileManager;
    private Wrapper mWrapper;
    private TextProcessor mEditor;
    private FileObject mFile;
    private Language mLanguage;

    private LinesCollection mLineNumbers;
    private Editable mText;
    private boolean isDirty; //На данный момент не используется

    /**
     * Метод для создания и открытия новой вкладки.
     * @param filePath - путь к файлу.
     * @return - возвращает открытый документ с заданным параметром filePath.
     */
    public static Document newInstance(String filePath, boolean isStartPage) {
        Document document = new Document();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isStartPage", isStartPage);
        bundle.putString("filePath", filePath);
        document.setArguments(bundle);
        return document;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileManager = new FileManager(getContext());
        mWrapper = new Wrapper(getContext());
        mLineNumbers = new LinesCollection();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        View view = null;
        if(args != null && args.getBoolean("isStartPage")) {
            view = inflater.inflate(R.layout.fragment_start_page, container, false);
        } else if(args != null){
            //Если это не стартовая страница, то заполняем соответствующим View фрагмент
            view = inflater.inflate(R.layout.fragment_document, container, false);

            mEditor = view.findViewById(R.id.editor);
            mEditor.init(this);

            final FastScrollerView mFastScrollerView = view.findViewById(R.id.fast_scroller);
            mFastScrollerView.link(mEditor); //подключаем FastScroller к редактору

            final GutterView mGutterView = view.findViewById(R.id.gutter);
            mGutterView.link(mEditor); //подключаем Gutter к редактору

            String currentPath = args.getString("filePath");
            assert currentPath != null;

            mFile = new FileObject(currentPath);
            try {
                mFileManager.loadFile(this, mFile);
            } catch (IOException e) {
                Logger.error(TAG, e);
            }

            refreshEditor(); //подключаем все настройки
            setReadOnly(mWrapper.getReadOnly()); //включаем режим чтения при открытии если он нужен
            setSyntaxHighlight(mWrapper.getSyntaxHighlight());
            mEditor.enableUndoRedoStack(); //включаем Undo/Redo ПОСЛЕ открытия файла
        } else {
            Logger.error(TAG, "onCreateView(), getArguments() = null", null);
        }
        return view;
    }

    @Override
    public void onDestroy() {
        mFileManager.closeDatabase(); //fix leak
        super.onDestroy();
    }

    /**
     * Метод для настройки редактора, тут все методы к нему.
     */
    public void refreshEditor() {
        if(mEditor != null) {
            mEditor.setTextSize(mWrapper.getFontSize());
            mEditor.setHorizontallyScrolling(!mWrapper.getWrapContent());
            mEditor.setShowLineNumbers(mWrapper.getShowLineNumbers());
            mEditor.setBracketMatching(mWrapper.getBracketMatching());
            mEditor.setHighlightCurrentLine(mWrapper.getHighlightCurrentLine());
            mEditor.setCodeCompletion(mWrapper.getCodeCompletion());
            mEditor.setPinchZoom(mWrapper.getPinchZoom());
            mEditor.setInsertBrackets(mWrapper.getInsertBracket());
            mEditor.setIndentLine(mWrapper.getIndentLine());
            mEditor.refreshTypeface();
            mEditor.refreshInputType();
        }
    }

    /**
     * Сохранение текущего документа.
     */
    public void saveFile() {
        if (mEditor != null) {
            try {
                mFileManager.saveFile(mFile, getText());
                setDirty(false);
            } catch (Exception e) {
                Logger.error(TAG, e);
            }
        } else {
            showToast(getString(R.string.editor_not_found), true);
        }
    }

    private void setDirty(boolean dirty) {
        isDirty = dirty;
        //тут будет добавление "*" после названия файла если документ был изменен
    }

    /**
     * Tag'ом фрагмента является путь к открытому файлу.
     */
    public String getFilePath() {
        String filePath = getTag();
        assert filePath != null;
        return filePath;
    }

    /**
     * @return - возвращает текст из редактора.
     */
    public String getText() {
        if(mText != null)
            return mText.toString();
        else
            return "";
    }

    public void showToast(String message, boolean isError) {
        if(isError)
            Toasty.error(getContext(), message, Toast.LENGTH_SHORT).show();
        else
            Toasty.success(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Устанавливаем язык для файла. В будущем сделаю много таких.
     * @param language - текущий язык. Из него будут браться данные для подсветки синтаксиса
     *                 а так же для автодополнения кода.
     */
    public void setLanguage(@Nullable Language language) {
        mLanguage = language;
    }

    @Nullable
    public Language getLanguage() {
        return mLanguage;
    }

    //region METHODS_DOC

    /**
     * Методы для редактора, чтобы менять их в "Runtime".
     */

    public void setReadOnly(boolean readOnly) {
        if(mEditor != null)
            mEditor.setReadOnly(readOnly);
    }

    public void setSyntaxHighlight(boolean syntaxHighlight) {
        if(mEditor != null)
            mEditor.setSyntaxHighlight(syntaxHighlight);
    }

    //endregion METHODS_DOC

    //region LINES

    public void setLineStartsList(LinesCollection list) {
        mLineNumbers = list;
    }

    public LinesCollection getLinesCollection() {
        return mLineNumbers;
    }

    public int getLineCount() {
        return mLineNumbers.getLineCount();
    }

    public int getLineForIndex(int index) {
        return mLineNumbers.getLineForIndex(index);
    }

    public int getIndexForStartOfLine(int line) {
        return mLineNumbers.getIndexForLine(line);
    }

    public int getIndexForEndOfLine(int line) {
        if (line == getLineCount() - 1) {
            return mText.length();
        }
        return mLineNumbers.getIndexForLine(line + 1) - 1;
    }

    public void replaceText(int start, int end, Editable text) {
        replaceText(start, end, text.toString());
    }

    public void replaceText(int start, int end, String text) {
        int i;
        if (mText == null) {
            mText = Editable.Factory.getInstance().newEditable("");
        }
        if (end >= mText.length()) {
            end = mText.length();
        }
        int newCharCount = text.length() - (end - start);
        int startLine = getLineForIndex(start);
        for (i = start; i < end; i++) {
            if (mText.charAt(i) == '\n') {
                mLineNumbers.remove(startLine + 1);
            }
        }
        mLineNumbers.shiftIndexes(getLineForIndex(start) + 1, newCharCount);
        for (i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                mLineNumbers.add(getLineForIndex(start + i) + 1, (start + i) + 1);
            }
        }
        if (start > end) {
            end = start;
        }
        if (start > mText.length()) {
            start = mText.length();
        }
        if (end > mText.length()) {
            end = mText.length();
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        mText.replace(start, end, text);
        setDirty(true);
    }

    public void setText(String text, int flag) {
        if (text != null) {
            setText(Editable.Factory.getInstance().newEditable(text), flag);
        } else {
            setText("", flag);
        }
    }

    public void setText(Editable text, int flag) {
        if (flag == 1) {
            mText = text;
            if(mEditor != null)
                mEditor.setText(mText);
            return;
        }
        int length = 0;
        if (mText != null) {
            length = mText.length();
        }
        replaceText(0, length, text);
        setDirty(false);
    }

    //endregion LINES

    //region METHODS

    public void insert(@NonNull CharSequence text) {
        if(mEditor != null)
            mEditor.insert(text);
    }

    public void cut() {
        if(mEditor != null)
            mEditor.cut();
        else
            showToast(getString(R.string.editor_not_found), true);
    }

    public void copy() {
        if(mEditor != null)
            mEditor.copy();
        else
            showToast(getString(R.string.editor_not_found), true);
    }

    public void paste() {
        if(mEditor != null)
            mEditor.paste();
        else
            showToast(getString(R.string.editor_not_found), true);
    }

    public void undo() {
        if(mEditor != null)
            mEditor.undo();
        else
            showToast(getString(R.string.editor_not_found), true);
    }

    public void redo() {
        if(mEditor != null)
            mEditor.redo();
        else
            showToast(getString(R.string.editor_not_found), true);
    }

    public void selectAll() {
        if(mEditor != null)
            mEditor.selectAll();
        else
            showToast(getString(R.string.editor_not_found), true);
    }

    public void selectLine() {
        if(mEditor != null)
            mEditor.selectLine();
        else
            showToast(getString(R.string.editor_not_found), true);
    }

    public void deleteLine() {
        if(mEditor != null)
            mEditor.deleteLine();
        else
            showToast(getString(R.string.editor_not_found), true);
    }

    public void duplicateLine() {
        if(mEditor != null)
            mEditor.duplicateLine();
        else
            showToast(getString(R.string.editor_not_found), true);
    }

    public void find(String what, boolean matchCase, boolean regex, boolean wordOnly) {
        if(mEditor != null && !what.equals("")) {
            mEditor.find(what, matchCase, regex, wordOnly, mEditor.getEditableText());
            showToast(getString(R.string.done), false);
        } else {
            showToast(getString(R.string.editor_not_found), true);
        }
    }

    public void replaceAll(String what, String with) {
        if(mEditor != null && !what.equals("") && !with.equals("")) {
            mEditor.replaceAll(what, with);
            showToast(getString(R.string.done), false);
        } else {
            showToast(getString(R.string.editor_not_found), true);
        }
    }

    public void gotoLine(int line) {
        if(mEditor != null)
            mEditor.gotoLine(line);
        else
            showToast(getString(R.string.editor_not_found), true);
    }

    //endregion METHODS
}
