/*
 * Copyright 2025 Squircle CE contributors.
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
import android.provider.DocumentsContract
import android.provider.DocumentsContract.Document.COLUMN_DOCUMENT_ID
import android.provider.DocumentsContract.Document.COLUMN_MIME_TYPE
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
        val fileUri = parent.fileUri.toUri()

        // FIXME Can't use child uri to get file list
        val parentId = DocumentsContract.getTreeDocumentId(fileUri)
        val parentUri = DocumentsContract.buildDocumentUriUsingTree(fileUri, parentId)

        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(fileUri, parentId)
        val children = mutableListOf<FileModel>()

        context.contentResolver.query(
            /* uri = */ childrenUri,
            /* projection = */ arrayOf(COLUMN_DOCUMENT_ID, COLUMN_MIME_TYPE),
            /* selection = */ null,
            /* selectionArgs = */ null,
            /* sortOrder = */ null
        )?.use { cursor ->
            val columnId = cursor.getColumnIndexOrThrow(COLUMN_DOCUMENT_ID)
            val columnMimeType = cursor.getColumnIndexOrThrow(COLUMN_MIME_TYPE)

            while (cursor.moveToNext()) {
                val childId = cursor.getString(columnId)
                val mimeType = cursor.getString(columnMimeType)
                val documentUri = DocumentsContract.buildDocumentUriUsingTree(parentUri, childId)

                val fileModel = FileModel(
                    fileUri = documentUri.toString(),
                    filesystemUuid = SAF_UUID,
                    isDirectory = mimeType == DocumentsContract.Document.MIME_TYPE_DIR,
                )
                children.add(fileModel)
            }
        }
        return children
    }

    override fun createFile(fileModel: FileModel) {
        throw UnsupportedOperationException()
    }

    override fun renameFile(source: FileModel, name: String) {
        val fileUri = source.fileUri.toUri()
        DocumentsContract.renameDocument(context.contentResolver, fileUri, name)
    }

    override fun deleteFile(fileModel: FileModel) {
        val fileUri = fileModel.fileUri.toUri()
        DocumentsContract.deleteDocument(context.contentResolver, fileUri)
    }

    override fun copyFile(source: FileModel, dest: FileModel) {
        val sourceUri = source.fileUri.toUri()
        val destUri = dest.fileUri.toUri()
        DocumentsContract.copyDocument(context.contentResolver, sourceUri, destUri)
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