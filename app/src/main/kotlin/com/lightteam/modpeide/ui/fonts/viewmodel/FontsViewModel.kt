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

package com.lightteam.modpeide.ui.fonts.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lightteam.modpeide.data.converter.FontConverter
import com.lightteam.modpeide.data.settings.SettingsManager
import com.lightteam.modpeide.data.utils.extensions.schedulersIoToMain
import com.lightteam.modpeide.database.AppDatabase
import com.lightteam.modpeide.domain.model.font.FontModel
import com.lightteam.modpeide.domain.providers.rx.SchedulersProvider
import com.lightteam.modpeide.ui.base.viewmodel.BaseViewModel
import com.lightteam.modpeide.utils.event.SingleLiveEvent
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