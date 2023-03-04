/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.editor.ui.manager

import androidx.core.view.isVisible
import com.blacksquircle.ui.feature.editor.databinding.FragmentEditorBinding
import com.blacksquircle.ui.feature.editor.ui.adapter.KeyAdapter
import com.blacksquircle.ui.feature.settings.domain.model.KeyModel

class KeyboardManager(private val listener: OnKeyboardListener) {

    var mode: Mode = Mode.NONE
        set(value) {
            field = value
            updatePanel()
        }

    private lateinit var binding: FragmentEditorBinding

    private var keyAdapter: KeyAdapter? = null

    fun bind(binding: FragmentEditorBinding) {
        this.binding = binding
        updatePanel()

        binding.extendedKeyboard.setHasFixedSize(true)
        binding.extendedKeyboard.adapter = KeyAdapter { keyModel ->
            listener.onKeyButton(keyModel.value)
        }.also {
            keyAdapter = it
        }
    }

    fun submitList(keys: List<KeyModel>) {
        keyAdapter?.submitList(keys)
    }

    private fun updatePanel() {
        when (mode) {
            Mode.KEYBOARD -> {
                binding.keyboard.isVisible = true
            }
            Mode.NONE -> {
                binding.keyboard.isVisible = false
            }
        }
    }

    interface OnKeyboardListener {
        fun onKeyButton(char: Char): Boolean
    }

    enum class Mode {
        KEYBOARD,
        NONE,
    }
}