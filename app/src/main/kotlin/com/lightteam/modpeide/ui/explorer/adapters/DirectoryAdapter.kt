package com.lightteam.modpeide.ui.explorer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lightteam.modpeide.databinding.ItemTabDirectoryBinding
import com.lightteam.modpeide.domain.model.explorer.FileModel
import com.lightteam.modpeide.ui.base.adapters.TabAdapter

class DirectoryAdapter(
    private val onTabSelectedListener: TabAdapter.OnTabSelectedListener
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
        return DirectoryViewHolder.create(parent, onTabSelectedListener)
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

    class DirectoryViewHolder(
        private val binding: ItemTabDirectoryBinding,
        private val onTabSelectedListener: TabAdapter.OnTabSelectedListener
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup, onTabSelectedListener: TabAdapter.OnTabSelectedListener): DirectoryViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemTabDirectoryBinding.inflate(inflater, parent, false)
                return DirectoryViewHolder(binding, onTabSelectedListener)
            }
        }

        init {
            itemView.setOnClickListener {
                onTabSelectedListener.onTabSelected(adapterPosition)
            }
        }

        fun bind(item: FileModel, isSelected: Boolean) {
            binding.selectionIndicator.isVisible = isSelected
            binding.itemTitle.text = item.name
        }
    }
}