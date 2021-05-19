/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.utils.extensions

import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout

/**
 * https://stackoverflow.com/a/17802569/4405457
 */
fun DrawerLayout.multiplyDraggingEdgeSizeBy(n: Int) {
    val leftDragger = javaClass.getDeclaredField("mLeftDragger")
    leftDragger.isAccessible = true

    val viewDragHelper = leftDragger.get(this) as ViewDragHelper
    val edgeSize = viewDragHelper.javaClass.getDeclaredField("mEdgeSize")
    edgeSize.isAccessible = true

    val edge = edgeSize.getInt(viewDragHelper)
    edgeSize.setInt(viewDragHelper, edge * n)
}