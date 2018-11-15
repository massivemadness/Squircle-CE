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

import android.widget.MultiAutoCompleteTextView;

/**
 * @author Trần Lê Duy
 */
public class SymbolsTokenizer implements MultiAutoCompleteTextView.Tokenizer {

    private static final String TOKEN = "!@#$%^&*()_+-={}|[]:;'<>/<.? \r\n\t";

    @Override
    public int findTokenStart(CharSequence text, int cursor) {
        int i = cursor;
        while (i > 0 && !TOKEN.contains(Character.toString(text.charAt(i - 1)))) {
            i--;
        }
        while (i < cursor && text.charAt(i) == ' ') {
            i++;
        }
        return i;
    }

    @Override
    public int findTokenEnd(CharSequence text, int cursor) {
        int i = cursor;
        int len = text.length();
        while (i < len) {
            if (TOKEN.contains(Character.toString(text.charAt(i - 1)))) {
                return i;
            } else {
                i++;
            }
        }
        return len;
    }

    @Override
    public CharSequence terminateToken(CharSequence text) {
        int i = text.length();
        while (i > 0 && text.charAt(i - 1) == ' ') {
            i--;
        }
        return text;
    }
}