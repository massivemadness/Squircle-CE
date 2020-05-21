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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
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

    var panel: Panel = Panel.DEFAULT
        set(value) {
            field = value
            updatePanel()
        }

    private var isRegex = false
    private var isMatchCase = true

    private lateinit var binding: FragmentEditorBinding

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

            // Find Menu
            R.id.action_find -> listener.onOpenFindButton()
            R.id.action_switch_find -> {
                listener.onCloseReplaceButton()
                listener.onCloseFindButton()
            }
            R.id.action_switch_replace -> {
                if (panel == Panel.FIND) {
                    listener.onOpenReplaceButton()
                } else {
                    listener.onCloseReplaceButton()
                }
            }
            R.id.action_regex -> {
                isRegex = !isRegex
                listener.onRegexChanged(isRegex)
            }
            R.id.action_match_case -> {
                isMatchCase = !isMatchCase
                listener.onMatchCaseChanged(isMatchCase)
            }

            // Tools Menu
            R.id.action_error_checking -> listener.onErrorCheckingButton()
            R.id.action_insert_color -> listener.onInsertColorButton()

            // Overflow Menu
            R.id.action_settings -> listener.onSettingsButton()
        }
        return false
    }

    fun bind(binding: FragmentEditorBinding) {
        this.binding = binding
        orientation = binding.root.resources?.configuration?.orientation ?: Configuration.ORIENTATION_PORTRAIT
        updatePanel()

        binding.actionDrawer.setOnClickListener { listener.onDrawerButton() }
        binding.actionSave.setOnClickListener { listener.onSaveButton() }
        binding.actionFind.setOnClickListener { listener.onOpenFindButton() }

        setMenuClickListener(binding.actionFile, R.menu.menu_file)
        setMenuClickListener(binding.actionEdit, R.menu.menu_edit)
        setMenuClickListener(binding.actionTools, R.menu.menu_tools)
        setMenuClickListener(binding.actionFindOverflow, R.menu.menu_find)

        binding.actionUndo.setOnClickListener { listener.onUndoButton() }
        binding.actionRedo.setOnClickListener { listener.onRedoButton() }

        binding.actionReplace.setOnClickListener {
            listener.onReplaceButton(binding.inputReplace.text.toString())
        }
        binding.actionReplaceAll.setOnClickListener {
            listener.onReplaceAllButton(binding.inputReplace.text.toString())
        }
        binding.actionDown.setOnClickListener { listener.onNextResultButton() }
        binding.actionUp.setOnClickListener { listener.onPreviousResultButton() }
        binding.inputFind.doAfterTextChanged {
            listener.onFindInputChanged(it.toString())
        }
    }

    private fun portrait(): Int {
        binding.actionSave.visibility = View.GONE
        binding.actionFind.visibility = View.GONE
        binding.actionTools.visibility = View.GONE
        setMenuClickListener(binding.actionOverflow, R.menu.menu_overflow_vertical)
        return Configuration.ORIENTATION_PORTRAIT
    }

    private fun landscape(): Int {
        binding.actionSave.visibility = View.VISIBLE
        binding.actionFind.visibility = View.VISIBLE
        binding.actionTools.visibility = View.VISIBLE
        setMenuClickListener(binding.actionOverflow, R.menu.menu_overflow_horizontal)
        return Configuration.ORIENTATION_LANDSCAPE
    }

    private fun setMenuClickListener(view: View, menuRes: Int) {
        view.setOnClickListener {
            val wrapper = ContextThemeWrapper(it.context, R.style.Widget_AppTheme_PopupMenu)
            val popupMenu = PopupMenu(wrapper, it)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.inflate(menuRes)
            popupMenu.makeRightPaddingRecursively()
            onPreparePopupMenu(popupMenu.menu)
            popupMenu.show()
        }
    }

    private fun onPreparePopupMenu(menu: Menu) {
        val switchReplace = menu.findItem(R.id.action_switch_replace)
        val regex = menu.findItem(R.id.action_regex)
        val matchCase = menu.findItem(R.id.action_match_case)

        if (panel == Panel.FIND_REPLACE) {
            switchReplace?.setTitle(R.string.action_close_replace)
        }

        regex?.isChecked = isRegex
        matchCase?.isChecked = isMatchCase
    }

    private fun updatePanel() {
        when (panel) {
            Panel.DEFAULT -> {
                binding.defaultPanel.isVisible = true
                binding.findPanel.isVisible = false
                binding.replacePanel.isVisible = false
            }
            Panel.FIND -> {
                binding.defaultPanel.isVisible = false
                binding.findPanel.isVisible = true
                binding.replacePanel.isVisible = false
            }
            Panel.FIND_REPLACE -> {
                binding.defaultPanel.isVisible = false
                binding.findPanel.isVisible = true
                binding.replacePanel.isVisible = true
            }
        }
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

        fun onOpenFindButton()
        fun onCloseFindButton()
        fun onOpenReplaceButton()
        fun onCloseReplaceButton()
        fun onReplaceButton(replaceText: String)
        fun onReplaceAllButton(replaceText: String)
        fun onNextResultButton()
        fun onPreviousResultButton()
        fun onRegexChanged(regex: Boolean)
        fun onMatchCaseChanged(matchCase: Boolean)
        fun onFindInputChanged(findText: String)

        fun onErrorCheckingButton()
        fun onInsertColorButton()

        fun onUndoButton()
        fun onRedoButton()

        fun onSettingsButton()
    }
}