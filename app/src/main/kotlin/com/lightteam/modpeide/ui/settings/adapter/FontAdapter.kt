package com.lightteam.modpeide.ui.settings.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.lightteam.modpeide.databinding.ItemFontBinding
import com.lightteam.modpeide.ui.base.adapters.BaseViewHolder
import com.lightteam.modpeide.ui.base.adapters.OnItemClickListener
import com.lightteam.modpeide.ui.settings.adapter.item.FontItem
import com.lightteam.modpeide.utils.extensions.*

class FontAdapter(
    private val onItemClickListener: OnItemClickListener<FontItem>
) : ListAdapter<FontItem, FontAdapter.FontViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<FontItem>() {
            override fun areItemsTheSame(oldItem: FontItem, newItem: FontItem): Boolean {
                return oldItem.fontPath == newItem.fontPath
            }
            override fun areContentsTheSame(oldItem: FontItem, newItem: FontItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FontViewHolder {
        return FontViewHolder.create(parent, onItemClickListener)
    }

    override fun onBindViewHolder(holder: FontViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FontViewHolder(
        private val binding: ItemFontBinding,
        private val onItemClickListener: OnItemClickListener<FontItem>
    ) : BaseViewHolder<FontItem>(binding.root) {

        companion object {
            fun create(parent: ViewGroup, onItemClickListener: OnItemClickListener<FontItem>): FontViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemFontBinding.inflate(inflater, parent, false)
                return FontViewHolder(binding, onItemClickListener)
            }
        }

        private lateinit var fontItem: FontItem

        init {
            binding.actionSelect.setOnClickListener {
                onItemClickListener.onClick(fontItem)
            }
        }

        override fun bind(item: FontItem) {
            fontItem = item
            binding.itemTitle.text = item.fontName
            binding.itemContent.typeface = itemView.context.createTypefaceFromPath(item.fontPath)
            binding.itemSubtitle.isVisible = item.supportLigatures
            binding.actionSelect.isEnabled = !item.isPaid || itemView.context.isUltimate()
            itemView.setOnClickListener {
                if (!binding.actionSelect.isEnabled) {
                    onItemClickListener.onClick(fontItem)
                }
            }
        }
    }
}