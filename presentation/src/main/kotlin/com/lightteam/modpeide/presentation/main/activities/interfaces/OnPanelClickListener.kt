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

package com.lightteam.modpeide.presentation.main.activities.interfaces

interface OnPanelClickListener {
    fun onDrawerButton()

    fun onNewButton()
    fun onOpenButton()
    fun onSaveButton()
    fun onPropertiesButton()
    fun onCloseButton()

    fun onCutButton()
    fun onCopyButton()
    fun onPasteButton()
    fun onSelectAllButton()
    fun onSelectLineButton()
    fun onDeleteLineButton()
    fun onDuplicateLineButton()

    fun onFindButton()
    fun onReplaceAllButton()
    fun onGoToLineButton()

    fun onSyntaxValidatorButton()
    fun onInsertColorButton()
    //fun onDownloadSourceButton()

    fun onUndoButton()
    fun onRedoButton()

    fun onSettingsButton()
}