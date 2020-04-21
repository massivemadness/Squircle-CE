package com.lightteam.modpeide.ui.editor.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.utils.extensions.replaceList
import com.lightteam.modpeide.domain.model.editor.DocumentModel
import com.lightteam.modpeide.utils.extensions.makeRightPaddingRecursively

class DocumentAdapter(
    private val onTabSelectedListener: OnTabSelectedListener,
    private val tabInteractor: TabInteractor
) : RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {

    val selectedPosition
        get() = _selectedPosition
    private var _selectedPosition = -1

    private var recyclerView: RecyclerView? = null
    private var tabsList: MutableList<DocumentModel> = mutableListOf()
    private var isClosing = false

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        return DocumentViewHolder.create(parent, tabInteractor) {
            select(it)
        }
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        holder.bind(tabsList[position], position == selectedPosition)
    }

    override fun getItemCount(): Int {
        return tabsList.size
    }

    fun submitList(list: List<DocumentModel>) {
        tabsList.replaceList(list)
        notifyDataSetChanged()
    }

    fun select(newPosition: Int) {
        if (newPosition == selectedPosition && !isClosing) {
            onTabSelectedListener.onTabReselected(selectedPosition)
        } else {
            val previousPosition = selectedPosition
            _selectedPosition = newPosition
            if (previousPosition > -1 && selectedPosition > -1 && previousPosition < tabsList.size) {
                notifyItemChanged(previousPosition) // Update previous selected item
                if (!isClosing) {
                    onTabSelectedListener.onTabUnselected(previousPosition)
                }
            }
            if (selectedPosition > -1) {
                notifyItemChanged(selectedPosition) // Update new selected item
                onTabSelectedListener.onTabSelected(selectedPosition)
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
        tabsList.removeAt(position)
        notifyItemRemoved(position)
        select(newPosition)
        isClosing = false
    }

    class DocumentViewHolder(
        itemView: View,
        private val tabInteractor: TabInteractor,
        private val tabCallback: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        companion object {
            fun create(
                parent: ViewGroup,
                tabInteractor: TabInteractor,
                tabCallback: (Int) -> Unit
            ): DocumentViewHolder {
                val itemView = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_tab_document, parent, false)
                return DocumentViewHolder(itemView, tabInteractor, tabCallback)
            }
        }

        private val itemTitle: TextView = itemView.findViewById(R.id.item_title)
        private val itemIcon: ImageView = itemView.findViewById(R.id.item_icon)
        private val selectionIndicator: View = itemView.findViewById(R.id.selection_indicator)

        init {
            itemView.setOnClickListener {
                tabCallback.invoke(adapterPosition)
            }
            itemView.setOnLongClickListener {
                val wrapper = ContextThemeWrapper(it.context, R.style.Widget_Darcula_PopupMenu)
                val popupMenu = PopupMenu(wrapper, it)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_close -> tabInteractor.close(adapterPosition)
                        R.id.action_close_others -> tabInteractor.closeOthers(adapterPosition)
                        R.id.action_close_all -> tabInteractor.closeAll(adapterPosition)
                    }
                    return@setOnMenuItemClickListener true
                }
                popupMenu.inflate(R.menu.menu_document)
                popupMenu.makeRightPaddingRecursively()
                popupMenu.show()
                return@setOnLongClickListener true
            }
            itemIcon.setOnClickListener {
                tabInteractor.close(adapterPosition)
            }
        }

        fun bind(item: DocumentModel, isSelected: Boolean) {
            selectionIndicator.isVisible = isSelected
            itemTitle.text = item.name
        }
    }

    interface OnTabSelectedListener {
        fun onTabReselected(position: Int)
        fun onTabUnselected(position: Int)
        fun onTabSelected(position: Int)
    }

    interface TabInteractor {
        fun close(position: Int)
        fun closeOthers(position: Int)
        fun closeAll(position: Int)
    }
}