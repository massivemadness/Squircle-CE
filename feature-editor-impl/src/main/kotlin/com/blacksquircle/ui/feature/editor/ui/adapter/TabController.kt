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

package com.blacksquircle.ui.feature.editor.ui.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.blacksquircle.ui.core.adapter.TabAdapter

class TabController : ItemTouchHelper(itemTouchCallback) {

    companion object {

        private const val STATE_DRAG = 0.5f
        private const val STATE_NORMAL = 1.0f

        private val itemTouchCallback = object : SimpleCallback(START or END, ACTION_STATE_IDLE) {

            private var from = -1
            private var to = -1

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                if (from == -1) { // initial position
                    from = viewHolder.adapterPosition
                }
                to = target.adapterPosition
                val adapter = recyclerView.adapter as TabAdapter<*, *>
                adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSelectedChanged(
                viewHolder: RecyclerView.ViewHolder?,
                actionState: Int,
            ) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = STATE_DRAG
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
            ) {
                super.clearView(recyclerView, viewHolder)
                val adapter = recyclerView.adapter as TabAdapter<*, *>
                if (from > -1 && to > -1 && from != to) {
                    adapter.move(from, to)
                }
                viewHolder.itemView.alpha = STATE_NORMAL
                reset()
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

            private fun reset() {
                from = -1
                to = -1
            }
        }
    }
}