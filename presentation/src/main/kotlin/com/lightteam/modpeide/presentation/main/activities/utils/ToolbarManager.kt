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

package com.lightteam.modpeide.presentation.main.activities.utils

import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.ActivityMainBinding
import com.lightteam.modpeide.presentation.main.activities.interfaces.OnPanelClickListener
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper

class ToolbarManager(
    private val listener: OnPanelClickListener
) : PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityMainBinding

    fun init(activityMainBinding: ActivityMainBinding) {
        binding = activityMainBinding

        binding.actionMenuDrawer.setOnClickListener { listener.onDrawerButton() }
        binding.actionMenuSave.setOnClickListener { listener.onSaveButton() }

        setMenuClickListener(binding.actionMenuFile, R.menu.menu_file)
        setMenuClickListener(binding.actionMenuEdit, R.menu.menu_edit)
        setMenuClickListener(binding.actionMenuSearch, R.menu.menu_search)
        setMenuClickListener(binding.actionMenuTools, R.menu.menu_tools)

        binding.actionMenuUndo.setOnClickListener { listener.onUndoButton() }
        binding.actionMenuRedo.setOnClickListener { listener.onRedoButton() }
    }

    /**
     * Скрытие некоторых кнопок в вертикальном режиме.
     */
    fun portrait() {
        binding.actionMenuSave.visibility = View.GONE
        binding.actionMenuSearch.visibility = View.GONE
        binding.actionMenuTools.visibility = View.GONE
        setMenuClickListener(binding.actionMenuOverflow, R.menu.menu_overflow_vertical)
    }

    /**
     * Добавление некоторых кнопок в горизонтальном режиме.
     */
    fun landscape() {
        binding.actionMenuSave.visibility = View.VISIBLE
        binding.actionMenuSearch.visibility = View.VISIBLE
        binding.actionMenuTools.visibility = View.VISIBLE
        setMenuClickListener(binding.actionMenuOverflow, R.menu.menu_overflow_horizontal)
    }

    private fun setMenuClickListener(view: View, menuRes: Int) {
        view.setOnClickListener {
            val wrapper = ContextThemeWrapper(view.context, R.style.Theme_Internal_PopupMenu)

            val popupMenu = PopupMenu(wrapper, view)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.inflate(menuRes)

            val menuHelper = MenuPopupHelper(wrapper, popupMenu.menu as MenuBuilder, view)
            menuHelper.setForceShowIcon(true)
            menuHelper.show()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {

            //File Menu
            R.id.menu_file_new -> listener.onNewButton()
            R.id.menu_file_open -> listener.onOpenButton()
            R.id.menu_file_save -> listener.onSaveButton()
            R.id.menu_file_properties -> listener.onPropertiesButton()
            R.id.menu_file_close -> listener.onCloseButton()

            //Edit Menu
            R.id.menu_edit_cut -> listener.onCutButton()
            R.id.menu_edit_copy -> listener.onCopyButton()
            R.id.menu_edit_paste -> listener.onPasteButton()
            R.id.menu_edit_selectAll -> listener.onSelectAllButton()
            R.id.menu_edit_selectLine -> listener.onSelectLineButton()
            R.id.menu_edit_deleteLine -> listener.onDeleteLineButton()
            R.id.menu_edit_duplicateLine -> listener.onDuplicateLineButton()

            //Search Menu
            R.id.menu_search_find -> listener.onFindButton()
            R.id.menu_search_replace_all -> listener.onReplaceAllButton()
            R.id.menu_search_gotoLine -> listener.onGoToLineButton()
            
            //Tools Menu
            R.id.menu_tools_syntaxValidator -> listener.onSyntaxValidatorButton()
            R.id.menu_tools_insertColor -> listener.onInsertColorButton()
            //case R.id.menu_tools_downloadSource:
            //    listener.onDownloadSourceButton();
            //    break;

            //Overflow Menu
            R.id.menu_overflow_settings -> listener.onSettingsButton()
        }
        return false
    }
}