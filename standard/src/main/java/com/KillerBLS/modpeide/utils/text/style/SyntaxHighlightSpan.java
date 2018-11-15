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