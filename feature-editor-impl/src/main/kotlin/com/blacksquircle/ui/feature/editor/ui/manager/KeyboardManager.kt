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

import androidx.core.view.isGone
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

        binding.keyboardRecycler.setHasFixedSize(true)
        binding.keyboardRecycler.adapter = KeyAdapter { keyModel ->
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
            Mode.KEYBOARD -> with(binding) {
                keyboardBackground.isVisible = true
                keyboardDivider.isVisible = true
                keyboardRecycler.isVisible = true
                keyboardToolOpen.isInvisible = true
                keyboardToolSave.isInvisible = true
                keyboardToolClose.isInvisible = true
                keyboardToolUndo.isInvisible = true
                keyboardToolRedo.isInvisible = true
                keyboardSwap.isVisible = true
            }
            Mode.TOOLS -> with(binding) {
                keyboardBackground.isVisible = true
                keyboardDivider.isVisible = true
                keyboardRecycler.isInvisible = true
                keyboardToolOpen.isVisible = true
                keyboardToolSave.isVisible = true
                keyboardToolClose.isVisible = true
                keyboardToolUndo.isVisible = true
                keyboardToolRedo.isVisible = true
                keyboardSwap.isVisible = true
            }
            Mode.NONE -> with(binding) {
                keyboardBackground.isGone = true
                keyboardDivider.isGone = true
                keyboardRecycler.isGone = true
                keyboardToolOpen.isGone = true
                keyboardToolSave.isGone = true
                keyboardToolClose.isGone = true
                keyboardToolUndo.isGone = true
                keyboardToolRedo.isGone = true
                keyboardSwap.isGone = true
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