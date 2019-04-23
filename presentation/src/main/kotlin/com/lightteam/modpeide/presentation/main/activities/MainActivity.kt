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
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider.getUriForFile
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.lightteam.modpeide.BaseApplication
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.utils.extensions.endsWith
import com.lightteam.modpeide.databinding.ActivityMainBinding
import com.lightteam.modpeide.domain.model.DocumentModel
import com.lightteam.modpeide.presentation.base.activities.BaseActivity
import com.lightteam.modpeide.presentation.main.activities.interfaces.OnPanelClickListener
import com.lightteam.modpeide.presentation.main.activities.utils.ToolbarManager
import com.lightteam.modpeide.presentation.main.adapters.DocumentAdapter
import com.lightteam.modpeide.presentation.main.customview.ExtendedKeyboard
import com.lightteam.modpeide.presentation.main.viewmodel.MainViewModel
import com.lightteam.modpeide.presentation.settings.activities.SettingsActivity
import com.lightteam.modpeide.utils.commons.MenuUtils
import com.lightteam.modpeide.utils.commons.TypefaceFactory
import com.lightteam.modpeide.utils.extensions.launchActivity
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.io.File
import javax.inject.Inject

class MainActivity : BaseActivity(),
    OnPanelClickListener,
    ExtendedKeyboard.OnKeyListener,
    TabLayout.OnTabSelectedListener {

    companion object {
        const val REQUEST_READ_WRITE = 1 //Запрос на разрешения через диалог
        const val REQUEST_READ_WRITE2 = 2 //Запрос на разрешения через активность настроек
    }

    @Inject
    lateinit var viewModel: MainViewModel
    @Inject
    lateinit var toolbarManager: ToolbarManager
    @Inject
    lateinit var adapter: DocumentAdapter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.observePreferences()
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
        toolbarManager.setOrientation(newConfig.orientation)
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
                viewModel.addDocument(File(intent.data?.path))
            }
        }
    }

    // endregion PERMISSIONS

    // region TABS

    override fun onPause() {
        super.onPause()
        val selectedDocument = adapter.get(binding.tabDocumentLayout.selectedTabPosition)
        selectedDocument?.let {
            val document = it.copy(
                scrollX = binding.editor.scrollX,
                scrollY = binding.editor.scrollY,
                selectionStart = binding.editor.selectionStart,
                selectionEnd = binding.editor.selectionEnd
            )
            adapter.add(document) //update adapter
            viewModel.saveToCache(document, binding.editor.getFacadeText().toString())
            viewModel.documentLoadingIndicator.set(true)
            binding.editor.clearText() //TTL Exception bypass
        }
    }

    override fun onResume() {
        super.onResume()
        val selectedDocument = adapter.get(binding.tabDocumentLayout.selectedTabPosition)
        selectedDocument?.let {
            viewModel.loadFile(it)
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab) {}
    override fun onTabUnselected(tab: TabLayout.Tab) {
        val selectedDocument = adapter.get(tab.position)
        selectedDocument?.let {
            val document = it.copy(
                scrollX = binding.editor.scrollX,
                scrollY = binding.editor.scrollY,
                selectionStart = binding.editor.selectionStart,
                selectionEnd = binding.editor.selectionEnd
            )
            adapter.add(document) //update adapter
            viewModel.saveToCache(document, binding.editor.getFacadeText().toString())
            binding.editor.clearText()
        }
        closeKeyboard() // Обход бага, когда после переключения вкладок редактирование не работало
        // (позиция курсора менялась программно)
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        val selectedDocument = adapter.get(tab.position)
        selectedDocument?.let {
            viewModel.loadFile(it)
        }
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
        viewModel.documentAllTabsEvent.observe(this, Observer { list ->
            list.forEach { addTab(it, false) }
        })
        viewModel.documentTabEvent.observe(this, Observer { document ->
            if(document.name.endsWith(viewModel.unopenableExtensions)) { //Если расширение не поддерживается
                try { //Открытие файла через соответствующую программу
                    val uri = getUriForFile(this, "$packageName.provider", File(document.path))
                    val mime = contentResolver.getType(uri)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, mime)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    viewModel.toastEvent.value = R.string.message_cannot_be_opened
                }
            } else {
                closeDrawersIfNecessary()
                if(adapter.size() < viewModel.tabLimitEvent.value!!) {
                    addTab(document, true)
                } else {
                    viewModel.toastEvent.value = R.string.message_tab_limit_achieved
                }
            }
        })
        viewModel.documentTextEvent.observe(this, Observer {
            binding.editor.setFacadeText(it)
        })
        viewModel.documentLoadedEvent.observe(this, Observer { loadedDocument ->
            binding.editor.scrollX = loadedDocument.scrollX
            binding.editor.scrollY = loadedDocument.scrollY
            binding.editor.setSelection(loadedDocument.selectionStart, loadedDocument.selectionEnd)
        })
        KeyboardVisibilityEvent.registerEventListener(this) { isOpen ->
            if(viewModel.extendedKeyboardEvent.value!!) {
                binding.extendedKeyboard.visibility = if (isOpen) View.VISIBLE else View.GONE
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
        viewModel.highlightLineEvent.observe(this, Observer { highlight ->
            val newConfiguration = binding.editor.configuration.copy(highlightCurrentLine = highlight)
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

        // endregion PREFERENCES

        viewModel.loadAllFiles()
    }

    private fun addTab(documentModel: DocumentModel, select: Boolean) {
        val index = adapter.indexOf(documentModel)
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
                MenuUtils.makeRightPaddingRecursively(view, popupMenu)
                popupMenu.show()
                return@setOnLongClickListener true
            }

            adapter.add(documentModel)
            binding.tabDocumentLayout.addTab(tab)
            if(select) {
                binding.tabDocumentLayout.post { tab.select() }
            }
            viewModel.noDocumentsIndicator.set(adapter.isEmpty())
        } else {
            binding.tabDocumentLayout.getTabAt(index)?.select()
        }
    }

    private fun removeAllTabs() {
        for(i in adapter.count() downTo 0) {
            removeTab(i)
        }
    }

    private fun removeOtherTabs(position: Int) {
        for(index in adapter.count() downTo 0) {
            if (index != position) {
                removeTab(index)
            }
        }
    }

    private fun removeTab(index: Int) {
        val selectedIndex = binding.tabDocumentLayout.selectedTabPosition

        val document = adapter.get(index)
        if(document != null) {
            if(index == selectedIndex) {
                binding.editor.clearText() //TTL Exception Bypass
            }
            adapter.removeAt(index)
            binding.tabDocumentLayout.removeTabAt(index)

            viewModel.removeDocument(document)
            viewModel.noDocumentsIndicator.set(adapter.isEmpty())

            // Обход бага, когда после удаления вкладки индикатор не обновляет свою позицию
            if(index < selectedIndex) {
                binding.tabDocumentLayout.setScrollPosition(selectedIndex - 1, 0f, false)
            }

            // Обход бага, когда после удаления вкладки можно было редактировать в ней текст
            if(index == selectedIndex) {
                closeKeyboard()
            }
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

    private fun showStoreDialog() {
        val dialog = MaterialAlertDialogBuilder(this, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
            .setView(R.layout.dialog_store)
            .show()

        dialog.findViewById<View>(R.id.button_get_it)?.setOnClickListener {
            val packageName = BaseApplication.ULTIMATE
            try {
                val intent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName"))
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                val intent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                startActivity(intent)
            }
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.button_continue)?.setOnClickListener {
            dialog.dismiss()
        }
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
            adapter.get(position)?.let {
                viewModel.saveFile(it, binding.editor.getFacadeText().toString())
            }
        } else {
            viewModel.toastEvent.value = R.string.message_no_open_files
        }
    }

    override fun onPropertiesButton() {
        val position = binding.tabDocumentLayout.selectedTabPosition
        if(position != -1) {
            adapter.get(position)?.let {
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
    }

    override fun onReplaceAllButton() {
    }

    override fun onGoToLineButton() {
    }

    override fun onSyntaxValidatorButton() {
        if(viewModel.isUltimate()) {
            //...
        } else {
            showStoreDialog()
        }
    }

    override fun onInsertColorButton() {
        if(viewModel.isUltimate()) {
            //...
        } else {
            showStoreDialog()
        }
    }

    override fun onUndoButton() {
    }

    override fun onRedoButton() {
    }

    override fun onSettingsButton() {
        launchActivity<SettingsActivity>()
    }

    // endregion OTHER
}
