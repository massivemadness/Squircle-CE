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

package com.lightteam.modpeide.ui.presets.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lightteam.modpeide.data.converter.PresetConverter
import com.lightteam.modpeide.data.utils.commons.PreferenceHandler
import com.lightteam.modpeide.data.utils.extensions.schedulersIoToMain
import com.lightteam.modpeide.database.AppDatabase
import com.lightteam.modpeide.domain.model.preset.PresetModel
import com.lightteam.modpeide.domain.providers.rx.SchedulersProvider
import com.lightteam.modpeide.ui.base.viewmodel.BaseViewModel
import com.lightteam.modpeide.utils.event.SingleLiveEvent
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy

class PresetsViewModel @ViewModelInject constructor(
    private val schedulersProvider: SchedulersProvider,
    private val preferenceHandler: PreferenceHandler,
    private val appDatabase: AppDatabase
) : BaseViewModel() {

    val presetsEvent: MutableLiveData<List<PresetModel>> = MutableLiveData()
    val presetEvent: MutableLiveData<PresetModel> = MutableLiveData()
    val validationEvent: MutableLiveData<Boolean> = MutableLiveData()

    val selectEvent: SingleLiveEvent<String> = SingleLiveEvent()
    val insertEvent: SingleLiveEvent<String> = SingleLiveEvent()
    val removeEvent: SingleLiveEvent<String> = SingleLiveEvent()

    var searchQuery = ""

    fun fetchPresets() {
        appDatabase.presetDao().loadAll(searchQuery)
            .map { it.map(PresetConverter::toModel) }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { presetsEvent.value = it }
            .disposeOnViewModelDestroy()
    }

    fun fetchPreset(uuid: String?) {
        appDatabase.presetDao().load(uuid ?: "unknown")
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = {
                    presetEvent.value = PresetConverter.toModel(it)
                },
                onError = {
                    presetEvent.value = PresetConverter.toModel(null)
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun selectPreset(presetModel: PresetModel) {
        preferenceHandler.getKeyboardPreset().set(presetModel.uuid)
        selectEvent.value = presetModel.name
    }

    fun insertPreset(presetModel: PresetModel) {
        Completable
            .fromAction {
                appDatabase.presetDao().insert(PresetConverter.toEntity(presetModel))
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { insertEvent.value = presetModel.name }
            .disposeOnViewModelDestroy()
    }

    fun removePreset(presetModel: PresetModel) {
        Completable
            .fromAction {
                appDatabase.presetDao().delete(PresetConverter.toEntity(presetModel))
                if (preferenceHandler.getKeyboardPreset().get() == presetModel.uuid) {
                    preferenceHandler.getKeyboardPreset().delete()
                }
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy {
                removeEvent.value = presetModel.name
                fetchPresets() // Update list
            }
            .disposeOnViewModelDestroy()
    }

    fun validateInput(presetName: String, presetChars: String) {
        val isPresetNameValid = presetName.trim().isNotBlank()
        val isPresetCharsValid = presetChars.length <= 32 && presetChars.trim().isNotBlank()
        validationEvent.value = isPresetNameValid && isPresetCharsValid
    }
}