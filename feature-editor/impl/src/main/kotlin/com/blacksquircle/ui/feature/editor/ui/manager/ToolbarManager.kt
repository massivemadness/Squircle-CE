/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.editor.ui.manager

import android.content.res.Configuration
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.blacksquircle.ui.core.view.MaterialPopupMenu
import com.blacksquircle.ui.editorkit.model.FindParams
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.databinding.FragmentEditorBinding

class ToolbarManager(
    private val listener: Listener,
) : PopupMenu.OnMenuItemClickListener {

    var mode: Mode = Mode.DEFAULT
        set(value) {
            field = value
            updateToolbar()
        }

    var params = FindParams()

    private var orientation: Int = Configuration.ORIENTATION_UNDEFINED
        set(value) {
            field = when (value) {
                Configuration.ORIENTATION_PORTRAIT -> portrait()
                Configuration.ORIENTATION_LANDSCAPE -> landscape()
                else -> Configuration.ORIENTATION_UNDEFINED
            }
        }

    private lateinit var binding: FragmentEditorBinding

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            // File Menu
            R.id.action_new -> listener.onNewButton()
            R.id.action_open -> listener.onOpenButton()
            R.id.action_save -> listener.onSaveButton()
            R.id.action_save_as -> listener.onSaveAsButton()
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
                if (mode == Mode.FIND) {
                    listener.onOpenReplaceButton()
                } else {
                    listener.onCloseReplaceButton()
                }
            }
            R.id.action_goto_line -> listener.onGoToLineButton()
            R.id.action_regex -> listener.onFindRegexButton()
            R.id.action_match_case -> listener.onFindMatchCaseButton()
            R.id.action_words_only -> listener.onFindWordsOnlyButton()

            // Tools Menu
            R.id.action_force_syntax -> listener.onForceSyntaxButton()
            R.id.action_insert_color -> listener.onInsertColorButton()

            // Overflow Menu
            R.id.action_settings -> listener.onSettingsButton()
        }
        return false
    }

    fun bind(binding: FragmentEditorBinding) {
        this.binding = binding
        orientation = binding.root.resources?.configuration
            ?.orientation ?: Configuration.ORIENTATION_PORTRAIT
        updateToolbar()

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
            listener.onFindQueryChanged(it.toString())
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
            val popupMenu = MaterialPopupMenu(it.context)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.inflate(menuRes)
            onPreparePopupMenu(popupMenu.menu)
            popupMenu.show(view)
        }
    }

    private fun onPreparePopupMenu(menu: Menu) {
        val switchReplace = menu.findItem(R.id.action_switch_replace)
        val regex = menu.findItem(R.id.action_regex)
        val matchCase = menu.findItem(R.id.action_match_case)
        val wordsOnly = menu.findItem(R.id.action_words_only)

        if (mode == Mode.FIND_REPLACE) {
            switchReplace?.setTitle(R.string.action_close_replace)
        }

        regex?.isChecked = params.regex
        matchCase?.isChecked = params.matchCase
        wordsOnly?.isChecked = params.wordsOnly
    }

    private fun updateToolbar() {
        when (mode) {
            Mode.DEFAULT -> {
                binding.defaultPanel.isVisible = true
                binding.findPanel.isVisible = false
                binding.replacePanel.isVisible = false
                binding.editor.requestFocus()
            }
            Mode.FIND -> {
                binding.defaultPanel.isVisible = false
                binding.findPanel.isVisible = true
                binding.replacePanel.isVisible = false
            }
            Mode.FIND_REPLACE -> {
                binding.defaultPanel.isVisible = false
                binding.findPanel.isVisible = true
                binding.replacePanel.isVisible = true
            }
        }
    }

    interface Listener {
        fun onDrawerButton()

        fun onNewButton(): Boolean
        fun onOpenButton(): Boolean
        fun onSaveButton(): Boolean
        fun onSaveAsButton(): Boolean
        fun onCloseButton(): Boolean

        fun onCutButton(): Boolean
        fun onCopyButton(): Boolean
        fun onPasteButton(): Boolean
        fun onSelectAllButton(): Boolean
        fun onSelectLineButton(): Boolean
        fun onDeleteLineButton(): Boolean
        fun onDuplicateLineButton(): Boolean
        fun onToggleCaseButton(): Boolean
        fun onPreviousWordButton(): Boolean
        fun onNextWordButton(): Boolean
        fun onStartOfLineButton(): Boolean
        fun onEndOfLineButton(): Boolean

        fun onOpenFindButton(): Boolean
        fun onCloseFindButton()
        fun onOpenReplaceButton(): Boolean
        fun onCloseReplaceButton()
        fun onGoToLineButton(): Boolean
        fun onReplaceButton(replaceText: String)
        fun onReplaceAllButton(replaceText: String)
        fun onNextResultButton()
        fun onPreviousResultButton()

        fun onFindQueryChanged(query: String)
        fun onFindRegexButton()
        fun onFindMatchCaseButton()
        fun onFindWordsOnlyButton()

        fun onForceSyntaxButton(): Boolean
        fun onInsertColorButton(): Boolean

        fun onUndoButton(): Boolean
        fun onRedoButton(): Boolean

        fun onSettingsButton(): Boolean
    }

    enum class Mode {
        DEFAULT,
        FIND,
        FIND_REPLACE,
    }
}