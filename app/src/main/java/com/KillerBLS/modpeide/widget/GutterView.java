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
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.KillerBLS.modpeide.document.Document;
import com.KillerBLS.modpeide.document.commons.LinesCollection;
import com.KillerBLS.modpeide.interfaces.OnScrollChangedListener;
import com.KillerBLS.modpeide.manager.DocumentsManager;
import com.KillerBLS.modpeide.processor.TextProcessor;
import com.KillerBLS.modpeide.processor.style.StylePaint;

/**
 * @author Henry Thompson
 */
public class GutterView extends View implements OnScrollChangedListener {

    private Document mDocument;
    private int bottomLayoutLine;
    private TextProcessor mEditor = null;
    private StylePaint mLinePaint;
    private StylePaint mTextPaint;
    private int topLayoutLine;

    public GutterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode())
            initLineNumbers();
    }

    public void initLineNumbers() {
        mTextPaint = new StylePaint(true, false);
        mTextPaint.setTextSize(getResources().getDisplayMetrics().density * 11.0f);
        mTextPaint.setColor(Color.rgb(113, 128, 120));

        mLinePaint = new StylePaint(false, false);
        mLinePaint.setColor(Color.rgb(113, 128, 120));
        mLinePaint.setStyle(StylePaint.Style.STROKE);
    }

    public void link(TextProcessor editor) {
        if (editor != null) {
            mEditor = editor;
            mEditor.addOnScrollChangedListener(this);
            mDocument = DocumentsManager.getDisplayedDocument();
            invalidate();
        }
    }

    private int getLineNumberAtPoint(int y) {
        int i = topLayoutLine;
        while (i <= bottomLayoutLine) {
            int lineBound = (mEditor.getLineBounds(i, null) - mEditor.getScrollY()) + 4;
            int lineHeight = mEditor.getLineHeight();
            if (y >= lineBound || y <= lineBound - lineHeight) {
                i++;
            } else {
                return mDocument.getLinesCollection().getLineForIndex(mEditor.getLayout().getLineStart(i));
            }
        }
        return -1;
    }

    public void getTopAndBottomLayoutLines() {
        if (mEditor != null) {
            topLayoutLine = Math.abs((mEditor.getScrollY() -
                    mEditor.getLayout().getTopPadding()) / mEditor.getLineHeight());
            bottomLayoutLine =
                    (mEditor.getScrollY() + mEditor.getHeight()) / mEditor.getLineHeight();
            if (topLayoutLine < 0) {
                topLayoutLine = 0;
            }
            if (bottomLayoutLine > mEditor.getLineCount() - 1) {
                bottomLayoutLine = mEditor.getLineCount() - 1;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(
                getWidth() - 2, 0.0f, getWidth() - 1, getHeight(), mLinePaint);
        if (mEditor == null) {
            super.onDraw(canvas);
            return;
        }
        getTopAndBottomLayoutLines();
        if (mDocument != null) {
            LinesCollection list = mDocument.getLinesCollection();
            if (list != null) {
                int i = topLayoutLine > 0 ? topLayoutLine - 1 : 0;
                while (i <= bottomLayoutLine) {
                    int realLineNumber = list.getLineForIndex(mEditor.getLayout().getLineStart(i));
                    int prevRealLineNumber;
                    int lineBottom;
                    if (i != 0) {
                        prevRealLineNumber =
                                list.getLineForIndex(mEditor.getLayout().getLineStart(i - 1));
                    } else {
                        prevRealLineNumber = -1;
                    }
                    lineBottom = mEditor.getLineBounds(i, null) - mEditor.getScrollY();
                    if (prevRealLineNumber != realLineNumber) {
                        canvas.drawText(
                                Integer.toString(realLineNumber + 1),
                                5.0f, lineBottom, mTextPaint);
                    }
                    i++;
                }
            }
            mEditor.updateGutter(); //Обновление списка линий (при редактировании текста тоже работает)
        }
    }

    @Override
    public void onScrollChanged(int x, int y, int oldx, int oldy) {
        invalidate();
    }
}
