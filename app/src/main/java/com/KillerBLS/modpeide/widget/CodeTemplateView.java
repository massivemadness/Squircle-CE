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

package com.KillerBLS.modpeide.widget;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.interfaces.OnScrollChangedListener;
import com.KillerBLS.modpeide.manager.TypefaceManager;
import com.KillerBLS.modpeide.processor.language.Language;
import com.KillerBLS.modpeide.processor.language.ModPELanguage;
import com.KillerBLS.modpeide.processor.style.StylePaint;
import com.KillerBLS.modpeide.processor.style.StyleSpan;
import com.KillerBLS.modpeide.processor.style.SyntaxHighlightSpan;
import com.KillerBLS.modpeide.utils.Converter;
import com.KillerBLS.modpeide.utils.Wrapper;

import java.util.regex.Matcher;

public class CodeTemplateView extends AppCompatTextView {

    private Context mContext;
    private Wrapper mWrapper;

    private Scroller mScroller;
    private OnScrollChangedListener[] mScrollChangedListeners;
    private VelocityTracker mVelocityTracker;

    float mPreviousTouchX = 0.0f;
    float mPreviousTouchY = 0.0f;
    int mMaximumVelocity;
    int mMinimumVelocity;

    private StyleSpan mSyntaxNumbers;
    private StyleSpan mSyntaxSymbols;
    private StyleSpan mSyntaxBrackets;
    private StyleSpan mSyntaxKeywords;
    private StyleSpan mSyntaxMethods;
    private StyleSpan mSyntaxStrings;
    private StyleSpan mSyntaxComments;

    //region CONSTRUCTOR

    public CodeTemplateView(Context context) {
        super(context);
        init(context);
    }

    public CodeTemplateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CodeTemplateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    //endregion CONSTRUCTOR

    //region INIT

    protected void init(Context context) {
        mContext = context;
        if(!isInEditMode()) {
            initParameters();
            initTheme();
            initMethods();
            postInit();
        }
    }

    protected void initParameters() {
        mWrapper = new Wrapper(mContext);
        mScroller = new Scroller(mContext);
        mScrollChangedListeners = new OnScrollChangedListener[0];
    }

    /**
     * Загрузка темы. Все основные элементы, которые зависят от темы.
     */
    protected void initTheme() {
        TypedValue colorAttr = new TypedValue();
        mContext.getTheme()
                .resolveAttribute(R.attr.colorSelection, colorAttr, true);
        setHighlightColor(colorAttr.data); //Selection Color

        colorAttr = new TypedValue();
        mContext.getTheme()
                .resolveAttribute(R.attr.syntaxNumbers, colorAttr, true);
        mSyntaxNumbers = new StyleSpan(colorAttr.data, false, false);

        colorAttr = new TypedValue();
        mContext.getTheme()
                .resolveAttribute(R.attr.syntaxSymbols, colorAttr, true);
        mSyntaxSymbols = new StyleSpan(colorAttr.data, false, false);

        colorAttr = new TypedValue();
        mContext.getTheme()
                .resolveAttribute(R.attr.syntaxBrackets, colorAttr, true);
        mSyntaxBrackets = new StyleSpan(colorAttr.data, false, false);

        colorAttr = new TypedValue();
        mContext.getTheme()
                .resolveAttribute(R.attr.syntaxKeywords, colorAttr, true);
        mSyntaxKeywords = new StyleSpan(colorAttr.data, false, false);

        colorAttr = new TypedValue();
        mContext.getTheme()
                .resolveAttribute(R.attr.syntaxMethods, colorAttr, true);
        mSyntaxMethods = new StyleSpan(colorAttr.data, false, false);

        colorAttr = new TypedValue();
        mContext.getTheme()
                .resolveAttribute(R.attr.syntaxStrings, colorAttr, true);
        mSyntaxStrings = new StyleSpan(colorAttr.data, false, false);

        colorAttr = new TypedValue();
        mContext.getTheme()
                .resolveAttribute(R.attr.syntaxComments, colorAttr, true);
        mSyntaxComments = new StyleSpan(colorAttr.data, false, true);
    }

    protected void initMethods() {
        ViewConfiguration configuration = ViewConfiguration.get(mContext);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        setTextIsSelectable(true);
        int mPadding = Converter.dpAsPixels(this, 4);
        setPadding(mPadding, mPadding, mPadding, mPadding);
        refreshTypeface();
    }

    protected void postInit() {
        postInvalidate();
        refreshDrawableState();
    }

    //endregion INIT

    //region BASE_METHODS

    public void setTemplate(String text) {
        super.setText(syntaxHighlight(new SpannableStringBuilder(text)));
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        //Scroller
        for (OnScrollChangedListener l : mScrollChangedListeners) {
            int x = getScrollX();
            int y = getScrollY();
            l.onScrollChanged(x, y, x, y);
        }
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
                mPreviousTouchX = event.getX();
                mPreviousTouchY = event.getY();
                super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                int velocityX;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (mWrapper.getWrapContent()) {
                    velocityX = 0;
                } else {
                    velocityX = (int) mVelocityTracker.getXVelocity();
                }
                mPreviousTouchX = 0.0f;
                mPreviousTouchY = 0.0f;
                if (Math.abs(velocityY) > mMinimumVelocity
                        || Math.abs(velocityX) > mMinimumVelocity) {
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
                mPreviousTouchX = event.getX();
                mPreviousTouchY = event.getY();
                super.onTouchEvent(event);
                break;
            default:
                super.onTouchEvent(event);
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (!isInEditMode() && mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
        }
    }

    @Override
    public void onScrollChanged(int horiz, int vert, int oldHoriz, int oldVert) {
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert);
        if (mScrollChangedListeners != null) {
            for (OnScrollChangedListener l : mScrollChangedListeners) {
                l.onScrollChanged(horiz, vert, oldHoriz, oldVert);
            }
        }
    }

    //endregion BASE_METHODS

    //region METHODS

    @WorkerThread
    private Language getLanguage() {
        return new ModPELanguage(); //тут у нас может быть только один язык для шаблонов  - ModPE
    }

    @WorkerThread
    protected Editable syntaxHighlight(Editable editable) {
        if (editable.length() == 0) {
            return editable;
        }

        //region PROCESS_HIGHLIGHT

        Matcher m = getLanguage().getSyntaxNumbers().matcher(editable); //Numbers
        while (m.find()) {
            editable.setSpan(new SyntaxHighlightSpan(mSyntaxNumbers, m.start(), m.end()),
                    m.start(), m.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        m = getLanguage().getSyntaxSymbols().matcher(editable); //Symbols
        while (m.find()) {
            editable.setSpan(
                    new SyntaxHighlightSpan(
                            mSyntaxSymbols,
                            m.start(), m.end()),
                    m.start(), m.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        m = getLanguage().getSyntaxBrackets().matcher(editable); //Brackets
        while (m.find()) {
            editable.setSpan(
                    new SyntaxHighlightSpan(
                            mSyntaxBrackets,
                            m.start(), m.end()),
                    m.start(), m.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        m = getLanguage().getSyntaxKeywords().matcher(editable); //Keywords
        while (m.find()) {
            editable.setSpan(
                    new SyntaxHighlightSpan(
                            mSyntaxKeywords,
                            m.start(), m.end()),
                    m.start(), m.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        m = getLanguage().getSyntaxMethods().matcher(editable); //Methods
        while (m.find()) {
            editable.setSpan(
                    new SyntaxHighlightSpan(
                            mSyntaxMethods,
                            m.start(), m.end()),
                    m.start(), m.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        m = getLanguage().getSyntaxStrings().matcher(editable); //Strings
        while (m.find()) {
            for (ForegroundColorSpan span : editable.getSpans(
                    m.start(),
                    m.end(), ForegroundColorSpan.class)) {
                editable.removeSpan(span);
            }
            editable.setSpan(
                    new SyntaxHighlightSpan(
                            mSyntaxStrings,
                            m.start(), m.end()),
                    m.start(), m.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        m = getLanguage().getSyntaxComments().matcher(editable); //Comments
        while (m.find()) {
            boolean skip = false;
            for (ForegroundColorSpan span : editable.getSpans(m.start(),
                    m.end(), ForegroundColorSpan.class)) {

                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);
                if (((m.start() >= spanStart && m.start()
                        <= spanEnd && m.end()> spanEnd)
                        || (m.start() >= + spanEnd
                        && m.start() <= spanEnd))) {
                    skip = true;
                    break;
                }

            }
            if (!skip) {
                for (ForegroundColorSpan span : editable.getSpans(
                        m.start(), m.end(),
                        ForegroundColorSpan.class)) {
                    editable.removeSpan(span);
                }
                editable.setSpan(
                        new SyntaxHighlightSpan(
                                mSyntaxComments,
                                m.start(), m.end()),
                        m.start(), m.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        //endregion PROCESS_HIGHLIGHT

        return editable;
    }

    /**
     * Отображение шрифта.
     */
    public void refreshTypeface() {
        if(mWrapper.getCurrentTypeface().equals("droid_sans_mono")) {
            setTypeface(TypefaceManager.get(mContext, TypefaceManager.DROID_SANS_MONO));
        } else if(mWrapper.getCurrentTypeface().equals("source_code_pro")) {
            setTypeface(TypefaceManager.get(mContext, TypefaceManager.SOURCE_CODE_PRO));
        } else if(mWrapper.getCurrentTypeface().equals("roboto")) {
            setTypeface(TypefaceManager.get(mContext, TypefaceManager.ROBOTO));
        } else { //if(mWrapper.getCurrentTypeface().equals("roboto_light"))
            setTypeface(TypefaceManager.get(mContext, TypefaceManager.ROBOTO_LIGHT));
        }
        setPaintFlags(getPaintFlags() | StylePaint.SUBPIXEL_TEXT_FLAG);
    }

    //endregion METHODS
}
