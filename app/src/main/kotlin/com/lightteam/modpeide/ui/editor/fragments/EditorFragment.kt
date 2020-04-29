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

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import com.lightteam.filesystem.model.FileType
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.converter.DocumentConverter
import com.lightteam.modpeide.databinding.FragmentEditorBinding
import com.lightteam.modpeide.domain.editor.DocumentModel
import com.lightteam.modpeide.ui.base.adapters.TabAdapter
import com.lightteam.modpeide.ui.base.dialogs.DialogStore
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.editor.adapters.DocumentAdapter
import com.lightteam.modpeide.ui.editor.customview.ExtendedKeyboard
import com.lightteam.modpeide.ui.editor.customview.TextScroller
import com.lightteam.modpeide.ui.editor.utils.ToolbarManager
import com.lightteam.modpeide.ui.editor.viewmodel.EditorViewModel
import com.lightteam.modpeide.ui.main.viewmodel.MainViewModel
import com.lightteam.modpeide.ui.settings.activities.SettingsActivity
import com.lightteam.modpeide.utils.event.PreferenceEvent
import com.lightteam.modpeide.utils.extensions.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EditorFragment : BaseFragment(), ToolbarManager.OnPanelClickListener,
    ExtendedKeyboard.OnKeyListener, TabAdapter.OnTabSelectedListener, DocumentAdapter.TabInteractor {

    @Inject
    lateinit var sharedViewModel: MainViewModel
    @Inject
    lateinit var viewModel: EditorViewModel
    @Inject
    lateinit var toolbarManager: ToolbarManager
    @Inject
    lateinit var adapter: DocumentAdapter

    private lateinit var binding: FragmentEditorBinding
    private lateinit var drawerHandler: DrawerHandler

    override fun layoutId(): Int = R.layout.fragment_editor

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DrawerHandler) {
            drawerHandler = context
        } else {
            throw IllegalArgumentException("$context must implement DrawerHandler")
        }
    }

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
        adapter.onTabSelectedListener = this
        binding.documentRecyclerView.adapter = adapter
        binding.documentRecyclerView.setHasFixedSize(true)
        binding.extendedKeyboard.setKeyListener(this)
        binding.extendedKeyboard.setHasFixedSize(true)
        binding.scroller.link(binding.editor)

        binding.editor
            .afterTextChangeEvents()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                viewModel.canUndo.set(binding.editor.canUndo())
                viewModel.canRedo.set(binding.editor.canRedo())
            }
            .disposeOnFragmentDestroyView()

        if (requireContext().isUltimate()) {
            binding.editor
                .afterTextChangeEvents()
                .skipInitialValue()
                .debounce(1500, TimeUnit.MILLISECONDS)
                .filter { it.editable?.isNotEmpty() ?: false }
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

    override fun onKey(char: String) {
        binding.editor.insert(char)
    }

    private fun observeViewModel() {
        viewModel.toastEvent.observe(viewLifecycleOwner, Observer {
            showToast(it)
        })
        viewModel.tabsEvent.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        viewModel.tabSelectionEvent.observe(viewLifecycleOwner, Observer { position ->
            drawerHandler.handleDrawerClose()
            if (position > -1) {
                adapter.select(position)
            }
        })
        viewModel.parseEvent.observe(viewLifecycleOwner, Observer { model ->
            model.exception?.let {
                binding.editor.setErrorSpan(it.lineNumber)
            }
        })
        viewModel.contentEvent.observe(viewLifecycleOwner, Observer { content ->
            viewModel.setSelectedDocumentId(content.documentModel.uuid)
            binding.scroller.state = TextScroller.STATE_HIDDEN
            binding.editor.language = content.language
            binding.editor.undoStack = content.undoStack
            binding.editor.redoStack = content.redoStack
            binding.editor.processText(content.text)
            binding.editor.scrollX = content.documentModel.scrollX
            binding.editor.scrollY = content.documentModel.scrollY
            binding.editor.setSelection(
                content.documentModel.selectionStart,
                content.documentModel.selectionEnd
            )
            binding.editor.requestFocus()
        })
        sharedViewModel.openFileEvent.observe(viewLifecycleOwner, Observer { fileModel ->
            val documentModel = DocumentConverter.toModel(fileModel)
            if (fileModel.getType() == FileType.TEXT) {
                viewModel.openFile(documentModel)
            } else {
                openFile(documentModel)
            }
        })

        // region PREFERENCES

        viewModel.preferenceEvent.observe(viewLifecycleOwner, Observer { queue ->
            while (queue != null && queue.isNotEmpty()) {
                when (val event = queue.poll()) {
                    is PreferenceEvent.Theme -> binding.editor.colorScheme = event.value
                    is PreferenceEvent.FontSize -> {
                        val newConfiguration = binding.editor.configuration.copy(fontSize = event.value)
                        binding.editor.configuration = newConfiguration
                    }
                    is PreferenceEvent.FontType -> {
                        val newConfiguration = binding.editor.configuration.copy(
                            fontType = requireContext().createTypefaceFromAssets(event.value)
                        )
                        binding.editor.configuration = newConfiguration
                    }
                    is PreferenceEvent.WordWrap -> {
                        val newConfiguration = binding.editor.configuration.copy(wordWrap = event.value)
                        binding.editor.configuration = newConfiguration
                    }
                    is PreferenceEvent.CodeCompletion -> {
                        val newConfiguration = binding.editor.configuration.copy(codeCompletion = event.value)
                        binding.editor.configuration = newConfiguration
                    }
                    is PreferenceEvent.PinchZoom -> {
                        val newConfiguration = binding.editor.configuration.copy(pinchZoom = event.value)
                        binding.editor.configuration = newConfiguration
                    }
                    is PreferenceEvent.CurrentLine -> {
                        val newConfiguration = binding.editor.configuration.copy(highlightCurrentLine = event.value)
                        binding.editor.configuration = newConfiguration
                    }
                    is PreferenceEvent.Delimiters -> {
                        val newConfiguration = binding.editor.configuration.copy(highlightDelimiters = event.value)
                        binding.editor.configuration = newConfiguration
                    }
                    is PreferenceEvent.ExtendedKeys -> {
                        KeyboardVisibilityEvent.setEventListener(requireActivity()) { isOpen ->
                            if (event.value) {
                                binding.extendedKeyboard.visibility = if (isOpen) View.VISIBLE else View.GONE
                            } else {
                                binding.extendedKeyboard.visibility = View.GONE
                            }
                        }
                    }
                    is PreferenceEvent.SoftKeys -> {
                        val newConfiguration = binding.editor.configuration.copy(softKeyboard = event.value)
                        binding.editor.configuration = newConfiguration
                    }
                    is PreferenceEvent.AutoIndent -> {
                        val newConfiguration = binding.editor.configuration.copy(autoIndentation = event.value)
                        binding.editor.configuration = newConfiguration
                    }
                    is PreferenceEvent.AutoBrackets -> {
                        val newConfiguration = binding.editor.configuration.copy(autoCloseBrackets = event.value)
                        binding.editor.configuration = newConfiguration
                    }
                    is PreferenceEvent.AutoQuotes -> {
                        val newConfiguration = binding.editor.configuration.copy(autoCloseQuotes = event.value)
                        binding.editor.configuration = newConfiguration
                    }
                }
            }
        })

        // endregion PREFERENCES
    }

    private fun openFile(documentModel: DocumentModel) {
        try { // Открытие файла через подходящую программу
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${context?.packageName}.provider",
                File(documentModel.path)
            )
            val mime = context?.contentResolver?.getType(uri)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mime)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            viewModel.toastEvent.value = R.string.message_cannot_be_opened
        }
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
        if (position > -1) { // if there's at least 1 tab
            val document = viewModel.tabsList[position]
            viewModel.loadFile(document)
        }
    }

    private fun saveDocument(position: Int) {
        if (position > -1) { // if there's at least 1 tab
            viewModel.stateLoadingDocuments.set(true) // show loading indicator
            val document = viewModel.tabsList[position].copy(
                scrollX = binding.editor.scrollX,
                scrollY = binding.editor.scrollY,
                selectionStart = binding.editor.selectionStart,
                selectionEnd = binding.editor.selectionEnd
            )
            viewModel.tabsList[position] = document
            viewModel.saveToCache(document, binding.editor.getProcessedText())
            viewModel.saveUndoStack(document, binding.editor.undoStack)
            viewModel.saveRedoStack(document, binding.editor.redoStack)
            binding.editor.clearText() // TTL Exception bypass
        }
    }

    private fun removeDocument(position: Int) {
        val documentModel = viewModel.tabsList[position]
        viewModel.tabsList.removeAt(position)
        viewModel.stateNothingFound.set(viewModel.tabsList.isEmpty())
        viewModel.deleteCache(documentModel)
    }

    // endregion TABS

    // region TOOLBAR

    override fun onDrawerButton() {
        drawerHandler.handleDrawerOpen()
    }

    override fun onNewButton() {
        onDrawerButton()
    }

    override fun onOpenButton() {
        onDrawerButton()
        viewModel.toastEvent.value = R.string.message_select_file
    }

    override fun onSaveButton() {
        val position = adapter.selectedPosition
        if (position > -1) {
            val document = viewModel.tabsList[position]
            viewModel.saveFile(document, binding.editor.getProcessedText())
            viewModel.saveToCache(document, binding.editor.getProcessedText())
            viewModel.saveUndoStack(document, binding.editor.undoStack)
            viewModel.saveRedoStack(document, binding.editor.redoStack)
        } else {
            viewModel.toastEvent.value = R.string.message_no_open_files
        }
    }

    override fun onCloseButton() {
        val position = adapter.selectedPosition
        if (position > -1) {
            close(position)
        } else {
            viewModel.toastEvent.value = R.string.message_no_open_files
        }
    }

    override fun onCutButton() {
        if (binding.editor.hasSelection()) {
            binding.editor.cut()
        } else {
            viewModel.toastEvent.value = R.string.message_nothing_to_cut
        }
    }

    override fun onCopyButton() {
        if (binding.editor.hasSelection()) {
            binding.editor.copy()
        } else {
            viewModel.toastEvent.value = R.string.message_nothing_to_copy
        }
    }

    override fun onPasteButton() {
        val position = adapter.selectedPosition
        if (binding.editor.hasPrimaryClip() && position > -1) {
            binding.editor.paste()
        } else {
            viewModel.toastEvent.value = R.string.message_nothing_to_paste
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

    override fun onFindButton() {
        val position = adapter.selectedPosition
        if (position > -1) {
            MaterialDialog(requireContext()).show {
                title(R.string.dialog_title_find)
                customView(R.layout.dialog_find, scrollable = true)
                negativeButton(R.string.action_cancel)
                positiveButton(R.string.action_find) {
                    val textToFind = it.getCustomView()
                        .findViewById<TextInputEditText>(R.id.input).text.toString()
                    val isMatchCaseChecked = it.getCustomView()
                        .findViewById<CheckBox>(R.id.box_matchCase).isChecked
                    val isRegExpChecked = it.getCustomView()
                        .findViewById<CheckBox>(R.id.box_regExp).isChecked
                    val isWordsOnlyChecked = it.getCustomView()
                        .findViewById<CheckBox>(R.id.box_wordOnly).isChecked
                    if (textToFind.isNotEmpty() && textToFind.isNotBlank()) {
                        binding.editor.find(textToFind, isMatchCaseChecked, isRegExpChecked, isWordsOnlyChecked)
                    } else {
                        viewModel.toastEvent.value = R.string.message_enter_the_text
                    }
                }
            }
        } else {
            viewModel.toastEvent.value = R.string.message_no_open_files
        }
    }

    override fun onReplaceAllButton() {
        val position = adapter.selectedPosition
        if (position > -1) {
            MaterialDialog(requireContext()).show {
                title(R.string.dialog_title_replace_all)
                customView(R.layout.dialog_replace_all)
                negativeButton(R.string.action_cancel)
                positiveButton(R.string.action_replace_all) {
                    val textReplaceWhat = it.getCustomView()
                        .findViewById<TextInputEditText>(R.id.input).text.toString()
                    val textReplaceWith = it.getCustomView()
                        .findViewById<TextInputEditText>(R.id.input2).text.toString()
                    if (textReplaceWhat.isNotEmpty()) {
                        binding.editor.replaceAll(textReplaceWhat, textReplaceWith)
                    } else {
                        viewModel.toastEvent.value = R.string.message_enter_the_text
                    }
                }
            }
        } else {
            viewModel.toastEvent.value = R.string.message_no_open_files
        }
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
                        val toLine = inputResult.toInt() - 1 // т.к первая линия 0
                        when {
                            toLine <= 0 -> viewModel.toastEvent.value = R.string.message_line_above_than_0
                            toLine < binding.editor.arrayLineCount -> binding.editor.gotoLine(toLine)
                            else -> viewModel.toastEvent.value = R.string.message_line_not_exists
                        }
                    } else {
                        viewModel.toastEvent.value = R.string.message_line_not_exists
                    }
                }
            }
        } else {
            viewModel.toastEvent.value = R.string.message_no_open_files
        }
    }

    override fun onErrorCheckingButton() {
        if (requireContext().isUltimate()) {
            val position = adapter.selectedPosition
            if (position > -1) {
                MaterialDialog(requireContext()).show {
                    title(R.string.dialog_title_result)
                    message(R.string.message_no_errors_detected)
                    viewModel.parseEvent.value?.let { model ->
                        model.exception?.let {
                            message(text = it.message)
                            binding.editor.setErrorSpan(it.lineNumber)
                        }
                    }
                    positiveButton(R.string.action_ok)
                }
            } else {
                viewModel.toastEvent.value = R.string.message_no_open_files
            }
        } else {
            DialogStore.Builder(requireContext()).show()
        }
    }

    override fun onInsertColorButton() {
        if (requireContext().isUltimate()) {
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
                viewModel.toastEvent.value = R.string.message_no_open_files
            }
        } else {
            DialogStore.Builder(requireContext()).show()
        }
    }

    override fun onUndoButton() {
        binding.editor.undo()
    }

    override fun onRedoButton() {
        binding.editor.redo()
    }

    override fun onSettingsButton() {
        context?.launchActivity<SettingsActivity>()
    }

    // endregion TOOLBAR

    interface DrawerHandler {
        fun handleDrawerOpen()
        fun handleDrawerClose()
    }
}