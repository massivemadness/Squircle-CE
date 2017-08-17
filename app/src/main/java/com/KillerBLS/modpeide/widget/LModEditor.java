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

package com.KillerBLS.modpeide.widget;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.adapter.BasicCompletionAdapter;
import com.KillerBLS.modpeide.adapter.CompletionItem;
import com.KillerBLS.modpeide.hardware.OnScrollChangedListener;
import com.KillerBLS.modpeide.hardware.OnSizeRedrawListener;
import com.KillerBLS.modpeide.hardware.SyntaxEditorListener;
import com.KillerBLS.modpeide.manager.SuggestionManager;
import com.KillerBLS.modpeide.util.CompletionKeywords;
import com.KillerBLS.modpeide.util.LModLogUtils;
import com.KillerBLS.modpeide.util.SymbolsTokenizer;
import com.KillerBLS.modpeide.util.UndoRedoHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LModEditor extends AppCompatMultiAutoCompleteTextView
        implements OnKeyListener, OnGestureListener, SyntaxEditorListener {

    private static char[] BRACKETS = new char[]{'{', '[', '(', '}', ']', ')'}; //do not change
    private static String GLOBAL_TAB = "    ";

    /* Empty patterns, you can add your own using addSyntaxPattern(Pattern, SyntaxType, Color);*/
    private Pattern PATTERN_KEYWORDS = Pattern.compile("", Pattern.CASE_INSENSITIVE);
    private Pattern PATTERN_STRINGS = Pattern.compile("");
    private Pattern PATTERN_NUMBERS = Pattern.compile("");
    private Pattern PATTERN_SYMBOLS = Pattern.compile("");
    private Pattern PATTERN_KEYWORDS2 = Pattern.compile("", Pattern.CASE_INSENSITIVE);
    private Pattern PATTERN_COMMENTS = Pattern.compile("");
    private Pattern PATTERN_CLASSES = Pattern.compile("", Pattern.CASE_INSENSITIVE);

    //Syntax Highlight Colors
    private int COLOR_KEYWORDS = Color.RED;
    private int COLOR_KEYWORDS2 = Color.WHITE;
    private int COLOR_NUMBERS = Color.BLUE;
    private int COLOR_STRINGS = Color.GREEN;
    private int COLOR_COMMENTS = Color.GRAY;
    private int COLOR_SYMBOLS = Color.YELLOW;
    private int COLOR_CLASSES = Color.DKGRAY;

    //Settings
    private boolean PINCH_ZOOM = true;
    private boolean SHOW_LINE_NUMBERS = true;
    private float FIXED_TEXT_SIZE = 14;
    private boolean HIGHLIGHT_CURRENT_LINE = true;
    private int CURRENT_LINE_COLOR = Color.DKGRAY;
    private int SYNTAX_UPDATE_DELAY = 250; //Default
    private int SCROLL_RUNNABLE_DELAY = 150;
    private int LINE_NUMBERS_COLOR = Color.WHITE;
    private int SELECTION_COLOR = Color.BLUE;
    private boolean AUTOCOMPLETE = true;
    private boolean AUTOINDENTLINE = true;
    private boolean SYNTAX_HIGHLIGHT = true;
    private boolean BRACKET_MATCHING = true;
    private int MATCHED_BRACKET_COLOR = Color.GREEN;
    private boolean BRACKETS_AUTO_CLOSING = true;
    private BackgroundColorSpan openBracketSpan = new BackgroundColorSpan(MATCHED_BRACKET_COLOR);
    private BackgroundColorSpan closeBracketSpan = new BackgroundColorSpan(MATCHED_BRACKET_COLOR);
    private UndoRedoHelper mUndoRedoHelper;
    private int MAX_HISTORY_SIZE = 100;
    private int mCharHeight = 0;
    protected SymbolsTokenizer mTokenizer;

    private boolean HIGHLIGHT_KEYWORDS = true;
    private boolean HIGHLIGHT_KEYWORDS2 = true;
    private boolean HIGHLIGHT_NUMBERS = true;
    private boolean HIGHLIGHT_STRINGS = true;
    private boolean HIGHLIGHT_COMMENTS = true;
    private boolean HIGHLIGHT_SYMBOLS = true;
    private boolean HIGHLIGHT_CLASSES = true;

    //Additions
    private boolean sIsRunning = false;
    private boolean mScrollTouchStop = false;
    private boolean mSizeRedrawInvoked = false;
    private float textSize;
    private float scaledDensity;
    private boolean zoomPinch = false;
    private float zoomPinchFactor;
    private int mFirst;
    private int mLast;
    private int mLeftPadding;
    private int mLineCount;
    private int mPadding;
    private int mRotation;
    private int mScrollDirection;
    private Rect mDrawingRect;
    private GestureDetector mGestureDetector;
    private Handler mHandler = new Handler();
    private Rect mLineBounds;
    private OnScrollChangedListener mOnScrollChangedListener = null;
    private OnSizeRedrawListener mOnSizeRedrawListener = null;
    private Paint mPaintHighlight;
    private Paint mPaintNumbers;
    private Point mScrollMax;
    private Scroller mScroller;
    private WindowManager mWindowManager;
    private Handler updateHandler = new Handler();
    private Runnable scrollCompletionRunnable = new Runnable() {
        @Override
        public void run() {
            if (mScroller.isFinished()) {
                removeCallbacks(this);
                setLongClickable(true);
                if (mOnScrollChangedListener != null) {
                    mOnScrollChangedListener.onScrollChanged(mScrollDirection);
                    return;
                }
                if(SYNTAX_HIGHLIGHT) {
                    cancelUpdate();
                    updateHandler.postDelayed(updateRunnable, SYNTAX_UPDATE_DELAY);
                }
                return;
            }
            postDelayed(this, SCROLL_RUNNABLE_DELAY);
        }
    };
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            highlight();
        }
    };

    public LModEditor(Context context) {
        super(context);
        init(context);
    }

    public LModEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected void init(Context context) {
        setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mGestureDetector = new GestureDetector(getContext(), this);
        mGestureDetector.setIsLongpressEnabled(true);
        mScroller = new Scroller(getContext(), null, false);
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mRotation = mWindowManager.getDefaultDisplay().getRotation();
        mDrawingRect = new Rect();
        mLineBounds = new Rect();
        mScrollMax = new Point();
        mPaintNumbers = new Paint();
        mPaintNumbers.setAntiAlias(true);
        mPaintNumbers.setDither(false);
        mPaintHighlight = new Paint();
		mPaintHighlight.setStyle(Style.FILL);
        mPadding = (int) (6.0f * context.getResources().getDisplayMetrics().density);
        mScrollDirection = 0;
        setHorizontallyScrolling(true);
        addTextChangedListener(new EditTextChangeListener());
        mUndoRedoHelper = new UndoRedoHelper(this);
        mUndoRedoHelper.setMaxHistorySize(MAX_HISTORY_SIZE);
        setOnKeyListener(this);
        postInvalidate();
        refreshDrawableState();
        invalidateCharHeight();
    }

    //region BASIC

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int rotation = mWindowManager.getDefaultDisplay().getRotation();
        if (mRotation != rotation) {
            mRotation = rotation;
            mSizeRedrawInvoked = false;
            return;
        }
        mSizeRedrawInvoked = false;
        if(SYNTAX_HIGHLIGHT) {
            cancelUpdate();
            updateHandler.postDelayed(updateRunnable, SYNTAX_UPDATE_DELAY);
        }
        if(AUTOCOMPLETE) {
            onDropdownChangeSize(w, h);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(@NonNull Canvas canvas) {
        Layout layout = getLayout();
        if (layout != null) {
            int count = getLineCount();
            int scrollY = getScrollY();
            mFirst = layout.getLineForVertical(scrollY);
            mLast = layout.getLineForVertical(getHeight() + scrollY);
            getDrawingRect(mDrawingRect);
            mScrollMax.x = 0;
            getLineBounds(count - 1, mLineBounds);
            mScrollMax.y = Math.max((mLineBounds.bottom + mPadding) - mDrawingRect.height(), 0);
            int usableWidth = mDrawingRect.width() - (mLeftPadding + mPadding);
            if(HIGHLIGHT_CURRENT_LINE) {
                getLineBounds(getLine(), mLineBounds);
                canvas.drawRect(mLineBounds, mPaintHighlight);
            }
            if (SHOW_LINE_NUMBERS) {
                updateLinePadding();
                int lineX = (int) (mDrawingRect.left + mLeftPadding - (FIXED_TEXT_SIZE * 0.5));
                canvas.drawLine(lineX, mDrawingRect.top, lineX, mDrawingRect.bottom, mPaintNumbers);
            }
            int i = mFirst;
            while (i <= mLast) {
                int baseline = getLineBounds(i, mLineBounds);
                if(SHOW_LINE_NUMBERS) {
                    canvas.drawText("" + (i + 1), mDrawingRect.left, baseline, mPaintNumbers);
                }
                int lineWidth = (int) layout.getLineWidth(i);
                if (lineWidth > usableWidth) {
                    mScrollMax.x = Math.max(mScrollMax.x, (lineWidth - usableWidth) + mPadding);
                }
                i++;
            }
            if (!(mSizeRedrawInvoked || mOnSizeRedrawListener == null)) {
                mSizeRedrawInvoked = true;
                mOnSizeRedrawListener.onSizeRedraw();
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public void setSelection(int start, int stop) {
        super.setSelection(start, stop);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(0);
        }
    }

    @Override
    public void setSelection(int index) {
        super.setSelection(index);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(0);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller == null) {
            super.computeScroll();
        } else if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(event.getAction() == 0 && event.isCtrlPressed()
                && !event.isShiftPressed()
                && !event.isAltPressed()
                && !event.isFunctionPressed()
                && keyCode == KeyEvent.KEYCODE_X) {
            cut(); //Ctrl+X
            return true;
        } else if(event.getAction() == 0 && event.isCtrlPressed()
                && !event.isShiftPressed()
                && !event.isAltPressed()
                && !event.isFunctionPressed()
                && keyCode == KeyEvent.KEYCODE_C) {
            copy(); //Ctrl-C
            return true;
        } else if(event.getAction() == 0 && event.isCtrlPressed()
                && !event.isShiftPressed()
                && !event.isAltPressed()
                && !event.isFunctionPressed()
                && keyCode == KeyEvent.KEYCODE_V) {
            paste(); //Ctrl-V
            return true;
        } else if(event.getAction() == 0 && event.isCtrlPressed()
                && !event.isShiftPressed()
                && !event.isAltPressed()
                && !event.isFunctionPressed()
                && keyCode == KeyEvent.KEYCODE_Z) {
            undo(); //Ctrl-Z
            return true;
        } else if(event.getAction() == 0 && event.isCtrlPressed()
                && !event.isShiftPressed()
                && !event.isAltPressed()
                && !event.isFunctionPressed()
                && keyCode == KeyEvent.KEYCODE_Y) {
            redo(); //Ctrl-Y
            return true;
        } else if(event.getAction() == 0 && event.isCtrlPressed()
                && !event.isShiftPressed()
                && !event.isAltPressed()
                && !event.isFunctionPressed()
                && keyCode == KeyEvent.KEYCODE_A) {
            selectAll(); //Ctrl-A
            return true;
        } else if(event.getAction() == 0 && event.isCtrlPressed()
                && event.isShiftPressed()
                && !event.isAltPressed()
                && !event.isFunctionPressed()
                && keyCode == KeyEvent.KEYCODE_D) {
            deleteLine(); //Ctrl+Shift-D
            return true;
        } else if(event.getAction() == 0 && event.isCtrlPressed()
                && !event.isShiftPressed()
                && event.isAltPressed()
                && !event.isFunctionPressed()
                && keyCode == KeyEvent.KEYCODE_A) {
            selectLine(); //Ctrl+Alt-A
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return mGestureDetector == null || mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (!mScroller.isFinished()) {
            cancelScrollCompletion();
            mScroller.forceFinished(true);
            setLongClickable(false);
            mScrollTouchStop = true;
        }
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (!mScrollTouchStop) {
            return false;
        }
        if (!isSoftKeyboardVisible()) {
            ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getWindowToken(), 0);
        }
        setLongClickable(true);
        mScrollTouchStop = false;
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {}

    @Override
    public void onLongPress(MotionEvent e) {}

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (Math.abs(distanceY) >= Math.abs(distanceX) && ((distanceY >= 0.0f || mFirst != 0)
                && (distanceY <= 0.0f || mLast != mLineCount - 1))) {
            cancelScrollCompletion();
            mHandler.postDelayed(scrollCompletionRunnable, SCROLL_RUNNABLE_DELAY);
            mScrollDirection = (int) Math.signum(distanceY);
            if (mOnScrollChangedListener != null) {
                mOnScrollChangedListener.onScrollChanged(mScrollDirection);
            }
        }
        if(SYNTAX_HIGHLIGHT) {
            cancelUpdate();
            updateHandler.postDelayed(updateRunnable, SYNTAX_UPDATE_DELAY);
        }
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (mScroller != null) {
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                mScroller.fling(getScrollX(), getScrollY(),
                        -((int) velocityX), 0, 0, mScrollMax.x, 0, mScrollMax.y);
            } else {
                mScroller.fling(getScrollX(), getScrollY(), 0,
                        -((int) velocityY), 0, mScrollMax.x, 0, mScrollMax.y);
            }
        }
        if(SYNTAX_HIGHLIGHT) {
            cancelUpdate();
            updateHandler.postDelayed(updateRunnable, SYNTAX_UPDATE_DELAY);
        }
        return true;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        try {
            super.onSelectionChanged(selStart, selEnd);
            if (selStart == selEnd && BRACKET_MATCHING) {
                checkMatchingBracket(selStart);
            } else {
                clearSpans(getEditableText()); //use this method instead of s.clearSpans();
            }
        } catch (Exception ignored) {
            //ignored
        }
    }

    //endregion BASIC

    //region BUILT-IN

    protected void setDefaultKeyword() {
        ArrayList<CompletionItem> data = new ArrayList<>();
        for (String s : CompletionKeywords.ALL_KEYWORDS) {
            data.add(new CompletionItem(SuggestionManager.TYPE_KEYWORD, s)); //keyword
        }
        setSuggestData(data);
    }


    protected void onDropdownChangeSize(int w, int h) {
        Rect rect = new Rect();
        getWindowVisibleDisplayFrame(rect);

        LModLogUtils.d("onDropdownChangeSize: " + rect);
        w = rect.width();
        h = rect.height();

        // 1/2 width of screen
        setDropDownWidth((int) (w * 0.5f));

        // 0.5 height of screen
        setDropDownHeight((int) (h * 0.5f));

        //change position
        onPopupChangePosition();
    }

    protected void setSuggestData(ArrayList<CompletionItem> data) {
        LModLogUtils.d("setSuggestData");
        BasicCompletionAdapter mAdapter = new BasicCompletionAdapter(getContext(),
                R.layout.list_item_suggest, data);
        setAdapter(mAdapter);
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

                int offsetHorizontal = (int) x + mLeftPadding;
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
        } catch (Exception ignored) {
            //nothing
        }
    }

    @Override
    public void showDropDown() {
        if (!isPopupShowing()) {
            if (hasFocus()) {
                super.showDropDown();
            }
        }
    }

    protected int getHeightVisible() {
        Rect r = new Rect();
        getWindowVisibleDisplayFrame(r);
        return r.bottom - r.top;
    }

    protected void invalidateCharHeight() {
        mCharHeight = (int) Math.ceil(getPaint().getFontSpacing());
        mCharHeight = (int) getPaint().measureText("M");
    }

    protected int getLine() {
        int offset = getSelectionStart();
        if (offset == -1 || getLayout() == null) {
            return -1;
        }
        return getLayout().getLineForOffset(offset);
    }

    protected void setLine(int toLine) {
        int line = toLine;
        if (line < 0) {
            line = 0;
        }
        if (line > getLineCount() - 1) {
            line = getLineCount() - 1;
        }
        setSelection(getLayout().getLineStart(line));
    }

    @SuppressWarnings("deprecation")
    protected boolean isSoftKeyboardVisible() {
        int height = mWindowManager.getDefaultDisplay().getHeight();
        Rect outRect = new Rect();
        getWindowVisibleDisplayFrame(outRect);
        return outRect.bottom != height;
    }

    private class EditTextChangeListener implements TextWatcher {
        private int count = 0;
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            this.count = count;
            if (getSelectionStart() == getSelectionEnd() && BRACKET_MATCHING) {
                checkMatchingBracket(getSelectionStart());
            }
            if(AUTOCOMPLETE) {
                onPopupChangePosition();
            }
        }
        public void afterTextChanged(Editable s) {
            if(SYNTAX_HIGHLIGHT) {
                cancelUpdate();
                updateHandler.postDelayed(updateRunnable, SYNTAX_UPDATE_DELAY);
            } else {
                clearSpans(s); //use this method instead of s.clearSpans();
            }
            if(BRACKETS_AUTO_CLOSING) {
                autoClose(s, count);
            }
        }
    }

    protected void cancelScrollCompletion() {
        mHandler.removeCallbacks(scrollCompletionRunnable);
    }

    protected int getFirstLine() {
        return mFirst;
    }

    protected int getLastLine() {
        return mLast;
    }

    protected void cancelUpdate() {
        updateHandler.removeCallbacks(updateRunnable);
    }

    protected void highlight() {
        if (!sIsRunning && getLayout() != null) {
            final Editable e = getEditableText();
            int firstLine = getFirstLine();
            int lastLine = getLastLine();
            int delta = (lastLine - firstLine) / 2;
            firstLine -= delta;
            lastLine += delta;
            if (firstLine < 0) {
                firstLine = 0;
            }
            if (lastLine > getLineCount() - 1) {
                lastLine = getLineCount() - 1;
            }
            final int start = getLayout().getLineStart(firstLine);
            final int end = getLayout().getLineEnd(lastLine);
            try {
                if (e.length() != 0) {
                    sIsRunning = true;

                    //region HIGHLIGHT

                    for (ForegroundColorSpan span : e.getSpans(0, e.length(),
                            ForegroundColorSpan.class)) {
                        e.removeSpan(span);
                    }
                    Matcher m;
                    if(HIGHLIGHT_NUMBERS) {
                        m = PATTERN_NUMBERS.matcher(e.subSequence(start, end));
                        while (m.find()) {
                            e.setSpan(new ForegroundColorSpan(COLOR_NUMBERS),
                                    m.start() + start, m.end() + start,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                    if(HIGHLIGHT_SYMBOLS) {
                        m = PATTERN_SYMBOLS.matcher(e.subSequence(start, end));
                        while (m.find()) {
                            e.setSpan(new ForegroundColorSpan(COLOR_SYMBOLS),
                                    m.start() + start, m.end() + start,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                    if(HIGHLIGHT_KEYWORDS) {
                        m = PATTERN_KEYWORDS.matcher(e.subSequence(start, end));
                        while (m.find()) {
                            e.setSpan(new ForegroundColorSpan(COLOR_KEYWORDS),
                                    m.start() + start, m.end() + start,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                    if(HIGHLIGHT_KEYWORDS2) {
                        m = PATTERN_KEYWORDS2.matcher(e.subSequence(start, end));
                        while (m.find()) {
                            e.setSpan(new ForegroundColorSpan(COLOR_KEYWORDS2),
                                    m.start() + start, m.end() + start,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                    if(HIGHLIGHT_STRINGS) {
                        m = PATTERN_STRINGS.matcher(e.subSequence(start, end));
                        while (m.find()) {
                            for (ForegroundColorSpan span2 : e.getSpans(
                                    m.start() + start, m.end() + start, ForegroundColorSpan.class)) {
                                e.removeSpan(span2);
                            }
                            e.setSpan(new ForegroundColorSpan(COLOR_STRINGS),
                                    m.start() + start, m.end() + start,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                    if(HIGHLIGHT_CLASSES) {
                        m = PATTERN_CLASSES.matcher(e.subSequence(start, end));
                        while (m.find()) {
                            e.setSpan(new ForegroundColorSpan(COLOR_CLASSES),
                                    m.start() + start, m.end() + start,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                    if(HIGHLIGHT_COMMENTS) {
                        m = PATTERN_COMMENTS.matcher(e.subSequence(start, end));
                        while (m.find()) {
                            boolean skip = false;
                            for (ForegroundColorSpan span22 : e.getSpans(start, m.end() + start,
                                    ForegroundColorSpan.class)) {
                                int spanStart = e.getSpanStart(span22);
                                int spanEnd = e.getSpanEnd(span22);
                                if (span22.getForegroundColor() == COLOR_COMMENTS &&
                                        ((m.start() + start >= spanStart && m.start() + start
                                                <= spanEnd && m.end() + start > spanEnd)
                                                || (m.start() + start
                                                >= start + spanEnd && m.start() + start <= spanEnd))) {
                                    skip = true;
                                    break;
                                }
                            }
                            if (!skip) {
                                for (ForegroundColorSpan span222 : e.getSpans(
                                        m.start() + start, m.end() + start,
                                        ForegroundColorSpan.class)) {
                                    e.removeSpan(span222);
                                }
                                e.setSpan(new ForegroundColorSpan(COLOR_COMMENTS),
                                        m.start() + start, m.end() + start,
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    }

                    //endregion HIGHLIGHT

                    sIsRunning = false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void checkMatchingBracket(int pos) {
        getText().removeSpan(openBracketSpan);
        getText().removeSpan(closeBracketSpan);
        if (pos > 0 && pos <= getText().length()) {
            char c1 = getText().charAt(pos - 1);
            for (int i = 0; i < BRACKETS.length; i++) {
                if (BRACKETS[i] == c1) {
                    char c2 = BRACKETS[(i + 3) % 6];
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

    protected void showBracket(int i, int j) {
        getText().setSpan(openBracketSpan, i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        getText().setSpan(closeBracketSpan, j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    protected void updateLinePadding() {
        if (getLineCount() != mLineCount) {
            mLineCount = getLineCount();
            int leftPadding = (int) (mPaintNumbers.measureText("0") *
                    (Math.floor(Math.log10(getLineCount())) + 2.0d));
            if (mLeftPadding != leftPadding) {
                mLeftPadding = leftPadding;
                setPadding(mLeftPadding, mPadding, mPadding, mPadding);
            }
        }
    }

    protected boolean pinchZoom(MotionEvent ev) {
        switch(ev.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setOnLongClickListener(null);
                //setScrollingEnabled(true);
                zoomPinch = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if(ev.getPointerCount() == 2) {
                    float d = getDistanceBetweenTouches(ev);
                    if(!zoomPinch) {
                        setOnLongClickListener(
                                new View.OnLongClickListener(){
                                    @Override
                                    public boolean onLongClick(View v) {
                                        return true;
                                    }
                                });
                        //scrollView.setScrollingEnabled(false);
                        zoomPinchFactor = textSize/d;
                        zoomPinch = true;
                        break;
                    }
                    textSize = zoomPinchFactor*d;
                    validateTextSize();
                    setFixedTextSize(textSize);
                }
                break;
        }
        return zoomPinch;
    }

    protected float getDistanceBetweenTouches(MotionEvent ev) {
        float xx = ev.getX(1)-ev.getX(0);
        float yy = ev.getY(1)-ev.getY(0);

        return (float) Math.sqrt(xx*xx+yy*yy);
    }

    protected void validateTextSize() {
        if(textSize < 10) //minimum
            textSize = 10; //minimum
        else if(textSize > 20) //maximum
            textSize = 20; //maximum
    }

    @SuppressWarnings("StatementWithEmptyBody")
    protected void autoClose(Editable e, int count) {
        SpannableStringBuilder selectedStr = new SpannableStringBuilder(getText());
        Integer startSelection = getSelectionStart();
        Integer endSelection = getSelectionEnd();
        if (startSelection.equals(endSelection) && selectedStr.length() == startSelection) {}
        if (count > 0 && selectedStr.length() > 0 && startSelection > 0 && startSelection.equals(endSelection)) {
            char c = selectedStr.charAt(startSelection - 1);
            char nextC = 'x';
            char prevC = 'x';
            if (selectedStr.length() > startSelection) {
                nextC = selectedStr.charAt(startSelection);
            }
            if (startSelection > 1) {
                prevC = selectedStr.charAt(startSelection - 2);
            }
            if (!(c != '(' || nextC == ')' || prevC == '(')) {
                e.insert(startSelection, ")");
                setSelection(startSelection);
            } else if (!(c != '{' || nextC == '}' || prevC == '{')) {
                e.insert(startSelection, "}");
                setSelection(startSelection);
            } else if (!(c != '[' || nextC == ']' || prevC == '[')) {
                e.insert(startSelection, "]");
                setSelection(startSelection);
            }
        }
    }

    protected static void clearSpans(Editable e) {
        { //remove foreground color spans
            ForegroundColorSpan spans[] = e.getSpans(0, e.length(), ForegroundColorSpan.class);
            for (int i = spans.length; i-- > 0; ) {
                e.removeSpan(spans[i]);
            }
        }
        { //remove background color spans
            BackgroundColorSpan spans[] = e.getSpans(0, e.length(), BackgroundColorSpan.class);
            for (int i = spans.length; i-- > 0; ) {
                e.removeSpan(spans[i]);
            }
        }
    }

    protected Editable getSelectedText() {
        if (getSelectionEnd() > getSelectionStart()) {
            return (Editable) getText().subSequence(getSelectionStart(), getSelectionEnd());
        }
        return (Editable) getText().subSequence(getSelectionEnd(), getSelectionStart());
    }

    protected CharSequence indentLine(CharSequence source, int start, int end, Spanned dest,
                                      int dstart, int dend) {
        String indent = "";
        int indexStart = dstart - 1;
        int indexEnd;
        boolean dataBefore = false;
        int parenthesesCount = 0;

        for (; indexStart > -1; --indexStart) {
            char c = dest.charAt(indexStart);
            if (c == '\n')
                break;
            if (c != ' ' && c != '\t') {
                if (!dataBefore) {
                    if (c == '{' ||
                            c == '+' ||
                            c == '-' ||
                            c == '*' ||
                            c == '/' ||
                            c == '%' ||
                            c == '^' ||
                            c == '=' ||
                            c == '[')
                        --parenthesesCount;
                    dataBefore = true;
                }
                if (c == '(')
                    --parenthesesCount;
                else if (c == ')')
                    ++parenthesesCount;
            }
        }
        if (indexStart > -1) {
            char charAtCursor = dest.charAt(dstart);
            for (indexEnd = ++indexStart; indexEnd < dend; ++indexEnd) {
                char c = dest.charAt(indexEnd);
                if (charAtCursor != '\n'
                        && c == '/' && indexEnd + 1 < dend && dest.charAt(indexEnd) == c) {
                    indexEnd += 2;
                    break;
                }
                if (c != ' ' && c != '\t')
                    break;
            }
            indent += dest.subSequence(indexStart, indexEnd);
        }
        if (parenthesesCount < 0)
            indent += GLOBAL_TAB; //Tab

        start = dstart - 1; //because charAt(dstart) always is '\n'
        while (start > 0 && dest.charAt(start) != '\n') {
            start--;
        }
        if (start < 0) return source + indent;
        return source + indent;
    }

    //endregion BUILT-IN

    //region METHODS

    @Override
    public void setCurrentText(String text) {
        super.setText(text);
    }

    @Override
    public String getString() {
        return getText().toString();
    }

    @Override
    public void setCurrentTypeface(Typeface typeface) {
        super.setTypeface(typeface);
    }

    @Override
    public void setCodeColor(@ColorInt int color) {
        super.setTextColor(color);
    }

    @Override
    public void setCursorColor(EditText view, @ColorInt int color) {
        try {
            // Get the cursor resource id
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(view);

            // Get the editor
            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(view);

            // Get the drawable and set a color filter
            Drawable drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {drawable, drawable};

            // Set the drawables
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception ignored) {
            //nothing
        }
    }

    @Override
    public LModEditor getEditor() {
        return this;
    }

    @Override
    public void selectAll() {
        super.selectAll();
    }

    @Override
    public void setLineNumbersColor(@ColorInt int color) {
        LINE_NUMBERS_COLOR = color;
        mPaintNumbers.setColor(LINE_NUMBERS_COLOR);
    }

    @Override
    public void setCurrentLineHighlightColor(@ColorInt int color) {
        CURRENT_LINE_COLOR = color;
        mPaintHighlight.setColor(CURRENT_LINE_COLOR);
    }

    @Override
    public void setFixedTextSize(float textSize) { //use this method instead of setTextSize(float);
        FIXED_TEXT_SIZE = textSize;
        super.setTextSize(FIXED_TEXT_SIZE);
        mPaintNumbers.setTextSize(FIXED_TEXT_SIZE * 1.8f);
    }

    @Override
    public void setLineNumbersEnabled(boolean enabled) {
        SHOW_LINE_NUMBERS = enabled;
        invalidate();
    }

    @Override
    public void setHighlightCurrentLine(boolean enabled) {
        HIGHLIGHT_CURRENT_LINE = enabled;
        invalidate();
    }

    @Override
    public void setSelectionHighlightColor(@ColorInt int color) {
        SELECTION_COLOR = color;
        super.setHighlightColor(SELECTION_COLOR);
    }

    @Override
    public void addSyntaxPattern(Pattern pattern, int syntaxType, @ColorInt int color) {
        switch(syntaxType) {
            case 0: //Keywords
                PATTERN_KEYWORDS = pattern;
                COLOR_KEYWORDS = color;
                break;
            case 1: //Keywords_2
                PATTERN_KEYWORDS2 = pattern;
                COLOR_KEYWORDS2 = color;
                break;
            case 2: //Comments
                PATTERN_COMMENTS = pattern;
                COLOR_COMMENTS = color;
                break;
            case 3: //Strings
                PATTERN_STRINGS = pattern;
                COLOR_STRINGS = color;
                break;
            case 4: //Symbols
                PATTERN_SYMBOLS = pattern;
                COLOR_SYMBOLS = color;
                break;
            case 5: //Numbers
                PATTERN_NUMBERS = pattern;
                COLOR_NUMBERS = color;
                break;
            case 6: //Classes
                PATTERN_CLASSES = pattern;
                COLOR_CLASSES = color;
                break;
        }
    }

    @Override
    public void setSyntaxHighlightEnabled(boolean enabled) {
        SYNTAX_HIGHLIGHT = enabled;
    }

    @Override
    public void setSyntaxUpdateDelay(int delay) {
        SYNTAX_UPDATE_DELAY = delay;
    }

    @Override
    public void setBracketMatchingEnabled(boolean enabled) {
        BRACKET_MATCHING = enabled;
    }

    @Override
    public void setMatchedBracketsColor(@ColorInt int color) {
        MATCHED_BRACKET_COLOR = color;
        closeBracketSpan = new BackgroundColorSpan(MATCHED_BRACKET_COLOR);
        openBracketSpan = new BackgroundColorSpan(MATCHED_BRACKET_COLOR);
    }

    @Override
    public void setReadOnly(boolean value) {
        KeyListener keyListener = getKeyListener();
        if (value) {
            setKeyListener(null);
        } else {
            if (keyListener != null)
                setKeyListener(keyListener);
        }
    }

    @Override
    public void setBracketsAutoClosing(boolean autoClosing) {
        BRACKETS_AUTO_CLOSING = autoClosing;
    }

    @Override
    public void findText(String searchText, boolean ignoreCase) {
        String needle = searchText;
        if (needle.length() > 0) {
            int startSelection = getSelectionEnd();
            String haystack = getText().toString();
            if (ignoreCase) {
                needle = needle.toLowerCase();
                haystack = haystack.toLowerCase();
            }
            int foundPosition = haystack.substring(startSelection).indexOf(needle);
            if (foundPosition == -1) {
                foundPosition = haystack.indexOf(needle);
                startSelection = 0;
            }
            if (foundPosition != -1) {
                int newSelection = foundPosition + startSelection;
                setSelection(newSelection, needle.length() + newSelection);
            }
        }
    }

    @Override
    public void findPreviousText(String searchText, boolean ignoreCase) {
        String needle = searchText;
        if (needle.length() > 0) {
            int endSelection = getSelectionStart();
            String haystack = getText().toString();
            if (ignoreCase) {
                needle = needle.toLowerCase();
                haystack = haystack.toLowerCase();
            }
            int foundPosition = haystack.substring(0, endSelection).lastIndexOf(needle);
            if (foundPosition == -1) {
                foundPosition = haystack.lastIndexOf(needle);
            }
            if (foundPosition != -1) {
                setSelection(foundPosition, needle.length() + foundPosition);
            }
        }
    }

    @Override
    public void deleteLine() {
        int at = getSelectionEnd();
        if (at != -1) {
            int line = getLayout().getLineForOffset(at);
            int startAt = getLayout().getLineStart(line);
            int endAt = getLayout().getLineEnd(line);
            int len = getText().length();
            if (startAt > 1 && endAt > 1 && len == endAt) {
                startAt--;
            }
            getEditableText().delete(startAt, endAt);
        }
    }

    @Override
    public void selectLine() {
        int start = getSelectionEnd();
        if (start != -1) {
            int line = getLayout().getLineForOffset(start);
            setSelection(getLayout().getLineStart(line), getLayout().getLineEnd(line));
        }
    }

    @Override
    public void goToLine(int toLine) {
        int line = toLine - 1;
        if (line < 0) {
            line = 0;
        }
        if (line > getLineCount() - 1) {
            line = getLineCount() - 1;
        }
        setSelection(getLayout().getLineStart(line));
    }

    @Override
    public void toBegin() {
        setLine(0);
    }

    @Override
    public void toEnd() {
        setLine(getLineCount() - 1);
    }

    @Override
    public void replaceAll(String first, String last) {
        setText(getText().toString().replaceAll(first, last));
    }

    @Override
    public void highlightKeywords(boolean highlight) {
        HIGHLIGHT_KEYWORDS = highlight;
    }

    @Override
    public void highlightKeywords2(boolean highlight) {
        HIGHLIGHT_KEYWORDS2 = highlight;
    }

    @Override
    public void highlightComments(boolean highlight) {
        HIGHLIGHT_COMMENTS = highlight;
    }

    @Override
    public void highlightStrings(boolean highlight) {
        HIGHLIGHT_STRINGS = highlight;
    }

    @Override
    public void highlightSymbols(boolean highlight) {
        HIGHLIGHT_SYMBOLS = highlight;
    }

    @Override
    public void highlightNumbers(boolean highlight) {
        HIGHLIGHT_NUMBERS = highlight;
    }

    @Override
    public void highlightClasses(boolean highlight) {
        HIGHLIGHT_CLASSES = highlight;
    }

    @Override
    public void undo() {
        if (canUndo()) {
            mUndoRedoHelper.undo();
        }
    }

    @Override
    public void redo() {
        if (canRedo()) {
            mUndoRedoHelper.redo();
        }
    }

    @Override
    public boolean canUndo() {
        return mUndoRedoHelper.getCanUndo();
    }

    @Override
    public boolean canRedo() {
        return mUndoRedoHelper.getCanRedo();
    }

    @Override
    public void clearHistory() {
        mUndoRedoHelper.clearHistory();
    }

    @Override
    public void setMaxHistorySize(int maxSize) {
        MAX_HISTORY_SIZE = maxSize;
        mUndoRedoHelper.setMaxHistorySize(MAX_HISTORY_SIZE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void cut() {
        Editable copy = getSelectedText();
        ((ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE)).setText(copy);
        if (getSelectionEnd() > getSelectionStart()) {
            getText().replace(getSelectionStart(), getSelectionEnd(), "");
        } else {
            getText().replace(getSelectionEnd(), getSelectionStart(), "");
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void copy() {
        Editable copy = getSelectedText();
        //noinspection StatementWithEmptyBody
        if (copy == null || copy.toString().equals("")) {
            //nothing to copy
        } else {
            ((ClipboardManager)
                    getContext().getSystemService(Context.CLIPBOARD_SERVICE)).setText(copy);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void paste() {
        ClipboardManager clipboard = (
                ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        //noinspection StatementWithEmptyBody
        if (clipboard.getText() == null || clipboard.getText() == "") {
            //nothing to paste
        }
        if (!clipboard.hasText()) {
            return;
        }
        if (getSelectionEnd() > getSelectionStart()) {
            getText().replace(getSelectionStart(), getSelectionEnd(), clipboard.getText());
        } else {
            getText().replace(getSelectionEnd(), getSelectionStart(), clipboard.getText());
        }
    }

    @Override
    public void setAutoCompleteEnabled(boolean enabled) {
        AUTOCOMPLETE = enabled;
        if(!AUTOCOMPLETE) {
            setTokenizer(null); //Fix
        } else {
            setDefaultKeyword();
            mTokenizer = new SymbolsTokenizer();
            setTokenizer(mTokenizer);
            setThreshold(2);
        }
    }

    @Override
    public void setAutoIndentationEnabled(boolean enabled) {
        AUTOINDENTLINE = enabled;
        if(AUTOINDENTLINE) {
            setFilters(new InputFilter[]{
                    new InputFilter() {
                        @Override
                        public CharSequence filter(CharSequence source, int start,
                                                   int end, Spanned dest, int dstart, int dend) {
                            if (end - start == 1 && start < source.length()
                                    && dstart < dest.length()) {
                                char c = source.charAt(start);
                                if (c == '\n') {
                                    return indentLine(source, start, end, dest, dstart, dend);
                                }
                            }
                            return source;
                        }
                    }
            });
        } else {
            setFilters(new InputFilter[]{
                    new InputFilter() {
                        @Override
                        public CharSequence filter(CharSequence source, int start,
                                                   int end, Spanned dest, int dstart, int dend) {
                            return source;
                        }
                    }
            });
        }
    }

    @Override
    public void setPinchZoomEnabled(boolean enabled) {
        PINCH_ZOOM = enabled;
        if(PINCH_ZOOM) {
            scaledDensity = getResources().getDisplayMetrics().scaledDensity;
            textSize = getTextSize()/scaledDensity;
            setOnTouchListener(
                    new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent ev) {
                            return pinchZoom(ev);
                        }
                    });
        } else {
            setOnTouchListener(
                    new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent ev) {
                            return false;
                        }
                    });
        }
    }

    //endregion METHODS
}