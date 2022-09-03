package com.blacksquircle.ui.feature.settings.ui.adapter

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blacksquircle.ui.feature.settings.databinding.ItemReleaseBinding
import com.blacksquircle.ui.feature.settings.ui.adapter.item.ReleaseModel

class ReleaseAdapter : ListAdapter<ReleaseModel, ReleaseAdapter.ReleaseViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ReleaseModel>() {
            override fun areItemsTheSame(oldItem: ReleaseModel, newItem: ReleaseModel): Boolean {
                return oldItem.versionName == newItem.versionName
            }
            override fun areContentsTheSame(oldItem: ReleaseModel, newItem: ReleaseModel): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReleaseViewHolder {
        return ReleaseViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ReleaseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ReleaseViewHolder(
        private val binding: ItemReleaseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup): ReleaseViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemReleaseBinding.inflate(inflater, parent, false)
                return ReleaseViewHolder(binding)
            }
        }

        private lateinit var releaseModel: ReleaseModel

        init {
            binding.itemContent.movementMethod = LinkMovementMethod()
        }

        fun bind(item: ReleaseModel) {
            releaseModel = item
            binding.labelLatest.isVisible = adapterPosition == 0
            binding.itemTitle.text = releaseModel.versionName
            binding.itemDate.text = releaseModel.releaseDate
            binding.itemContent.text = releaseModel.releaseNotes.parseAsHtml()
        }
    }
}