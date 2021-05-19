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

package com.blacksquircle.ui.feature.themes.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.blacksquircle.ui.utils.extensions.dpToPx

class GridSpacingItemDecoration(
    marginDp: Int,
    private val columnCount: Int
) : RecyclerView.ItemDecoration() {

    /**
     * In this algorithm space should divide by 3 without remnant or width of items can have
     * a difference and we want them to be exactly the same.
     */
    private val margin = (
        if (marginDp % 3 == 0) {
            marginDp
        } else {
            marginDp + (3 - marginDp % 3)
        }
    ).dpToPx()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        when {
            columnCount == 1 -> {
                outRect.left = margin
                outRect.right = margin
            }
            position % columnCount == 0 -> {
                outRect.left = margin
                outRect.right = margin / 3
            }
            position % columnCount == columnCount - 1 -> {
                outRect.right = margin
                outRect.left = margin / 3
            }
            else -> {
                outRect.left = margin * 2 / 3
                outRect.right = margin * 2 / 3
            }
        }
        if (position < columnCount) {
            outRect.top = margin
        }
        outRect.bottom = margin
    }
}