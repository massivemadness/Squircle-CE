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

package com.brackeys.ui.feature.fonts.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.brackeys.ui.data.converter.FontConverter
import com.brackeys.ui.data.settings.SettingsManager
import com.brackeys.ui.data.utils.schedulersIoToMain
import com.brackeys.ui.database.AppDatabase
import com.brackeys.ui.domain.model.font.FontModel
import com.brackeys.ui.domain.providers.rx.SchedulersProvider
import com.brackeys.ui.feature.base.viewmodel.BaseViewModel
import com.brackeys.ui.utils.event.SingleLiveEvent
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import java.io.File

class FontsViewModel @ViewModelInject constructor(
    private val schedulersProvider: SchedulersProvider,
    private val settingsManager: SettingsManager,
    private val appDatabase: AppDatabase
) : BaseViewModel() {

    val fontsEvent: MutableLiveData<List<FontModel>> = MutableLiveData()
    val validationEvent: MutableLiveData<Boolean> = MutableLiveData()

    val selectEvent: SingleLiveEvent<String> = SingleLiveEvent()
    val insertEvent: SingleLiveEvent<String> = SingleLiveEvent()
    val removeEvent: SingleLiveEvent<String> = SingleLiveEvent()

    var searchQuery = ""

    fun fetchFonts() {
        appDatabase.fontDao().loadAll(searchQuery)
            .map { it.map(FontConverter::toModel) }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { fontsEvent.value = it }
            .disposeOnViewModelDestroy()
    }

    fun selectFont(fontModel: FontModel) {
        settingsManager.getFontType().set(fontModel.fontPath)
        selectEvent.value = fontModel.fontName
    }

    fun removeFont(fontModel: FontModel) {
        Completable
            .fromAction {
                appDatabase.fontDao().delete(FontConverter.toEntity(fontModel))
                if (settingsManager.getFontType().get() == fontModel.fontPath) {
                    settingsManager.getFontType().delete()
                }
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy {
                removeEvent.value = fontModel.fontName
                fetchFonts() // Update list
            }
            .disposeOnViewModelDestroy()
    }

    fun insertFont(fontModel: FontModel) {
        Completable
            .fromAction {
                appDatabase.fontDao().insert(FontConverter.toEntity(fontModel))
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { insertEvent.value = fontModel.fontName }
            .disposeOnViewModelDestroy()
    }

    fun validateInput(fontName: String, fontPath: String) {
        val isFontNameValid = fontName.trim().isNotBlank()
        val isFontPathValid = fontPath.trim().isNotBlank() && File(fontPath)
            .run { exists() && name.endsWith(".ttf") }
        validationEvent.value = isFontNameValid && isFontPathValid
    }
}