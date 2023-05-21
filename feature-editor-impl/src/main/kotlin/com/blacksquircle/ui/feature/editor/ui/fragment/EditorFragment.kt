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

package com.blacksquircle.ui.feature.editor.ui.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.blacksquircle.ui.core.adapter.TabAdapter
import com.blacksquircle.ui.core.contract.ContractResult
import com.blacksquircle.ui.core.contract.CreateFileContract
import com.blacksquircle.ui.core.contract.OpenFileContract
import com.blacksquircle.ui.core.delegate.viewBinding
import com.blacksquircle.ui.core.extensions.*
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.navigation.BackPressedHandler
import com.blacksquircle.ui.core.navigation.DrawerHandler
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.editorkit.*
import com.blacksquircle.ui.editorkit.plugin.autocomplete.codeCompletion
import com.blacksquircle.ui.editorkit.plugin.autoindent.autoIndentation
import com.blacksquircle.ui.editorkit.plugin.base.PluginSupplier
import com.blacksquircle.ui.editorkit.plugin.delimiters.highlightDelimiters
import com.blacksquircle.ui.editorkit.plugin.dirtytext.OnChangeListener
import com.blacksquircle.ui.editorkit.plugin.dirtytext.changeDetector
import com.blacksquircle.ui.editorkit.plugin.linenumbers.lineNumbers
import com.blacksquircle.ui.editorkit.plugin.pinchzoom.pinchZoom
import com.blacksquircle.ui.editorkit.plugin.shortcuts.OnShortcutListener
import com.blacksquircle.ui.editorkit.plugin.shortcuts.shortcuts
import com.blacksquircle.ui.editorkit.plugin.textscroller.textScroller
import com.blacksquircle.ui.editorkit.widget.TextScroller
import com.blacksquircle.ui.editorkit.widget.internal.UndoRedoEditText
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.data.utils.SettingsEvent
import com.blacksquircle.ui.feature.editor.databinding.FragmentEditorBinding
import com.blacksquircle.ui.feature.editor.ui.adapter.AutoCompleteAdapter
import com.blacksquircle.ui.feature.editor.ui.adapter.DocumentAdapter
import com.blacksquircle.ui.feature.editor.ui.adapter.TabController
import com.blacksquircle.ui.feature.editor.ui.manager.KeyboardManager
import com.blacksquircle.ui.feature.editor.ui.manager.ToolbarManager
import com.blacksquircle.ui.feature.editor.ui.mvi.*
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import com.blacksquircle.ui.feature.shortcuts.domain.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.domain.model.Shortcut
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.blacksquircle.ui.uikit.R as UiR

@AndroidEntryPoint
class EditorFragment : Fragment(R.layout.fragment_editor),
    BackPressedHandler, ToolbarManager.Listener, KeyboardManager.Listener {

    private val viewModel by activityViewModels<EditorViewModel>()
    private val binding by viewBinding(FragmentEditorBinding::bind)

    private val drawerHandler by lazy { parentFragment as DrawerHandler }
    private val toolbarManager by lazy { ToolbarManager(this) }
    private val keyboardManager by lazy { KeyboardManager(this) }
    private val tabController by lazy { TabController() }
    private val navController by lazy { findNavController() }
    private val newFileContract = CreateFileContract(this) { result ->
        when (result) {
            is ContractResult.Success -> viewModel.obtainEvent(EditorIntent.NewFile(result.uri))
            is ContractResult.Canceled -> Unit
        }
    }
    private val openFileContract = OpenFileContract(this) { result ->
        when (result) {
            is ContractResult.Success -> viewModel.obtainEvent(EditorIntent.OpenFileUri(result.uri))
            is ContractResult.Canceled -> Unit
        }
    }
    private val saveFileAsContract = CreateFileContract(this) { result ->
        when (result) {
            is ContractResult.Success -> viewModel.obtainEvent(EditorIntent.SaveFileAs(result.uri))
            is ContractResult.Canceled -> Unit
        }
    }

    private val onTabSelectedListener = object : TabAdapter.OnTabSelectedListener {
        override fun onTabUnselected(position: Int) = saveFile(local = false, unselected = true)
        override fun onTabSelected(position: Int) {
            viewModel.obtainEvent(EditorIntent.SelectTab(position))
        }
    }
    private val onTabMovedListener = object : TabAdapter.OnTabMovedListener {
        override fun onTabMoved(from: Int, to: Int) {
            viewModel.obtainEvent(EditorIntent.MoveTab(from, to))
        }
    }

    private lateinit var tabAdapter: DocumentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        view.applySystemWindowInsets(true) { _, top, _, bottom ->
            binding.toolbar.updatePadding(top = top)
            binding.root.updatePadding(bottom = bottom)
        }

        toolbarManager.bind(binding)
        keyboardManager.bind(binding)

        binding.tabLayout.setHasFixedSize(true)
        binding.tabLayout.adapter = DocumentAdapter(object : DocumentAdapter.TabInteractor {
            override fun close(position: Int) {
                viewModel.obtainEvent(EditorIntent.CloseTab(position, false))
            }
            override fun closeOthers(position: Int) {
                viewModel.obtainEvent(EditorIntent.CloseOthers(position))
            }
            override fun closeAll(position: Int) {
                viewModel.obtainEvent(EditorIntent.CloseAll)
            }
        }).also {
            this.tabAdapter = it
        }
        tabController.attachToRecyclerView(binding.tabLayout)

        binding.editor.freezesText = false
        binding.editor.onUndoRedoChangedListener = UndoRedoEditText.OnUndoRedoChangedListener {
            val canUndo = binding.editor.canUndo()
            val canRedo = binding.editor.canRedo()

            binding.actionUndo.isClickable = canUndo
            binding.actionRedo.isClickable = canRedo
            binding.actionUndo.imageAlpha = if (canUndo) ALPHA_FULL else ALPHA_SEMI
            binding.actionRedo.imageAlpha = if (canRedo) ALPHA_FULL else ALPHA_SEMI

            binding.keyboardToolUndo.isClickable = canUndo
            binding.keyboardToolRedo.isClickable = canRedo
            binding.keyboardToolUndo.imageAlpha = if (canUndo) ALPHA_FULL else ALPHA_SEMI
            binding.keyboardToolRedo.imageAlpha = if (canRedo) ALPHA_FULL else ALPHA_SEMI
        }

        binding.keyboardSwap.setOnClickListener {
            viewModel.obtainEvent(EditorIntent.SwapKeyboard)
        }

        viewModel.obtainEvent(EditorIntent.LoadSettings)
    }

    override fun onPause() {
        super.onPause()
        saveFile(local = false, unselected = false)
    }

    override fun handleOnBackPressed(): Boolean {
        if (toolbarManager.mode != ToolbarManager.Mode.DEFAULT) {
            onCloseFindButton()
            return true
        }
        return false
    }

    private fun observeViewModel() {
        viewModel.toolbarViewState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is ToolbarViewState.ActionBar -> {
                        tabAdapter.removeOnTabSelectedListener()
                        tabAdapter.removeOnTabMovedListener()
                        tabAdapter.submitList(state.documents, state.position)
                        tabAdapter.setOnTabSelectedListener(onTabSelectedListener)
                        tabAdapter.setOnTabMovedListener(onTabMovedListener)
                        toolbarManager.mode = state.mode
                        toolbarManager.params = state.findParams
                        if (state.mode == ToolbarManager.Mode.DEFAULT) {
                            binding.editor.clearFindResultSpans()
                        }
                        if (state.documents.getOrNull(state.position) != null) {
                            val document = state.documents[state.position]
                            binding.editor.language = document.language
                        }
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.editorViewState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is EditorViewState.Content -> {
                        binding.editor.isVisible = true
                        binding.scroller.isVisible = true
                        binding.errorView.root.isVisible = false
                        binding.loadingBar.isVisible = false

                        binding.scroller.state = TextScroller.State.HIDDEN
                        binding.editor.undoStack = state.content.undoStack
                        binding.editor.redoStack = state.content.redoStack
                        binding.editor.setTextContent(state.content.text)
                        binding.editor.abortFling()
                        binding.editor.scrollTo(
                            state.content.documentModel.scrollX,
                            state.content.documentModel.scrollY,
                        )
                        binding.editor.setSelectionRange(
                            state.content.documentModel.selectionStart,
                            state.content.documentModel.selectionEnd,
                        )
                        binding.editor.doOnPreDraw(View::requestFocus)
                    }
                    is EditorViewState.Error -> {
                        binding.editor.isInvisible = true
                        binding.scroller.isInvisible = true
                        binding.errorView.root.isVisible = true
                        binding.errorView.image.setImageResource(state.image)
                        binding.errorView.title.text = state.title
                        binding.errorView.subtitle.text = state.subtitle
                        when (state.action) {
                            is EditorErrorAction.Undefined -> {
                                binding.errorView.actionPrimary.isVisible = false
                                binding.errorView.actionPrimary.setOnClickListener(null)
                            }
                            is EditorErrorAction.CloseDocument -> {
                                binding.errorView.actionPrimary.isVisible = true
                                binding.errorView.actionPrimary.setText(R.string.action_close)
                                binding.errorView.actionPrimary.setOnClickListener {
                                    onCloseButton()
                                }
                            }
                        }
                        binding.loadingBar.isVisible = false
                        binding.editor.clearUndoHistory()
                    }
                    is EditorViewState.Loading -> {
                        binding.editor.isInvisible = true
                        binding.scroller.isInvisible = true
                        binding.errorView.root.isVisible = false
                        binding.loadingBar.isVisible = true
                        binding.editor.clearUndoHistory()
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.keyboardViewState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { keyboardManager.mode = it }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.viewEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { event ->
                when (event) {
                    is ViewEvent.Toast -> context?.showToast(text = event.message)
                    is ViewEvent.Navigation -> navController.navigate(event.screen)
                    is EditorViewEvent.FindResults -> binding.editor.find(event.results)
                    is EditorViewEvent.InsertColor -> binding.editor.insert(event.color)
                    is EditorViewEvent.GotoLine -> try {
                        binding.editor.gotoLine(event.line)
                    } catch (e: Exception) {
                        context?.showToast(R.string.message_line_not_exists)
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.settings.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { settings -> applySettings(settings) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    // region TOOLBAR

    override fun onDrawerButton() {
        drawerHandler.openDrawer()
    }

    override fun onNewButton(): Boolean {
        newFileContract.launch(getString(UiR.string.common_untitled), CreateFileContract.TEXT)
        return true
    }

    override fun onOpenButton(): Boolean {
        openFileContract.launch(OpenFileContract.ANY)
        return true
    }

    override fun onSaveButton(): Boolean {
        saveFile(local = true, unselected = false)
        return true
    }

    override fun onSaveAsButton(): Boolean {
        val position = tabAdapter.selectedPosition
        if (position > -1) {
            val documentModel = tabAdapter.currentList[position]
            saveFileAsContract.launch(
                documentModel.name,
                documentModel.mimeType,
            )
        }
        return true
    }

    override fun onCloseButton(): Boolean {
        viewModel.obtainEvent(EditorIntent.CloseTab(tabAdapter.selectedPosition, false))
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
        if (position > -1 && binding.editor.hasPrimaryClip()) {
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

    override fun onToggleCaseButton(): Boolean {
        binding.editor.toggleCase()
        return true
    }

    override fun onPreviousWordButton(): Boolean {
        binding.editor.moveCaretToPrevWord()
        return true
    }

    override fun onNextWordButton(): Boolean {
        binding.editor.moveCaretToNextWord()
        return true
    }

    override fun onStartOfLineButton(): Boolean {
        binding.editor.moveCaretToStartOfLine()
        return true
    }

    override fun onEndOfLineButton(): Boolean {
        binding.editor.moveCaretToEndOfLine()
        return true
    }

    override fun onOpenFindButton(): Boolean {
        viewModel.obtainEvent(EditorIntent.PanelFind)
        return true
    }

    override fun onCloseFindButton() {
        viewModel.obtainEvent(EditorIntent.PanelDefault)
    }

    override fun onOpenReplaceButton(): Boolean {
        viewModel.obtainEvent(EditorIntent.PanelFindReplace)
        return true
    }

    override fun onCloseReplaceButton() {
        viewModel.obtainEvent(EditorIntent.PanelFind)
    }

    override fun onGoToLineButton(): Boolean {
        viewModel.obtainEvent(EditorIntent.GotoLine)
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

    override fun onFindQueryChanged(query: String) {
        viewModel.obtainEvent(EditorIntent.FindQuery(binding.editor.text, query))
    }

    override fun onFindRegexButton() {
        viewModel.obtainEvent(EditorIntent.FindRegex(binding.editor.text))
    }

    override fun onFindMatchCaseButton() {
        viewModel.obtainEvent(EditorIntent.FindMatchCase(binding.editor.text))
    }

    override fun onFindWordsOnlyButton() {
        viewModel.obtainEvent(EditorIntent.FindWordsOnly(binding.editor.text))
    }

    override fun onForceSyntaxButton(): Boolean {
        viewModel.obtainEvent(EditorIntent.ForceSyntax)
        return true
    }

    override fun onInsertColorButton(): Boolean {
        viewModel.obtainEvent(EditorIntent.ColorPicker)
        return true
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

    override fun onKeyButton(char: Char): Boolean {
        activity?.focusedTextField()?.insert(char.toString())
        return true
    }

    // endregion TOOLBAR

    private fun saveFile(local: Boolean, unselected: Boolean) {
        if (!binding.editor.isVisible) return
        val action = EditorIntent.SaveFile(
            local = local,
            unselected = unselected,
            text = binding.editor.text,
            undoStack = binding.editor.undoStack,
            redoStack = binding.editor.redoStack,
            scrollX = binding.editor.scrollX,
            scrollY = binding.editor.scrollY,
            selectionStart = binding.editor.selectionStart,
            selectionEnd = binding.editor.selectionEnd,
        )
        viewModel.obtainEvent(action)
    }

    private fun applySettings(settings: List<SettingsEvent<*>>) {
        val pluginSupplier = PluginSupplier.create {
            settings.forEach { event ->
                when (event) {
                    is SettingsEvent.ColorScheme ->
                        binding.editor.colorScheme = event.value.colorScheme
                    is SettingsEvent.FontSize -> binding.editor.textSize = event.value
                    is SettingsEvent.FontType -> binding.editor.typeface = requireContext()
                        .createTypefaceFromPath(event.value.fontPath)
                    is SettingsEvent.WordWrap ->
                        binding.editor.setHorizontallyScrolling(!event.value)
                    is SettingsEvent.CodeCompletion -> if (event.value) {
                        codeCompletion {
                            suggestionAdapter = AutoCompleteAdapter(
                                requireContext(),
                                binding.editor.colorScheme,
                            )
                        }
                    }
                    is SettingsEvent.PinchZoom -> if (event.value) pinchZoom()
                    is SettingsEvent.LineNumbers -> lineNumbers {
                        lineNumbers = event.value.first
                        highlightCurrentLine = event.value.second
                    }
                    is SettingsEvent.Delimiters -> if (event.value) highlightDelimiters()
                    is SettingsEvent.ReadOnly -> binding.editor.readOnly = event.value
                    is SettingsEvent.KeyboardPreset -> keyboardManager.submitList(event.value)
                    is SettingsEvent.SoftKeys -> binding.editor.softKeyboard = event.value
                    is SettingsEvent.AutoIndentation -> autoIndentation {
                        autoIndentLines = event.value.first
                        autoCloseBrackets = event.value.second
                        autoCloseQuotes = event.value.third
                    }
                    is SettingsEvent.UseSpacesNotTabs ->
                        binding.editor.useSpacesInsteadOfTabs = event.value
                    is SettingsEvent.TabWidth -> binding.editor.tabWidth = event.value
                    is SettingsEvent.Keybindings -> shortcuts {
                        onShortcutListener = OnShortcutListener { (ctrl, shift, alt, keyCode) ->
                            when (hasShortcut(ctrl, shift, alt, keyCode, event.value)) {
                                Shortcut.NEW -> onNewButton()
                                Shortcut.OPEN -> onOpenButton()
                                Shortcut.SAVE -> onSaveButton()
                                Shortcut.SAVE_AS -> onSaveAsButton()
                                Shortcut.CLOSE -> onCloseButton()
                                Shortcut.CUT -> onCutButton()
                                Shortcut.COPY -> onCopyButton()
                                Shortcut.PASTE -> onPasteButton()
                                Shortcut.SELECT_ALL -> onSelectAllButton()
                                Shortcut.SELECT_LINE -> onSelectLineButton()
                                Shortcut.DELETE_LINE -> onDeleteLineButton()
                                Shortcut.DUPLICATE_LINE -> onDuplicateLineButton()
                                Shortcut.TOGGLE_CASE -> onToggleCaseButton()
                                Shortcut.PREV_WORD -> onPreviousWordButton()
                                Shortcut.NEXT_WORD -> onNextWordButton()
                                Shortcut.START_OF_LINE -> onStartOfLineButton()
                                Shortcut.END_OF_LINE -> onEndOfLineButton()
                                Shortcut.UNDO -> onUndoButton()
                                Shortcut.REDO -> onRedoButton()
                                Shortcut.FIND -> onOpenFindButton()
                                Shortcut.REPLACE -> onOpenReplaceButton()
                                Shortcut.GOTO_LINE -> onGoToLineButton()
                                Shortcut.FORCE_SYNTAX -> onForceSyntaxButton()
                                Shortcut.INSERT_COLOR -> onInsertColorButton()
                                else -> when (keyCode) {
                                    KeyEvent.KEYCODE_TAB -> onKeyButton('\t')
                                    else -> false
                                }
                            }
                        }
                        shortcutKeyFilter = listOf(KeyEvent.KEYCODE_TAB)
                    }
                }
            }
            textScroller {
                scroller = binding.scroller
            }
            changeDetector {
                onChangeListener = OnChangeListener {
                    viewModel.obtainEvent(EditorIntent.ModifyContent)
                }
            }
        }
        binding.editor.plugins(pluginSupplier)
    }

    private fun hasShortcut(
        ctrl: Boolean,
        shift: Boolean,
        alt: Boolean,
        keyCode: Int,
        data: List<Keybinding>,
    ): Shortcut? {
        val char = keyCode.keyCodeToChar()
        val keybinding = data.find {
            it.key == char.uppercaseChar() &&
                it.isCtrl == ctrl &&
                it.isShift == shift &&
                it.isAlt == alt
        }
        return keybinding?.shortcut
    }

    companion object {
        private const val ALPHA_FULL = 255
        private const val ALPHA_SEMI = 90
    }
}