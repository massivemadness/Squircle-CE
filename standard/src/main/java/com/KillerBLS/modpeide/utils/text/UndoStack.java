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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Henry Thompson
 */
public class UndoStack implements Serializable {

    public static final int MAX_SIZE = 1048576;
    private int mCurrentSize = 0;
    private ArrayList<TextChange> mStack = new ArrayList<>();

    public static class TextChange implements Serializable {

        public String newText;
        public String oldText;
        public int start;

        @Override
        public TextChange clone() {
            TextChange copy = new TextChange();
            copy.oldText = oldText;
            copy.newText = newText;
            copy.start = start;
            return copy;
        }
    }

    public TextChange pop() {
        int size = mStack.size();
        if (size <= 0) {
            return null;
        }
        TextChange item = mStack.get(size - 1);
        mStack.remove(size - 1);
        mCurrentSize -= item.newText.length() + item.oldText.length();
        return item;
    }

    public void push(TextChange item) {
        int i = 0;
        if (item.newText == null) {
            item.newText = "";
        }
        if (item.oldText == null) {
            item.oldText = "";
        }
        int delta = item.newText.length() + item.oldText.length();
        if (delta < MAX_SIZE) {
            if (mStack.size() > 0) {
                TextChange previous = mStack.get(mStack.size() - 1);
                boolean allWhitespace;
                char[] toCharArray;
                int length;
                boolean allLettersDigits;
                if (item.oldText.length() == 0 && item.newText.length() == 1 && previous.oldText.length() == 0) {
                    if (previous.start + previous.newText.length() != item.start) {
                        mStack.add(item);
                    } else if (Character.isWhitespace(item.newText.charAt(0))) {
                        allWhitespace = true;
                        toCharArray = previous.newText.toCharArray();
                        length = toCharArray.length;
                        while (i < length) {
                            if (!Character.isWhitespace(toCharArray[i])) {
                                allWhitespace = false;
                            }
                            i++;
                        }
                        if (allWhitespace) {
                            previous.newText += item.newText;
                        } else {
                            mStack.add(item);
                        }
                    } else if (Character.isLetterOrDigit(item.newText.charAt(0))) {
                        allLettersDigits = true;
                        toCharArray = previous.newText.toCharArray();
                        length = toCharArray.length;
                        while (i < length) {
                            if (!Character.isLetterOrDigit(toCharArray[i])) {
                                allLettersDigits = false;
                            }
                            i++;
                        }
                        if (allLettersDigits) {
                            previous.newText += item.newText;
                        } else {
                            mStack.add(item);
                        }
                    } else {
                        mStack.add(item);
                    }
                } else if (item.oldText.length() != 1 || item.newText.length() != 0 || previous.newText.length() != 0) {
                    mStack.add(item);
                } else if (previous.start - 1 != item.start) {
                    mStack.add(item);
                } else if (Character.isWhitespace(item.oldText.charAt(0))) {
                    allWhitespace = true;
                    toCharArray = previous.oldText.toCharArray();
                    length = toCharArray.length;
                    while (i < length) {
                        if (!Character.isWhitespace(toCharArray[i])) {
                            allWhitespace = false;
                        }
                        i++;
                    }
                    if (allWhitespace) {
                        previous.oldText = item.oldText + previous.oldText;
                        previous.start -= item.oldText.length();
                    } else {
                        mStack.add(item);
                    }
                } else if (Character.isLetterOrDigit(item.oldText.charAt(0))) {
                    allLettersDigits = true;
                    toCharArray = previous.oldText.toCharArray();
                    length = toCharArray.length;
                    while (i < length) {
                        if (!Character.isLetterOrDigit(toCharArray[i])) {
                            allLettersDigits = false;
                        }
                        i++;
                    }
                    if (allLettersDigits) {
                        previous.oldText = item.oldText + previous.oldText;
                        previous.start -= item.oldText.length();
                    } else {
                        mStack.add(item);
                    }
                } else {
                    mStack.add(item);
                }
            } else {
                mStack.add(item);
            }
            mCurrentSize += delta;
            while (mCurrentSize > MAX_SIZE) {
                if (!removeLast()) {
                    return;
                }
            }
            return;
        }
        removeAll();
    }

    public void removeAll() {
        mStack.removeAll(mStack);
        mCurrentSize = 0;
    }

    public boolean canUndo() {
        return mStack.size() > 0;
    }

    private boolean removeLast() {
        if (mStack.size() <= 0) {
            return false;
        }
        TextChange item = mStack.get(0);
        mStack.remove(0);
        mCurrentSize -= item.newText.length() + item.oldText.length();
        return true;
    }

    public boolean mergeTop() {
        if (mStack.size() >= 2) {
            TextChange newer = mStack.get(mStack.size() - 1);
            TextChange previous = mStack.get(mStack.size() - 2);
            if (previous.start + previous.newText.length() == newer.start) {
                previous.newText += newer.newText;
                previous.oldText += newer.oldText;
                mStack.remove(newer);
                return true;
            }
        }
        return false;
    }

    public int count() {
        return mStack.size();
    }

    public TextChange getItemAt(int index) {
        return mStack.get(index);
    }

    public void clear() {
        mStack.clear();
    }
}