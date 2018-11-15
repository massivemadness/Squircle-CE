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

package com.KillerBLS.modpeide.widget;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.EditorInfo;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.adapter.SuggestionAdapter;
import com.KillerBLS.modpeide.adapter.model.SuggestionItem;
import com.KillerBLS.modpeide.manager.TypefaceManager;
import com.KillerBLS.modpeide.utils.commons.EditorController;
import com.KillerBLS.modpeide.utils.commons.OnScrollChangedListener;
import com.KillerBLS.modpeide.utils.text.LineObject;
import com.KillerBLS.modpeide.utils.text.SymbolsTokenizer;
import com.KillerBLS.modpeide.utils.text.UndoStack;
import com.KillerBLS.modpeide.utils.text.style.StylePaint;
import com.KillerBLS.modpeide.utils.text.style.StyleSpan;
import com.KillerBLS.modpeide.utils.text.style.SyntaxHighlightSpan;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextProcessor extends AppCompatMultiAutoCompleteTextView implements View.OnKeyListener {

    private static final String TAG = TextProcessor.class.getSimpleName();

    private EditorController mController;
    private ClipboardManager mClipboardManager;
    private TextWatcher mTextWatcher;
    private Scroller mScroller;

    private OnScrollChangedListener[] mScrollChangedListeners;
    private VelocityTracker mVelocityTracker;

    private StylePaint mLineNumberPaint;
    private StylePaint mGutterBackgroundPaint;
    private StylePaint mLinePaint;
    private StylePaint mSelectedLinePaint;

    private StyleSpan mSyntaxNumbers;
    private StyleSpan mSyntaxSymbols;
    private StyleSpan mSyntaxBrackets;
    private StyleSpan mSyntaxKeywords;
    private StyleSpan mSyntaxMethods;
    private StyleSpan mSyntaxStrings;
    private StyleSpan mSyntaxComments;

    private BackgroundColorSpan mOpenBracketSpan;
    private BackgroundColorSpan mClosedBracketSpan;

    private UndoStack.TextChange mUpdateLastChange;
    private boolean isDoingUndoRedo = false;
    private boolean isAutoIndenting = false;

    boolean mWrapContent = true;
    boolean mShowLineNumbers = true;
    boolean mSyntaxHighlight = true;
    boolean mBracketMatching = true;
    boolean mHighlightCurrentLine = true;
    boolean mAutoComplete = true;
    boolean mPinchZoom = true;
    boolean mIndentLine = true;
    boolean mInsertBracket = true;

    int mOnTextChangedChangeEnd;
    int mOnTextChangedChangeStart;
    String mOnTextChangedNewText;

    int mAddedTextCount = 0;
    String mNewText;
    String mOldText;
    String TAB_STR = "    "; //4 spaces

    int mMaximumVelocity;
    int mGutterWidth;
    int mLineNumberDigitCount = 0;
    int mCharHeight = 0;
    int mIdealMargin;

    int mTopDirtyLine = 0;
    int mBottomDirtyLine = 0;

    boolean zoomPinch = false;
    float zoomPinchFactor;
    float textSize;

    // region CONSTRUCTOR

    public TextProcessor(Context context) {
        super(context);
    }

    public TextProcessor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextProcessor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // endregion CONSTRUCTOR

    // region INIT

    public void init(EditorController controller) {
        mController = controller;
        if(!isInEditMode()) {
            initParameters();
            initTheme();
            initMethods();
            postInit();
        }
    }

    protected void initParameters() {
        mClipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        mScroller = new Scroller(getContext());
        mScrollChangedListeners = new OnScrollChangedListener[0];
        mTextWatcher = new TextChangeWatcher();
    }

    /**
     * Загрузка темы. Все основные Paint'ы и прочие элементы, которые зависят от темы.
     */
    protected void initTheme() {

        // region PAINT

        mLineNumberPaint = new StylePaint(true, false);
        mLineNumberPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorLineNumbersText));
        mLineNumberPaint.setTextAlign(StylePaint.Align.RIGHT);
        mLineNumberPaint.setTextSize(getTextSize());

        mLinePaint = new StylePaint(false, false);
        mLinePaint.setColor(mLineNumberPaint.getColor());
        mLinePaint.setStyle(StylePaint.Style.STROKE);

        mGutterBackgroundPaint = new StylePaint(false, false);
        mGutterBackgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorNumbersBackground));

        mSelectedLinePaint = new StylePaint(false, false);
        mSelectedLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.colorSelectedLine));

        // endregion PAINT

        mSyntaxNumbers = new StyleSpan(ContextCompat.getColor(getContext(), R.color.syntaxNumbers), false, false);
        mSyntaxSymbols = new StyleSpan(ContextCompat.getColor(getContext(), R.color.syntaxSymbols), false, false);
        mSyntaxBrackets = new StyleSpan(ContextCompat.getColor(getContext(), R.color.syntaxBrackets), false, false);
        mSyntaxKeywords = new StyleSpan(ContextCompat.getColor(getContext(), R.color.syntaxKeywords), false, false);
        mSyntaxMethods = new StyleSpan(ContextCompat.getColor(getContext(), R.color.syntaxMethods), false, false);
        mSyntaxStrings = new StyleSpan(ContextCompat.getColor(getContext(), R.color.syntaxStrings), false, false);
        mSyntaxComments = new StyleSpan(ContextCompat.getColor(getContext(), R.color.syntaxComments), false, true);
        mOpenBracketSpan = new BackgroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorBracketSpan));
        mClosedBracketSpan = mOpenBracketSpan;
        setCursorColor(ContextCompat.getColor(getContext(), R.color.colorCursor)); //Cursor Color
        setHighlightColor(ContextCompat.getColor(getContext(), R.color.colorSelection)); //Selection Color
    }

    protected void initMethods() {
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity() * 100;
        mIdealMargin = (int) TypedValue.applyDimension(1, 4, getResources().getDisplayMetrics());
        setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        setOnKeyListener(this);
    }

    protected void postInit() {
        postInvalidate();
        refreshDrawableState();
        invalidateCharHeight();
    }

    protected void invalidateCharHeight() {
        mCharHeight = (int) Math.ceil(getPaint().getFontSpacing());
        mCharHeight = (int) getPaint().measureText("M");
    }

    // endregion INIT

    // region BASE

    @Override
    public void setTextSize(float textSize) {
        super.setTextSize(textSize);
        if(mLineNumberPaint != null) {
            mLineNumberPaint.setTextSize(getTextSize());
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        syntaxHighlight();
        for (OnScrollChangedListener l : mScrollChangedListeners) { //Scroller
            int x = getScrollX();
            int y = getScrollY();
            l.onScrollChanged(x, y, x, y);
        }
        if(mAutoComplete) {//Suggestions
            onDropDownChangeSize(w, h);
        }
    }

    protected class TextChangeWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            mAddedTextCount -= count;
            mOldText = s.subSequence(start, start + count).toString();
            updateDocumentBeforeTextChanged(start, count);
            updateUndoRedoBeforeTextChanged(s, start, count);
            abortFling();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mAddedTextCount += count;
            mNewText = s.subSequence(start, start + count).toString();
            generalOnTextChanged(start, count);
            updateDocumentOnTextChanged(s, start, count);
            updateUndoRedoOnTextChanged(s, start, count);
            mOldText = "";
            mNewText = "";
            if(mAutoComplete) {
                onPopupChangePosition();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            mAddedTextCount = 0;
            //Очищаем старые syntax-спаны (и background-спаны), чтобы наложить новые
            clearSpans(true, true);
            syntaxHighlight();
        }
    }

    private void updateDocumentBeforeTextChanged(int start, int count) {
        mOnTextChangedChangeStart = start;
        mOnTextChangedChangeEnd = start + count;
    }

    private void updateDocumentOnTextChanged(CharSequence s, int start, int count) {
        mOnTextChangedNewText = s.subSequence(start, start + count).toString();
        mController.replaceText(mOnTextChangedChangeStart, mOnTextChangedChangeEnd, mOnTextChangedNewText);
    }

    private void updateUndoRedoOnTextChanged(CharSequence s, int start, int count) {
        if (!isDoingUndoRedo && mUpdateLastChange != null) {
            if(count < UndoStack.MAX_SIZE) {
                mUpdateLastChange.newText = s.subSequence(start, start + count).toString();
                if(start == mUpdateLastChange.start &&
                        ((mUpdateLastChange.oldText.length() > 0
                                || mUpdateLastChange.newText.length() > 0)
                                && !mUpdateLastChange.oldText.equals(mUpdateLastChange.newText))) {
                    mController.getUndoStack().push(mUpdateLastChange);
                    mController.getRedoStack().removeAll();
                }
            } else {
                mController.getUndoStack().removeAll();
                mController.getRedoStack().removeAll();
            }
            mUpdateLastChange = null;
        }
    }

    private void updateUndoRedoBeforeTextChanged(CharSequence s, int start, int count) {
        if (!isDoingUndoRedo) {
            if (count < UndoStack.MAX_SIZE) {
                mUpdateLastChange = new UndoStack.TextChange();
                mUpdateLastChange.oldText = s.subSequence(start, start + count).toString();
                mUpdateLastChange.start = start;
                return;
            }
            mController.getUndoStack().removeAll();
            mController.getRedoStack().removeAll();
            mUpdateLastChange = null;
        }
    }

    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        if(selStart == selEnd) {
            checkMatchingBracket(selStart);
        }
        invalidate();
    }

    protected Editable getSelectedText() {
        if (getSelectionEnd() > getSelectionStart()) {
            return (Editable) getText().subSequence(getSelectionStart(), getSelectionEnd());
        }
        return (Editable) getText().subSequence(getSelectionEnd(), getSelectionStart());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.isCtrlPressed()) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_X: // CTRL+X - Cut
                    cut();
                    return true;
                case KeyEvent.KEYCODE_C: // CTRL+C - Copy
                    copy();
                    return true;
                case KeyEvent.KEYCODE_V: // CTRL+V - Paste
                    paste();
                    return true;
                case KeyEvent.KEYCODE_Z: // CTRL+Z - Undo
                    undo();
                    return true;
                case KeyEvent.KEYCODE_Y: // CTRL+Y - Redo
                    redo();
                    return true;
                case KeyEvent.KEYCODE_A: // CTRL+A - Select All
                    selectAll();
                    return true;
                case KeyEvent.KEYCODE_DEL: // CTRL+Delete - Delete Line
                    deleteLine();
                    return true;
                case KeyEvent.KEYCODE_D: // CTRL+D - Duplicate Line
                    duplicateLine();
                    return true;
                case KeyEvent.KEYCODE_S: // CTRL+S - Save File
                    //mController.saveFile();
                    return true;
                default:
                    return super.onKeyDown(keyCode, event);
            }
        } else {
            switch (keyCode) {
                case KeyEvent.KEYCODE_TAB: // TAB
                    int start, end;
                    start = Math.max(getSelectionStart(), 0);
                    end = Math.max(getSelectionEnd(), 0);
                    getText().replace(Math.min(start, end),
                            Math.max(start, end), TAB_STR, 0, TAB_STR.length());
                    return true;
                default:
                    try {
                        return super.onKeyDown(keyCode, event);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                    return false;
            }
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        return false;
    }

    // endregion BASE

    // region INDENTATION

    private void generalOnTextChanged(int start, int count) {
        if (!isDoingUndoRedo && !isAutoIndenting) {
            String replacementValue;
            int newCursorPosition;
            String[] result = executeIndentation(start);
            if (result[0] != null || result[1] != null) {
                String preText = result[0] != null ? result[0] : "";
                String postText = result[1] != null ? result[1] : "";
                if (!preText.equals("") || !postText.equals("")) {
                    replacementValue = String.valueOf(preText) + mNewText + postText;
                } else {
                    return;
                }
            } else if (result[2] != null) {
                replacementValue = result[2];
            } else {
                return;
            }
            if (result[3] != null) {
                newCursorPosition = Integer.parseInt(result[3]);
            } else {
                newCursorPosition = start + replacementValue.length();
            }
            final int i = start;
            final int i2 = count;
            post(() -> {
                isAutoIndenting = true;
                getText().replace(i, i + i2, replacementValue);
                mController.getUndoStack().pop();
                UndoStack.TextChange change = mController.getUndoStack().pop();
                if (!replacementValue.equals("")) {
                    change.newText = replacementValue;
                    mController.getUndoStack().push(change);
                }
                Selection.setSelection(getText(), newCursorPosition);
                isAutoIndenting = false;
            });
        }
    }

    private String[] executeIndentation(int start) {
        String[] strArr;
        if(mNewText.equals("\n") && mIndentLine) {
            String prevLineIndentation = getIndentationForOffset(start);
            StringBuilder indentation = new StringBuilder(prevLineIndentation);
            int newCursorPosition = (indentation.length() + start) + 1;
            if (start > 0 && getText().charAt(start - 1) == '{') {
                indentation.append(TAB_STR);
                newCursorPosition = (indentation.length() + start) + 1;
            }
            if (start + 1 < getText().length() && getText().charAt(start + 1) == '}') {
                indentation.append("\n").append(prevLineIndentation);
            }
            strArr = new String[4];
            strArr[1] = indentation.toString();
            strArr[3] = Integer.toString(newCursorPosition);
            return strArr;
        } else if(mInsertBracket && mNewText.equals("{")) {
            strArr = new String[4];
            strArr[1] = "}";
            strArr[3] = Integer.toString(start + 1);
            return strArr;
        } else if(mInsertBracket && mNewText.equals("}")) {
            if (start + 1 < getText().length() && getText().charAt(start + 1) == '}') {
                strArr = new String[4];
                strArr[2] = "";
                strArr[3] = Integer.toString(start + 1);
                return strArr;
            }
        } else if(mInsertBracket && mNewText.equals("(")) {
            strArr = new String[4];
            strArr[1] = ")";
            strArr[3] = Integer.toString(start + 1);
            return strArr;
        } else if(mInsertBracket && mNewText.equals(")")) {
            if (start + 1 < getText().length() && getText().charAt(start + 1) == ')') {
                strArr = new String[4];
                strArr[2] = "";
                strArr[3] = Integer.toString(start + 1);
                return strArr;
            }
        } else if(mInsertBracket && mNewText.equals("[")) {
            strArr = new String[4];
            strArr[1] = "]";
            strArr[3] = Integer.toString(start + 1);
            return strArr;
        } else if(mInsertBracket && mNewText.equals("]")
                && start + 1 < getText().length() && getText().charAt(start + 1) == ']') {
            strArr = new String[4];
            strArr[2] = "";
            strArr[3] = Integer.toString(start + 1);
            return strArr;
        }
        return new String[4];
    }

    public String getIndentationForOffset(int offset) {
        return getIndentationForLine(mController.getLinesCollection().getLineForIndex(offset));
    }

    public String getIndentationForLine(int line) {
        LineObject l = mController.getLinesCollection().getLine(line);
        if (l == null) {
            return "";
        }
        int start = l.getStart();
        int i = start;
        while (i < getText().length()) {
            char c = getText().charAt(i);
            if (!Character.isWhitespace(c) || c == '\n') {
                break;
            }
            i++;
        }
        return getText().subSequence(start, i).toString();
    }

    // endregion INDENTATION

    // region SUGGESTIONS

    @Override
    public void showDropDown() {
        if (!isPopupShowing()) {
            if (hasFocus()) {
                super.showDropDown();
            }
        }
    }

    protected void loadSuggestions() {
        if(mController.getLanguage() != null) {
            ArrayList<SuggestionItem> data = new ArrayList<>();
            for (String name : mController.getLanguage().getAllCompletions()) {
                data.add(new SuggestionItem(name));
            }
            setSuggestData(data);
        }
    }

    protected void setSuggestData(ArrayList<SuggestionItem> data) {
        SuggestionAdapter mAdapter = new SuggestionAdapter(getContext(), R.layout.item_suggestion, data);
        setAdapter(mAdapter);
    }

    protected int getHeightVisible() {
        Rect rect = new Rect();
        getWindowVisibleDisplayFrame(rect);
        return rect.bottom - rect.top;
    }

    protected void onDropDownChangeSize(int w, int h) {
        Rect rect = new Rect();
        getWindowVisibleDisplayFrame(rect);

        // 1/2 width of screen
        setDropDownWidth((int) (w * 0.5f));

        // 0.5 height of screen
        setDropDownHeight((int) (h * 0.5f));

        //change position
        onPopupChangePosition();
    }

    protected void onPopupChangePosition() {
        try {
            Layout layout = getLayout();
            if (layout != null) {
                int pos = getSelectionStart();
                int line = layout.getLineForOffset(pos);
                int baseline = layout.getLineBaseline(line);
                int ascent = layout.getLineAscent(line);

                float x = layout.getPrimaryHorizontal(pos);
                float y = baseline + ascent;

                int offsetHorizontal = (int) x + mGutterWidth;
                setDropDownHorizontalOffset(offsetHorizontal);

                int heightVisible = getHeightVisible();
                int offsetVertical = (int) ((y + mCharHeight) - getScrollY());

                int tmp = offsetVertical + getDropDownHeight() + mCharHeight;
                if (tmp < heightVisible) {
                    tmp = offsetVertical + mCharHeight / 2;
                    setDropDownVerticalOffset(tmp);
                } else {
                    tmp = offsetVertical - getDropDownHeight() - mCharHeight;
                    setDropDownVerticalOffset(tmp);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    // endregion SUGGESTIONS

    // region SCROLLER

    @Override
    public void onScrollChanged(int horiz, int vert, int oldHoriz, int oldVert) {
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert);
        if (mScrollChangedListeners != null) {
            for (OnScrollChangedListener l : mScrollChangedListeners) {
                l.onScrollChanged(horiz, vert, oldHoriz, oldVert);
            }
        }
        if (mTopDirtyLine > getTopVisibleLine() || mBottomDirtyLine < getBottomVisibleLine()) {
            //Очищаем старые syntax-спаны, чтобы наложить новые
            clearSpans(false, true);
            syntaxHighlight();
        }
    }

    public void addOnScrollChangedListener(OnScrollChangedListener listener) {
        OnScrollChangedListener[] newListener =
                new OnScrollChangedListener[mScrollChangedListeners.length + 1];
        int length = mScrollChangedListeners.length;
        System.arraycopy(mScrollChangedListeners, 0, newListener, 0, length);
        newListener[newListener.length - 1] = listener;
        mScrollChangedListeners = newListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                int velocityX;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (mWrapContent) {
                    velocityX = 0;
                } else {
                    velocityX = (int) mVelocityTracker.getXVelocity();
                }
                if (Math.abs(velocityY) >= 0 || Math.abs(velocityX) >= 0) {
                    if (getLayout() == null) {
                        return super.onTouchEvent(event);
                    }
                    mScroller.fling(
                            getScrollX(), getScrollY(),
                            -velocityX, -velocityY,
                            0, ((getLayout().getWidth() - getWidth())
                                    + getPaddingLeft()) + getPaddingRight(),
                            0, ((getLayout().getHeight() - getHeight())
                                    + getPaddingTop()) + getPaddingBottom());
                } else if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                super.onTouchEvent(event);
                break;
            default:
                super.onTouchEvent(event);
                break;
        }
        return true;
    }

    /**
     * Останавливает анимацию скроллинга.
     */
    public void abortFling() {
        if (mScroller != null && !mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
    }

    @Override
    public void computeScroll() {
        if (!isInEditMode() && mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
        }
    }

    // endregion SCROLLER

    // region LINE_NUMBERS

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        if (!isInEditMode()) {
            int top;
            Layout layout = getLayout();
            if (layout != null && mController != null && mHighlightCurrentLine) {
                int currentLineStart = mController.getLineForIndex(getSelectionStart());
                if (currentLineStart == mController.getLineForIndex(getSelectionEnd())) {
                    int selectedLineStartIndex = mController.getIndexForStartOfLine(currentLineStart);
                    int selectedLineEndIndex = mController.getIndexForEndOfLine(currentLineStart);
                    int topVisualLine = layout.getLineForOffset(selectedLineStartIndex);
                    int bottomVisualLine = layout.getLineForOffset(selectedLineEndIndex);
                    int left = mGutterWidth;
                    if(!mShowLineNumbers) {
                        left = 0; //убираем отступ для Paint'а если номера строк отключены
                    }
                    top = layout.getLineTop(topVisualLine) + getPaddingTop();
                    int right = (layout.getWidth() + getPaddingLeft()) + getPaddingRight();
                    int bottom = layout.getLineBottom(bottomVisualLine) + getPaddingTop();
                    canvas.drawRect(left, top, right, bottom, mSelectedLinePaint);
                }
            }
            super.onDraw(canvas);
            if (layout != null && mShowLineNumbers) {
                int prevLineNumber = -1;
                canvas.drawRect(getScrollX(), getScrollY(),
                        mGutterWidth + getScrollX(),
                        getScrollY() + getHeight(), mGutterBackgroundPaint);
                int paddingTop = getPaddingTop();
                int max = getBottomVisibleLine();
                int textRight = (mGutterWidth - mIdealMargin / 2) + getScrollX();
                if (mController != null) {
                    int i = getTopVisibleLine();
                    if (i >= 2) {
                        i -= 2;
                    } else {
                        i = 0;
                    }
                    while (i <= max) {
                        int number = mController.getLineForIndex(getLayout().getLineStart(i));
                        if (number != prevLineNumber) {
                            canvas.drawText(Integer.toString(number + 1), textRight,
                                    layout.getLineBaseline(i) + paddingTop, mLineNumberPaint);
                        }
                        prevLineNumber = number;
                        i++;
                    }
                    top = getScrollY();
                    canvas.drawLine(mGutterWidth + getScrollX(), top,
                            mGutterWidth + getScrollX(), top + getHeight(), mLinePaint);
                }
            }
        }
    }

    /**
     * Обновление Gutter'а (панель с нумерацией строк).
     */
    public void updateGutter() {
        int max = 3;
        if(mShowLineNumbers && mController != null && getLayout() != null) {
            TextPaint paint = getLayout().getPaint();
            if(paint != null) {
                mLineNumberDigitCount = Integer.toString(mController.getLineCount()).length();
                int widestNumber = 0;
                float widestWidth = 0.0f;
                for (int i = 0; i <= 9; i++) {
                    float width = paint.measureText(Integer.toString(i));
                    if (width > widestWidth) {
                        widestNumber = i;
                        widestWidth = width;
                    }
                }
                StringBuilder builder = new StringBuilder();
                if (mLineNumberDigitCount >= 3) {
                    max = mLineNumberDigitCount;
                }
                for (int i = 0; i < max; i++) {
                    builder.append(Integer.toString(widestNumber));
                }
                mGutterWidth = (int) paint.measureText(builder.toString());
                mGutterWidth += mIdealMargin;
                if(getPaddingLeft() != mGutterWidth + mIdealMargin) {
                    setPadding(mGutterWidth + mIdealMargin,
                            mIdealMargin, getPaddingRight(), getPaddingBottom());
                }
            }
        } else if(mIdealMargin != getPaddingLeft()) {
            setPadding(mIdealMargin, mIdealMargin, getPaddingRight(), getPaddingBottom());
        }
    }

    protected int getTopVisibleLine() {
        int lineHeight = getLineHeight();
        if (lineHeight == 0) {
            return 0;
        }
        int line = getScrollY() / lineHeight;
        if (line < 0) {
            return 0;
        }
        if (line >= getLineCount()) {
            return getLineCount() - 1;
        }
        return line;
    }

    protected int getBottomVisibleLine() {
        int lineHeight = getLineHeight();
        if (lineHeight == 0) {
            return 0;
        }
        int line = Math.abs((getScrollY() + getHeight()) / lineHeight) + 1;
        if (line < 0) {
            return 0;
        }
        if (line >= getLineCount()) {
            return getLineCount() - 1;
        }
        return line;
    }

    // endregion LINE_NUMBERS

    // region SPANS

    /**
     * Очистка текста от спанов разного типа.
     * @param backgroundSpans - очищать background-спаны?
     * @param foregroundSpans - очищать foreground-спаны?
     */
    protected void clearSpans(boolean backgroundSpans, boolean foregroundSpans) {
        if(backgroundSpans) { //remove background color spans
            BackgroundColorSpan spans[] = getText().getSpans(0, getText().length(), BackgroundColorSpan.class);
            for (BackgroundColorSpan span : spans) {
                getText().removeSpan(span);
            }
        }
        if(foregroundSpans) {
            SyntaxHighlightSpan[] spans = getText().getSpans(0, getText().length(), SyntaxHighlightSpan.class);
            for (SyntaxHighlightSpan span : spans) {
                getText().removeSpan(span);
            }
        }
    }

    /**
     * Процесс подсветки синтаксиса.
     */
    protected void syntaxHighlight() {
        if (mSyntaxHighlight && getLayout() != null) {
            int topLine = (getScrollY() / getLineHeight()) - 10;
            int bottomLine = (((getScrollY() + getHeight()) / getLineHeight()) + 1) + 10;
            if (topLine < 0) {
                topLine = 0;
            }
            if (bottomLine > getLayout().getLineCount()) {
                bottomLine = getLayout().getLineCount();
            }
            if (topLine > getLayout().getLineCount()) {
                topLine = getLayout().getLineCount();
            }
            if (bottomLine >= 0 && topLine >= 0) {
                int topLineOffset;
                mTopDirtyLine = topLine;
                mBottomDirtyLine = bottomLine;
                topLineOffset = getLayout().getLineStart(topLine);
                final int bottomLineOffset = bottomLine < getLineCount()
                        ? getLayout().getLineStart(bottomLine) : getLayout().getLineStart(getLineCount());

                // region PROCESS_HIGHLIGHT

                if(mController.getLanguage() != null) {
                    Matcher m = mController.getLanguage().getSyntaxNumbers().matcher( //Numbers
                            getText().subSequence(topLineOffset, bottomLineOffset));
                    while (m.find()) {
                        getText().setSpan(
                                new SyntaxHighlightSpan(
                                        mSyntaxNumbers,
                                        topLineOffset, bottomLineOffset),
                                m.start() + topLineOffset, m.end() + topLineOffset,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    m = mController.getLanguage().getSyntaxSymbols().matcher( //Symbols
                            getText().subSequence(topLineOffset, bottomLineOffset));
                    while (m.find()) {
                        getText().setSpan(
                                new SyntaxHighlightSpan(
                                        mSyntaxSymbols,
                                        topLineOffset, bottomLineOffset),
                                m.start() + topLineOffset, m.end() + topLineOffset,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    m = mController.getLanguage().getSyntaxBrackets().matcher( //Brackets
                            getText().subSequence(topLineOffset, bottomLineOffset));
                    while (m.find()) {
                        getText().setSpan(
                                new SyntaxHighlightSpan(
                                        mSyntaxBrackets,
                                        topLineOffset, bottomLineOffset),
                                m.start() + topLineOffset, m.end() + topLineOffset,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    m = mController.getLanguage().getSyntaxKeywords().matcher( //Keywords
                            getText().subSequence(topLineOffset, bottomLineOffset));
                    while (m.find()) {
                        getText().setSpan(
                                new SyntaxHighlightSpan(
                                        mSyntaxKeywords,
                                        topLineOffset, bottomLineOffset),
                                m.start() + topLineOffset, m.end() + topLineOffset,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    m = mController.getLanguage().getSyntaxMethods().matcher( //Methods
                            getText().subSequence(topLineOffset, bottomLineOffset));
                    while (m.find()) {
                        getText().setSpan(
                                new SyntaxHighlightSpan(
                                        mSyntaxMethods,
                                        topLineOffset, bottomLineOffset),
                                m.start() + topLineOffset, m.end() + topLineOffset,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    m = mController.getLanguage().getSyntaxStrings().matcher( //Strings
                            getText().subSequence(topLineOffset, bottomLineOffset));
                    while (m.find()) {
                        for (ForegroundColorSpan span : getText().getSpans(
                                m.start() + topLineOffset,
                                m.end() + topLineOffset, ForegroundColorSpan.class)) {
                            getText().removeSpan(span);
                        }
                        getText().setSpan(
                                new SyntaxHighlightSpan(
                                        mSyntaxStrings,
                                        topLineOffset, bottomLineOffset),
                                m.start() + topLineOffset, m.end() + topLineOffset,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    m = mController.getLanguage().getSyntaxComments().matcher( //Comments
                            getText().subSequence(topLineOffset, bottomLineOffset));
                    while (m.find()) {
                        boolean skip = false;
                        for (ForegroundColorSpan span : getText().getSpans(topLineOffset,
                                m.end() + topLineOffset,
                                ForegroundColorSpan.class)) {

                            int spanStart = getText().getSpanStart(span);
                            int spanEnd = getText().getSpanEnd(span);
                            if (((m.start() + topLineOffset >= spanStart && m.start() + topLineOffset
                                    <= spanEnd && m.end() + topLineOffset > spanEnd)
                                    || (m.start() + topLineOffset
                                    >= topLineOffset + spanEnd
                                    && m.start() + topLineOffset <= spanEnd))) {
                                skip = true;
                                break;
                            }

                        }
                        if (!skip) {
                            for (ForegroundColorSpan span : getText().getSpans(
                                    m.start() + topLineOffset, m.end() + topLineOffset,
                                    ForegroundColorSpan.class)) {
                                getText().removeSpan(span);
                            }
                            getText().setSpan(
                                    new SyntaxHighlightSpan(
                                            mSyntaxComments,
                                            topLineOffset, bottomLineOffset),
                                    m.start() + topLineOffset, m.end() + topLineOffset,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }

                // endregion PROCESS_HIGHLIGHT

                post(this::invalidateVisibleArea);
            }
        }
    }

    private void invalidateVisibleArea() {
        invalidate(getPaddingLeft(), getScrollY() + getPaddingTop(),
                getWidth(), (getScrollY() + getPaddingTop()) + getHeight());
    }

    /**
     * Алгоритм совпадения скобок.
     * @param pos - позиция курсора.
     */
    protected void checkMatchingBracket(int pos) {
        getText().removeSpan(mOpenBracketSpan);
        getText().removeSpan(mClosedBracketSpan);
        if (mBracketMatching && mController.getLanguage() != null) {
            if (pos > 0 && pos <= getText().length()) {
                char c1 = getText().charAt(pos - 1);
                for (int i = 0; i < mController.getLanguage().getLanguageBrackets().length; i++) {
                    if (mController.getLanguage().getLanguageBrackets()[i] == c1) {
                        char c2 = mController.getLanguage().getLanguageBrackets()[(i + 3) % 6];
                        boolean open = false;
                        if (i <= 2) {
                            open = true;
                        }
                        int k;
                        if (open) {
                            int nob = 1;
                            for (k = pos; k < getText().length(); k++) {
                                if (getText().charAt(k) == c2) {
                                    nob--;
                                }
                                if (getText().charAt(k) == c1) {
                                    nob++;
                                }
                                if (nob == 0) {
                                    showBracket(pos - 1, k);
                                    break;
                                }
                            }
                        } else {
                            int ncb = 1;
                            for (k = pos - 2; k >= 0; k--) {
                                if (getText().charAt(k) == c2) {
                                    ncb--;
                                }
                                if (getText().charAt(k) == c1) {
                                    ncb++;
                                }
                                if (ncb == 0) {
                                    showBracket(k, pos - 1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Подсветка найденных скобок.
     * @param i - позиция первой скобки.
     * @param j - позиция второй скобки.
     */
    protected void showBracket(int i, int j) {
        getText().setSpan(mOpenBracketSpan, i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        getText().setSpan(mClosedBracketSpan, j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    // endregion SPANS

    // region PINCH_ZOOM

    protected boolean pinchZoom(MotionEvent ev) {
        switch(ev.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                zoomPinch = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if(ev.getPointerCount() == 2) {
                    float distance = getDistanceBetweenTouches(ev);
                    if(!zoomPinch) {
                        zoomPinchFactor = textSize / distance;
                        zoomPinch = true;
                        break;
                    }
                    textSize = zoomPinchFactor * distance;
                    validateTextSize();
                    setTextSize(textSize);
                }
                break;
        }
        return zoomPinch;
    }

    protected float getDistanceBetweenTouches(MotionEvent ev) {
        float xx = ev.getX(1) - ev.getX(0);
        float yy = ev.getY(1) - ev.getY(0);
        return (float) Math.sqrt(xx*xx + yy*yy);
    }

    protected void validateTextSize() {
        if(textSize < 10) { //minimum
            textSize = 10; //minimum
        } else if(textSize > 20) { //maximum
            textSize = 20; //maximum
        }
    }

    // endregion PINCH_ZOOM

    // region DOC_METHODS

    /**
     * Включает перенос слов на строку ниже, если на текущей строке закончилось место.
     * @param wrapContent - отвечает за перенос строк.
     */
    public void setWrapContent(boolean wrapContent) {
        mWrapContent = wrapContent;
        setHorizontallyScrolling(!wrapContent);
    }

    /**
     * Отключаем фокусировку на редакторе, нажатия не будут засчитываться,
     * однако скроллинг будет продолжать работать.
     * @param readOnly - отвечает за режим "Read Only".
     */
    public void setReadOnly(boolean readOnly) {
        setFocusable(!readOnly);
        setFocusableInTouchMode(!readOnly);
    }

    /**
     * Метод для переключения подсветки синтаксиса.
     * @param syntaxHighlight - отвечает за включение подсветки синтаксиса.
     */
    public void setSyntaxHighlight(boolean syntaxHighlight) {
        mSyntaxHighlight = syntaxHighlight;
        if(mSyntaxHighlight) {
            syntaxHighlight();
        } else { //Очищаем syntax-спаны, потому что они больше не нужны при выключенной подсветке
            clearSpans(false, true);
        }
    }

    /**
     * Метод переключает подсветку скобок.
     * @param bracketMatching - отвечает за включение сопоставления скобок.
     */
    public void setBracketMatching(boolean bracketMatching) {
        mBracketMatching = bracketMatching;
    }

    /**
     * Обновление типа ввода. Если в настройках отключены подсказки на клавиатуре,
     * то они не будут отображаться.
     */
    public void setImeKeyboard(boolean useImeKeyboard) {
        if(useImeKeyboard) {
            setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        } else {
            setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }
    }

    /**
     * Установка шрифта. Применяется также и к нумерации строк.
     */
    public void setFontType(String fontType) {
        switch (fontType) {
            case "droid_sans_mono":
                setTypeface(TypefaceManager.get(getContext(), TypefaceManager.DROID_SANS_MONO));
                break;
            case "source_code_pro":
                setTypeface(TypefaceManager.get(getContext(), TypefaceManager.SOURCE_CODE_PRO));
                break;
            case "roboto":
                setTypeface(TypefaceManager.get(getContext(), TypefaceManager.ROBOTO));
                break;
            case "roboto_light":
                setTypeface(TypefaceManager.get(getContext(), TypefaceManager.ROBOTO_LIGHT));
                break;
            default:
                setTypeface(TypefaceManager.get(getContext(), TypefaceManager.DROID_SANS_MONO));
                break;
        }
        mLineNumberPaint.setTypeface(getTypeface());
        setPaintFlags(getPaintFlags() | StylePaint.SUBPIXEL_TEXT_FLAG);
    }

    /**
     * Отображение панели с нумерацией строк.
     * @param show - вкл. / выкл.
     */
    public void setShowLineNumbers(boolean show) {
        if (mShowLineNumbers != show) {
            mShowLineNumbers = show;
            post(this::updateGutter);
        }
    }

    /**
     * Включение автодополнения кода.
     * @param enabled - вкл. / выкл.
     */
    public void setCodeCompletion(boolean enabled) {
        mAutoComplete = enabled;
        if(mAutoComplete) {
            SymbolsTokenizer mTokenizer = new SymbolsTokenizer();
            setTokenizer(mTokenizer);
            setThreshold(2); //задержка перед отображением = 2 символа
            loadSuggestions(); //загружаем список слов
        } else {
            setTokenizer(null);
        }
    }

    /**
     * Подсветка текущей линии.
     * @param enabled - вкл. / выкл.
     */
    public void setHighlightCurrentLine(boolean enabled) {
        mHighlightCurrentLine = enabled;
    }

    public void enableStacks() { //включаем заполнение стака
        addTextChangedListener(mTextWatcher);
    }

    public void disableStacks() {
        removeTextChangedListener(mTextWatcher);
    }

    /**
     * Увеличивать размер текста жестом приближения.
     * @param enabled - вкл. / выкл.
     */
    public void setPinchZoom(boolean enabled) {
        mPinchZoom = enabled;
        if(mPinchZoom) {
            float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
            textSize = getTextSize()/scaledDensity;
            setOnTouchListener((v, ev) -> pinchZoom(ev));
        } else {
            setOnTouchListener((v, ev) -> false);
        }
    }

    /**
     * Включение индентации при переходе на строку ниже.
     * @param enabled - вкл. / выкл.
     */
    public void setIndentLine(boolean enabled) {
        mIndentLine = enabled;
    }

    /**
     * Автоматически закрывать открытые скобки.
     * @param enabled - вкл. / выкл.
     */
    public void setInsertBrackets(boolean enabled) {
        mInsertBracket = enabled;
    }

    /**
     * Изменение цвета курсора в редакторе. { https://stackoverflow.com/a/26543290/4405457 }
     * @param color - цвет курсора.
     */
    public void setCursorColor(@ColorInt int color) {
        try {
            // Get the cursor resource id
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(this);

            // Get the editor
            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(this);

            // Get the drawable and set a color filter
            Drawable drawable = ContextCompat.getDrawable(getContext(), drawableResId);
            if (drawable != null) {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
            Drawable[] drawables = {drawable, drawable};

            // Set the drawables
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    // endregion DOC_METHODS

    // region METHODS

    public void insert(CharSequence delta) {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        selectionStart = Math.max(0, selectionStart);
        selectionEnd = Math.max(0, selectionEnd);
        selectionStart = Math.min(selectionStart, selectionEnd);
        selectionEnd = Math.max(selectionStart, selectionEnd);
        try {
            getText().delete(selectionStart, selectionEnd);
            getText().insert(selectionStart, delta);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void cut() {
        Editable selectedText = getSelectedText();
        if(selectedText == null || selectedText.toString().equals("")) {
            Toast.makeText(getContext(), R.string.message_nothing_to_cut, Toast.LENGTH_SHORT).show();
        } else {
            mClipboardManager.setPrimaryClip(ClipData.newPlainText("CUT", selectedText));
            if (getSelectionEnd() > getSelectionStart()) {
                getText().replace(getSelectionStart(), getSelectionEnd(), "");
            } else {
                getText().replace(getSelectionEnd(), getSelectionStart(), "");
            }
        }
    }

    public void copy() {
        Editable selectedText = getSelectedText();
        if (selectedText == null || selectedText.toString().equals("")) {
            Toast.makeText(getContext(), R.string.message_nothing_to_copy, Toast.LENGTH_SHORT).show();
        } else {
            mClipboardManager.setPrimaryClip(ClipData.newPlainText("COPY", selectedText));
        }
    }

    public void paste() {
        if (mClipboardManager.getPrimaryClip() == null ||
                mClipboardManager.getPrimaryClip().toString().equals("")) {
            Toast.makeText(getContext(), R.string.message_nothing_to_paste, Toast.LENGTH_SHORT).show();
        }
        if (!mClipboardManager.hasPrimaryClip()) {
            return;
        }
        if (getSelectionEnd() > getSelectionStart()) {
            getText().replace(getSelectionStart(), getSelectionEnd(),
                    mClipboardManager.getPrimaryClip()
                            .getItemAt(0).coerceToText(getContext()));
        } else {
            getText().replace(getSelectionEnd(), getSelectionStart(),
                    mClipboardManager.getPrimaryClip()
                            .getItemAt(0).coerceToText(getContext()));
        }
    }

    public void selectLine() {
        int start = Math.min(getSelectionStart(), getSelectionEnd());
        int end = Math.max(getSelectionStart(), getSelectionEnd());
        if (end > start) {
            end--;
        }
        while (end < getText().length() && getText().charAt(end) != '\n') {
            end++;
        }
        while (start > 0 && getText().charAt(start - 1) != '\n') {
            start--;
        }
        setSelection(start, end);
    }

    public void deleteLine() {
        int start = Math.min(getSelectionStart(), getSelectionEnd());
        int end = Math.max(getSelectionStart(), getSelectionEnd());
        if (end > start) {
            end--;
        }
        while (end < getText().length() && getText().charAt(end) != '\n') {
            end++;
        }
        while (start > 0 && getText().charAt(start - 1) != '\n') {
            start--;
        }
        getEditableText().delete(start, end);
    }

    public void duplicateLine() {
        int start = Math.min(getSelectionStart(), getSelectionEnd());
        int end = Math.max(getSelectionStart(), getSelectionEnd());
        if (end > start) {
            end--;
        }
        while (end < getText().length() && getText().charAt(end) != '\n') {
            end++;
        }
        while (start > 0 && getText().charAt(start - 1) != '\n') {
            start--;
        }
        getEditableText().insert(end, "\n" +
                getText().subSequence(start, end).toString());
    }

    public void undo() {
        UndoStack.TextChange textChange = mController.getUndoStack().pop();
        if (textChange == null) {
            Toast.makeText(getContext(), R.string.message_nothing_to_undo, Toast.LENGTH_SHORT).show();
        } else if (textChange.start >= 0) {
            isDoingUndoRedo = true;
            if (textChange.start > getText().length()) {
                textChange.start = getText().length();
            }
            int end = textChange.start + textChange.newText.length();
            if (end < 0) {
                end = 0;
            }
            if (end > getText().length()) {
                end = getText().length();
            }
            getText().replace(textChange.start, end, textChange.oldText);
            Selection.setSelection(getText(), textChange.start + textChange.oldText.length());
            mController.getRedoStack().push(textChange);
            isDoingUndoRedo = false;
        } else {
            Log.e(TAG, "undo(): unknown error", null);
            mController.getUndoStack().clear();
        }
    }

    public void redo() {
        UndoStack.TextChange textChange = mController.getRedoStack().pop();
        if (textChange == null) {
            Toast.makeText(getContext(), R.string.message_nothing_to_redo, Toast.LENGTH_SHORT).show();
        } else if (textChange.start >= 0) {
            isDoingUndoRedo = true;
            getText().replace(textChange.start,
                    textChange.start + textChange.oldText.length(), textChange.newText);
            Selection.setSelection(getText(), textChange.start + textChange.newText.length());
            mController.getUndoStack().push(textChange);
            isDoingUndoRedo = false;
        } else {
            Log.e(TAG, "redo(): unknown error", null);
            mController.getUndoStack().clear();
        }
    }

    public void gotoLine(int toLine) {
        int realLine = toLine - 1; //т.к первая линия = 0
        if(realLine == -1) {
            Toast.makeText(getContext(), R.string.message_line_above_than_0, Toast.LENGTH_SHORT).show();
        } else if(realLine < mController.getLineCount()) {
            setSelection(mController.getIndexForStartOfLine(realLine));
        } else {
            Toast.makeText(getContext(), R.string.message_line_not_exists, Toast.LENGTH_SHORT).show();
        }
    }

    public void find(String what, boolean matchCase, boolean regex, boolean wordOnly) {
        Pattern pattern;
        if (regex) {
            if (matchCase) {
                pattern = Pattern.compile(what);
            } else {
                pattern = Pattern.compile(what,
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            }
        } else {
            if (wordOnly) {
                if (matchCase) {
                    pattern = Pattern.compile("\\s" + what + "\\s");
                } else {
                    pattern = Pattern.compile("\\s" + Pattern.quote(what) + "\\s",
                            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                }
            } else {
                if (matchCase) {
                    pattern = Pattern.compile(Pattern.quote(what));
                } else {
                    pattern = Pattern.compile(Pattern.quote(what),
                            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                }
            }
        }
        //Очищаем background-спаны, потому что будем накладывать новые
        clearSpans(true, false);
        for (Matcher m = pattern.matcher(getText()); m.find(); ) {
            getText().setSpan(new BackgroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorSearchSpan)),
                    m.start(),
                    m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public void replaceAll(String what, String with) {
        //Очищаем спаны из-за смены текста
        clearSpans(true, true);
        setText(getText().toString().replaceAll(what, with));
    }

    // endregion METHODS
}
