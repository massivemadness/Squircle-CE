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

package com.KillerBLS.modpeide.utils.text;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Henry Thompson
 */
public class LinesCollection implements Serializable, Iterable<LineObject> {

    private ArrayList<LineObject> mLines = new ArrayList<>();

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