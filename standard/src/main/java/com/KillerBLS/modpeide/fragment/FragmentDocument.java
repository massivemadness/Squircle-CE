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

package com.KillerBLS.modpeide.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.manager.FileManager;
import com.KillerBLS.modpeide.manager.database.AppData;
import com.KillerBLS.modpeide.utils.commons.EditorDelegate;
import com.KillerBLS.modpeide.utils.Wrapper;
import com.KillerBLS.modpeide.manager.database.Document;
import com.KillerBLS.modpeide.utils.commons.EditorController;
import com.KillerBLS.modpeide.utils.files.FileUtils;
import com.KillerBLS.modpeide.utils.text.LinesCollection;
import com.KillerBLS.modpeide.utils.text.UndoStack;
import com.KillerBLS.modpeide.utils.text.language.Language;
import com.KillerBLS.modpeide.utils.text.language.ModPELanguage;
import com.KillerBLS.modpeide.widget.FastScroller;
import com.KillerBLS.modpeide.widget.Gutter;
import com.KillerBLS.modpeide.widget.TextProcessor;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;

public class FragmentDocument extends Fragment implements EditorController, EditorDelegate {

    private static final String TAG = FragmentDocument.class.getSimpleName();

    public static final int FLAG_SET_TEXT_DEFAULT = 0;
    public static final int FLAG_SET_TEXT_DONT_SHIFT_LINES = 1;

    private Unbinder unbinder;

    @Inject
    Wrapper mWrapper;
    @Inject
    AppData mDatabase;
    @Inject
    FileManager mFileManager; //Файловый менеджер

    private String mUUID; //UUID документа
    private Document mDocument; //Документ с информацией
    private Editable mText; //Текст документа
    private Language mLanguage; //Язык программирования
    private UndoStack mUndoStack; //Стэк для undo-операций
    private UndoStack mRedoStack; //Стэк для redo-операций
    private LinesCollection mLinesCollection; //Массив строк документа

    @BindView(R.id.gutter)
    Gutter gutter;
    @BindView(R.id.editor)
    TextProcessor editor;
    @BindView(R.id.scroller)
    FastScroller scroller;

    // region BASE

    /**
     * Создание нового фрагмента.
     * @param uuid - UUID документа с заполненной информацией.
     * @return - возвращает фрагмент с заполненными данными.
     */
    public static FragmentDocument newInstance(String uuid) {
        FragmentDocument fragmentDocument = new FragmentDocument();
        Bundle bundle = new Bundle();
        bundle.putString("UUID", uuid);
        fragmentDocument.setArguments(bundle);
        return fragmentDocument;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUUID = getArguments().getString("UUID");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document, container, false);
        unbinder = ButterKnife.bind(this, view);
        mDocument = mDatabase.getDao().select(mUUID);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editor.init(this);
        scroller.link(editor);
        gutter.link(editor, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // endregion BASE

    // region STATE

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, mUUID + " (restore state)");
        editor.setTextSize(mWrapper.getFontSize());
        editor.setFontType(mWrapper.getFontType());
        editor.setWrapContent(mWrapper.getWrapContent());
        editor.setShowLineNumbers(mWrapper.getShowLineNumbers());
        editor.setHighlightCurrentLine(mWrapper.getHighlightCurrentLine());
        editor.setPinchZoom(mWrapper.getPinchZoom());
        editor.setIndentLine(mWrapper.getIndentLine());
        editor.setInsertBrackets(mWrapper.getInsertBracket());
        editor.setReadOnly(mWrapper.getReadOnly());
        editor.setImeKeyboard(mWrapper.getImeKeyboard());

        if(mFileManager.canLoadFromCache(mUUID)) { //если можно загрузить из кеша
            mFileManager.loadFromCache(this, mUUID); //загружаем из кеша
            setLanguage(mDocument.getLanguage());
        } else {
            mFileManager.loadFromStorage(this, mDocument.getPath()); //иначе из хранилища
        }

        try {
            editor.setScrollX(mDocument.getScrollX());
            editor.setScrollY(mDocument.getScrollY());
            editor.setSelection(mDocument.getSelectionStart(), mDocument.getSelectionEnd());
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(getContext(), R.string.message_error_load_cache, Toast.LENGTH_SHORT).show();
        }
        editor.setCodeCompletion(mWrapper.getCodeCompletion());
        editor.setSyntaxHighlight(mWrapper.getSyntaxHighlight());
        editor.setBracketMatching(mWrapper.getBracketMatching());
        editor.enableStacks();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, mUUID + " (save state)");
        editor.disableStacks();
        mFileManager.saveToCache(this, mUUID);
        mDocument.setLanguage(FileUtils.getExtension(mDocument.getName()));
        mDocument.setScrollX(editor.getScrollX());
        mDocument.setScrollY(editor.getScrollY());
        mDocument.setSelectionStart(editor.getSelectionStart());
        mDocument.setSelectionEnd(editor.getSelectionEnd());
        mDatabase.getDao().update(mDocument);
        mLanguage = null;
        mUndoStack.clear();
        mRedoStack.clear();
        setText("", FLAG_SET_TEXT_DEFAULT);
    }

    // endregion STATE

    // region DELEGATE

    @Override
    public void notifySaveClicked() {
        mFileManager.saveToStorage(this, mDocument.getPath());
    }

    @Override
    public void notifyCutClicked() {
        editor.cut();
    }

    @Override
    public void notifyCopyClicked() {
        editor.copy();
    }

    @Override
    public void notifyPasteClicked() {
        editor.paste();
    }

    @Override
    public void notifySelectAllClicked() {
        editor.selectAll();
    }

    @Override
    public void notifySelectLineClicked() {
        editor.selectLine();
    }

    @Override
    public void notifyDeleteLineClicked() {
        editor.deleteLine();
    }

    @Override
    public void notifyDuplicateLineClicked() {
        editor.duplicateLine();
    }

    @Override
    public void notifyFindClicked(String text, boolean matchCase, boolean regExp, boolean wordOnly) {
        editor.find(text, matchCase, regExp, wordOnly);
    }

    @Override
    public void notifyReplaceAllClicked(String replaceWhat, String replaceWith) {
        //setText(getText().replaceAll(replaceWhat, replaceWith), FLAG_SET_TEXT_DEFAULT);
        editor.replaceAll(replaceWhat, replaceWith);
    }

    @Override
    public void notifyGoToLineClicked(int line) {
        editor.gotoLine(line);
    }

    @Override
    public void notifyInsertClicked(String text) {
        editor.insert(text);
    }

    @Override
    public void notifyUndoClicked() {
        editor.undo();
    }

    @Override
    public void notifyRedoClicked() {
        editor.redo();
    }

    // endregion DELEGATE

    // region CONTROLLER

    @Override
    public Language getLanguage() {
        return mLanguage;
    }

    @Override
    public void setLanguage(Language language) {
        mLanguage = language;
    }

    @Override
    public void setLanguage(String language) {
        switch (language) {
            case ".js": //FIXME bad solution
                mLanguage = new ModPELanguage();
                break;
            default:
                mLanguage = null;
                break;
        }
    }

    @Override
    public UndoStack getUndoStack() {
        return mUndoStack;
    }

    @Override
    public UndoStack getRedoStack() {
        return mRedoStack;
    }

    @Override
    public void setUndoStack(UndoStack undoStack) {
        mUndoStack = undoStack;
    }

    @Override
    public void setRedoStack(UndoStack redoStack) {
        mRedoStack = redoStack;
    }

    @Override
    public LinesCollection getLinesCollection() {
        return mLinesCollection;
    }

    @Override
    public void setLinesCollection(LinesCollection linesCollection) {
        mLinesCollection = linesCollection;
    }

    @Override
    public int getLineCount() {
        return mLinesCollection.getLineCount();
    }

    @Override
    public int getLineForIndex(int index) {
        return mLinesCollection.getLineForIndex(index);
    }

    @Override
    public int getIndexForStartOfLine(int line) {
        return mLinesCollection.getIndexForLine(line);
    }

    @Override
    public int getIndexForEndOfLine(int line) {
        if (line == getLineCount() - 1) {
            return mText.length();
        }
        return mLinesCollection.getIndexForLine(line + 1) - 1;
    }

    @Override
    public String getText() {
        if(mText != null) {
            return mText.toString();
        } else {
            return "";
        }
    }

    @Override
    public void setText(String text, int flag) {
        if (text != null) {
            setText(Editable.Factory.getInstance().newEditable(text), flag);
        } else {
            setText("", flag);
        }
    }

    @Override
    public void setText(Editable text, int flag) {
        if (flag == FLAG_SET_TEXT_DONT_SHIFT_LINES) {
            mText = text;
            if(mUndoStack != null /*&& mRedoStack != null*/) {
                mUndoStack.clear();
                mRedoStack.clear();
            }
            if(editor != null) {
                editor.setText(mText);
            }
            return;
        } else if(flag == FLAG_SET_TEXT_DEFAULT) {
            editor.disableStacks();
            editor.setText(text);
            if(mText != null) {
                mText.clear();
            }
        }
        int length = 0;
        if (mText != null) {
            length = mText.length();
        }
        replaceText(0, length, text);
    }

    @Override
    public void replaceText(int start, int end, CharSequence text) {
        int i;
        if (mText == null) {
            mText = Editable.Factory.getInstance().newEditable("");
        }
        if (start < 0) {
            start = 0;
        }
        if (end >= mText.length()) {
            end = mText.length();
        }
        int newCharCount = text.length() - (end - start);
        int startLine = getLineForIndex(start);
        for (i = start; i < end; i++) {
            if (mText.charAt(i) == '\n') {
                mLinesCollection.remove(startLine + 1);
            }
        }
        mLinesCollection.shiftIndexes(getLineForIndex(start) + 1, newCharCount);
        for (i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                mLinesCollection.add(getLineForIndex(start + i) + 1, (start + i) + 1);
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
    }

    // endregion CONTROLLER
}
