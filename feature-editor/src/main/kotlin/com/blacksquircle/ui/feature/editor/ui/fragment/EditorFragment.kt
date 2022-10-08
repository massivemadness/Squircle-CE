/*
 * Copyright 2022 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.editor.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blacksquircle.ui.core.ui.adapter.TabAdapter
import com.blacksquircle.ui.core.ui.delegate.viewBinding
import com.blacksquircle.ui.core.ui.extensions.applySystemWindowInsets
import com.blacksquircle.ui.core.ui.extensions.navigate
import com.blacksquircle.ui.core.ui.extensions.showToast
import com.blacksquircle.ui.core.ui.extensions.toHexString
import com.blacksquircle.ui.core.ui.navigation.BackPressedHandler
import com.blacksquircle.ui.core.ui.navigation.DrawerHandler
import com.blacksquircle.ui.core.ui.navigation.Screen
import com.blacksquircle.ui.core.ui.viewstate.ViewEvent
import com.blacksquircle.ui.editorkit.*
import com.blacksquircle.ui.editorkit.exception.LineException
import com.blacksquircle.ui.editorkit.model.FindParams
import com.blacksquircle.ui.editorkit.widget.internal.UndoRedoEditText
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.data.utils.Panel
import com.blacksquircle.ui.feature.editor.data.utils.TabController
import com.blacksquircle.ui.feature.editor.data.utils.ToolbarManager
import com.blacksquircle.ui.feature.editor.databinding.FragmentEditorBinding
import com.blacksquircle.ui.feature.editor.domain.model.DocumentContent
import com.blacksquircle.ui.feature.editor.domain.model.DocumentParams
import com.blacksquircle.ui.feature.editor.ui.adapter.DocumentAdapter
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorIntent
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import com.blacksquircle.ui.feature.editor.ui.viewstate.DocumentViewState
import com.blacksquircle.ui.feature.editor.ui.viewstate.EditorViewState
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class EditorFragment : Fragment(R.layout.fragment_editor), BackPressedHandler,
    ToolbarManager.OnPanelClickListener {

    private val viewModel by activityViewModels<EditorViewModel>()
    private val binding by viewBinding(FragmentEditorBinding::bind)

    private val drawerHandler by lazy { parentFragment as DrawerHandler }
    private val toolbarManager by lazy { ToolbarManager(this) }
    private val tabController by lazy { TabController() }
    private val navController by lazy { findNavController() }

    private val onTabSelectedListener = object : TabAdapter.OnTabSelectedListener {
        override fun onTabSelected(position: Int) {
            viewModel.obtainEvent(EditorIntent.SelectTab(position))
        }
    }

    private lateinit var tabAdapter: DocumentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        view.applySystemWindowInsets(true) { _, top, _, bottom ->
            binding.toolbar.updatePadding(top = top)
            binding.errorView.root.updatePadding(bottom = bottom)
            binding.loadingBar.updatePadding(bottom = bottom)
            if (!binding.keyboard.isVisible) {
                binding.editor.updatePadding(bottom = bottom)
                binding.scroller.updatePadding(bottom = bottom)
            } else {
                binding.keyboard.updatePadding(bottom = bottom)
            }
        }

        toolbarManager.bind(binding)

        binding.tabLayout.setHasFixedSize(true)
        binding.tabLayout.adapter = DocumentAdapter(object : DocumentAdapter.TabInteractor {
            override fun close(position: Int) {
                viewModel.obtainEvent(EditorIntent.CloseTab(position))
            }
            override fun closeOthers(position: Int) {
                viewModel.obtainEvent(EditorIntent.CloseOthers(position))
            }
            override fun closeAll(position: Int) {
                viewModel.obtainEvent(EditorIntent.CloseAll(position))
            }
        }).also {
            this.tabAdapter = it
        }
        tabController.attachToRecyclerView(binding.tabLayout)

        binding.extendedKeyboard.setKeyListener(binding.editor::insert)
        binding.extendedKeyboard.setHasFixedSize(true)

        binding.editor.onUndoRedoChangedListener = UndoRedoEditText.OnUndoRedoChangedListener {
            val canUndo = binding.editor.canUndo()
            val canRedo = binding.editor.canRedo()

            binding.actionUndo.isClickable = canUndo
            binding.actionRedo.isClickable = canRedo

            binding.actionUndo.imageAlpha = if (canUndo) ALPHA_FULL else ALPHA_SEMI
            binding.actionRedo.imageAlpha = if (canRedo) ALPHA_FULL else ALPHA_SEMI
        }

        binding.editor.clearText()

        binding.actionTab.setOnClickListener {
            binding.editor.insert(binding.editor.tab())
        }
    }

    override fun handleOnBackPressed(): Boolean {
        if (toolbarManager.panel != Panel.DEFAULT) {
            onCloseFindButton()
            return true
        }
        return false
    }

    private fun observeViewModel() {
        viewModel.editorViewState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is EditorViewState.ActionBar -> {
                        tabAdapter.removeOnTabSelectedListener()
                        tabAdapter.submitList(state.documents)
                        tabAdapter.select(state.position)
                        tabAdapter.setOnTabSelectedListener(onTabSelectedListener)
                    }
                    is EditorViewState.Stub -> Unit
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.documentViewState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is DocumentViewState.Content -> {
                        binding.editor.isVisible = true
                        binding.scroller.isVisible = true
                        binding.errorView.root.isVisible = false
                        binding.loadingBar.isVisible = false

                        binding.editor.setTextContent(state.content.text)
                    }
                    is DocumentViewState.Error -> {
                        binding.editor.isVisible = false
                        binding.scroller.isVisible = false
                        binding.errorView.root.isVisible = true
                        binding.errorView.image.setImageResource(state.image)
                        binding.errorView.title.text = state.title
                        binding.errorView.subtitle.text = state.subtitle
                        binding.errorView.actionPrimary.isVisible = false
                        binding.loadingBar.isVisible = false
                    }
                    is DocumentViewState.Loading -> {
                        binding.editor.isVisible = false
                        binding.scroller.isVisible = false
                        binding.errorView.root.isVisible = false
                        binding.loadingBar.isVisible = true
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.viewEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { event ->
                when (event) {
                    is ViewEvent.Toast -> context?.showToast(text = event.message)
                    is ViewEvent.Navigation -> navController.navigate(event.screen)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        /*viewModel.settingsEvent.observe(viewLifecycleOwner) { settings ->
            val pluginSupplier = PluginSupplier.create {
                settings.forEach { event ->
                    when (event) {
                        is SettingsEvent.ThemePref ->
                            binding.editor.colorScheme = event.value.colorScheme
                        is SettingsEvent.FontSize -> binding.editor.textSize = event.value
                        is SettingsEvent.FontType -> binding.editor.typeface = requireContext()
                            .createTypefaceFromPath(event.value)
                        is SettingsEvent.WordWrap ->
                            binding.editor.setHorizontallyScrolling(!event.value)
                        is SettingsEvent.CodeCompletion -> if (event.value) codeCompletion {
                            suggestionAdapter = AutoCompleteAdapter(
                                requireContext(),
                                binding.editor.colorScheme
                            )
                        }
                        is SettingsEvent.ErrorHighlight -> {
                            if (event.value) {
                                binding.editor.debounce(
                                    coroutineScope = viewLifecycleOwner.lifecycleScope,
                                    waitMs = 1500
                                ) { text ->
                                    if (text.isNotEmpty()) {
                                        val position = adapter.selectedPosition
                                        if (position > -1) {
                                            *//*viewModel.parse(
                                                adapter.currentList[position],
                                                binding.editor.language,
                                                binding.editor.text.toString()
                                            )*//*
                                        }
                                    }
                                }
                            }
                        }
                        is SettingsEvent.PinchZoom -> if (event.value) pinchZoom()
                        is SettingsEvent.LineNumbers -> lineNumbers {
                            lineNumbers = event.value.first
                            highlightCurrentLine = event.value.second
                        }
                        is SettingsEvent.Delimiters -> if (event.value) highlightDelimiters()
                        is SettingsEvent.ExtendedKeys -> {
                            binding.keyboard.isVisible = event.value
                        }
                        is SettingsEvent.KeyboardPreset ->
                            binding.extendedKeyboard.submitList(event.value)
                        is SettingsEvent.SoftKeys -> binding.editor.softKeyboard = event.value
                        is SettingsEvent.AutoIndentation -> autoIndentation {
                            autoIndentLines = event.value.first
                            autoCloseBrackets = event.value.second
                            autoCloseQuotes = event.value.third
                        }
                        is SettingsEvent.UseSpacesNotTabs -> binding.editor.useSpacesInsteadOfTabs = event.value
                        is SettingsEvent.TabWidth -> binding.editor.tabWidth = event.value
                    }
                }
                textScroller {
                    scroller = binding.scroller
                }
                changeDetector {
                    onChangeListener = OnChangeListener {
                        val position = adapter.selectedPosition
                        if (position > -1) {
                            val isModified = adapter.currentList[position].modified
                            if (!isModified) {
                                adapter.currentList[position].modified = true
                                adapter.notifyItemChanged(position)
                            }
                        }
                    }
                }
                shortcuts {
                    onShortcutListener = OnShortcutListener { (ctrl, shift, alt, keyCode) ->
                        when {
                            ctrl && shift && keyCode == KeyEvent.KEYCODE_Z -> onUndoButton()
                            ctrl && shift && keyCode == KeyEvent.KEYCODE_S -> onSaveAsButton()
                            ctrl && keyCode == KeyEvent.KEYCODE_X -> onCutButton()
                            ctrl && keyCode == KeyEvent.KEYCODE_C -> onCopyButton()
                            ctrl && keyCode == KeyEvent.KEYCODE_V -> onPasteButton()
                            ctrl && keyCode == KeyEvent.KEYCODE_A -> onSelectAllButton()
                            ctrl && keyCode == KeyEvent.KEYCODE_DEL -> onDeleteLineButton()
                            ctrl && keyCode == KeyEvent.KEYCODE_D -> onDuplicateLineButton()
                            ctrl && keyCode == KeyEvent.KEYCODE_Z -> onUndoButton()
                            ctrl && keyCode == KeyEvent.KEYCODE_Y -> onRedoButton()
                            ctrl && keyCode == KeyEvent.KEYCODE_S -> onSaveButton()
                            ctrl && keyCode == KeyEvent.KEYCODE_W -> onCloseButton()
                            ctrl && keyCode == KeyEvent.KEYCODE_F -> onOpenFindButton()
                            ctrl && keyCode == KeyEvent.KEYCODE_R -> onOpenReplaceButton()
                            ctrl && keyCode == KeyEvent.KEYCODE_G -> onGoToLineButton()
                            ctrl && keyCode == KeyEvent.KEYCODE_DPAD_LEFT -> binding.editor.moveCaretToStartOfLine()
                            ctrl && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT -> binding.editor.moveCaretToEndOfLine()
                            alt && keyCode == KeyEvent.KEYCODE_DPAD_LEFT -> binding.editor.moveCaretToPrevWord()
                            alt && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT -> binding.editor.moveCaretToNextWord()
                            alt && keyCode == KeyEvent.KEYCODE_A -> onSelectLineButton()
                            alt && keyCode == KeyEvent.KEYCODE_S -> onSettingsButton()
                            keyCode == KeyEvent.KEYCODE_TAB -> { binding.editor.insert(binding.editor.tab()); true }
                            else -> false
                        }
                    }
                    shortcutKeyFilter = listOf(KeyEvent.KEYCODE_TAB)
                }
            }
            binding.editor.plugins(pluginSupplier)
        }*/
    }

    // region TOOLBAR

    override fun onDrawerButton() {
        drawerHandler.openDrawer()
    }

    override fun onNewButton() {
        onDrawerButton() // TODO 27/02/21 Add Dialog
    }

    override fun onOpenButton() {
        onDrawerButton()
        context?.showToast(R.string.message_select_file)
    }

    override fun onSaveButton(): Boolean {
        val position = tabAdapter.selectedPosition
        if (position > -1) {
            val isModified = tabAdapter.currentList[position].modified
            if (isModified) {
                tabAdapter.currentList[position].modified = false
                tabAdapter.notifyItemChanged(position)
            }

            val documentContent = DocumentContent(
                documentModel = tabAdapter.currentList[position],
                language = binding.editor.language,
                undoStack = binding.editor.undoStack.clone(),
                redoStack = binding.editor.redoStack.clone(),
                text = binding.editor.text.toString()
            )

            val params = DocumentParams(
                local = true,
                cache = true
            )
            // viewModel.saveFile(documentContent, params)
        } else {
            context?.showToast(R.string.message_no_open_files)
        }
        return true
    }

    override fun onSaveAsButton(): Boolean {
        val position = tabAdapter.selectedPosition
        if (position > -1) {
            val document = tabAdapter.currentList[position]
            MaterialDialog(requireContext()).show {
                title(R.string.dialog_title_save_as)
                customView(R.layout.dialog_save_as, scrollable = true)
                negativeButton(R.string.action_cancel)
                positiveButton(R.string.action_save) {
                    val enterFilePath = findViewById<TextInputEditText>(R.id.input)
                    val filePath = enterFilePath.text?.toString()?.trim()
                    if (!filePath.isNullOrBlank()) {
                        val updateDocument = document.copy(
                            uuid = "whatever",
                            fileUri = document.scheme + filePath
                        )
                        val documentContent = DocumentContent(
                            documentModel = updateDocument,
                            language = binding.editor.language,
                            undoStack = binding.editor.undoStack.clone(),
                            redoStack = binding.editor.redoStack.clone(),
                            text = binding.editor.text.toString()
                        )
                        val params = DocumentParams(
                            local = true,
                            cache = false
                        )
                        // viewModel.saveFile(documentContent, params)
                    } else {
                        context.showToast(R.string.message_invalid_file_path)
                    }
                }
                val enterFilePath = findViewById<TextInputEditText>(R.id.input)
                enterFilePath.setText(document.path)
            }
        } else {
            context?.showToast(R.string.message_no_open_files)
        }
        return true
    }

    override fun onCloseButton(): Boolean {
        val position = tabAdapter.selectedPosition
        if (position > -1) {
            // close(position)
        } else {
            context?.showToast(R.string.message_no_open_files)
        }
        return true
    }

    override fun onCutButton(): Boolean {
        if (binding.editor.hasSelection()) {
            binding.editor.cut()
        } else {
            context?.showToast(R.string.message_nothing_to_cut)
        }
        return true
    }

    override fun onCopyButton(): Boolean {
        if (binding.editor.hasSelection()) {
            binding.editor.copy()
        } else {
            context?.showToast(R.string.message_nothing_to_copy)
        }
        return true
    }

    override fun onPasteButton(): Boolean {
        val position = tabAdapter.selectedPosition
        if (binding.editor.hasPrimaryClip() && position > -1) {
            binding.editor.paste()
        } else {
            context?.showToast(R.string.message_nothing_to_paste)
        }
        return true
    }

    override fun onSelectAllButton(): Boolean {
        binding.editor.selectAll()
        return true
    }

    override fun onSelectLineButton(): Boolean {
        binding.editor.selectLine()
        return true
    }

    override fun onDeleteLineButton(): Boolean {
        binding.editor.deleteLine()
        return true
    }

    override fun onDuplicateLineButton(): Boolean {
        binding.editor.duplicateLine()
        return true
    }

    override fun onOpenFindButton(): Boolean {
        val position = tabAdapter.selectedPosition
        if (position > -1) {
            toolbarManager.panel = Panel.FIND
        } else {
            context?.showToast(R.string.message_no_open_files)
        }
        return true
    }

    override fun onCloseFindButton() {
        toolbarManager.panel = Panel.DEFAULT
        binding.inputFind.setText("")
        binding.editor.clearFindResultSpans()
    }

    override fun onOpenReplaceButton(): Boolean {
        val position = tabAdapter.selectedPosition
        if (position > -1) {
            toolbarManager.panel = Panel.FIND_REPLACE
        } else {
            context?.showToast(R.string.message_no_open_files)
        }
        return true
    }

    override fun onCloseReplaceButton() {
        toolbarManager.panel = Panel.FIND
        binding.inputReplace.setText("")
    }

    override fun onGoToLineButton(): Boolean {
        val position = tabAdapter.selectedPosition
        if (position > -1) {
            MaterialDialog(requireContext()).show {
                title(R.string.dialog_title_goto_line)
                customView(R.layout.dialog_goto_line)
                negativeButton(R.string.action_cancel)
                positiveButton(R.string.action_go_to) {
                    val input = getCustomView().findViewById<TextInputEditText>(R.id.input)
                    val inputNumber = input.text.toString()
                    try {
                        val lineNumber = inputNumber.toIntOrNull() ?: 0
                        binding.editor.gotoLine(lineNumber)
                    } catch (e: LineException) {
                        context.showToast(R.string.message_line_not_exists)
                    }
                }
            }
        } else {
            context?.showToast(R.string.message_no_open_files)
        }
        return true
    }

    override fun onReplaceButton(replaceText: String) {
        binding.editor.replaceFindResult(replaceText)
    }

    override fun onReplaceAllButton(replaceText: String) {
        binding.editor.replaceAllFindResults(replaceText)
    }

    override fun onNextResultButton() {
        binding.editor.findNext()
    }

    override fun onPreviousResultButton() {
        binding.editor.findPrevious()
    }

    override fun onFindParamsChanged(params: FindParams) {
        binding.editor.clearFindResultSpans()
        binding.editor.find(params)
    }

    override fun onErrorCheckingButton() {
        val position = tabAdapter.selectedPosition
        if (position > -1) {
            MaterialDialog(requireContext()).show {
                title(R.string.dialog_title_result)
                message(R.string.message_no_errors_detected)
                /*viewModel.parseEvent.value?.let { model ->
                    model.exception?.let {
                        message(text = it.message)
                        binding.editor.setErrorLine(it.lineNumber)
                    }
                }*/
                positiveButton(R.string.action_ok)
            }
        } else {
            context?.showToast(R.string.message_no_open_files)
        }
    }

    @SuppressLint("CheckResult")
    override fun onInsertColorButton() {
        val position = tabAdapter.selectedPosition
        if (position > -1) {
            MaterialDialog(requireContext()).show {
                title(R.string.dialog_title_color_picker)
                colorChooser(
                    colors = ColorPalette.Primary,
                    subColors = ColorPalette.PrimarySub,
                    allowCustomArgb = true,
                    showAlphaSelector = true
                ) { _, color ->
                    binding.editor.insert(color.toHexString())
                }
                positiveButton(R.string.action_insert)
                negativeButton(R.string.action_cancel)
            }
        } else {
            context?.showToast(R.string.message_no_open_files)
        }
    }

    override fun onUndoButton(): Boolean {
        if (binding.editor.canUndo()) {
            binding.editor.undo()
        }
        return true
    }

    override fun onRedoButton(): Boolean {
        if (binding.editor.canRedo()) {
            binding.editor.redo()
        }
        return true
    }

    override fun onSettingsButton(): Boolean {
        navController.navigate(Screen.Settings)
        return true
    }

    // endregion TOOLBAR

    companion object {
        private const val ALPHA_FULL = 255
        private const val ALPHA_SEMI = 90
        private const val TAB_LIMIT = 10
    }
}