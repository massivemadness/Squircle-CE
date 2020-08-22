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

package com.lightteam.modpeide.ui.editor.fragments

import android.os.Bundle
import android.view.View
import androidx.core.widget.TextViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding3.widget.textChangeEvents
import com.lightteam.editorkit.internal.UndoRedoEditText
import com.lightteam.editorkit.widget.TextScroller
import com.lightteam.filesystem.model.FileType
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.converter.DocumentConverter
import com.lightteam.modpeide.data.utils.extensions.toHexString
import com.lightteam.modpeide.databinding.FragmentEditorBinding
import com.lightteam.modpeide.domain.model.editor.DocumentContent
import com.lightteam.modpeide.ui.base.adapters.TabAdapter
import com.lightteam.modpeide.ui.base.dialogs.StoreDialog
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.base.utils.OnBackPressedHandler
import com.lightteam.modpeide.ui.editor.adapters.AutoCompleteAdapter
import com.lightteam.modpeide.ui.editor.adapters.DocumentAdapter
import com.lightteam.modpeide.ui.editor.customview.ExtendedKeyboard
import com.lightteam.modpeide.ui.editor.utils.Panel
import com.lightteam.modpeide.ui.editor.utils.ToolbarManager
import com.lightteam.modpeide.ui.editor.viewmodel.EditorViewModel
import com.lightteam.modpeide.ui.main.viewmodel.MainViewModel
import com.lightteam.modpeide.ui.settings.activities.SettingsActivity
import com.lightteam.modpeide.utils.event.PreferenceEvent
import com.lightteam.modpeide.utils.extensions.createTypefaceFromPath
import com.lightteam.modpeide.utils.extensions.isUltimate
import com.lightteam.modpeide.utils.extensions.launchActivity
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

@AndroidEntryPoint
class EditorFragment : BaseFragment(R.layout.fragment_editor), ToolbarManager.OnPanelClickListener,
    ExtendedKeyboard.OnKeyListener, TabAdapter.OnTabSelectedListener,
    DocumentAdapter.TabInteractor, OnBackPressedHandler {

    private val sharedViewModel: MainViewModel by activityViewModels()
    private val viewModel: EditorViewModel by viewModels()
    private val toolbarManager: ToolbarManager by lazy { ToolbarManager(this) }

    private lateinit var binding: FragmentEditorBinding
    private lateinit var adapter: DocumentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.observePreferences() // and loadFiles()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)!!
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        observeViewModel()

        toolbarManager.bind(binding)

        binding.documentRecyclerView.setHasFixedSize(true)
        binding.documentRecyclerView.adapter = DocumentAdapter(this, this)
            .also { adapter = it }

        binding.extendedKeyboard.setKeyListener(this)
        binding.extendedKeyboard.setHasFixedSize(true)
        binding.scroller.link(binding.editor)

        binding.editor.suggestionAdapter = AutoCompleteAdapter(requireContext())
        binding.editor.onUndoRedoChangedListener =
            object : UndoRedoEditText.OnUndoRedoChangedListener {
                override fun onUndoRedoChanged() {
                    viewModel.canUndo.set(binding.editor.canUndo())
                    viewModel.canRedo.set(binding.editor.canRedo())
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.tabSelectionEvent.value = adapter.selectedPosition
        viewModel.tabsEvent.value = viewModel.tabsList
    }

    override fun onPause() {
        super.onPause()
        saveDocument(adapter.selectedPosition)
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
        viewModel.toastEvent.observe(viewLifecycleOwner, {
            showToast(it)
        })
        viewModel.tabsEvent.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            viewModel.loadSelection()
        })
        viewModel.tabSelectionEvent.observe(viewLifecycleOwner, { position ->
            sharedViewModel.closeDrawerEvent.call()
            if (position > -1) {
                adapter.select(position)
            }
        })
        viewModel.parseEvent.observe(viewLifecycleOwner, { model ->
            model.exception?.let {
                binding.editor.setErrorLine(it.lineNumber)
            }
        })
        viewModel.contentEvent.observe(viewLifecycleOwner, {
            val content = it.first
            val textParams = it.second

            binding.scroller.state = TextScroller.STATE_HIDDEN
            binding.editor.language = content.language
            binding.editor.undoStack = content.undoStack
            binding.editor.redoStack = content.redoStack
            binding.editor.processText(textParams)
            binding.editor.scrollX = content.documentModel.scrollX
            binding.editor.scrollY = content.documentModel.scrollY
            binding.editor.setSelection(
                content.documentModel.selectionStart,
                content.documentModel.selectionEnd
            )
            binding.editor.requestFocus()
        })
        sharedViewModel.openEvent.observe(viewLifecycleOwner, { fileModel ->
            val documentModel = DocumentConverter.toModel(fileModel)
            val type = fileModel.getType()
            if ((type == FileType.DEFAULT && viewModel.openUnknownFiles) || type == FileType.TEXT) {
                viewModel.openFile(documentModel)
            } else {
                sharedViewModel.openAsEvent.value = fileModel
            }
        })

        // region PREFERENCES

        viewModel.preferenceEvent.observe(viewLifecycleOwner, { queue ->
            while (queue != null && queue.isNotEmpty()) {
                when (val event = queue.poll()) {
                    is PreferenceEvent.ThemePref -> {
                        binding.editor.colorScheme = event.value.colorScheme
                    }
                    is PreferenceEvent.FontSize -> {
                        val newConfiguration =
                            binding.editor.config.copy(fontSize = event.value)
                        binding.editor.config = newConfiguration
                    }
                    is PreferenceEvent.FontType -> {
                        val newConfiguration = binding.editor.config.copy(
                            fontType = requireContext().createTypefaceFromPath(event.value)
                        )
                        binding.editor.config = newConfiguration
                    }
                    is PreferenceEvent.WordWrap -> {
                        val newConfiguration =
                            binding.editor.config.copy(wordWrap = event.value)
                        binding.editor.config = newConfiguration
                    }
                    is PreferenceEvent.CodeCompletion -> {
                        val newConfiguration =
                            binding.editor.config.copy(codeCompletion = event.value)
                        binding.editor.config = newConfiguration
                    }
                    is PreferenceEvent.ErrorHighlight -> {
                        if (isUltimate() && event.value) {
                            binding.editor
                                .textChangeEvents()
                                .skipInitialValue()
                                .debounce(1500, TimeUnit.MILLISECONDS)
                                .filter { it.text.isNotEmpty() }
                                .distinctUntilChanged()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeBy {
                                    if (adapter.selectedPosition > -1) {
                                        viewModel.parse(
                                            binding.editor.language,
                                            adapter.selectedPosition,
                                            binding.editor.getProcessedText()
                                        )
                                    }
                                }
                                .disposeOnFragmentDestroyView()
                        }
                    }
                    is PreferenceEvent.PinchZoom -> {
                        val newConfiguration =
                            binding.editor.config.copy(pinchZoom = event.value)
                        binding.editor.config = newConfiguration
                    }
                    is PreferenceEvent.CurrentLine -> {
                        val newConfiguration =
                            binding.editor.config.copy(highlightCurrentLine = event.value)
                        binding.editor.config = newConfiguration
                    }
                    is PreferenceEvent.Delimiters -> {
                        val newConfiguration =
                            binding.editor.config.copy(highlightDelimiters = event.value)
                        binding.editor.config = newConfiguration
                    }
                    is PreferenceEvent.ExtendedKeys -> {
                        KeyboardVisibilityEvent.setEventListener(requireActivity()) { isOpen ->
                            if (event.value) {
                                binding.extendedKeyboard.visibility =
                                    if (isOpen) View.VISIBLE else View.GONE
                            } else {
                                binding.extendedKeyboard.visibility = View.GONE
                            }
                        }
                    }
                    is PreferenceEvent.KeyboardPreset -> {
                        binding.extendedKeyboard.submitList(event.value.keys)
                    }
                    is PreferenceEvent.SoftKeys -> {
                        val newConfiguration =
                            binding.editor.config.copy(softKeyboard = event.value)
                        binding.editor.config = newConfiguration
                    }
                    is PreferenceEvent.AutoIndent -> {
                        val newConfiguration =
                            binding.editor.config.copy(autoIndentation = event.value)
                        binding.editor.config = newConfiguration
                    }
                    is PreferenceEvent.AutoBrackets -> {
                        val newConfiguration =
                            binding.editor.config.copy(autoCloseBrackets = event.value)
                        binding.editor.config = newConfiguration
                    }
                    is PreferenceEvent.AutoQuotes -> {
                        val newConfiguration =
                            binding.editor.config.copy(autoCloseQuotes = event.value)
                        binding.editor.config = newConfiguration
                    }
                    is PreferenceEvent.UseSpacesNotTabs -> {
                        val newConfiguration =
                            binding.editor.config.copy(useSpacesInsteadOfTabs = event.value)
                        binding.editor.config = newConfiguration
                    }
                    is PreferenceEvent.TabWidth -> {
                        val newConfiguration =
                            binding.editor.config.copy(tabWidth = event.value)
                        binding.editor.config = newConfiguration
                    }
                }
            }
        })

        // endregion PREFERENCES
    }

    // region TABS

    override fun onTabReselected(position: Int) {}
    override fun onTabUnselected(position: Int) {
        saveDocument(position)
        closeKeyboard() // Обход бага, когда после переключения вкладок позиция курсора не менялась с предыдущей вкладки
    }

    override fun onTabSelected(position: Int) {
        loadDocument(position)
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
        if (position > -1 && position < viewModel.tabsList.size) {
            val document = viewModel.tabsList[position]
            viewModel.loadFile(document, TextViewCompat.getTextMetricsParams(binding.editor))
        }
    }

    private fun saveDocument(position: Int) {
        if (position > -1 && position < viewModel.tabsList.size) {
            viewModel.stateLoadingDocuments.set(true) // show loading indicator
            val document = viewModel.tabsList[position].copy(
                scrollX = binding.editor.scrollX,
                scrollY = binding.editor.scrollY,
                selectionStart = binding.editor.selectionStart,
                selectionEnd = binding.editor.selectionEnd
            )
            viewModel.tabsList[position] = document
            val text = binding.editor.getProcessedText()
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
        if (position > -1 && position < viewModel.tabsList.size) {
            val documentModel = viewModel.tabsList[position]
            viewModel.tabsList.removeAt(position)
            viewModel.stateNothingFound.set(viewModel.tabsList.isEmpty())
            viewModel.deleteCache(documentModel)
        }
    }

    // endregion TABS

    private fun showStoreDialog() {
        StoreDialog().show(childFragmentManager, StoreDialog.DIALOG_TAG)
    }

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

    override fun onSaveButton() {
        val position = adapter.selectedPosition
        if (position > -1) {
            val documentContent = DocumentContent(
                documentModel = viewModel.tabsList[position],
                language = binding.editor.language,
                undoStack = binding.editor.undoStack,
                redoStack = binding.editor.redoStack,
                text = binding.editor.getProcessedText()
            )
            viewModel.saveFile(documentContent, toCache = false) // Save to local storage
            viewModel.saveFile(documentContent, toCache = true) // Save to app cache
        } else {
            showToast(R.string.message_no_open_files)
        }
    }

    override fun onSaveAsButton() {
        val position = adapter.selectedPosition
        if (position > -1) {
            val document = viewModel.tabsList[position]
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
                            text = binding.editor.getProcessedText()
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
    }

    override fun onPropertiesButton() {
        val position = adapter.selectedPosition
        if (position > -1) {
            val document = viewModel.tabsList[position]
            sharedViewModel.propertiesEvent.value = DocumentConverter.toModel(document)
        } else {
            showToast(R.string.message_no_open_files)
        }
    }

    override fun onCloseButton() {
        val position = adapter.selectedPosition
        if (position > -1) {
            close(position)
        } else {
            showToast(R.string.message_no_open_files)
        }
    }

    override fun onCutButton() {
        if (binding.editor.hasSelection()) {
            binding.editor.cut()
        } else {
            showToast(R.string.message_nothing_to_cut)
        }
    }

    override fun onCopyButton() {
        if (binding.editor.hasSelection()) {
            binding.editor.copy()
        } else {
            showToast(R.string.message_nothing_to_copy)
        }
    }

    override fun onPasteButton() {
        val position = adapter.selectedPosition
        if (binding.editor.hasPrimaryClip() && position > -1) {
            binding.editor.paste()
        } else {
            showToast(R.string.message_nothing_to_paste)
        }
    }

    override fun onSelectAllButton() {
        binding.editor.selectAll()
    }

    override fun onSelectLineButton() {
        binding.editor.selectLine()
    }

    override fun onDeleteLineButton() {
        binding.editor.deleteLine()
    }

    override fun onDuplicateLineButton() {
        binding.editor.duplicateLine()
    }

    override fun onOpenFindButton() {
        val position = adapter.selectedPosition
        if (position > -1) {
            toolbarManager.panel = Panel.FIND
        } else {
            showToast(R.string.message_no_open_files)
        }
    }

    override fun onCloseFindButton() {
        toolbarManager.panel = Panel.DEFAULT
        binding.inputFind.setText("")
        binding.editor.clearFindResultSpans()
    }

    override fun onOpenReplaceButton() {
        val position = adapter.selectedPosition
        if (position > -1) {
            toolbarManager.panel = Panel.FIND_REPLACE
        } else {
            showToast(R.string.message_no_open_files)
        }
    }

    override fun onCloseReplaceButton() {
        toolbarManager.panel = Panel.FIND
        binding.inputReplace.setText("")
    }

    override fun onGoToLineButton() {
        val position = adapter.selectedPosition
        if (position > -1) {
            MaterialDialog(requireContext()).show {
                title(R.string.dialog_title_goto_line)
                customView(R.layout.dialog_goto_line)
                negativeButton(R.string.action_cancel)
                positiveButton(R.string.action_go_to) {
                    val input = getCustomView().findViewById<TextInputEditText>(R.id.input)
                    val inputResult = input.text.toString()
                    if (inputResult.isNotEmpty()) {
                        val line = inputResult.toInt() - 1 // т.к первая линия 0
                        when {
                            line <= 0 -> showToast(R.string.message_line_above_than_0)
                            line < binding.editor.arrayLineCount -> binding.editor.gotoLine(line)
                            else -> showToast(R.string.message_line_not_exists)
                        }
                    } else {
                        showToast(R.string.message_line_not_exists)
                    }
                }
            }
        } else {
            showToast(R.string.message_no_open_files)
        }
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
        binding.editor.find(findText)
    }

    override fun onRegexChanged(regex: Boolean) {
        binding.editor.isRegexEnabled = regex
        onFindInputChanged(binding.inputFind.text.toString())
    }

    override fun onMatchCaseChanged(matchCase: Boolean) {
        binding.editor.isMatchCaseEnabled = matchCase
        onFindInputChanged(binding.inputFind.text.toString())
    }

    override fun onWordsOnlyChanged(wordsOnly: Boolean) {
        binding.editor.isWordsOnlyEnabled = wordsOnly
        onFindInputChanged(binding.inputFind.text.toString())
    }

    override fun onErrorCheckingButton() {
        if (isUltimate()) {
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
        } else {
            showStoreDialog()
        }
    }

    override fun onInsertColorButton() {
        if (isUltimate()) {
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
        } else {
            showStoreDialog()
        }
    }

    override fun onUndoButton() {
        if (binding.editor.canUndo()) {
            binding.editor.undo()
        }
    }

    override fun onRedoButton() {
        if (binding.editor.canRedo()) {
            binding.editor.redo()
        }
    }

    override fun onSettingsButton() {
        context?.launchActivity<SettingsActivity>()
    }

    // endregion TOOLBAR
}