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

package com.blacksquircle.ui.core.adapter

import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class TabAdapter<T, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>,
) : ListAdapter<T, VH>(diffCallback) {

    val selectedPosition
        get() = _selectedPosition
    private var _selectedPosition = -1

    private var onTabSelectedListener: OnTabSelectedListener? = null
    private var onTabMovedListener: OnTabMovedListener? = null
    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    fun submitList(list: List<T>, position: Int) {
        submitList(list) {
            val currentAnimator = recyclerView?.itemAnimator
            if (currentAnimator == null) {
                recyclerView?.doOnPreDraw { // fixes animation
                    recyclerView?.itemAnimator = DefaultItemAnimator()
                }
            }
        }
        select(position)
    }

    fun move(from: Int, to: Int) {
        when {
            selectedPosition in to until from -> _selectedPosition++
            selectedPosition in (from + 1)..to -> _selectedPosition--
            from == selectedPosition -> _selectedPosition = to
        }
        recyclerView?.itemAnimator = null // fixes animation
        onTabMovedListener?.onTabMoved(from, to)
    }

    fun select(newPosition: Int) {
        if (newPosition == selectedPosition) {
            onTabSelectedListener?.onTabReselected(selectedPosition)
        } else {
            val previousPosition = selectedPosition
            _selectedPosition = newPosition
            if (previousPosition > -1 && previousPosition < currentList.size) {
                notifyItemChanged(previousPosition) // Update previous selected item
                onTabSelectedListener?.onTabUnselected(previousPosition)
            }
            if (newPosition > -1) {
                notifyItemChanged(newPosition) // Update new selected item
                onTabSelectedListener?.onTabSelected(newPosition)
                recyclerView?.doOnPreDraw {
                    recyclerView?.smoothScrollToPosition(newPosition)
                }
            }
        }
    }

    fun setOnTabSelectedListener(listener: OnTabSelectedListener) {
        onTabSelectedListener = listener
    }

    fun setOnTabMovedListener(listener: OnTabMovedListener) {
        onTabMovedListener = listener
    }

    fun removeOnTabSelectedListener() {
        onTabSelectedListener = null
    }

    fun removeOnTabMovedListener() {
        onTabMovedListener = null
    }

    interface OnTabSelectedListener {
        fun onTabReselected(position: Int) = Unit
        fun onTabUnselected(position: Int) = Unit
        fun onTabSelected(position: Int) = Unit
    }

    interface OnTabMovedListener {
        fun onTabMoved(from: Int, to: Int) = Unit
    }
}