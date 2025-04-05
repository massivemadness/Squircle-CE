/*
 * Copyright 2025 Squircle CE contributors.
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

package com.blacksquircle.ui.ds.scaffold

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxBy
import com.blacksquircle.ui.ds.drawer.DrawerState
import com.blacksquircle.ui.ds.drawer.DrawerSuite
import com.blacksquircle.ui.ds.drawer.rememberDrawerState

/**
 * Fork of material Scaffold with minor tweaks:
 * - Replaced ModalDrawer with custom [DrawerSuite]
 * - Default value for [contentWindowInsets] is [WindowInsets.Companion.systemBars]
 * - Removed [ScaffoldState], it's parameters and now part of this composable
 * - Changed drawer background, disabled the elevation
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScaffoldSuite(
    modifier: Modifier = Modifier,
    contentWindowInsets: WindowInsets = WindowInsets.systemBars,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    isFloatingActionButtonDocked: Boolean = false,
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    drawerGesturesEnabled: Boolean = true,
    drawerShape: Shape = MaterialTheme.shapes.large,
    drawerElevation: Dp = 0.dp,
    drawerBackgroundColor: Color = MaterialTheme.colors.background,
    drawerContentColor: Color = contentColorFor(drawerBackgroundColor),
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    content: @Composable (PaddingValues) -> Unit
) {
    val safeInsets = remember(contentWindowInsets) { MutableWindowInsets(contentWindowInsets) }
    val child = @Composable { childModifier: Modifier ->
        Surface(
            modifier = childModifier.onConsumedWindowInsetsChanged { consumedWindowInsets ->
                // Exclude currently consumed window insets from user provided contentWindowInsets
                safeInsets.insets = contentWindowInsets.exclude(consumedWindowInsets)
            },
            color = backgroundColor,
            contentColor = contentColor
        ) {
            ScaffoldLayout(
                isFabDocked = isFloatingActionButtonDocked,
                fabPosition = floatingActionButtonPosition,
                topBar = topBar,
                content = content,
                contentWindowInsets = safeInsets,
                snackbar = { snackbarHost(snackbarHostState) },
                fab = floatingActionButton,
                bottomBar = bottomBar
            )
        }
    }

    if (drawerContent != null) {
        DrawerSuite(
            modifier = modifier,
            drawerState = drawerState,
            gesturesEnabled = drawerGesturesEnabled,
            drawerContent = drawerContent,
            drawerShape = drawerShape,
            drawerElevation = drawerElevation,
            drawerBackgroundColor = drawerBackgroundColor,
            drawerContentColor = drawerContentColor,
            content = { child(Modifier) }
        )
    } else {
        child(modifier)
    }
}

@Composable
@UiComposable
private fun ScaffoldLayout(
    isFabDocked: Boolean,
    fabPosition: FabPosition,
    topBar: @Composable @UiComposable () -> Unit,
    content: @Composable @UiComposable (PaddingValues) -> Unit,
    snackbar: @Composable @UiComposable () -> Unit,
    fab: @Composable @UiComposable () -> Unit,
    contentWindowInsets: WindowInsets,
    bottomBar: @Composable @UiComposable () -> Unit
) {
    SubcomposeLayout { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight

        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        val topBarPlaceables = subcompose(ScaffoldLayoutContent.TopBar, topBar).fastMap {
            it.measure(looseConstraints)
        }

        val topBarHeight = topBarPlaceables.fastMaxBy(Placeable::height)?.height ?: 0

        val snackbarPlaceables = subcompose(ScaffoldLayoutContent.Snackbar, snackbar).fastMap {
            // respect only bottom and horizontal for snackbar and fab
            val leftInset = contentWindowInsets
                .getLeft(this@SubcomposeLayout, layoutDirection)
            val rightInset = contentWindowInsets
                .getRight(this@SubcomposeLayout, layoutDirection)
            val bottomInset = contentWindowInsets.getBottom(this@SubcomposeLayout)
            // offset the snackbar constraints by the insets values
            it.measure(
                looseConstraints.offset(
                    -leftInset - rightInset,
                    -bottomInset
                )
            )
        }

        val snackbarHeight = snackbarPlaceables.fastMaxBy(Placeable::height)?.height ?: 0

        val fabPlaceables =
            subcompose(ScaffoldLayoutContent.Fab, fab).fastMap { measurable ->
                // respect only bottom and horizontal for snackbar and fab
                val leftInset =
                    contentWindowInsets.getLeft(this@SubcomposeLayout, layoutDirection)
                val rightInset =
                    contentWindowInsets.getRight(this@SubcomposeLayout, layoutDirection)
                val bottomInset = contentWindowInsets.getBottom(this@SubcomposeLayout)
                measurable.measure(
                    looseConstraints.offset(
                        -leftInset - rightInset,
                        -bottomInset
                    )
                )
            }

        val fabPlacement = if (fabPlaceables.isNotEmpty()) {
            val fabWidth = fabPlaceables.fastMaxBy(Placeable::width)?.width ?: 0
            val fabHeight = fabPlaceables.fastMaxBy(Placeable::height)?.height ?: 0
            // FAB distance from the left of the layout, taking into account LTR / RTL
            if (fabWidth != 0 && fabHeight != 0) {
                val fabLeftOffset = when (fabPosition) {
                    FabPosition.Start -> {
                        if (layoutDirection == LayoutDirection.Ltr) {
                            FabSpacing.roundToPx()
                        } else {
                            layoutWidth - FabSpacing.roundToPx() - fabWidth
                        }
                    }

                    FabPosition.End -> {
                        if (layoutDirection == LayoutDirection.Ltr) {
                            layoutWidth - FabSpacing.roundToPx() - fabWidth
                        } else {
                            FabSpacing.roundToPx()
                        }
                    }

                    else -> (layoutWidth - fabWidth) / 2
                }

                FabPlacement(
                    isDocked = isFabDocked,
                    left = fabLeftOffset,
                    width = fabWidth,
                    height = fabHeight
                )
            } else {
                null
            }
        } else {
            null
        }

        val bottomBarPlaceables = subcompose(ScaffoldLayoutContent.BottomBar) {
            CompositionLocalProvider(
                LocalFabPlacement provides fabPlacement,
                content = bottomBar
            )
        }.fastMap { it.measure(looseConstraints) }

        val bottomBarHeight = bottomBarPlaceables.fastMaxBy(Placeable::height)?.height
        val fabOffsetFromBottom = fabPlacement?.let {
            if (bottomBarHeight == null) {
                it.height + FabSpacing.roundToPx() +
                    contentWindowInsets.getBottom(this@SubcomposeLayout)
            } else {
                if (isFabDocked) {
                    // Total height is the bottom bar height + half the FAB height
                    bottomBarHeight + (it.height / 2)
                } else {
                    // Total height is the bottom bar height + the FAB height + the padding
                    // between the FAB and bottom bar
                    bottomBarHeight + it.height + FabSpacing.roundToPx()
                }
            }
        }

        val snackbarOffsetFromBottom = if (snackbarHeight != 0) {
            snackbarHeight +
                (fabOffsetFromBottom ?: bottomBarHeight
                    ?: contentWindowInsets.getBottom(this@SubcomposeLayout))
        } else {
            0
        }

        val bodyContentHeight = layoutHeight - topBarHeight

        val bodyContentPlaceables = subcompose(ScaffoldLayoutContent.MainContent) {
            val insets = contentWindowInsets.asPaddingValues(this@SubcomposeLayout)
            val innerPadding = PaddingValues(
                top =
                    if (topBarPlaceables.isEmpty()) {
                        insets.calculateTopPadding()
                    } else {
                        0.dp
                    },
                bottom =
                    if (bottomBarPlaceables.isEmpty() || bottomBarHeight == null) {
                        insets.calculateBottomPadding()
                    } else {
                        bottomBarHeight.toDp()
                    },
                start = insets.calculateStartPadding((this@SubcomposeLayout).layoutDirection),
                end = insets.calculateEndPadding((this@SubcomposeLayout).layoutDirection)
            )
            content(innerPadding)
        }.fastMap { it.measure(looseConstraints.copy(maxHeight = bodyContentHeight)) }

        layout(layoutWidth, layoutHeight) {
            // Placing to control drawing order to match default elevation of each placeable

            bodyContentPlaceables.fastForEach {
                it.place(0, topBarHeight)
            }
            topBarPlaceables.fastForEach {
                it.place(0, 0)
            }
            snackbarPlaceables.fastForEach {
                it.place(0, layoutHeight - snackbarOffsetFromBottom)
            }
            // The bottom bar is always at the bottom of the layout
            bottomBarPlaceables.fastForEach {
                it.place(0, layoutHeight - (bottomBarHeight ?: 0))
            }
            // Explicitly not using placeRelative here as `leftOffset` already accounts for RTL
            fabPlaceables.fastForEach {
                it.place(fabPlacement?.left ?: 0, layoutHeight - (fabOffsetFromBottom ?: 0))
            }
        }
    }
}

@Immutable
private class FabPlacement(
    val isDocked: Boolean,
    val left: Int,
    val width: Int,
    val height: Int
)

private val LocalFabPlacement = staticCompositionLocalOf<FabPlacement?> { null }

private val FabSpacing = 16.dp
private val EndDrawerPadding = 56.dp

private enum class ScaffoldLayoutContent { TopBar, MainContent, Snackbar, Fab, BottomBar }

@JvmInline
value class FabPosition(@Suppress("unused") private val value: Int) {

    override fun toString(): String {
        return when (this) {
            Start -> "FabPosition.Start"
            Center -> "FabPosition.Center"
            else -> "FabPosition.End"
        }
    }

    companion object {
        val Start = FabPosition(0)
        val Center = FabPosition(1)
        val End = FabPosition(2)
    }
}