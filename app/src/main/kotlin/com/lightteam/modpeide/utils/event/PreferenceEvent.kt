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

package com.lightteam.modpeide.utils.event

import com.lightteam.language.scheme.ColorScheme

sealed class PreferenceEvent<T>(val value: T) {
    class Theme(value: ColorScheme) : PreferenceEvent<ColorScheme>(value)
    class FontSize(value: Float) : PreferenceEvent<Float>(value)
    class FontType(value: String) : PreferenceEvent<String>(value)
    class WordWrap(value: Boolean) : PreferenceEvent<Boolean>(value)
    class CodeCompletion(value: Boolean) : PreferenceEvent<Boolean>(value)
    class PinchZoom(value: Boolean) : PreferenceEvent<Boolean>(value)
    class CurrentLine(value: Boolean) : PreferenceEvent<Boolean>(value)
    class Delimiters(value: Boolean) : PreferenceEvent<Boolean>(value)
    class ExtendedKeys(value: Boolean) : PreferenceEvent<Boolean>(value)
    class SoftKeys(value: Boolean) : PreferenceEvent<Boolean>(value)
    class AutoIndent(value: Boolean) : PreferenceEvent<Boolean>(value)
    class AutoBrackets(value: Boolean) : PreferenceEvent<Boolean>(value)
    class AutoQuotes(value: Boolean) : PreferenceEvent<Boolean>(value)
}