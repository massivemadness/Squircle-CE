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

package com.lightteam.language.base.styler.utils

typealias Region = Pair<Int, Int>

/**
 * Проверяет, находится ли комментарий внутри двойных кавычек (строки).
 * FIXME https://github.com/massivemadness/ModPE-IDE/issues/9#issuecomment-672956922
 * @param startIndex позиция начала комментария
 * @param endIndex позиция конца комментария
 */
fun List<Region>.inRegion(startIndex: Int, endIndex: Int): Boolean {
    for ((start, end) in this) {
        if (start <= startIndex && end >= endIndex &&
            (startIndex in start until end || endIndex in start until end)) {
            return true
        }
    }
    return false
}