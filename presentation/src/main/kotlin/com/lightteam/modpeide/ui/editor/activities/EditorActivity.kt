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

package com.lightteam.modpeide.ui.editor.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.tabs.TabLayout
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import com.lightteam.modpeide.BaseApplication
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.ActivityMainBinding
import com.lightteam.modpeide.domain.model.DocumentModel
import com.lightteam.modpeide.ui.base.activities.BaseActivity
import com.lightteam.modpeide.ui.common.dialogs.DialogStore
import com.lightteam.modpeide.ui.editor.activities.interfaces.OnPanelClickListener
import com.lightteam.modpeide.ui.editor.activities.utils.ToolbarManager
import com.lightteam.modpeide.ui.editor.customview.ExtendedKeyboard
import com.lightteam.modpeide.ui.editor.viewmodel.EditorViewModel
import com.lightteam.modpeide.ui.settings.activities.SettingsActivity
import com.lightteam.modpeide.utils.commons.TypefaceFactory
import com.lightteam.modpeide.utils.extensions.launchActivity
import com.lightteam.modpeide.utils.extensions.makeRightPaddingRecursively
import com.lightteam.modpeide.utils.extensions.newTab
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.io.File
import javax.inject.Inject

class EditorActivity : BaseActivity(), DrawerLayout.DrawerListener,
    OnPanelClickListener, TabLayout.OnTabSelectedListener, ExtendedKeyboard.OnKeyListener {

    @Inject
    lateinit var viewModel: EditorViewModel
    @Inject
    lateinit var toolbarManager: ToolbarManager

    private lateinit var binding: ActivityMainBinding
    private var documents: MutableList<DocumentModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        observeViewModel()

        toolbarManager.bind(binding)
        onConfigurationChanged(resources.configuration)

        binding.drawerLayout.addDrawerListener(this)
        binding.tabDocumentLayout.addOnTabSelectedListener(this)
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
            .disposeOnActivityDestroy()
    }

    override fun onPause() {
        super.onPause()
        saveDocument(binding.tabDocumentLayout.selectedTabPosition)
    }

    override fun onResume() {
        super.onResume()
        loadDocument(binding.tabDocumentLayout.selectedTabPosition)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toolbarManager.orientation = newConfig.orientation
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            onBackPressedDispatcher.onBackPressed()
        } else {
            if (viewModel.backEvent.value!!) {
                showExitDialog()
            } else {
                finish()
            }
        }
    }

    // region DRAWER

    override fun onDrawerStateChanged(newState: Int) {}
    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
    override fun onDrawerClosed(drawerView: View) {}
    override fun onDrawerOpened(drawerView: View) {
        closeKeyboard()
    }

    fun closeDrawers() {
        binding.drawerLayout.closeDrawers()
    }

    // endregion DRAWER

    // region TABS

    override fun onTabReselected(tab: TabLayout.Tab?) {}
    override fun onTabUnselected(tab: TabLayout.Tab) {
        saveDocument(tab.position)
        closeKeyboard() // Обход бага, когда после переключения вкладок редактирование не работало
        // (позиция курсора не менялась с предыдущей вкладки)
    }
    override fun onTabSelected(tab: TabLayout.Tab) {
        loadDocument(tab.position)
    }

    private fun addTab(documentModel: DocumentModel, selection: Boolean) {
        viewModel.stateNothingFound.set(documents.isEmpty())
        binding.tabDocumentLayout.newTab(documentModel.name, R.layout.item_tab_document) { tab ->
            val closeIcon = tab.customView?.findViewById<View>(R.id.item_icon)
            closeIcon?.setOnClickListener {
                removeTab(tab.position)
            }
            tab.view.setOnLongClickListener {
                val wrapper = ContextThemeWrapper(it.context, R.style.Theme_Darcula_PopupMenu)
                val popupMenu = PopupMenu(wrapper, it)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
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
            if (selection) {
                tab.select()
            }
        }
    }

    private fun removeAllTabs() {
        for (index in binding.tabDocumentLayout.tabCount - 1 downTo 0) {
            removeTab(index)
        }
    }

    private fun removeOtherTabs(position: Int) {
        for (index in binding.tabDocumentLayout.tabCount - 1 downTo 0) {
            if (index != position) {
                removeTab(index)
            }
        }
    }

    private fun removeTab(index: Int) {
        val position = removeDocument(index)
        val selectedIndex = binding.tabDocumentLayout.selectedTabPosition

        if (position == selectedIndex) {
            binding.editor.clearText() //TTL Exception bypass
        }
        binding.tabDocumentLayout.removeTabAt(position)

        // Обход бага, когда после удаления вкладки индикатор не обновляет свою позицию
        if (position < selectedIndex) {
            binding.tabDocumentLayout.setScrollPosition(selectedIndex - 1, 0f, false)
        }

        // Обход бага, когда после удаления вкладки можно было редактировать в ней текст
        if (position == selectedIndex) {
            closeKeyboard()
        }
    }

    // endregion TABS

    private fun observeViewModel() {
        viewModel.toastEvent.observe(this, Observer {
            showToast(it)
        })
        viewModel.documentsEvent.observe(this, Observer { docs ->
            documents.clear()
            documents.addAll(docs)
            for (document in docs) {
                addTab(document, false)
            }
        })
        viewModel.documentEvent.observe(this, Observer {
            documents.add(it)
            addTab(it, true)
            closeDrawers()
        })
        viewModel.unopenableEvent.observe(this, Observer {
            openFile(it)
        })
        viewModel.textEvent.observe(this, Observer {
            binding.editor.setFacadeText(it)
        })
        viewModel.loadedEvent.observe(this, Observer { document ->
            binding.editor.scrollX = document.scrollX
            binding.editor.scrollY = document.scrollY
            binding.editor.setSelection(
                document.selectionStart,
                document.selectionEnd
            )
        })
        viewModel.stacksEvent.observe(this, Observer { pair ->
            binding.editor.undoStack = pair.first
            binding.editor.redoStack = pair.second
        })

        // region PREFERENCES

        viewModel.themeEvent.observe(this, Observer { newTheme ->
            binding.editor.theme = newTheme
        })
        viewModel.fullscreenEvent.observe(this, Observer { isFullscreen ->
            if (isFullscreen) {
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
        viewModel.extendedKeyboardEvent.observe(this, Observer { isEnabled ->
            KeyboardVisibilityEvent.setEventListener(this) { isOpen ->
                if (isEnabled) {
                    binding.extendedKeyboard.visibility = if (isOpen) View.VISIBLE else View.GONE
                } else {
                    binding.extendedKeyboard.visibility = View.GONE
                }
            }
        })
        viewModel.softKeyboardEvent.observe(this, Observer { softKeyboard ->
            val newConfiguration = binding.editor.configuration.copy(softKeyboard = softKeyboard)
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

        viewModel.observePreferences() // and loadFiles()
    }

    private fun openFile(documentModel: DocumentModel) {
        try { //Открытие файла через подходящую программу
            val packageName = if (viewModel.isUltimate()) BaseApplication.ULTIMATE else BaseApplication.STANDARD
            val uri = FileProvider.getUriForFile(
                this,
                "$packageName.provider",
                File(documentModel.path)
            )
            val mime = contentResolver.getType(uri)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, mime)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            viewModel.toastEvent.value = R.string.message_cannot_be_opened
        }
    }

    private fun loadDocument(position: Int) {
        if (position > -1) { // if there's at least 1 tab
            val document = documents[position]
            viewModel.loadFile(document)
        }
    }

    private fun saveDocument(position: Int) {
        if (position > -1) { // if there's at least 1 tab
            val document = documents[position].copy(
                scrollX = binding.editor.scrollX,
                scrollY = binding.editor.scrollY,
                selectionStart = binding.editor.selectionStart,
                selectionEnd = binding.editor.selectionEnd
            )
            documents[position] = document
            viewModel.saveToCache(document, binding.editor.getFacadeText())
            viewModel.saveUndoStack(document, binding.editor.undoStack)
            viewModel.saveRedoStack(document, binding.editor.redoStack)
            viewModel.stateLoadingDocuments.set(true) // show loading indicator
            binding.editor.clearText() //TTL Exception bypass
        }
    }

    private fun removeDocument(position: Int): Int {
        val document = documents[position]
        documents.removeAt(position)
        viewModel.stateNothingFound.set(documents.isEmpty())
        viewModel.deleteCache(document)
        return position
    }

    // region DIALOGS

    private fun showExitDialog() {
        MaterialDialog(this).show {
            title(R.string.dialog_title_exit)
            message(R.string.dialog_message_exit)
            negativeButton(R.string.action_no)
            positiveButton(R.string.action_yes) {
                finish()
            }
        }
    }

    // endregion DIALOGS

    // region TOOLBAR

    override fun onKey(char: String) {
        binding.editor.insert(char)
    }

    override fun onDrawerButton() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
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
            val document = documents[position]
            viewModel.saveFile(document, binding.editor.getFacadeText())
            viewModel.saveToCache(document, binding.editor.getFacadeText())
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
        DialogStore.Builder(this).show()
    }

    override fun onReplaceAllButton() {
        DialogStore.Builder(this).show()
    }

    override fun onGoToLineButton() {
        DialogStore.Builder(this).show()
    }

    override fun onCodeAnalysisButton() {
        DialogStore.Builder(this).show()
    }

    override fun onInsertColorButton() {
        DialogStore.Builder(this).show()
    }

    override fun onUndoButton() {
        binding.editor.undo()
    }

    override fun onRedoButton() {
        binding.editor.redo()
    }

    override fun onSettingsButton() {
        launchActivity<SettingsActivity>()
    }

    // endregion TOOLBAR
}
