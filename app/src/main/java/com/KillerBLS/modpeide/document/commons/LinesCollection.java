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

package com.KillerBLS.modpeide.document.commons;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Henry Thompson
 */
public class LinesCollection implements Serializable, Iterable<LineObject> {

    private ArrayList<LineObject> mLines = new ArrayList();

    public LinesCollection() {
        mLines.add(new LineObject(0));
    }

    public void add(int line, int index) {
        if (mLines.size() <= 0 || line != 0) {
            mLines.add(line, new LineObject(index));
        }
    }

    public void remove(int line) {
        if (line != 0) {
            mLines.remove(line);
        }
    }

    public void shiftIndexes(int fromLine, int shiftBy) {
        if (fromLine > 0 && fromLine < mLines.size()) {
            int i = fromLine;
            while (i < mLines.size()) {
                Integer newIndex = getIndexForLine(i) + shiftBy;
                if (i <= 0 || newIndex > 0) {
                    mLines.get(i).setStart(newIndex);
                } else {
                    remove(i);
                    i--;
                }
                i++;
            }
        }
    }

    public int getIndexForLine(int line) {
        if (line >= mLines.size()) {
            return -1;
        }
        return mLines.get(line).getStart();
    }

    public int getLineForIndex(int index) {
        int first = 0;
        int upto = mLines.size() - 1;
        while (first < upto) {
            int mid = (first + upto) / 2;
            if (index < getIndexForLine(mid)) {
                upto = mid;
            } else if (index <= getIndexForLine(mid) || index < getIndexForLine(mid + 1)) {
                return mid;
            } else {
                first = mid + 1;
            }
        }
        return mLines.size() - 1;
    }

    public int getLineCount() {
        return mLines.size();
    }

    public LineObject getLine(int lineNumber) {
        if (lineNumber < 0 || lineNumber >= mLines.size()) {
            return null;
        }
        return mLines.get(lineNumber);
    }

    @NonNull
    public Iterator<LineObject> iterator() {
        return mLines.iterator();
    }

    @Override
    public LinesCollection clone() {
        LinesCollection clone = new LinesCollection();
        clone.mLines = (ArrayList) mLines.clone();
        return clone;
    }
}

