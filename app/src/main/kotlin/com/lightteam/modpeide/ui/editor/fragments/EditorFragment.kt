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
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.FragmentEditorBinding
import com.lightteam.modpeide.domain.model.editor.DocumentModel
import com.lightteam.modpeide.ui.base.dialogs.DialogStore
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.editor.utils.ToolbarManager
import com.lightteam.modpeide.ui.editor.viewmodel.EditorViewModel
import com.lightteam.modpeide.ui.settings.activities.SettingsActivity
import com.lightteam.modpeide.utils.commons.TypefaceFactory
import com.lightteam.modpeide.utils.event.PreferenceEvent
import com.lightteam.modpeide.utils.extensions.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.io.File
import javax.inject.Inject

class EditorFragment : BaseFragment(), ToolbarManager.OnPanelClickListener {

    @Inject
    lateinit var viewModel: EditorViewModel
    @Inject
    lateinit var toolbarManager: ToolbarManager

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)!!
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        observeViewModel()

        toolbarManager.bind(binding)
        binding.tabDocumentLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab) {
                saveDocument(tab.position)
                closeKeyboard() // Обход бага, когда после переключения вкладок
                // позиция курсора не менялась с предыдущей вкладки
            }
            override fun onTabSelected(tab: TabLayout.Tab) {
                loadDocument(tab.position)
            }
        })
        binding.extendedKeyboard.setKeyListener(binding.editor)

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
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toolbarManager.orientation = newConfig.orientation
    }

    override fun onPause() {
        super.onPause()
        saveDocument(binding.tabDocumentLayout.selectedTabPosition)
    }

    override fun onResume() {
        super.onResume()
        loadDocument(binding.tabDocumentLayout.selectedTabPosition)
    }

    private fun observeViewModel() {
        viewModel.toastEvent.observe(viewLifecycleOwner, Observer {
            showToast(it)
        })
        viewModel.documentsEvent.observe(viewLifecycleOwner, Observer { documents ->
            for (document in documents) {
                addTab(document, false)
            }
        })
        viewModel.documentEvent.observe(viewLifecycleOwner, Observer { document ->
            addTab(document, true)
            drawerHandler.handleDrawerClose()
        })
        viewModel.selectionEvent.observe(viewLifecycleOwner, Observer {
            binding.tabDocumentLayout.getTabAt(it)?.select()
            drawerHandler.handleDrawerClose()
        })
        viewModel.unopenableEvent.observe(viewLifecycleOwner, Observer {
            openFile(it)
        })
        viewModel.parseEvent.observe(viewLifecycleOwner, Observer { model ->
            MaterialDialog(requireContext()).show {
                title(R.string.dialog_title_result)
                message(R.string.message_no_errors_detected)
                model.exception?.let { exception ->
                    message(text = exception.message)
                }
                positiveButton(R.string.action_ok)
            }
        })
        viewModel.contentEvent.observe(viewLifecycleOwner, Observer { content ->
            binding.editor.processText(content.text)
            binding.editor.undoStack = content.undoStack
            binding.editor.redoStack = content.redoStack
            binding.editor.scrollX = content.documentModel.scrollX
            binding.editor.scrollY = content.documentModel.scrollY
            binding.editor.setSelection(
                content.documentModel.selectionStart,
                content.documentModel.selectionEnd
            )
        })

        // region PREFERENCES

        viewModel.preferenceEvent.observe(viewLifecycleOwner, Observer { queue ->
            while (queue != null && queue.isNotEmpty()) {
                when (val event = queue.poll()) {
                    is PreferenceEvent.Theme -> binding.editor.theme = event.value
                    is PreferenceEvent.FontSize -> {
                        val newConfiguration = binding.editor.configuration.copy(fontSize = event.value)
                        binding.editor.configuration = newConfiguration
                    }
                    is PreferenceEvent.FontType -> {
                        val newConfiguration = binding.editor.configuration.copy(
                            fontType = TypefaceFactory.create(requireContext(), event.value)
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

        viewModel.observePreferences() // and loadFiles()
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

    private fun loadDocument(position: Int) {
        if (position > -1) { // if there's at least 1 tab
            val document = viewModel.tabsList[position]
            viewModel.loadFile(document)
        }
    }

    private fun saveDocument(position: Int) {
        if (position > -1) { // if there's at least 1 tab
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
            viewModel.stateLoadingDocuments.set(true) // show loading indicator
            binding.editor.clearText() //TTL Exception bypass
        }
    }

    private fun removeDocument(position: Int) {
        val document = viewModel.tabsList[position]
        viewModel.tabsList.removeAt(position)
        viewModel.stateNothingFound.set(viewModel.tabsList.isEmpty())
        viewModel.deleteCache(document)
    }

    // region TABS

    private fun addTab(documentModel: DocumentModel, selection: Boolean) {
        binding.tabDocumentLayout.newTab(documentModel.name, R.layout.item_tab_document) { tab ->
            val closeIcon = tab.customView?.findViewById<View>(R.id.item_icon)
            closeIcon?.setOnClickListener {
                removeTab(tab.position)
            }
            tab.view.setOnLongClickListener {
                val wrapper = ContextThemeWrapper(it.context, R.style.Widget_Darcula_PopupMenu)
                val popupMenu = PopupMenu(wrapper, it)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_close -> removeTab(tab.position)
                        R.id.action_close_others -> removeOtherTabs(tab.position)
                        R.id.action_close_all -> removeAllTabs(tab.position)
                    }
                    return@setOnMenuItemClickListener true
                }
                popupMenu.inflate(R.menu.menu_document)
                popupMenu.makeRightPaddingRecursively()
                popupMenu.show()
                return@setOnLongClickListener true
            }
            if (selection) {
                tab.select()
            }
        }
    }

    private fun removeAllTabs(position: Int) {
        removeOtherTabs(position)
        removeTab(binding.tabDocumentLayout.selectedTabPosition)
    }

    private fun removeOtherTabs(position: Int) {
        val tabCount = binding.tabDocumentLayout.tabCount - 1
        for (index in tabCount downTo 0) {
            if (index != position) {
                removeTab(index)
            }
        }
    }

    private fun removeTab(index: Int) {
        val selectedIndex = binding.tabDocumentLayout.selectedTabPosition
        if (index == selectedIndex) {
            binding.editor.clearText() //TTL Exception bypass
            closeKeyboard() // Обход бага, когда после удаления вкладки можно было редактировать в ней текст
        }
        // Обход бага, когда после удаления вкладки индикатор не обновлял свою позицию
        if (index < selectedIndex) {
            binding.tabDocumentLayout.setScrollPosition(selectedIndex - 1, 0f, false)
        }
        removeDocument(index)
        binding.tabDocumentLayout.removeTabAt(index)
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
        val position = binding.tabDocumentLayout.selectedTabPosition
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
        val position = binding.tabDocumentLayout.selectedTabPosition
        if (position > -1) {
            removeTab(position)
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
        val position = binding.tabDocumentLayout.selectedTabPosition
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
        val position = binding.tabDocumentLayout.selectedTabPosition
        if (position > -1) {
            MaterialDialog(requireContext()).show {
                title(R.string.dialog_title_find)
                customView(R.layout.dialog_find)
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
        val position = binding.tabDocumentLayout.selectedTabPosition
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
        val position = binding.tabDocumentLayout.selectedTabPosition
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
            val position = binding.tabDocumentLayout.selectedTabPosition
            if (position > -1) {
                viewModel.parse(position, binding.editor.getProcessedText())
            } else {
                viewModel.toastEvent.value = R.string.message_no_open_files
            }
        } else {
            DialogStore.Builder(requireContext()).show()
        }
    }

    override fun onInsertColorButton() {
        if (requireContext().isUltimate()) {
            val position = binding.tabDocumentLayout.selectedTabPosition
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