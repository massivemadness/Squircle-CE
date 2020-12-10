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

package com.brackeys.ui.feature.base.providers

import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 * Facilitates secure sharing of files associated with an app by creating a content://
 * [Uri] for a file instead of a file:/// [Uri].
 *
 * @author gzu-liyujiang (1032694760@qq.com)
 * @since 2020/12/10 10:00
 */
internal class FileProvider : androidx.core.content.FileProvider()

fun getUriForFile(file: File): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val authority = applicationContext().packageName + ".brackeyside.file_provider"
        FileProvider.getUriForFile(applicationContext(), authority, file)
    } else {
        Uri.fromFile(file)
    }
}