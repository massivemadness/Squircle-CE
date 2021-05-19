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

package com.blacksquircle.ui.utils.adapters

import androidx.recyclerview.widget.RecyclerView

abstract class TabAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    val selectedPosition
        get() = _selectedPosition
    private var _selectedPosition = -1

    val currentList: List<T>
        get() = _currentList
    private var _currentList: MutableList<T> = mutableListOf()

    private var onTabSelectedListener: OnTabSelectedListener? = null
    private var onTabMovedListener: OnTabMovedListener? = null
    private var onDataRefreshListener: OnDataRefreshListener? = null

    private var recyclerView: RecyclerView? = null
    private var isClosing = false

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun getItemCount(): Int = currentList.size

    fun submitList(list: List<T>) {
        _currentList = list.toMutableList()
        notifyDataSetChanged()
        onDataRefreshListener?.onDataRefresh()
    }

    fun move(from: Int, to: Int): Boolean {
        val temp = currentList[from]
        _currentList.removeAt(from)
        _currentList.add(to, temp)

        when {
            selectedPosition in to until from -> _selectedPosition++
            selectedPosition in (from + 1)..to -> _selectedPosition--
            from == selectedPosition -> _selectedPosition = to
        }

        onTabMovedListener?.onTabMoved(from, to)
        notifyItemMoved(from, to)
        return true
    }

    fun select(newPosition: Int) {
        if (newPosition == selectedPosition && !isClosing) {
            onTabSelectedListener?.onTabReselected(selectedPosition)
        } else {
            val previousPosition = selectedPosition
            _selectedPosition = newPosition
            if (previousPosition > -1 && selectedPosition > -1 && previousPosition < currentList.size) {
                notifyItemChanged(previousPosition) // Update previous selected item
                if (!isClosing) {
                    onTabSelectedListener?.onTabUnselected(previousPosition)
                }
            }
            if (selectedPosition > -1) {
                notifyItemChanged(selectedPosition) // Update new selected item
                onTabSelectedListener?.onTabSelected(selectedPosition)
                recyclerView?.smoothScrollToPosition(selectedPosition)
            }
        }
    }

    // I'm going crazy with this
    fun close(position: Int) {
        isClosing = true
        var newPosition = selectedPosition
        if (position == selectedPosition) {
            newPosition = when {
                position - 1 > -1 -> position - 1
                position + 1 < itemCount -> position
                else -> -1
            }
        }
        if (position < selectedPosition) {
            newPosition -= 1
        }
        _currentList.removeAt(position)
        notifyItemRemoved(position)
        onDataRefreshListener?.onDataRefresh()
        select(newPosition)
        isClosing = false
    }

    fun setOnTabSelectedListener(listener: OnTabSelectedListener) {
        onTabSelectedListener = listener
    }

    fun setOnTabMovedListener(listener: OnTabMovedListener) {
        onTabMovedListener = listener
    }

    fun setOnDataRefreshListener(listener: OnDataRefreshListener) {
        onDataRefreshListener = listener
    }

    interface OnTabSelectedListener {
        fun onTabReselected(position: Int) = Unit
        fun onTabUnselected(position: Int) = Unit
        fun onTabSelected(position: Int) = Unit
    }

    interface OnTabMovedListener {
        fun onTabMoved(from: Int, to: Int)
    }

    interface OnDataRefreshListener {
        fun onDataRefresh()
    }
}