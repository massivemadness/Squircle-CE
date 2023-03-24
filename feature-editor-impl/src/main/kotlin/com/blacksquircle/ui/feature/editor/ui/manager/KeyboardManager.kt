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

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.blacksquircle.ui.feature.editor.databinding.FragmentEditorBinding
import com.blacksquircle.ui.feature.editor.ui.adapter.KeyAdapter
import com.blacksquircle.ui.feature.settings.domain.model.KeyModel

class KeyboardManager(private val listener: Listener) {

    var mode: Mode = Mode.NONE
        set(value) {
            field = value
            updateKeyboard()
        }

    private lateinit var binding: FragmentEditorBinding

    private var keyAdapter: KeyAdapter? = null

    fun bind(binding: FragmentEditorBinding) {
        this.binding = binding
        updateKeyboard()

        binding.keyboardExtended.setHasFixedSize(true)
        binding.keyboardExtended.adapter = KeyAdapter { keyModel ->
            listener.onKeyButton(keyModel.value)
        }.also {
            keyAdapter = it
        }

        binding.keyboardToolOpen.setOnClickListener { listener.onOpenButton() }
        binding.keyboardToolSave.setOnClickListener { listener.onSaveButton() }
        binding.keyboardToolClose.setOnClickListener { listener.onCloseButton() }
        binding.keyboardToolUndo.setOnClickListener { listener.onUndoButton() }
        binding.keyboardToolRedo.setOnClickListener { listener.onRedoButton() }
    }

    fun submitList(keys: List<KeyModel>) {
        keyAdapter?.submitList(keys)
    }

    private fun updateKeyboard() {
        when (mode) {
            Mode.KEYBOARD -> {
                binding.keyboardBackground.isVisible = true
                binding.keyboardDivider.isVisible = true
                binding.keyboardExtended.isVisible = true
                binding.keyboardToolOpen.isInvisible = true
                binding.keyboardToolSave.isInvisible = true
                binding.keyboardToolClose.isInvisible = true
                binding.keyboardToolUndo.isInvisible = true
                binding.keyboardToolRedo.isInvisible = true
                binding.keyboardSwap.isVisible = true
            }
            Mode.TOOLS -> {
                binding.keyboardBackground.isVisible = true
                binding.keyboardDivider.isVisible = true
                binding.keyboardExtended.isInvisible = true
                binding.keyboardToolOpen.isVisible = true
                binding.keyboardToolSave.isVisible = true
                binding.keyboardToolClose.isVisible = true
                binding.keyboardToolUndo.isVisible = true
                binding.keyboardToolRedo.isVisible = true
                binding.keyboardSwap.isVisible = true
            }
            Mode.NONE -> {
                binding.keyboardBackground.isVisible = false
                binding.keyboardDivider.isVisible = false
                binding.keyboardExtended.isVisible = false
                binding.keyboardToolOpen.isVisible = false
                binding.keyboardToolSave.isVisible = false
                binding.keyboardToolClose.isVisible = false
                binding.keyboardToolUndo.isVisible = false
                binding.keyboardToolRedo.isVisible = false
                binding.keyboardSwap.isVisible = false
            }
        }
    }

    interface Listener {
        fun onKeyButton(char: Char): Boolean
        fun onOpenButton(): Boolean
        fun onSaveButton(): Boolean
        fun onCloseButton(): Boolean
        fun onUndoButton(): Boolean
        fun onRedoButton(): Boolean
    }

    enum class Mode {
        KEYBOARD,
        TOOLS,
        NONE,
    }
}