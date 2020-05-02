package com.lightteam.modpeide.ui.settings.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.lightteam.modpeide.databinding.ItemPreferenceBinding
import com.lightteam.modpeide.ui.base.adapters.BaseViewHolder
import com.lightteam.modpeide.ui.base.adapters.OnItemClickListener
import com.lightteam.modpeide.ui.settings.adapter.item.PreferenceItem

class PreferenceAdapter(
    private val onItemClickListener: OnItemClickListener<PreferenceItem>
) : ListAdapter<PreferenceItem, PreferenceAdapter.PreferenceViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PreferenceItem>() {
            override fun areItemsTheSame(oldItem: PreferenceItem, newItem: PreferenceItem): Boolean {
                return oldItem.navigationId == newItem.navigationId
            }
            override fun areContentsTheSame(oldItem: PreferenceItem, newItem: PreferenceItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferenceViewHolder {
        return PreferenceViewHolder.create(parent, onItemClickListener)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PreferenceViewHolder(
        private val binding: ItemPreferenceBinding,
        private val onItemClickListener: OnItemClickListener<PreferenceItem>
    ) : BaseViewHolder<PreferenceItem>(binding.root) {

        companion object {
            fun create(parent: ViewGroup, onItemClickListener: OnItemClickListener<PreferenceItem>): PreferenceViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemPreferenceBinding.inflate(inflater, parent, false)
                return PreferenceViewHolder(binding, onItemClickListener)
            }
        }

        private lateinit var preferenceItem: PreferenceItem

        init {
            itemView.setOnClickListener {
                onItemClickListener.onClick(preferenceItem)
            }
        }

        override fun bind(item: PreferenceItem) {
            preferenceItem = item
            binding.itemTitle.setText(item.title)
            binding.itemSubtitle.setText(item.subtitle)
        }
    }
}