/*
 * Copyright Squircle CE contributors.
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

package com.blacksquircle.ui.filesystem.saf

import android.content.Context
import androidx.core.net.toUri
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileParams
import kotlinx.coroutines.flow.Flow
import java.io.BufferedReader
import java.io.IOException

class SAFFilesystem(private val context: Context) : Filesystem {

    override fun ping() = Unit

    override fun listFiles(parent: FileModel): List<FileModel> {
        throw UnsupportedOperationException()
    }

    override fun createFile(fileModel: FileModel) {
        throw UnsupportedOperationException()
    }

    override fun renameFile(source: FileModel, name: String) {
        throw UnsupportedOperationException()
    }

    override fun deleteFile(fileModel: FileModel) {
        throw UnsupportedOperationException()
    }

    override fun copyFile(source: FileModel, dest: FileModel) {
        throw UnsupportedOperationException()
    }

    override fun compressFiles(source: List<FileModel>, dest: FileModel): Flow<FileModel> {
        throw UnsupportedOperationException()
    }

    override fun extractFiles(source: FileModel, dest: FileModel): Flow<FileModel> {
        throw UnsupportedOperationException()
    }

    override fun loadFile(fileModel: FileModel, fileParams: FileParams): String {
        val fileUri = fileModel.fileUri.toUri()
        // TODO chardet?
        context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
            return inputStream.bufferedReader(fileParams.charset)
                .use(BufferedReader::readText)
        }
        throw IOException("Unable to open file: ${fileModel.fileUri}")
    }

    override fun saveFile(fileModel: FileModel, text: String, fileParams: FileParams) {
        val fileUri = fileModel.fileUri.toUri()
        val fileText = text.replace(fileParams.linebreak.regex, fileParams.linebreak.replacement)
        context.contentResolver.openOutputStream(fileUri)?.use { output ->
            output.bufferedWriter(fileParams.charset).use {
                it.write(fileText)
            }
        } ?: throw IOException("Unable to save file: ${fileModel.fileUri}")
    }

    companion object {
        const val SAF_UUID = "saf"
    }
}