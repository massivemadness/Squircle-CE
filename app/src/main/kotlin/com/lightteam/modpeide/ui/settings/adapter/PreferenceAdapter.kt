package com.lightteam.modpeide.ui.settings.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.lightteam.modpeide.databinding.ItemPreferenceBinding
import com.lightteam.modpeide.ui.base.adapters.BaseViewHolder
import com.lightteam.modpeide.ui.base.adapters.OnItemClickListener

class PreferenceAdapter(
    private val onItemClickListener: OnItemClickListener<PreferenceModel>
) : ListAdapter<PreferenceModel, PreferenceAdapter.PreferenceViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PreferenceModel>() {
            override fun areItemsTheSame(oldItem: PreferenceModel, newItem: PreferenceModel): Boolean {
                return oldItem.navigationId == newItem.navigationId
            }
            override fun areContentsTheSame(oldItem: PreferenceModel, newItem: PreferenceModel): Boolean {
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
        private val onItemClickListener: OnItemClickListener<PreferenceModel>
    ) : BaseViewHolder<PreferenceModel>(binding.root) {

        companion object {
            fun create(parent: ViewGroup, onItemClickListener: OnItemClickListener<PreferenceModel>): PreferenceViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemPreferenceBinding.inflate(inflater, parent, false)
                return PreferenceViewHolder(binding, onItemClickListener)
            }
        }

        private lateinit var preferenceModel: PreferenceModel

        init {
            itemView.setOnClickListener {
                onItemClickListener.onClick(preferenceModel)
            }
        }

        override fun bind(item: PreferenceModel) {
            preferenceModel = item
            binding.itemTitle.setText(item.title)
            binding.itemSubtitle.setText(item.subtitle)
        }
    }
}