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

package com.lightteam.modpeide.ui.editor.utils

import android.content.res.Configuration
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.FragmentEditorBinding
import com.lightteam.modpeide.utils.extensions.makeRightPaddingRecursively

class ToolbarManager(
    private val listener: OnPanelClickListener
) : PopupMenu.OnMenuItemClickListener {

    var orientation: Int = Configuration.ORIENTATION_UNDEFINED
        set(value) {
            field = when (value) {
                Configuration.ORIENTATION_PORTRAIT -> portrait()
                Configuration.ORIENTATION_LANDSCAPE -> landscape()
                else -> Configuration.ORIENTATION_UNDEFINED
            }
        }

    private lateinit var binding: FragmentEditorBinding

    fun bind(binding: FragmentEditorBinding) {
        this.binding = binding
        orientation = binding.root.resources?.configuration?.orientation ?: Configuration.ORIENTATION_PORTRAIT

        binding.actionMenuDrawer.setOnClickListener { listener.onDrawerButton() }
        binding.actionMenuSave.setOnClickListener { listener.onSaveButton() }

        setMenuClickListener(binding.actionMenuFile, R.menu.menu_file)
        setMenuClickListener(binding.actionMenuEdit, R.menu.menu_edit)
        setMenuClickListener(binding.actionMenuSearch, R.menu.menu_search)
        setMenuClickListener(binding.actionMenuTools, R.menu.menu_tools)

        binding.actionMenuUndo.setOnClickListener { listener.onUndoButton() }
        binding.actionMenuRedo.setOnClickListener { listener.onRedoButton() }
    }

    private fun portrait(): Int {
        binding.actionMenuSave.visibility = View.GONE
        binding.actionMenuSearch.visibility = View.GONE
        binding.actionMenuTools.visibility = View.GONE
        setMenuClickListener(binding.actionMenuOverflow, R.menu.menu_overflow_vertical)
        return Configuration.ORIENTATION_PORTRAIT
    }

    private fun landscape(): Int {
        binding.actionMenuSave.visibility = View.VISIBLE
        binding.actionMenuSearch.visibility = View.VISIBLE
        binding.actionMenuTools.visibility = View.VISIBLE
        setMenuClickListener(binding.actionMenuOverflow, R.menu.menu_overflow_horizontal)
        return Configuration.ORIENTATION_LANDSCAPE
    }

    private fun setMenuClickListener(view: View, menuRes: Int) {
        view.setOnClickListener {
            val wrapper = ContextThemeWrapper(it.context, R.style.Widget_AppTheme_PopupMenu)
            val popupMenu = PopupMenu(wrapper, it)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.inflate(menuRes)
            popupMenu.makeRightPaddingRecursively()
            popupMenu.show()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {

            // File Menu
            R.id.action_new -> listener.onNewButton()
            R.id.action_open -> listener.onOpenButton()
            R.id.action_save -> listener.onSaveButton()
            R.id.action_save_as -> listener.onSaveAsButton()
            R.id.action_properties -> listener.onPropertiesButton()
            R.id.action_close -> listener.onCloseButton()

            // Edit Menu
            R.id.action_cut -> listener.onCutButton()
            R.id.action_copy -> listener.onCopyButton()
            R.id.action_paste -> listener.onPasteButton()
            R.id.action_select_all -> listener.onSelectAllButton()
            R.id.action_select_line -> listener.onSelectLineButton()
            R.id.action_delete_line -> listener.onDeleteLineButton()
            R.id.action_duplicate_line -> listener.onDuplicateLineButton()

            // Search Menu
            R.id.action_find -> listener.onFindButton()
            R.id.action_replace_all -> listener.onReplaceAllButton()
            R.id.action_goto_line -> listener.onGoToLineButton()
            
            // Tools Menu
            R.id.action_error_checking -> listener.onErrorCheckingButton()
            R.id.action_insert_color -> listener.onInsertColorButton()

            // Overflow Menu
            R.id.action_settings -> listener.onSettingsButton()
        }
        return false
    }

    interface OnPanelClickListener {
        fun onDrawerButton()

        fun onNewButton()
        fun onOpenButton()
        fun onSaveButton()
        fun onSaveAsButton()
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

        fun onErrorCheckingButton()
        fun onInsertColorButton()

        fun onUndoButton()
        fun onRedoButton()

        fun onSettingsButton()
    }
}