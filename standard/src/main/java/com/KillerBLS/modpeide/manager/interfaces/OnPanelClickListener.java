/*
 * Copyright (C) 2018 Light Team Software
 *
 * This file is part of ModPE IDE.
 *
 * ModPE IDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ModPE IDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.KillerBLS.modpeide.manager.interfaces;

/**
 * Обработка нажатий меню Toolbar'а.
 */
public interface OnPanelClickListener {
    void onNewButton();
    void onOpenButton();
    void onSaveButton();
    void onPropertiesButton();
    void onCloseButton();
    void onCutButton();
    void onCopyButton();
    void onPasteButton();
    void onSelectAllButton();
    void onSelectLineButton();
    void onDeleteLineButton();
    void onDuplicateLineButton();
    void onFindButton();
    void onReplaceAllButton();
    void onGoToLineButton();
    void onSyntaxValidatorButton();
    void onInsertColorButton();
    //void onDownloadSourceButton();
    void onUndoButton();
    void onRedoButton();
    void onSettingsButton();
}
