package com.lightteam.modpeide.ui.base.adapters

import androidx.recyclerview.widget.RecyclerView
import com.lightteam.modpeide.data.utils.extensions.replaceList

abstract class TabAdapter <T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    val selectedPosition
        get() = _selectedPosition
    private var _selectedPosition = -1

    var onTabSelectedListener: OnTabSelectedListener? = null

    private var recyclerView: RecyclerView? = null
    private var currentList: MutableList<T> = mutableListOf()
    private var isClosing = false

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    fun getItem(position: Int): T {
        return currentList[position]
    }

    fun submitList(list: List<T>) {
        currentList.replaceList(list)
        notifyDataSetChanged()
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
        currentList.removeAt(position)
        notifyItemRemoved(position)
        select(newPosition)
        isClosing = false
    }

    interface OnTabSelectedListener {
        fun onTabReselected(position: Int)
        fun onTabUnselected(position: Int)
        fun onTabSelected(position: Int)
    }
}