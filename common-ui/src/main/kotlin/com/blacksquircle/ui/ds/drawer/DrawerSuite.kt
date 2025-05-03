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

package com.blacksquircle.ui.ds.drawer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.DrawerDefaults
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

private val DrawerMaxWidth = 312.dp
private const val DrawerScrimOpacity = 0.45f

/**
 * Fork of material ModalDrawer with minor tweaks:
 * - Added offset for [content] when dragging the drawer
 * - Drawer max width changed to 300dp
 * - Changed [drawerBackgroundColor] value
 * - Changed [scrimColor] value
 * - Removed gesturesEnabled condition in [Scrim]
 */
@Composable
@OptIn(ExperimentalFoundationApi::class)
internal fun DrawerSuite(
    drawerContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    drawerShape: Shape = DrawerDefaults.shape,
    drawerElevation: Dp = DrawerDefaults.Elevation,
    drawerBackgroundColor: Color = MaterialTheme.colors.background,
    drawerContentColor: Color = contentColorFor(drawerBackgroundColor),
    scrimColor: Color = Color.Black.copy(alpha = DrawerScrimOpacity),
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    BoxWithConstraints(modifier.fillMaxSize()) {
        val modalDrawerConstraints = constraints
        if (!modalDrawerConstraints.hasBoundedWidth) {
            throw IllegalStateException("Drawer shouldn't have infinite width")
        }
        val density = LocalDensity.current
        val maxWidth = min(
            modalDrawerConstraints.maxWidth.toFloat(),
            with(density) { DrawerMaxWidth.toPx() }
        )
        val minValue = -maxWidth
        val maxValue = 0f

        SideEffect {
            drawerState.density = density
            val anchors = DraggableAnchors {
                DrawerValue.Closed at minValue
                DrawerValue.Open at maxValue
            }
            drawerState.anchoredDraggableState.updateAnchors(anchors)
        }

        val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
        Box(
            Modifier.anchoredDraggable(
                state = drawerState.anchoredDraggableState,
                orientation = Orientation.Horizontal,
                enabled = gesturesEnabled,
                reverseDirection = isRtl
            )
        ) {
            Box(
                Modifier.offset {
                    val drawerOffset = abs(drawerState.offset)
                    val contentOffset = (maxWidth - drawerOffset).fastRoundToInt()
                    IntOffset(x = contentOffset, y = 0)
                }
            ) {
                content()
            }
            Scrim(
                open = drawerState.isOpen,
                onClose = {
                    if (drawerState.isOpen) {
                        scope.launch { drawerState.close() }
                    }
                },
                fraction = { calculateFraction(minValue, maxValue, drawerState.requireOffset()) },
                color = scrimColor
            )
            Surface(
                modifier = with(LocalDensity.current) {
                    Modifier.sizeIn(
                        minWidth = modalDrawerConstraints.minWidth.toDp(),
                        minHeight = modalDrawerConstraints.minHeight.toDp(),
                        maxWidth = maxWidth.toDp(),
                        maxHeight = modalDrawerConstraints.maxHeight.toDp()
                    )
                }
                    .offset {
                        IntOffset(
                            drawerState
                                .requireOffset()
                                .roundToInt(), 0
                        )
                    }
                    .semantics {
                        paneTitle = "Menu"
                        if (drawerState.isOpen) {
                            dismiss {
                                scope.launch { drawerState.close() }
                                true
                            }
                        }
                    },
                shape = drawerShape,
                color = drawerBackgroundColor,
                contentColor = drawerContentColor,
                elevation = drawerElevation
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    content = drawerContent,
                )
            }
        }
    }
}

@Composable
private fun Scrim(
    open: Boolean,
    onClose: () -> Unit,
    fraction: () -> Float,
    color: Color,
) {
    val closeDrawer = "Close drawer"
    val dismissDrawer = if (open) {
        Modifier
            .pointerInput(onClose) { detectTapGestures { onClose() } }
            .semantics(mergeDescendants = true) {
                contentDescription = closeDrawer
                onClick {
                    onClose()
                    true
                }
            }
    } else {
        Modifier
    }

    Canvas(
        Modifier
            .fillMaxSize()
            .then(dismissDrawer)
    ) {
        drawRect(color, alpha = fraction())
    }
}

@Suppress("SameParameterValue")
private fun calculateFraction(a: Float, b: Float, pos: Float) =
    ((pos - a) / (b - a)).fastCoerceIn(0f, 1f)