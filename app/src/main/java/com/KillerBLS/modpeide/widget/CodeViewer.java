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

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.syntax.SyntaxPatterns;

import java.util.regex.Matcher;

public class CodeViewer extends android.support.v7.widget.AppCompatTextView {
    private boolean WITHOUT_SYMBOLS = false;

    public CodeViewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.MONOSPACE);
    }

    public void setHighlightedText(String text) {
        super.setText(highlight(new SpannableStringBuilder(text)));
    }

    private Editable highlight(Editable e) {
        clearSpans(e); //use this method instead of e.clearSpans();
        if (e.length() == 0) {
            return e;
        }
        for (Matcher m = SyntaxPatterns.KEYWORDS.matcher(e); m.find(); ) {
            e.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(getContext(),
                            R.color.syntax_keyword)),
                    m.start(),
                    m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (Matcher m = SyntaxPatterns.NUMBERS.matcher(e); m.find(); ) {
            e.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(getContext(),
                            R.color.syntax_number)),
                    m.start(),
                    m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if(!WITHOUT_SYMBOLS) {
            for (Matcher m = SyntaxPatterns.SYMBOLS.matcher(e); m.find(); ) {
                e.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(getContext(),
                                R.color.syntax_symbols)),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        for (Matcher m = SyntaxPatterns.CLASSES.matcher(e); m.find(); ) {
            e.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(getContext(),
                            R.color.syntax_classes)),
                    m.start(),
                    m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (Matcher m = SyntaxPatterns.COMMENTS.matcher(e); m.find(); ) {
            e.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(getContext(),
                            R.color.syntax_comment)),
                    m.start(),
                    m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (Matcher m = SyntaxPatterns.KEYWORDS2.matcher(e); m.find(); ) {
            e.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(getContext(),
                            R.color.syntax_keyword2)),
                    m.start(),
                    m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (Matcher m = SyntaxPatterns.STRINGS.matcher(e); m.find(); ) {
            e.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(getContext(),
                            R.color.syntax_string)),
                    m.start(),
                    m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return e;
    }

    private static void clearSpans(Editable e) {
        // remove foreground color spans
        {
            ForegroundColorSpan spans[] = e.getSpans(
                    0,
                    e.length(),
                    ForegroundColorSpan.class);

            for (int i = spans.length; i-- > 0; ) {
                e.removeSpan(spans[i]);
            }
        }
        // remove background color spans
        {
            BackgroundColorSpan spans[] = e.getSpans(
                    0,
                    e.length(),
                    BackgroundColorSpan.class);

            for (int i = spans.length; i-- > 0; ) {
                e.removeSpan(spans[i]);
            }
        }
    }

    public void withoutSymbols(boolean withoutSymbols) {
        WITHOUT_SYMBOLS = withoutSymbols;
    }
}
