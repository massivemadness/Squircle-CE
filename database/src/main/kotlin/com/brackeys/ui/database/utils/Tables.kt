/*
 * Copyright 2020 Brackeys IDE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.brackeys.ui.database.utils

object Tables {
    @Deprecated("Use Tables.DOCUMENTS instead")
    const val FILE_HISTORY = "tbl_file_history"
    const val DOCUMENTS = "tbl_documents"
    const val FONTS = "tbl_fonts"
    const val THEMES = "tbl_themes"
    @Deprecated("Keyboard presets have been removed in v2020.2.5")
    const val PRESETS = "tbl_presets"
}