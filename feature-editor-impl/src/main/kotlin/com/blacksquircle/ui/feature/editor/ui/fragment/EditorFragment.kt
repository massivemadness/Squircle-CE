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
import androidx.core.text.PrecomputedTextCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.blacksquircle.ui.core.ui.adapter.TabAdapter
import com.blacksquircle.ui.core.ui.contract.ContractResult
import com.blacksquircle.ui.core.ui.contract.CreateFileContract
import com.blacksquircle.ui.core.ui.delegate.viewBinding
import com.blacksquircle.ui.core.ui.extensions.*
import com.blacksquircle.ui.core.ui.navigation.BackPressedHandler
import com.blacksquircle.ui.core.ui.navigation.DrawerHandler
import com.blacksquircle.ui.core.ui.navigation.Screen
import com.blacksquircle.ui.core.ui.viewstate.ViewEvent
import com.blacksquircle.ui.editorkit.*
import com.blacksquircle.ui.editorkit.model.FindParams
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
import com.blacksquircle.ui.feature.editor.data.utils.Panel
import com.blacksquircle.ui.feature.editor.data.utils.SettingsEvent
import com.blacksquircle.ui.feature.editor.data.utils.TabController
import com.blacksquircle.ui.feature.editor.data.utils.ToolbarManager
import com.blacksquircle.ui.feature.editor.databinding.FragmentEditorBinding
import com.blacksquircle.ui.feature.editor.ui.adapter.AutoCompleteAdapter
import com.blacksquircle.ui.feature.editor.ui.adapter.DocumentAdapter
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorIntent
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewEvent
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import com.blacksquircle.ui.feature.editor.ui.viewstate.DocumentViewState
import com.blacksquircle.ui.feature.editor.ui.viewstate.EditorViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class EditorFragment : Fragment(R.layout.fragment_editor),
    BackPressedHandler, ToolbarManager.OnPanelClickListener {

    private val viewModel by activityViewModels<EditorViewModel>()
    private val binding by viewBinding(FragmentEditorBinding::bind)

    private val drawerHandler by lazy { parentFragment as DrawerHandler }
    private val toolbarManager by lazy { ToolbarManager(this) }
    private val tabController by lazy { TabController() }
    private val navController by lazy { findNavController() }

    private val createFileContract = CreateFileContract(this) { result ->
        when (result) {
            is ContractResult.Success -> viewModel.obtainEvent(EditorIntent.SaveFileAs(result.uri))
            is ContractResult.Canceled -> Unit
        }
    }

    private val onTabSelectedListener = object : TabAdapter.OnTabSelectedListener {
        override fun onTabUnselected(position: Int) = saveFile()
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

        binding.extendedKeyboard.setKeyListener(binding.editor::insert)
        binding.extendedKeyboard.setHasFixedSize(true)

        binding.editor.freezesText = false
        binding.editor.onUndoRedoChangedListener = UndoRedoEditText.OnUndoRedoChangedListener {
            val canUndo = binding.editor.canUndo()
            val canRedo = binding.editor.canRedo()

            binding.actionUndo.isClickable = canUndo
            binding.actionRedo.isClickable = canRedo

            binding.actionUndo.imageAlpha = if (canUndo) ALPHA_FULL else ALPHA_SEMI
            binding.actionRedo.imageAlpha = if (canRedo) ALPHA_FULL else ALPHA_SEMI
        }

        binding.actionTab.setOnClickListener {
            binding.editor.insert(binding.editor.tab())
        }

        viewModel.obtainEvent(EditorIntent.LoadSettings)
    }

    override fun onPause() {
        super.onPause()
        saveFile()
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
                        tabAdapter.removeOnTabMovedListener()
                        tabAdapter.submitList(state.documents, state.position)
                        tabAdapter.setOnTabSelectedListener(onTabSelectedListener)
                        tabAdapter.setOnTabMovedListener(onTabMovedListener)
                        toolbarManager.panel = state.panel
                        if (state.panel == Panel.DEFAULT) {
                            binding.editor.clearFindResultSpans()
                        }
                    }
                    is EditorViewState.Stub -> Unit
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.documentViewState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is DocumentViewState.Content -> {
                        val measurement = withContext(Dispatchers.Default) {
                            val textMetrics = TextViewCompat.getTextMetricsParams(binding.editor)
                            PrecomputedTextCompat.create(state.content.text, textMetrics)
                        }
                        binding.editor.isVisible = true
                        binding.scroller.isVisible = true
                        binding.keyboard.isVisible = state.showKeyboard
                        binding.errorView.root.isVisible = false
                        binding.loadingBar.isVisible = false

                        binding.scroller.state = TextScroller.State.HIDDEN
                        binding.editor.language = state.content.documentModel.language
                        binding.editor.undoStack = state.content.undoStack
                        binding.editor.redoStack = state.content.redoStack
                        binding.editor.setTextContent(measurement)
                        binding.editor.scrollX = state.content.documentModel.scrollX
                        binding.editor.scrollY = state.content.documentModel.scrollY
                        binding.editor.setSelectionRange(
                            state.content.documentModel.selectionStart,
                            state.content.documentModel.selectionEnd,
                        )
                        binding.editor.doOnPreDraw(View::requestFocus)
                    }
                    is DocumentViewState.Error -> {
                        binding.editor.isInvisible = true
                        binding.scroller.isInvisible = true
                        binding.keyboard.isVisible = false
                        binding.errorView.root.isVisible = true
                        binding.errorView.image.setImageResource(state.image)
                        binding.errorView.title.text = state.title
                        binding.errorView.subtitle.text = state.subtitle
                        binding.errorView.actionPrimary.isVisible = false
                        binding.loadingBar.isVisible = false
                        binding.editor.clearUndoHistory()
                    }
                    is DocumentViewState.Loading -> {
                        binding.editor.isInvisible = true
                        binding.scroller.isInvisible = true
                        binding.keyboard.isVisible = false
                        binding.errorView.root.isVisible = false
                        binding.loadingBar.isVisible = true
                        binding.editor.clearUndoHistory()
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.viewEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { event ->
                when (event) {
                    is ViewEvent.Toast -> context?.showToast(text = event.message)
                    is ViewEvent.Navigation -> navController.navigate(event.screen)
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

    override fun onNewButton() {
        onDrawerButton() // TODO 27/02/21 Add Dialog
    }

    override fun onOpenButton() {
        onDrawerButton()
        context?.showToast(R.string.message_select_file)
    }

    override fun onSaveButton(): Boolean {
        saveFile(local = true)
        return true
    }

    override fun onSaveAsButton(): Boolean {
        val position = tabAdapter.selectedPosition
        if (position > -1) {
            val documentModel = tabAdapter.currentList[position]
            createFileContract.launch(
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

    override fun onFindParamsChanged(params: FindParams) {
        binding.editor.clearFindResultSpans()
        binding.editor.find(params)
    }

    override fun onInsertColorButton() {
        viewModel.obtainEvent(EditorIntent.ColorPicker)
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

    private fun saveFile(local: Boolean = false) {
        if (!binding.editor.isVisible) return
        val action = EditorIntent.SaveFile(
            local = local,
            text = binding.editor.text.toString(),
            undoStack = binding.editor.undoStack.clone(),
            redoStack = binding.editor.redoStack.clone(),
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
                    is SettingsEvent.ThemePref ->
                        binding.editor.colorScheme = event.value.colorScheme
                    is SettingsEvent.FontSize -> binding.editor.textSize = event.value
                    is SettingsEvent.FontType -> binding.editor.typeface = requireContext()
                        .createTypefaceFromPath(event.value)
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
                    is SettingsEvent.KeyboardPreset ->
                        binding.extendedKeyboard.submitList(event.value)
                    is SettingsEvent.SoftKeys -> binding.editor.softKeyboard = event.value
                    is SettingsEvent.AutoIndentation -> autoIndentation {
                        autoIndentLines = event.value.first
                        autoCloseBrackets = event.value.second
                        autoCloseQuotes = event.value.third
                    }
                    is SettingsEvent.UseSpacesNotTabs ->
                        binding.editor.useSpacesInsteadOfTabs = event.value
                    is SettingsEvent.TabWidth -> binding.editor.tabWidth = event.value
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
                        keyCode == KeyEvent.KEYCODE_TAB -> {
                            binding.editor.insert(binding.editor.tab()); true
                        }
                        else -> false
                    }
                }
                shortcutKeyFilter = listOf(KeyEvent.KEYCODE_TAB)
            }
        }
        binding.editor.plugins(pluginSupplier)
    }

    companion object {
        private const val ALPHA_FULL = 255
        private const val ALPHA_SEMI = 90
    }
}