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

package com.lightteam.modpeide.presentation.main.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.ActivityMainBinding
import com.lightteam.modpeide.domain.model.DocumentModel
import com.lightteam.modpeide.presentation.base.activities.BaseActivity
import com.lightteam.modpeide.presentation.common.dialogs.DialogStore
import com.lightteam.modpeide.presentation.main.activities.interfaces.OnPanelClickListener
import com.lightteam.modpeide.presentation.main.activities.utils.ToolbarManager
import com.lightteam.modpeide.presentation.main.customview.ExtendedKeyboard
import com.lightteam.modpeide.presentation.main.viewmodel.MainViewModel
import com.lightteam.modpeide.presentation.settings.activities.SettingsActivity
import com.lightteam.modpeide.utils.commons.TypefaceFactory
import com.lightteam.modpeide.utils.extensions.launchActivity
import com.lightteam.modpeide.utils.extensions.makeRightPaddingRecursively
import com.lightteam.modpeide.utils.extensions.toHexString
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.io.File
import javax.inject.Inject

class MainActivity : BaseActivity(),
    OnPanelClickListener,
    ExtendedKeyboard.OnKeyListener,
    TabLayout.OnTabSelectedListener {

    companion object {
        const val REQUEST_READ_WRITE = 1 // Запрос разрешений через диалог
        const val REQUEST_READ_WRITE2 = 2 // Запрос разрешений через настройки системы
    }

    @Inject
    lateinit var viewModel: MainViewModel
    @Inject
    lateinit var toolbarManager: ToolbarManager

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        toolbarManager.bind(binding)
        onConfigurationChanged(resources.configuration)
        setupListeners()
        setupObservers()
        checkPermissions()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toolbarManager.orientation = newConfig.orientation
    }

    override fun onBackPressed() {
        if(!closeDrawersIfNecessary()) {
            if(viewModel.backEvent.value!!) {
                MaterialDialog(this).show {
                    title(R.string.dialog_title_exit)
                    message(R.string.dialog_message_exit)
                    negativeButton(R.string.action_no)
                    positiveButton(R.string.action_yes, click = {
                        finish()
                    })
                }
            } else {
                finish()
            }
        }
    }

    // region PERMISSIONS

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_READ_WRITE -> {
                viewModel.hasAccessEvent.value = grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        checkPermissions()
    }

    private fun checkPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            viewModel.hasAccessEvent.value = true

            // Check if user opened a file from another app
            if(intent.action == Intent.ACTION_VIEW) {
                //path must be started with /storage/emulated/0/...
                viewModel.openDocument(File(intent.data?.path))
            }
        }
    }

    // endregion PERMISSIONS

    // region TABS

    override fun onPause() {
        super.onPause()
        saveDocument(binding.tabDocumentLayout.selectedTabPosition)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadDocument(binding.tabDocumentLayout.selectedTabPosition)
    }

    override fun onTabReselected(tab: TabLayout.Tab) {}
    override fun onTabUnselected(tab: TabLayout.Tab) {
        saveDocument(tab.position)
        closeKeyboard() // Обход бага, когда после переключения вкладок редактирование не работало
        // (позиция курсора не менялась с предыдущей вкладки)
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        viewModel.loadDocument(tab.position)
    }

    // endregion TABS

    private fun setupListeners() {
        binding.tabDocumentLayout.addOnTabSelectedListener(this)
        binding.extendedKeyboard.setHasFixedSize(true)
        binding.extendedKeyboard.setKeyListener(this)
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerOpened(drawerView: View) {
                closeKeyboard()
            }
        })
        binding.scroller.link(binding.editor)
    }

    private fun setupObservers() {
        viewModel.toastEvent.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
        viewModel.analysisEvent.observe(this, Observer { model ->
            MaterialDialog(this).show {
                title(R.string.dialog_title_result)
                if(model.exception == null) {
                    message(R.string.message_no_errors_detected)
                } else {
                    message(text = model.exception!!.message)
                }
                positiveButton(R.string.action_ok)
            }
        })
        viewModel.documentAllTabsEvent.observe(this, Observer { list ->
            list.forEach { addTab(it, false) }
        })
        viewModel.documentTabEvent.observe(this, Observer { document ->
            closeDrawersIfNecessary()
            addTab(document, true)
        })
        viewModel.documentTextEvent.observe(this, Observer {
            binding.editor.setFacadeText(it)
        })
        viewModel.documentLoadedEvent.observe(this, Observer { loadedDocument ->
            binding.editor.scrollX = loadedDocument.scrollX
            binding.editor.scrollY = loadedDocument.scrollY
            binding.editor.setSelection(
                loadedDocument.selectionStart,
                loadedDocument.selectionEnd
            )
        })
        viewModel.documentStacksEvent.observe(this, Observer { pair ->
            binding.editor.undoStack = pair.first
            binding.editor.redoStack = pair.second
        })
        viewModel.editorEvents(binding.editor)
        KeyboardVisibilityEvent.registerEventListener(this) { isOpen ->
            if(viewModel.extendedKeyboardEvent.value!!) {
                binding.extendedKeyboard.visibility = if (isOpen) View.VISIBLE else View.GONE
            } else {
                binding.extendedKeyboard.visibility = View.GONE
            }
        }

        // region PREFERENCES

        viewModel.themeEvent.observe(this, Observer { newTheme ->
            binding.editor.theme = newTheme
        })
        viewModel.fullscreenEvent.observe(this, Observer { isFullscreen ->
            if(isFullscreen) {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        })
        viewModel.fontSizeEvent.observe(this, Observer { fontSize ->
            val newConfiguration = binding.editor.configuration.copy(fontSize = fontSize)
            binding.editor.configuration = newConfiguration
        })
        viewModel.fontTypeEvent.observe(this, Observer { fontType ->
            val newConfiguration = binding.editor.configuration.copy(
                fontType = TypefaceFactory.create(this, fontType)
            )
            binding.editor.configuration = newConfiguration
        })
        viewModel.wordWrapEvent.observe(this, Observer { wordWrap ->
            val newConfiguration = binding.editor.configuration.copy(wordWrap = wordWrap)
            binding.editor.configuration = newConfiguration
        })
        viewModel.codeCompletionEvent.observe(this, Observer { completion ->
            val newConfiguration = binding.editor.configuration.copy(codeCompletion = completion)
            binding.editor.configuration = newConfiguration
        })
        viewModel.pinchZoomEvent.observe(this, Observer { pinchZoom ->
            val newConfiguration = binding.editor.configuration.copy(pinchZoom = pinchZoom)
            binding.editor.configuration = newConfiguration
        })
        viewModel.highlightLineEvent.observe(this, Observer { highlight ->
            val newConfiguration = binding.editor.configuration.copy(highlightCurrentLine = highlight)
            binding.editor.configuration = newConfiguration
        })
        viewModel.highlightDelimitersEvent.observe(this, Observer { highlight ->
            val newConfiguration = binding.editor.configuration.copy(highlightDelimiters = highlight)
            binding.editor.configuration = newConfiguration
        })
        viewModel.softKeyboardEvent.observe(this, Observer { softKeyboard ->
            val newConfiguration = binding.editor.configuration.copy(softKeyboard = softKeyboard)
            binding.editor.configuration = newConfiguration
        })
        viewModel.imeKeyboardEvent.observe(this, Observer { imeKeyboard ->
            val newConfiguration = binding.editor.configuration.copy(imeKeyboard = imeKeyboard)
            binding.editor.configuration = newConfiguration
        })
        viewModel.autoIndentationEvent.observe(this, Observer { autoIndentation ->
            val newConfiguration = binding.editor.configuration.copy(autoIndentation = autoIndentation)
            binding.editor.configuration = newConfiguration
        })
        viewModel.autoCloseBracketsEvent.observe(this, Observer { autoCloseBrackets ->
            val newConfiguration = binding.editor.configuration.copy(autoCloseBrackets = autoCloseBrackets)
            binding.editor.configuration = newConfiguration
        })
        viewModel.autoCloseQuotesEvent.observe(this, Observer { autoCloseQuotes ->
            val newConfiguration = binding.editor.configuration.copy(autoCloseQuotes = autoCloseQuotes)
            binding.editor.configuration = newConfiguration
        })

        // endregion PREFERENCES

        viewModel.observePreferences()
        viewModel.loadAllFiles()
    }

    private fun addTab(documentModel: DocumentModel, select: Boolean) {
        val index = viewModel.addDocument(documentModel)
        if(index == -1) {
            val tab = binding.tabDocumentLayout.newTab()
            tab.text = documentModel.name
            tab.setCustomView(R.layout.item_tab_document)

            val closeIcon = tab.customView?.findViewById<View>(R.id.item_icon)
            closeIcon?.setOnClickListener {
                removeTab(tab.position)
            }
            (tab.customView?.parent as View).setOnLongClickListener { view ->
                val wrapper = ContextThemeWrapper(view.context, R.style.Theme_Darcula_PopupMenu)
                val popupMenu = PopupMenu(wrapper, view)
                popupMenu.setOnMenuItemClickListener { item ->
                    when(item.itemId) {
                        R.id.action_close -> removeTab(tab.position)
                        R.id.action_close_others -> removeOtherTabs(tab.position)
                        R.id.action_close_all -> removeAllTabs()
                    }
                    return@setOnMenuItemClickListener true
                }
                popupMenu.inflate(R.menu.menu_document)
                popupMenu.makeRightPaddingRecursively()
                popupMenu.show()
                return@setOnLongClickListener true
            }

            binding.tabDocumentLayout.addTab(tab)
            if(select) {
                binding.tabDocumentLayout.post { tab.select() }
            }
        } else {
            binding.tabDocumentLayout.getTabAt(index)?.select()
        }
    }

    private fun removeAllTabs() {
        for(i in binding.tabDocumentLayout.tabCount - 1 downTo 0) {
            removeTab(i)
        }
    }

    private fun removeOtherTabs(position: Int) {
        for(index in binding.tabDocumentLayout.tabCount - 1 downTo 0) {
            if (index != position) {
                removeTab(index)
            }
        }
    }

    private fun removeTab(index: Int) {
        val position = viewModel.removeDocument(index)
        val selectedIndex = binding.tabDocumentLayout.selectedTabPosition

        if(position == selectedIndex) {
            binding.editor.clearText() //TTL Exception Bypass
        }
        binding.tabDocumentLayout.removeTabAt(position)

        // Обход бага, когда после удаления вкладки индикатор не обновляет свою позицию
        if(position < selectedIndex) {
            binding.tabDocumentLayout.setScrollPosition(selectedIndex - 1, 0f, false)
        }

        // Обход бага, когда после удаления вкладки можно было редактировать в ней текст
        if(position == selectedIndex) {
            closeKeyboard()
        }
    }

    private fun saveDocument(position: Int) {
        val selectedDocument = viewModel.getDocument(position)
        selectedDocument?.let {
            val document = it.copy(
                scrollX = binding.editor.scrollX,
                scrollY = binding.editor.scrollY,
                selectionStart = binding.editor.selectionStart,
                selectionEnd = binding.editor.selectionEnd
            )
            viewModel.saveToCache(document, binding.editor.getFacadeText())
            viewModel.saveUndoStack(document, binding.editor.undoStack)
            viewModel.saveRedoStack(document, binding.editor.redoStack)
            viewModel.documentLoadingIndicator.set(true)
            binding.editor.clearText() //TTL Exception bypass
        }
    }

    private fun closeKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val windowToken = currentFocus?.windowToken
        val hideType = InputMethodManager.HIDE_NOT_ALWAYS
        inputManager.hideSoftInputFromWindow(windowToken, hideType)
    }

    @SuppressLint("RtlHardcoded")
    private fun closeDrawersIfNecessary(): Boolean {
        val isOpen = binding.drawerLayout.isDrawerOpen(Gravity.LEFT)
        if(isOpen) {
            binding.drawerLayout.closeDrawer(Gravity.LEFT)
        }
        return isOpen
    }

    // region OTHER

    override fun onKey(char: String) {
        binding.editor.insert(char)
    }

    @SuppressLint("RtlHardcoded")
    override fun onDrawerButton() {
        binding.drawerLayout.openDrawer(Gravity.LEFT)
    }

    override fun onNewButton() = onDrawerButton()

    override fun onOpenButton() {
        onDrawerButton()
        viewModel.toastEvent.value = R.string.message_select_file
    }

    override fun onSaveButton() {
        val position = binding.tabDocumentLayout.selectedTabPosition
        if(position != -1) {
            viewModel.getDocument(position)?.let {
                viewModel.saveFile(it, binding.editor.getFacadeText())
                viewModel.saveToCache(it, binding.editor.getFacadeText())
                viewModel.saveUndoStack(it, binding.editor.undoStack)
                viewModel.saveRedoStack(it, binding.editor.redoStack)
            }
        } else {
            viewModel.toastEvent.value = R.string.message_no_open_files
        }
    }

    override fun onPropertiesButton() {
        val position = binding.tabDocumentLayout.selectedTabPosition
        if(position != -1) {
            viewModel.getDocument(position)?.let {
                viewModel.propertiesOf(it)
            }
        } else {
            viewModel.toastEvent.value = R.string.message_no_open_files
        }
    }

    override fun onCloseButton() {
        val position = binding.tabDocumentLayout.selectedTabPosition
        if(position != -1) {
            removeTab(position)
        } else {
            viewModel.toastEvent.value = R.string.message_no_open_files
        }
    }

    override fun onCutButton() {
        if(binding.editor.hasSelection()) {
            binding.editor.cut()
        } else {
            viewModel.toastEvent.value = R.string.message_nothing_to_cut
        }
    }

    override fun onCopyButton() {
        if(binding.editor.hasSelection()) {
            binding.editor.copy()
        } else {
            viewModel.toastEvent.value = R.string.message_nothing_to_copy
        }
    }

    override fun onPasteButton() {
        val position = binding.tabDocumentLayout.selectedTabPosition
        if(binding.editor.hasPrimaryClip() && position != -1) {
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
        if(position != -1) {
            MaterialDialog(this).show {
                title(R.string.dialog_title_find)
                customView(R.layout.dialog_find)
                negativeButton(R.string.action_cancel)
                positiveButton(R.string.action_find, click = {
                    val textToFind = it.getCustomView()
                        .findViewById<TextInputEditText>(R.id.input).text.toString()
                    val isMatchCaseChecked = it.getCustomView()
                        .findViewById<CheckBox>(R.id.box_matchCase).isChecked
                    val isRegExpChecked = it.getCustomView()
                        .findViewById<CheckBox>(R.id.box_regExp).isChecked
                    val isWordsOnlyChecked = it.getCustomView()
                        .findViewById<CheckBox>(R.id.box_wordOnly).isChecked

                    if(textToFind.isNotEmpty() && textToFind.isNotBlank()) {
                        binding.editor.find(textToFind, isMatchCaseChecked, isRegExpChecked, isWordsOnlyChecked)
                    } else {
                        viewModel.toastEvent.value = R.string.message_enter_the_text
                    }
                })
            }
        } else {
            viewModel.toastEvent.value = R.string.message_no_open_files
        }
    }

    override fun onReplaceAllButton() {
        val position = binding.tabDocumentLayout.selectedTabPosition
        if(position != -1) {
            MaterialDialog(this).show {
                title(R.string.dialog_title_replace_all)
                customView(R.layout.dialog_replace_all)
                negativeButton(R.string.action_cancel)
                positiveButton(R.string.action_replace_all, click = {
                    val textReplaceWhat = it.getCustomView()
                        .findViewById<TextInputEditText>(R.id.input).text.toString()
                    val textReplaceWith = it.getCustomView()
                        .findViewById<TextInputEditText>(R.id.input2).text.toString()

                    if(textReplaceWhat.isNotEmpty()) {
                        binding.editor.replaceAll(textReplaceWhat, textReplaceWith)
                    } else {
                        viewModel.toastEvent.value = R.string.message_enter_the_text
                    }
                })
            }
        } else {
            viewModel.toastEvent.value = R.string.message_no_open_files
        }
    }

    override fun onGoToLineButton() {
        val position = binding.tabDocumentLayout.selectedTabPosition
        if(position != -1) {
            MaterialDialog(this).show {
                title(R.string.dialog_title_goto_line)
                input(hintRes = R.string.hint_line, inputType = InputType.TYPE_CLASS_NUMBER)
                negativeButton(R.string.action_cancel)
                positiveButton(R.string.action_go_to, click = {
                    val toLine = getInputField().text.toString().toInt()
                    val realLine = toLine - 1 // т.к первая линия = 0
                    when {
                        realLine < 0 ->
                            viewModel.toastEvent.value = R.string.message_line_above_than_0
                        realLine < binding.editor.getArrayLineCount() ->
                            binding.editor.gotoLine(realLine)
                        else ->
                            viewModel.toastEvent.value = R.string.message_line_not_exists
                    }
                })
            }
        } else {
            viewModel.toastEvent.value = R.string.message_no_open_files
        }
    }

    override fun onCodeAnalysisButton() {
        if(viewModel.isUltimate()) {
            val position = binding.tabDocumentLayout.selectedTabPosition
            if(position != -1) {
                viewModel.analyze(viewModel.getDocument(position)!!.name, binding.editor.getFacadeText())
            } else {
                viewModel.toastEvent.value = R.string.message_no_open_files
            }
        } else {
            DialogStore.Builder(this).show()
        }
    }

    override fun onInsertColorButton() {
        if(viewModel.isUltimate()) {
            val position = binding.tabDocumentLayout.selectedTabPosition
            if(position != -1) {
                MaterialDialog(this).show {
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
            DialogStore.Builder(this).show()
        }
    }

    override fun onUndoButton() = binding.editor.undo()
    override fun onRedoButton() = binding.editor.redo()

    override fun onSettingsButton() {
        launchActivity<SettingsActivity>()
    }

    // endregion OTHER
}
