/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.feature.editor.fragments

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.brackeys.ui.R
import com.brackeys.ui.data.converter.DocumentConverter
import com.brackeys.ui.data.utils.toHexString
import com.brackeys.ui.databinding.FragmentEditorBinding
import com.brackeys.ui.domain.model.editor.DocumentContent
import com.brackeys.ui.editorkit.exception.LineException
import com.brackeys.ui.editorkit.listener.OnChangeListener
import com.brackeys.ui.editorkit.listener.OnShortcutListener
import com.brackeys.ui.editorkit.listener.OnUndoRedoChangedListener
import com.brackeys.ui.editorkit.model.Shortcut
import com.brackeys.ui.editorkit.widget.TextScroller
import com.brackeys.ui.feature.base.adapters.TabAdapter
import com.brackeys.ui.feature.base.fragments.BaseFragment
import com.brackeys.ui.feature.base.utils.OnBackPressedHandler
import com.brackeys.ui.feature.editor.adapters.AutoCompleteAdapter
import com.brackeys.ui.feature.editor.adapters.DocumentAdapter
import com.brackeys.ui.feature.editor.customview.ExtendedKeyboard
import com.brackeys.ui.feature.editor.utils.Panel
import com.brackeys.ui.feature.editor.utils.TabController
import com.brackeys.ui.feature.editor.utils.ToolbarManager
import com.brackeys.ui.feature.editor.viewmodel.EditorViewModel
import com.brackeys.ui.feature.main.viewmodel.MainViewModel
import com.brackeys.ui.feature.settings.activities.SettingsActivity
import com.brackeys.ui.filesystem.base.model.FileType
import com.brackeys.ui.utils.event.SettingsEvent
import com.brackeys.ui.utils.extensions.createTypefaceFromPath
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding3.widget.textChangeEvents
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class EditorFragment : BaseFragment(R.layout.fragment_editor), ToolbarManager.OnPanelClickListener,
    ExtendedKeyboard.OnKeyListener, TabAdapter.OnTabSelectedListener, TabAdapter.OnDataRefreshListener,
    DocumentAdapter.TabInteractor, OnBackPressedHandler {

    companion object {
        private const val ALPHA_FULL = 255
        private const val ALPHA_SEMI = 90
    }

    private val sharedViewModel: MainViewModel by activityViewModels()
    private val viewModel: EditorViewModel by viewModels()
    private val toolbarManager: ToolbarManager by lazy { ToolbarManager(this) }
    private val tabController: TabController by lazy { TabController() }

    private lateinit var binding: FragmentEditorBinding
    private lateinit var adapter: DocumentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.observeSettings()
        if (savedInstanceState == null) {
            viewModel.loadFiles()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)!!
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        observeViewModel()

        toolbarManager.bind(binding)

        binding.tabLayout.setHasFixedSize(true)
        binding.tabLayout.adapter = DocumentAdapter(this).also {
            it.setOnTabSelectedListener(this)
            it.setOnDataRefreshListener(this)
            adapter = it
        }
        tabController.attachToRecyclerView(binding.tabLayout)

        binding.extendedKeyboard.setKeyListener(this)
        binding.extendedKeyboard.setHasFixedSize(true)
        binding.scroller.link(binding.editor)

        binding.editor.suggestionAdapter = AutoCompleteAdapter(requireContext())
        binding.editor.onUndoRedoChangedListener = object : OnUndoRedoChangedListener {
            override fun onUndoRedoChanged() {
                val canUndo = binding.editor.canUndo()
                val canRedo = binding.editor.canRedo()

                binding.actionUndo.isClickable = canUndo
                binding.actionRedo.isClickable = canRedo

                binding.actionUndo.imageAlpha = if (canUndo) ALPHA_FULL else ALPHA_SEMI
                binding.actionRedo.imageAlpha = if (canRedo) ALPHA_FULL else ALPHA_SEMI
            }
        }

        binding.editor.onUndoRedoChangedListener?.onUndoRedoChanged() // update undo/redo alpha

        binding.editor.onChangeListener = object : OnChangeListener {
            override fun onChange() {
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

        binding.actionTab.setOnClickListener {
            onKey(binding.editor.tab())
        }

        // region SHORTCUTS

        binding.editor.onShortcutListener = object : OnShortcutListener {
            override fun onShortcut(shortcut: Shortcut): Boolean {
                val (ctrl, shift, alt, keyCode) = shortcut
                return when {
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
                    ctrl && keyCode == KeyEvent.KEYCODE_P -> onPropertiesButton()
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
                    keyCode == KeyEvent.KEYCODE_TAB -> binding.actionTab.performClick()
                    else -> false
                }
            }
        }

        // endregion SHORTCUTS
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.loadFilesEvent.value = adapter.currentList
        viewModel.selectTabEvent.value = adapter.selectedPosition
    }

    override fun onPause() {
        super.onPause()
        saveDocument(adapter.selectedPosition)
        viewModel.updateDocuments(adapter.currentList)
    }

    override fun onResume() {
        super.onResume()
        loadDocument(adapter.selectedPosition)
    }

    override fun handleOnBackPressed(): Boolean {
        if (toolbarManager.panel != Panel.DEFAULT) {
            onCloseFindButton()
            return true
        }
        return false
    }

    override fun onKey(char: String) {
        binding.editor.insert(char)
    }

    private fun observeViewModel() {
        viewModel.toastEvent.observe(viewLifecycleOwner) {
            showToast(it)
        }
        viewModel.loadFilesEvent.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            viewModel.findRecentTab(list)
        }
        viewModel.selectTabEvent.observe(viewLifecycleOwner) { position ->
            sharedViewModel.closeDrawerEvent.call()
            if (position > -1) {
                adapter.select(position)
            }
        }
        viewModel.parseEvent.observe(viewLifecycleOwner) { model ->
            model.exception?.let {
                binding.editor.setErrorLine(it.lineNumber)
            }
        }
        viewModel.contentEvent.observe(viewLifecycleOwner) { (content, textParams) ->
            binding.scroller.state = TextScroller.STATE_HIDDEN
            binding.editor.language = content.language
            binding.editor.undoStack = content.undoStack
            binding.editor.redoStack = content.redoStack
            binding.editor.setTextContent(textParams)
            binding.editor.scrollX = content.documentModel.scrollX
            binding.editor.scrollY = content.documentModel.scrollY
            binding.editor.setSelection(
                content.documentModel.selectionStart,
                content.documentModel.selectionEnd
            )
            binding.editor.requestFocus()
        }
        sharedViewModel.openEvent.observe(viewLifecycleOwner) { fileModel ->
            val type = fileModel.getType()
            val documentModel = DocumentConverter.toModel(fileModel)
            val canOpenUnknownFile = type == FileType.DEFAULT && viewModel.openUnknownFiles
            if (canOpenUnknownFile || type == FileType.TEXT) {
                viewModel.openFile(adapter.currentList, documentModel)
            } else {
                sharedViewModel.openAsEvent.value = fileModel
            }
        }

        // region PREFERENCES

        viewModel.settingsEvent.observe(viewLifecycleOwner) { queue ->
            val config = binding.editor.editorConfig
            while (queue != null && queue.isNotEmpty()) {
                when (val event = queue.poll()) {
                    is SettingsEvent.ThemePref -> {
                        binding.editor.colorScheme = event.value.colorScheme
                    }
                    is SettingsEvent.FontSize -> config.fontSize = event.value
                    is SettingsEvent.FontType -> {
                        config.fontType = requireContext().createTypefaceFromPath(event.value)
                    }
                    is SettingsEvent.WordWrap -> config.wordWrap = event.value
                    is SettingsEvent.CodeCompletion -> config.codeCompletion = event.value
                    is SettingsEvent.ErrorHighlight -> {
                        if (event.value) {
                            binding.editor
                                .textChangeEvents()
                                .skipInitialValue()
                                .debounce(1500, TimeUnit.MILLISECONDS)
                                .filter { it.text.isNotEmpty() }
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeBy {
                                    val position = adapter.selectedPosition
                                    if (position > -1) {
                                        viewModel.parse(
                                            adapter.currentList[position],
                                            binding.editor.language,
                                            binding.editor.text.toString()
                                        )
                                    }
                                }
                                .disposeOnFragmentDestroyView()
                        }
                    }
                    is SettingsEvent.PinchZoom -> config.pinchZoom = event.value
                    is SettingsEvent.CurrentLine -> config.highlightCurrentLine = event.value
                    is SettingsEvent.Delimiters -> config.highlightDelimiters = event.value
                    is SettingsEvent.ExtendedKeys -> {
                        KeyboardVisibilityEvent.setEventListener(requireActivity()) { isOpen ->
                            binding.keyboardContainer.isVisible = event.value && isOpen
                        }
                    }
                    is SettingsEvent.KeyboardPreset -> {
                        binding.extendedKeyboard.submitList(event.value)
                    }
                    is SettingsEvent.SoftKeys -> config.softKeyboard = event.value
                    is SettingsEvent.AutoIndent -> config.autoIndentation = event.value
                    is SettingsEvent.AutoBrackets -> config.autoCloseBrackets = event.value
                    is SettingsEvent.AutoQuotes -> config.autoCloseQuotes = event.value
                    is SettingsEvent.UseSpacesNotTabs -> config.useSpacesInsteadOfTabs = event.value
                    is SettingsEvent.TabWidth -> config.tabWidth = event.value
                }
            }
            binding.editor.editorConfig = config
        }

        // endregion PREFERENCES
    }

    // region TABS

    override fun onTabUnselected(position: Int) {
        saveDocument(position)
        closeKeyboard() // Обход бага, когда после переключения вкладок позиция курсора не менялась с предыдущей вкладки
    }

    override fun onTabSelected(position: Int) {
        loadDocument(position)
    }

    override fun onDataRefresh() {
        viewModel.stateNothingFound.set(adapter.currentList.isEmpty())
    }

    override fun close(position: Int) {
        val selectedPosition = adapter.selectedPosition
        if (position == selectedPosition) {
            binding.scroller.state = TextScroller.STATE_HIDDEN
            binding.editor.clearText() // TTL Exception bypass
            closeKeyboard() // Обход бага, когда после удаления вкладки можно было редактировать в ней текст
        }
        removeDocument(position)
        adapter.close(position)
    }

    override fun closeOthers(position: Int) {
        val tabCount = adapter.itemCount - 1
        for (index in tabCount downTo 0) {
            if (index != position) {
                close(index)
            }
        }
    }

    override fun closeAll(position: Int) {
        closeOthers(position)
        close(adapter.selectedPosition)
    }

    private fun loadDocument(position: Int) {
        if (position > -1) {
            val document = adapter.currentList[position]
            viewModel.loadFile(document, TextViewCompat.getTextMetricsParams(binding.editor))
        }
    }

    private fun saveDocument(position: Int) {
        if (position > -1) {
            viewModel.stateLoadingDocuments.set(true) // show loading indicator
            val document = adapter.currentList[position].apply {
                scrollX = binding.editor.scrollX
                scrollY = binding.editor.scrollY
                selectionStart = binding.editor.selectionStart
                selectionEnd = binding.editor.selectionEnd
            }
            val text = binding.editor.text.toString()
            if (text.isNotEmpty()) {
                val documentContent = DocumentContent(
                    documentModel = document,
                    language = binding.editor.language,
                    undoStack = binding.editor.undoStack,
                    redoStack = binding.editor.redoStack,
                    text = text
                )
                viewModel.saveFile(documentContent, toCache = true)
            }
            binding.editor.clearText() // TTL Exception bypass
        }
    }

    private fun removeDocument(position: Int) {
        if (position > -1) {
            val documentModel = adapter.currentList[position]
            viewModel.deleteCache(documentModel)
        }
    }

    // endregion TABS

    // region TOOLBAR

    override fun onDrawerButton() {
        sharedViewModel.openDrawerEvent.call()
    }

    override fun onNewButton() {
        onDrawerButton()
    }

    override fun onOpenButton() {
        onDrawerButton()
        showToast(R.string.message_select_file)
    }

    override fun onSaveButton(): Boolean {
        val position = adapter.selectedPosition
        if (position > -1) {
            val isModified = adapter.currentList[position].modified
            if (isModified) {
                adapter.currentList[position].modified = false
                adapter.notifyItemChanged(position)
            }

            val documentContent = DocumentContent(
                documentModel = adapter.currentList[position],
                language = binding.editor.language,
                undoStack = binding.editor.undoStack,
                redoStack = binding.editor.redoStack,
                text = binding.editor.text.toString()
            )

            viewModel.saveFile(documentContent, toCache = false) // Save to local storage
            viewModel.saveFile(documentContent, toCache = true) // Save to app cache
        } else {
            showToast(R.string.message_no_open_files)
        }
        return true
    }

    override fun onSaveAsButton(): Boolean {
        val position = adapter.selectedPosition
        if (position > -1) {
            val document = adapter.currentList[position]
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
                            path = filePath
                        )
                        val documentContent = DocumentContent(
                            documentModel = updateDocument,
                            language = binding.editor.language,
                            undoStack = binding.editor.undoStack,
                            redoStack = binding.editor.redoStack,
                            text = binding.editor.text.toString()
                        )

                        viewModel.saveFile(documentContent)
                    } else {
                        showToast(R.string.message_invalid_file_path)
                    }
                }
                val enterFilePath = findViewById<TextInputEditText>(R.id.input)
                enterFilePath.setText(document.path)
            }
        } else {
            showToast(R.string.message_no_open_files)
        }
        return true
    }

    override fun onPropertiesButton(): Boolean {
        val position = adapter.selectedPosition
        if (position > -1) {
            val document = adapter.currentList[position]
            sharedViewModel.propertiesEvent.value = DocumentConverter.toModel(document)
        } else {
            showToast(R.string.message_no_open_files)
        }
        return true
    }

    override fun onCloseButton(): Boolean {
        val position = adapter.selectedPosition
        if (position > -1) {
            close(position)
        } else {
            showToast(R.string.message_no_open_files)
        }
        return true
    }

    override fun onCutButton(): Boolean {
        if (binding.editor.hasSelection()) {
            binding.editor.cut()
        } else {
            showToast(R.string.message_nothing_to_cut)
        }
        return true
    }

    override fun onCopyButton(): Boolean {
        if (binding.editor.hasSelection()) {
            binding.editor.copy()
        } else {
            showToast(R.string.message_nothing_to_copy)
        }
        return true
    }

    override fun onPasteButton(): Boolean {
        val position = adapter.selectedPosition
        if (binding.editor.hasPrimaryClip() && position > -1) {
            binding.editor.paste()
        } else {
            showToast(R.string.message_nothing_to_paste)
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
        val position = adapter.selectedPosition
        if (position > -1) {
            toolbarManager.panel = Panel.FIND
        } else {
            showToast(R.string.message_no_open_files)
        }
        return true
    }

    override fun onCloseFindButton() {
        toolbarManager.panel = Panel.DEFAULT
        binding.inputFind.setText("")
        binding.editor.clearFindResultSpans()
    }

    override fun onOpenReplaceButton(): Boolean {
        val position = adapter.selectedPosition
        if (position > -1) {
            toolbarManager.panel = Panel.FIND_REPLACE
        } else {
            showToast(R.string.message_no_open_files)
        }
        return true
    }

    override fun onCloseReplaceButton() {
        toolbarManager.panel = Panel.FIND
        binding.inputReplace.setText("")
    }

    override fun onGoToLineButton(): Boolean {
        val position = adapter.selectedPosition
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
                        showToast(R.string.message_line_not_exists)
                    }
                }
            }
        } else {
            showToast(R.string.message_no_open_files)
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

    override fun onFindInputChanged(findText: String) {
        binding.editor.clearFindResultSpans()
        binding.editor.find(findText, toolbarManager.findParams())
    }

    override fun onErrorCheckingButton() {
        val position = adapter.selectedPosition
        if (position > -1) {
            MaterialDialog(requireContext()).show {
                title(R.string.dialog_title_result)
                message(R.string.message_no_errors_detected)
                viewModel.parseEvent.value?.let { model ->
                    model.exception?.let {
                        message(text = it.message)
                        binding.editor.setErrorLine(it.lineNumber)
                    }
                }
                positiveButton(R.string.action_ok)
            }
        } else {
            showToast(R.string.message_no_open_files)
        }
    }

    override fun onInsertColorButton() {
        val position = adapter.selectedPosition
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
            showToast(R.string.message_no_open_files)
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
        val intent = Intent(context, SettingsActivity::class.java)
        startActivity(intent)
        return true
    }

    // endregion TOOLBAR
}