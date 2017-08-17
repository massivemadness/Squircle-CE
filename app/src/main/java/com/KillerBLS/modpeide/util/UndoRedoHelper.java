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

package com.KillerBLS.modpeide.util;

import android.content.SharedPreferences;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import java.util.LinkedList;

public class UndoRedoHelper {
    private boolean mIsUndoOrRedo = false;

    private EditHistory mEditHistory;

    private EditTextChangeListener mChangeListener;
    private TextView mTextView;

    public UndoRedoHelper(TextView textView) {
        mTextView = textView;
        mEditHistory = new EditHistory();
        mChangeListener = new EditTextChangeListener();
        mTextView.addTextChangedListener(mChangeListener);
    }

    public void disconnect() {
        if (mTextView != null) {
            mTextView.removeTextChangedListener(mChangeListener);
        }
    }

    public void setMaxHistorySize(int maxHistorySize) {
        mEditHistory.setMaxHistorySize(maxHistorySize);
    }

    public void clearHistory() {
        mEditHistory.clear();
    }

    public boolean getCanUndo() {
        return (mEditHistory.mmPosition > 0);
    }

    public void undo() {
        EditItem edit = mEditHistory.getPrevious();
        if (edit == null) {
            return;
        }

        Editable editable = mTextView.getEditableText();
        int start = edit.start;
        int end = start + (edit.after != null ? edit.after.length() : 0);

        mIsUndoOrRedo = true;
        editable.replace(start, end, edit.before);
        mIsUndoOrRedo = false;

        for (Object o : editable.getSpans(0, editable.length(), UnderlineSpan.class)) {
            editable.removeSpan(o);
        }

        Selection.setSelection(editable,
                edit.before == null ? start : (start + edit.before.length()));
    }

    public boolean getCanRedo() {
        return (mEditHistory.mmPosition < mEditHistory.mmHistory.size());
    }

    public void redo() {
        EditItem edit = mEditHistory.getNext();
        if (edit == null) {
            return;
        }

        Editable text = mTextView.getEditableText();
        int start = edit.start;
        int end = start + (edit.before != null ? edit.before.length() : 0);

        mIsUndoOrRedo = true;
        text.replace(start, end, edit.after);
        mIsUndoOrRedo = false;

        // This will get rid of underlines inserted when editor tries to come
        // up with a suggestion.
        for (Object o : text.getSpans(0, text.length(), UnderlineSpan.class)) {
            text.removeSpan(o);
        }

        Selection.setSelection(text, edit.after == null ? start
                : (start + edit.after.length()));
    }

    public void storePersistentState(SharedPreferences.Editor editor, String prefix) {
        // Store hash code of text in the editor so that we can check if the
        // editor contents has changed.
        editor.putString(prefix + ".hash",
                String.valueOf(mTextView.getText().toString().hashCode()));
        editor.putInt(prefix + ".maxSize", mEditHistory.mmMaxHistorySize);
        editor.putInt(prefix + ".position", mEditHistory.mmPosition);
        editor.putInt(prefix + ".size", mEditHistory.mmHistory.size());

        int i = 0;
        for (EditItem ei : mEditHistory.mmHistory) {
            String pre = prefix + "." + i;

            editor.putInt(pre + ".start", ei.start);
            editor.putString(pre + ".before", ei.before.toString());
            editor.putString(pre + ".after", ei.after.toString());

            i++;
        }
    }

    public boolean restorePersistentState(SharedPreferences sp, String prefix)
            throws IllegalStateException {

        boolean ok = doRestorePersistentState(sp, prefix);
        if (!ok) {
            mEditHistory.clear();
        }

        return ok;
    }

    private boolean doRestorePersistentState(SharedPreferences sp, String prefix) {
        String hash = sp.getString(prefix + ".hash", null);
        if (hash == null) {
            // No state to be restored.
            return true;
        }

        if (Integer.valueOf(hash) != mTextView.getText().toString().hashCode()) {
            return false;
        }

        mEditHistory.clear();
        mEditHistory.mmMaxHistorySize = sp.getInt(prefix + ".maxSize", -1);

        int count = sp.getInt(prefix + ".size", -1);
        if (count == -1) {
            return false;
        }

        for (int i = 0; i < count; i++) {
            String pre = prefix + "." + i;

            int start = sp.getInt(pre + ".start", -1);
            String before = sp.getString(pre + ".before", null);
            String after = sp.getString(pre + ".after", null);

            if (start == -1 || before == null || after == null) {
                return false;
            }
            mEditHistory.add(new EditItem(start, before, after));
        }

        mEditHistory.mmPosition = sp.getInt(prefix + ".position", -1);
        return mEditHistory.mmPosition != -1;

    }

    private enum ActionType {
        INSERT, DELETE, PASTE, NOT_DEF
    }

    private final class EditHistory {
        private final LinkedList<EditItem> mmHistory = new LinkedList<>();
        private int mmPosition = 0;
        private int mmMaxHistorySize = -1;

        private void clear() {
            mmPosition = 0;
            mmHistory.clear();
        }

        private void add(EditItem item) {
            while (mmHistory.size() > mmPosition) {
                mmHistory.removeLast();
            }
            mmHistory.add(item);
            mmPosition++;

            if (mmMaxHistorySize >= 0) {
                trimHistory();
            }
        }

        private void setMaxHistorySize(int maxHistorySize) {
            mmMaxHistorySize = maxHistorySize;
            if (mmMaxHistorySize >= 0) {
                trimHistory();
            }
        }

        private void trimHistory() {
            while (mmHistory.size() > mmMaxHistorySize) {
                mmHistory.removeFirst();
                mmPosition--;
            }

            if (mmPosition < 0) {
                mmPosition = 0;
            }
        }

        private EditItem getCurrent() {
            if (mmPosition == 0) {
                return null;
            }
            return mmHistory.get(mmPosition - 1);
        }

        private EditItem getPrevious() {
            if (mmPosition == 0) {
                return null;
            }
            mmPosition--;
            return mmHistory.get(mmPosition);
        }

        private EditItem getNext() {
            if (mmPosition >= mmHistory.size()) {
                return null;
            }

            EditItem item = mmHistory.get(mmPosition);
            mmPosition++;
            return item;
        }
    }

    private final class EditItem {
        private int start;
        private CharSequence before;
        private CharSequence after;

        EditItem(int start, CharSequence before, CharSequence after) {
            this.start = start;
            this.before = before;
            this.after = after;
        }

        @Override
        public String toString() {
            return "EditItem{" +
                    "start=" + start +
                    ", before=" + before +
                    ", after=" + after +
                    '}';
        }
    }

    private final class EditTextChangeListener implements TextWatcher {
        private CharSequence mBeforeChange;
        private CharSequence mAfterChange;
        private ActionType lastActionType = ActionType.NOT_DEF;
        private long lastActionTime = 0;

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (mIsUndoOrRedo) {
                return;
            }
            mBeforeChange = s.subSequence(start, start + count);
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mIsUndoOrRedo) {
                return;
            }
            mAfterChange = s.subSequence(start, start + count);
            makeBatch(start);
        }

        private void makeBatch(int start) {
            ActionType at = getActionType();
            EditItem editItem = mEditHistory.getCurrent();
            if ((lastActionType != at || ActionType.PASTE == at ||
                    System.currentTimeMillis() - lastActionTime > 1000) || editItem == null) {
                mEditHistory.add(new EditItem(start, mBeforeChange, mAfterChange));
            } else {
                if (at == ActionType.DELETE) {
                    editItem.start = start;
                    editItem.before = mBeforeChange + editItem.before.toString();
                } else {
                    editItem.after = editItem.after + mAfterChange.toString();
                }
            }
            lastActionType = at;
            lastActionTime = System.currentTimeMillis();
        }

        private ActionType getActionType() {
            if (!TextUtils.isEmpty(mBeforeChange) && TextUtils.isEmpty(mAfterChange)) {
                return ActionType.DELETE;
            } else if (TextUtils.isEmpty(mBeforeChange) && !TextUtils.isEmpty(mAfterChange)) {
                return ActionType.INSERT;
            } else {
                return ActionType.PASTE;
            }
        }

        public void afterTextChanged(Editable s) {
        }
    }
}
