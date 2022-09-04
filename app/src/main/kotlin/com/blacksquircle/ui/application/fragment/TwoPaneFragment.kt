package com.blacksquircle.ui.application.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.blacksquircle.ui.R
import com.blacksquircle.ui.application.navigation.AppScreen
import com.blacksquircle.ui.application.viewmodel.MainViewModel
import com.blacksquircle.ui.core.ui.delegate.viewBinding
import com.blacksquircle.ui.core.ui.extensions.fragment
import com.blacksquircle.ui.core.ui.extensions.navigate
import com.blacksquircle.ui.core.ui.extensions.showToast
import com.blacksquircle.ui.core.ui.navigation.BackPressedHandler
import com.blacksquircle.ui.core.ui.navigation.DrawerHandler
import com.blacksquircle.ui.core.ui.viewstate.ViewEvent
import com.blacksquircle.ui.databinding.FragmentTwoPaneBinding
import com.blacksquircle.ui.feature.editor.data.converter.DocumentConverter
import com.blacksquircle.ui.feature.editor.ui.fragment.EditorFragment
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import com.blacksquircle.ui.feature.explorer.data.utils.openFileAs
import com.blacksquircle.ui.feature.explorer.ui.fragment.ExplorerFragment
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import com.blacksquircle.ui.utils.extensions.multiplyDraggingEdgeSizeBy
import com.blacksquircle.ui.utils.extensions.resolveFilePath
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File

@AndroidEntryPoint
class TwoPaneFragment : Fragment(R.layout.fragment_two_pane), DrawerHandler {

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val explorerViewModel by activityViewModels<ExplorerViewModel>()
    private val editorViewModel by activityViewModels<EditorViewModel>()
    private val navController by lazy { findNavController() }
    private val binding by viewBinding(FragmentTwoPaneBinding::bind)
    private val drawerLayout: DrawerLayout?
        get() = binding.drawerLayout as? DrawerLayout

    private lateinit var editorBackPressedHandler: BackPressedHandler
    private lateinit var explorerBackPressedHandler: BackPressedHandler

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        editorBackPressedHandler = childFragmentManager
            .fragment<EditorFragment>(R.id.fragment_editor)
        explorerBackPressedHandler = childFragmentManager
            .fragment<ExplorerFragment>(R.id.fragment_explorer)

        drawerLayout?.multiplyDraggingEdgeSizeBy(2)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
                        if (!explorerBackPressedHandler.handleOnBackPressed()) {
                            closeDrawer()
                        }
                    } else {
                        if (!editorBackPressedHandler.handleOnBackPressed()) {
                            if (mainViewModel.confirmExit) {
                                navController.navigate(AppScreen.ConfirmExit)
                            } else {
                                activity?.finish()
                            }
                        }
                    }
                }
            }
        )
    }

    override fun openDrawer() {
        drawerLayout?.openDrawer(GravityCompat.START)
    }

    override fun closeDrawer() {
        drawerLayout?.closeDrawer(GravityCompat.START)
    }

    private fun observeViewModel() {
        mainViewModel.viewEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { event ->
                when (event) {
                    is ViewEvent.Toast -> context?.showToast(text = event.message)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        mainViewModel.intentEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach(::handleIntent)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        explorerViewModel.openFileEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                editorViewModel.openFileEvent.value = DocumentConverter.toModel(it)
                closeDrawer()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        explorerViewModel.openFileAsEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                context?.openFileAs(it)
                closeDrawer()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        /*editorViewModel.openPropertiesEvent.observe(viewLifecycleOwner) {
            explorerViewModel.openPropertiesEvent.value = it
        }*/
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            val contentUri = intent.data ?: return
            Log.d(TAG, "Handle external content uri = $contentUri")

            val filePath = requireContext().resolveFilePath(contentUri)
            Log.d(TAG, "Does it looks like a valid file path? ($filePath)")

            val isValidFile = try {
                File(filePath).exists()
            } catch (e: Exception) {
                false
            }
            Log.d(TAG, "isValidFile = $isValidFile")

            if (isValidFile) {
                val file = File(filePath)
                mainViewModel.handleDocument(file) {
                    editorViewModel.loadFiles()
                }
            } else {
                Log.d(TAG, "Invalid path")
                context?.showToast(R.string.message_file_not_found)
            }
        }
    }

    companion object {
        private const val TAG = "TwoPaneFragment"
    }
}