package com.blacksquircle.ui.feature.terminal

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.termux.view.TerminalView

@Composable
internal fun TerminalScreen(navController: NavController) {
    AndroidView(
        factory = { context ->
            TerminalView(context, null)
        },
        modifier = Modifier.fillMaxSize(),
    )
}