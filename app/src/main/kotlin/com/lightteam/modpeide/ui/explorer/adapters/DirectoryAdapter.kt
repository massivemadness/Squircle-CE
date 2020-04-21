package com.lightteam.modpeide.ui.explorer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lightteam.modpeide.R
import com.lightteam.modpeide.domain.model.explorer.FileModel
import com.lightteam.modpeide.ui.base.adapters.OnItemClickListener

class DirectoryAdapter(
    private val onItemClickListener: OnItemClickListener<FileModel>
) : ListAdapter<FileModel, DirectoryAdapter.DirectoryViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<FileModel>() {
            override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
                return oldItem.path == newItem.path
            }
            override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder {
        return DirectoryViewHolder.create(parent, onItemClickListener)
    }

    override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    fun submitList(list: List<FileModel>, position: Int) {
        submitList(list)
        if (selectedPosition != position) {
            if (selectedPosition > -1) {
                notifyItemChanged(selectedPosition) // Update previous selected item
            }
            selectedPosition = position
            notifyItemChanged(selectedPosition) // Update new selected item
        }
    }

    fun indexOf(fileModel: FileModel): Int {
        return currentList.indexOf(fileModel)
    }

    class DirectoryViewHolder(
        itemView: View,
        private val onItemClickListener: OnItemClickListener<FileModel>
    ) : RecyclerView.ViewHolder(itemView) {

        companion object {
            fun create(parent: ViewGroup, onItemClickListener: OnItemClickListener<FileModel>): DirectoryViewHolder {
                val itemView = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_tab_directory, parent, false)
                return DirectoryViewHolder(itemView, onItemClickListener)
            }
        }

        private val itemTitle: TextView = itemView.findViewById(R.id.item_title)
        private val selectionIndicator: View = itemView.findViewById(R.id.selection_indicator)

        private lateinit var fileModel: FileModel

        init {
            itemView.setOnClickListener {
                onItemClickListener.onClick(fileModel)
            }
        }

        fun bind(item: FileModel, isSelected: Boolean) {
            fileModel = item
            selectionIndicator.isVisible = isSelected
            itemTitle.text = item.name
        }
    }
}