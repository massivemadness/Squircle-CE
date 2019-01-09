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

package com.KillerBLS.modpeide.manager;

import android.app.Activity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.manager.interfaces.OnPanelClickListener;
import com.KillerBLS.modpeide.utils.commons.MenuHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Класс для работы с кастомным меню.
 */
public class ToolbarManager implements PopupMenu.OnMenuItemClickListener {

    private OnPanelClickListener mListener;

    @BindView(R.id.action_menu_drawer)
    ImageView mDrawerButton;
    @BindView(R.id.action_menu_save)
    ImageView mSaveButton;
    @BindView(R.id.action_menu_file)
    ImageView mFileButton;
    @BindView(R.id.action_menu_edit)
    ImageView mEditButton;
    @BindView(R.id.action_menu_search)
    ImageView mSearchButton;
    @BindView(R.id.action_menu_tools)
    ImageView mToolsButton;
    @BindView(R.id.action_menu_undo)
    ImageView mUndoButton;
    @BindView(R.id.action_menu_redo)
    ImageView mRedoButton;
    @BindView(R.id.action_menu_overflow)
    ImageView mOverflowButton;

    public ToolbarManager(Activity activity) {
        mListener = (OnPanelClickListener) activity;
        ButterKnife.bind(this, activity);
        initMenu();
    }

    // region MENU

    /**
     * Настройка меню. Установка действий.
     */
    private void initMenu() {
        mDrawerButton.setOnClickListener(view -> mListener.onDrawerButton());
        mSaveButton.setOnClickListener(view -> mListener.onSaveButton());
        setMenuClickListener(mFileButton, R.menu.menu_file);
        setMenuClickListener(mEditButton, R.menu.menu_edit);
        setMenuClickListener(mSearchButton, R.menu.menu_search);
        setMenuClickListener(mToolsButton, R.menu.menu_tools);
        mUndoButton.setOnClickListener(view -> mListener.onUndoButton());
        mRedoButton.setOnClickListener(view -> mListener.onRedoButton());
    }

    /**
     * Скрытие некоторых кнопок в вертикальном режиме.
     */
    public void portrait() {
        mSaveButton.setVisibility(View.GONE);
        mSearchButton.setVisibility(View.GONE);
        mToolsButton.setVisibility(View.GONE);
        setMenuClickListener(mOverflowButton, R.menu.menu_overflow_vertical);
    }

    /**
     * Добавление некоторых кнопок в горизонтальном режиме.
     */
    public void landscape() {
        mSaveButton.setVisibility(View.VISIBLE);
        mSearchButton.setVisibility(View.VISIBLE);
        mToolsButton.setVisibility(View.VISIBLE);
        setMenuClickListener(mOverflowButton, R.menu.menu_overflow_horizontal);
    }

    // endregion MENU

    // region CLICK

    /**
     * Установка действий на ImageButton'ы.
     * @param imageButton - кнопка на которую будет накладываться OnClickListener.
     * @param menuRes - меню для кнопки.
     */
    private void setMenuClickListener(ImageView imageButton, final int menuRes) {
        imageButton.setOnClickListener(view ->
                MenuHelper.forceShow(imageButton.getContext(), imageButton, menuRes, this));
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            //File Menu
            case R.id.menu_file_new:
                mListener.onNewButton();
                break;
            case R.id.menu_file_open:
                mListener.onOpenButton();
                break;
            case R.id.menu_file_save:
                mListener.onSaveButton();
                break;
            case R.id.menu_file_properties:
                mListener.onPropertiesButton();
                break;
            case R.id.menu_file_close:
                mListener.onCloseButton();
                break;
            //Edit Menu
            case R.id.menu_edit_cut:
                mListener.onCutButton();
                break;
            case R.id.menu_edit_copy:
                mListener.onCopyButton();
                break;
            case R.id.menu_edit_paste:
                mListener.onPasteButton();
                break;
            case R.id.menu_edit_selectAll:
                mListener.onSelectAllButton();
                break;
            case R.id.menu_edit_selectLine:
                mListener.onSelectLineButton();
                break;
            case R.id.menu_edit_deleteLine:
                mListener.onDeleteLineButton();
                break;
            case R.id.menu_edit_duplicateLine:
                mListener.onDuplicateLineButton();
                break;
            //Search Menu
            case R.id.menu_search_find:
                mListener.onFindButton();
                break;
            case R.id.menu_search_replace_all:
                mListener.onReplaceAllButton();
                break;
            case R.id.menu_search_gotoLine:
                mListener.onGoToLineButton();
                break;
            //Tools Menu
            case R.id.menu_tools_syntaxValidator:
                mListener.onSyntaxValidatorButton();
                break;
            case R.id.menu_tools_insertColor:
                mListener.onInsertColorButton();
                break;
            //case R.id.menu_tools_downloadSource:
            //    mListener.onDownloadSourceButton();
            //    break;
            //Overflow Menu
            case R.id.menu_overflow_settings:
                mListener.onSettingsButton();
                break;
        }
        return false;
    }

    // endregion CLICK
}
