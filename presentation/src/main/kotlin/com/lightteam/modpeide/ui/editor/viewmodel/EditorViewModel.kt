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

package com.lightteam.modpeide.ui.editor.viewmodel

import androidx.databinding.ObservableBoolean
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.data.utils.extensions.schedulersIoToMain
import com.lightteam.modpeide.domain.providers.SchedulersProvider
import com.lightteam.modpeide.domain.repository.FileRepository
import com.lightteam.modpeide.ui.base.viewmodel.BaseViewModel
import com.lightteam.modpeide.ui.editor.customview.TextProcessor
import com.lightteam.modpeide.utils.event.SingleLiveEvent
import com.lightteam.modpeide.utils.theming.ThemeFactory
import io.reactivex.rxkotlin.subscribeBy

class EditorViewModel(
    private val schedulersProvider: SchedulersProvider,
    private val fileRepository: FileRepository,
    private val preferenceHandler: PreferenceHandler
) : BaseViewModel() {

    companion object {
        private const val TAG = "EditorViewModel"
    }

    // region UI

    val canUndo: ObservableBoolean = ObservableBoolean(false) //Кликабельность кнопки Undo
    val canRedo: ObservableBoolean = ObservableBoolean(false) //Кликабельность кнопки Redo

    // endregion UI

    // region EVENTS

    val toastEvent: SingleLiveEvent<Int> = SingleLiveEvent() //Отображение сообщений

    // endregion EVENTS

    // region PREFERENCES

    val themeEvent: SingleLiveEvent<TextProcessor.Theme> = SingleLiveEvent() //Тема редактора
    val fullscreenEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Полноэкранный режим
    val backEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Подтверждение выхода

    val fontSizeEvent: SingleLiveEvent<Float> = SingleLiveEvent() //Размер шрифта
    val fontTypeEvent: SingleLiveEvent<String> = SingleLiveEvent() //Тип шрифта

    val resumeSessionEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Повторное открытие вкладок после выхода
    val tabLimitEvent: SingleLiveEvent<Int> = SingleLiveEvent() //Лимит вкладок

    val wordWrapEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Смещать текст на новую строку если нет места
    val codeCompletionEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Автодополнение кода
    val pinchZoomEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Жест масштабирования текста
    val highlightLineEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Подсветка текущей строки
    val highlightDelimitersEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Подсветка ближайших скобок

    val extendedKeyboardEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Отображать доп. символы
    val softKeyboardEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Упрощенная клавиатура (landscape orientation)

    val autoIndentationEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Отступы при переходе на новую строку
    val autoCloseBracketsEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Автоматическое закрытие скобок
    val autoCloseQuotesEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Автоматическое закрытие кавычек

    // endregion PREFERENCES

    // region PREFERENCES

    fun observePreferences() {

        //Theme
        preferenceHandler.getTheme()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { themeEvent.value = ThemeFactory.create(it) }
            .disposeOnViewModelDestroy()

        //Fullscreen Mode
        preferenceHandler.getFullscreenMode()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { fullscreenEvent.value = it }
            .disposeOnViewModelDestroy()

        //Confirm Exit
        preferenceHandler.getConfirmExit()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { backEvent.value = it }
            .disposeOnViewModelDestroy()

        //Font Size
        preferenceHandler.getFontSize()
            .asObservable()
            .map { it.toFloat() }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { fontSizeEvent.value = it }
            .disposeOnViewModelDestroy()

        //Font Type
        preferenceHandler.getFontType()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { fontTypeEvent.value = it }
            .disposeOnViewModelDestroy()

        //Resume Session
        preferenceHandler.getResumeSession()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { resumeSessionEvent.value = it }
            .disposeOnViewModelDestroy()

        //Tab Limit
        preferenceHandler.getTabLimit()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { tabLimitEvent.value = it }
            .disposeOnViewModelDestroy()

        //Word Wrap
        preferenceHandler.getWordWrap()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { wordWrapEvent.value = it }
            .disposeOnViewModelDestroy()

        //Code Completion
        preferenceHandler.getCodeCompletion()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { codeCompletionEvent.value = it }
            .disposeOnViewModelDestroy()

        //Pinch Zoom
        preferenceHandler.getPinchZoom()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { pinchZoomEvent.value = it }
            .disposeOnViewModelDestroy()

        //Highlight Current Line
        preferenceHandler.getHighlightCurrentLine()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { highlightLineEvent.value = it }
            .disposeOnViewModelDestroy()

        //Highlight Matching Delimiters
        preferenceHandler.getHighlightMatchingDelimiters()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { highlightDelimitersEvent.value = it }
            .disposeOnViewModelDestroy()

        //Extended Keyboard
        preferenceHandler.getExtendedKeyboard()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { extendedKeyboardEvent.value = it }
            .disposeOnViewModelDestroy()

        //Soft Keyboard
        preferenceHandler.getSoftKeyboard()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { softKeyboardEvent.value = it }
            .disposeOnViewModelDestroy()

        //Auto Indentation
        preferenceHandler.getAutoIndentation()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { autoIndentationEvent.value = it }
            .disposeOnViewModelDestroy()

        //Auto-close Brackets
        preferenceHandler.getAutoCloseBrackets()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { autoCloseBracketsEvent.value = it }
            .disposeOnViewModelDestroy()

        //Auto-close Quotes
        preferenceHandler.getAutoCloseQuotes()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { autoCloseQuotesEvent.value = it }
            .disposeOnViewModelDestroy()
    }

    // endregion PREFERENCES
}