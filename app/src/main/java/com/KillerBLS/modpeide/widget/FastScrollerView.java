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
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.interfaces.OnScrollChangedListener;
import com.KillerBLS.modpeide.processor.TextProcessor;
import com.KillerBLS.modpeide.processor.style.StylePaint;

/**
 * @author Henry Thompson
 */
public class FastScrollerView extends View implements OnScrollChangedListener {

    public static final int STATE_EXITING = 3;
    public static final int STATE_DRAGGING = 2;
    public static final int STATE_HIDDEN = 0;
    public static final int STATE_VISIBLE = 1;

    private final Runnable hideScroller = () -> setState(STATE_EXITING);

    private Bitmap mBitmapDragging;
    private Bitmap mBitmapNormal;
    private TextProcessor mEditor;
    private float mScrollMax;
    private float mScrollY;
    private Handler mHandler = new Handler();
    private StylePaint mPaint;
    private int mState = STATE_HIDDEN;
    private Drawable mThumbDrawableDragging;
    private Drawable mThumbDrawableNormal;
    private int mThumbHeight;
    private float mThumbTop = 0.0f;
    private int mViewHeight;

    @Override
    public void onScrollChanged(int x, int y, int oldx, int oldy) {
        if (mState != STATE_DRAGGING) {
            getMeasurements();
            setState(STATE_VISIBLE);
            mHandler.postDelayed(hideScroller, 2000);
        }
    }

    public FastScrollerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            mThumbDrawableNormal = context.getResources().getDrawable(R.drawable.fastscroll_thumb_default);
            mThumbDrawableDragging = context.getResources().getDrawable(R.drawable.fastscroll_thumb_pressed);

            TypedValue colorAccent = new TypedValue();
            getContext().getTheme()
                    .resolveAttribute(R.attr.colorAccent, colorAccent, true);

            mThumbDrawableNormal.mutate().setColorFilter(colorAccent.data, PorterDuff.Mode.SRC_IN);
            mThumbDrawableDragging.mutate().setColorFilter(colorAccent.data, PorterDuff.Mode.SRC_IN);
            mThumbHeight = mThumbDrawableNormal.getIntrinsicHeight();

            mPaint = new StylePaint(true, false);
            mPaint.setAlpha(250);
        }
    }

    public void link(TextProcessor editor) {
        if (editor != null) {
            mEditor = editor;
            mEditor.addOnScrollChangedListener(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEditor == null || mState == 0) {
            return false;
        }
        getMeasurements();
        switch (event.getAction()) {
            case 0:
                if (!isPointInThumb(event.getX(), event.getY())) {
                    return false;
                }
                mEditor.abortFling();
                setState(STATE_DRAGGING);
                setPressed(true);
                return true;
            case 1:
                setState(STATE_VISIBLE);
                setPressed(false);
                mHandler.postDelayed(hideScroller, 2000);
                return false;
            case 2:
                if (mState != STATE_DRAGGING) {
                    return false;
                }
                setPressed(true);
                mEditor.abortFling();
                int newThumbTop = ((int) event.getY()) - (mThumbHeight / 2);
                if (newThumbTop < 0) {
                    newThumbTop = 0;
                } else if (mThumbHeight + newThumbTop > mViewHeight) {
                    newThumbTop = mViewHeight - mThumbHeight;
                }
                mThumbTop = newThumbTop;
                scrollCodeView();
                invalidate();
                return true;
            default:
                return false;
        }
    }

    private void scrollCodeView() {
        float scrollToAsFraction = mThumbTop / (mViewHeight - mThumbHeight);
        mEditor.scrollTo(mEditor.getScrollX(), ((int) (mScrollMax * scrollToAsFraction)) - ((int) (scrollToAsFraction * (mEditor.getHeight() - mEditor.getLineHeight()))));
    }

    private int getThumbTop() {
        int absoluteThumbTop = Math.round((mViewHeight - mThumbHeight) * (mScrollY / ((mScrollMax - mEditor.getHeight()) + mEditor.getLineHeight())));
        if (absoluteThumbTop > getHeight() - mThumbHeight) {
            return getHeight() - mThumbHeight;
        }
        return absoluteThumbTop;
    }

    private boolean isPointInThumb(float x, float y) {
        return x >= 0.0f && x <= getWidth() && y >= mThumbTop && y <= mThumbTop + mThumbHeight;
    }

    private void getMeasurements() {
        if (mEditor != null && mEditor.getLayout() != null) {
            mViewHeight = getHeight();
            mScrollMax = mEditor.getLayout().getHeight();
            mScrollY = mEditor.getScrollY();
            mEditor.getHeight();
            mEditor.getLayout().getHeight();
            mThumbTop = getThumbTop();
        }
    }

    private boolean isShowScrollerJustified() {
        return (mScrollMax / mEditor.getHeight()) >= 1.5d;
    }

    public void setState(int state) {
        switch (state) {
            case 0:
                mHandler.removeCallbacks(hideScroller);
                mState = STATE_HIDDEN;
                invalidate();
                return;
            case 1:
                if (isShowScrollerJustified()) {
                    mHandler.removeCallbacks(hideScroller);
                    mState = STATE_VISIBLE;
                    invalidate();
                    return;
                }
                return;
            case 2:
                mHandler.removeCallbacks(hideScroller);
                mState = STATE_DRAGGING;
                invalidate();
                return;
            case STATE_EXITING:
                mHandler.removeCallbacks(hideScroller);
                mState = STATE_EXITING;
                invalidate();
                return;
            default:
        }
    }

    public int getState() {
        return mState;
    }

    public void onDraw(Canvas canvas) {
        if (mEditor != null && getState() != STATE_HIDDEN) {
            if (mBitmapNormal == null) {
                mThumbDrawableNormal.setBounds(new Rect(0, 0, getWidth(), mThumbHeight));
                mBitmapNormal = Bitmap.createBitmap(getWidth(), mThumbHeight, Config.ARGB_8888);
                mThumbDrawableNormal.draw(new Canvas(mBitmapNormal));
            }
            if (mBitmapDragging == null) {
                mThumbDrawableDragging.setBounds(new Rect(0, 0, getWidth(), mThumbHeight));
                mBitmapDragging = Bitmap.createBitmap(getWidth(), mThumbHeight, Config.ARGB_8888);
                mThumbDrawableDragging.draw(new Canvas(mBitmapDragging));
            }
            super.onDraw(canvas);
            if (getState() == STATE_VISIBLE || getState() == STATE_DRAGGING) {
                mPaint.setAlpha(250);
                if (getState() == STATE_VISIBLE) {
                    canvas.drawBitmap(mBitmapNormal, 0.0f, mThumbTop, mPaint);
                } else {
                    canvas.drawBitmap(mBitmapDragging, 0.0f, mThumbTop, mPaint);
                }
            } else if (getState() != STATE_EXITING) {
                //nothing
            } else {
                if (mPaint.getAlpha() > 25) {
                    mPaint.setAlpha(mPaint.getAlpha() - 25);
                    canvas.drawBitmap(mBitmapNormal, 0.0f, mThumbTop, mPaint);
                    mHandler.postDelayed(hideScroller, 17);
                    return;
                }
                mPaint.setAlpha(0);
                setState(STATE_HIDDEN);
            }
        }
    }
}
