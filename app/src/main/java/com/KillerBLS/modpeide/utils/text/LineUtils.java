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

package com.KillerBLS.modpeide.utils.text;

import android.widget.TextView;

public class LineUtils {

    public int getTopVisibleLine(TextView editor) {
        int lineHeight = editor.getLineHeight();
        if (lineHeight == 0) {
            return 0;
        }
        int line = editor.getScrollY() / lineHeight;
        if (line < 0) {
            return 0;
        }
        if (line >= editor.getLineCount()) {
            return editor.getLineCount() - 1;
        }
        return line;
    }

    public int getBottomVisibleLine(TextView editor) {
        int lineHeight = editor.getLineHeight();
        if (lineHeight == 0) {
            return 0;
        }
        int line = Math.abs((editor.getScrollY() + editor.getHeight()) / lineHeight) + 1;
        if (line < 0) {
            return 0;
        }
        if (line >= editor.getLineCount()) {
            return editor.getLineCount() - 1;
        }
        return line;
    }
}
