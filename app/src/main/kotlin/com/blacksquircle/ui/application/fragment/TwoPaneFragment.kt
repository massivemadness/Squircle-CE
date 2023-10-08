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

package com.blacksquircle.ui.application.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.ColorUtils
import androidx.core.view.GravityCompat
import androidx.core.view.doOnLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.blacksquircle.ui.R
import com.blacksquircle.ui.application.navigation.AppScreen
import com.blacksquircle.ui.application.viewmodel.MainViewModel
import com.blacksquircle.ui.core.delegate.viewBinding
import com.blacksquircle.ui.core.extensions.*
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.navigation.BackPressedHandler
import com.blacksquircle.ui.core.navigation.DrawerHandler
import com.blacksquircle.ui.databinding.FragmentTwoPaneBinding
import com.blacksquircle.ui.feature.editor.ui.fragment.EditorFragment
import com.blacksquircle.ui.feature.editor.ui.mvi.EditorIntent
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import com.blacksquircle.ui.feature.explorer.data.utils.openFileWith
import com.blacksquircle.ui.feature.explorer.ui.fragment.ExplorerFragment
import com.blacksquircle.ui.feature.explorer.ui.mvi.ExplorerViewEvent
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class TwoPaneFragment : Fragment(R.layout.fragment_two_pane), DrawerHandler {

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val explorerViewModel by activityViewModels<ExplorerViewModel>()
    private val editorViewModel by activityViewModels<EditorViewModel>()
    private val navController by lazy { findNavController() }
    private val binding by viewBinding(FragmentTwoPaneBinding::bind)

    private val drawerListener = object : DrawerListener {
        override fun onDrawerStateChanged(newState: Int) = Unit
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            binding.fragmentEditor.translationX = slideOffset * drawerView.width
        }
        override fun onDrawerOpened(drawerView: View) {
            binding.fragmentEditor.translationX = drawerView.width.toFloat()
        }
        override fun onDrawerClosed(drawerView: View) {
            binding.fragmentEditor.translationX = 0f
        }
    }

    private var editorBackPressedHandler: BackPressedHandler? = null
    private var explorerBackPressedHandler: BackPressedHandler? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFadeTransition(binding.root as ViewGroup)
        postponeEnterTransition(view)
        observeViewModel()

        editorBackPressedHandler = childFragmentManager
            .fragment<EditorFragment>(R.id.fragment_editor)
        explorerBackPressedHandler = childFragmentManager
            .fragment<ExplorerFragment>(R.id.fragment_explorer)

        val alphaFiftyPercent = 0x80
        val scrimColor = ColorUtils.setAlphaComponent(
            requireContext().getColorAttr(android.R.attr.colorBackground),
            alphaFiftyPercent,
        )
        binding.drawerLayout?.setScrimColor(scrimColor)
        binding.drawerLayout?.addDrawerListener(drawerListener)
        binding.drawerLayout?.doOnLayout {
            if (binding.drawerLayout?.isOpen == true) {
                drawerListener.onDrawerOpened(binding.fragmentExplorer)
            } else {
                drawerListener.onDrawerClosed(binding.fragmentExplorer)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.drawerLayout?.isOpen == true) {
                        if (explorerBackPressedHandler?.handleOnBackPressed() == false) {
                            closeDrawer()
                        }
                    } else {
                        if (editorBackPressedHandler?.handleOnBackPressed() == false) {
                            if (mainViewModel.confirmExit) {
                                navController.navigate(AppScreen.ConfirmExit)
                            } else {
                                activity?.finish()
                            }
                        }
                    }
                }
            },
        )
    }

    override fun openDrawer() {
        binding.drawerLayout?.openDrawer(GravityCompat.START)
    }

    override fun closeDrawer() {
        binding.drawerLayout?.closeDrawer(GravityCompat.START)
    }

    private fun observeViewModel() {
        mainViewModel.viewEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { event ->
                when (event) {
                    is ViewEvent.Toast -> context?.showToast(text = event.message)
                    is ViewEvent.NewIntent -> {
                        val fileUri = event.intent.data ?: return@onEach
                        val editorIntent = EditorIntent.OpenFileUri(fileUri)
                        editorViewModel.obtainEvent(editorIntent)
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        explorerViewModel.customEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { event ->
                when (event) {
                    is ExplorerViewEvent.OpenFile -> {
                        editorViewModel.obtainEvent(EditorIntent.OpenFile(event.fileModel))
                        closeDrawer()
                    }
                    is ExplorerViewEvent.OpenFileWith -> {
                        context?.openFileWith(event.fileModel)
                        closeDrawer()
                    }
                    else -> Unit
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}