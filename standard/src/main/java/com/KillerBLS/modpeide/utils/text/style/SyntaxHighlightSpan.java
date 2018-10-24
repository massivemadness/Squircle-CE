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

package com.KillerBLS.modpeide.utils.text.style;

import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.CharacterStyle;

import java.io.Serializable;

/**
 * @author Henry Thompson
 */
public class SyntaxHighlightSpan extends CharacterStyle
        implements Serializable, Comparable<SyntaxHighlightSpan> {

    private boolean bold;
    private boolean italics;

    public int color;
    public int end;
    public int start;

    public SyntaxHighlightSpan(StyleSpan span, int start, int end) {
        this.color = span.getColor();
        this.bold = span.getIsBold();
        this.italics = span.getIsItalic();
        this.start = start;
        this.end = end;
    }

    public SyntaxHighlightSpan(int color, boolean bold, boolean italics, int start, int end) {
        this.color = color;
        this.start = start;
        this.end = end;
        this.bold = bold;
        this.italics = italics;
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setColor(color);
        tp.setFakeBoldText(bold);
        if(italics) {
            tp.setTextSkewX(-0.1f);
        }
    }

    @Override
    public int compareTo(@NonNull SyntaxHighlightSpan other) {
        return this.start - other.start;
    }
}