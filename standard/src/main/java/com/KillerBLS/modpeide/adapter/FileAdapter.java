/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.KillerBLS.modpeide.adapter;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.adapter.interfaces.SelectionListener;
import com.KillerBLS.modpeide.adapter.interfaces.SelectionTransfer;
import com.KillerBLS.modpeide.adapter.model.FileModel;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder>
        implements SelectionListener, Filterable {

    private List<FileModel> mData = new LinkedList<>(); //Полный список, без изменений
    private List<FileModel> mDataFiltered = new LinkedList<>(); //Фильтрованный список

    private SelectionTransfer mSelectionTransfer;

    public FileAdapter(SelectionTransfer selectionTransfer) {
        mSelectionTransfer = selectionTransfer;
    }

    // region BASE

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder viewHolder, int position) {
        viewHolder.setIsRecyclable(false);
        viewHolder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence query) {
                FilterResults results = new FilterResults();
                if(query == null || query.length() == 0) {
                    results.values = mDataFiltered;
                    results.count = mDataFiltered.size();
                } else {
                    LinkedList<FileModel> mHolderList = new LinkedList<>();
                    for(FileModel model : mDataFiltered) {
                        if(model.getName().toLowerCase().contains(query.toString().toLowerCase()))
                            mHolderList.add(model);
                    }
                    results.values = mHolderList;
                    results.count = mHolderList.size();
                }
                return results;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mData = (LinkedList<FileModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    // endregion BASE

    // region METHODS

    /**
     * Устанавливает и обновляет список элементов в списке.
     * @param list - новый список элементов.
     */
    public void setData(List<FileModel> list) {
        mData = list;
        mDataFiltered = list;
        notifyDataSetChanged();
    }

    // endregion METHODS

    // region CLICK

    @Override
    public void onClick(int position) {
        mSelectionTransfer.onClick(mData.get(position));
    }

    @Override
    public void onLongClick(int position) {
        mSelectionTransfer.onLongClick(mData.get(position), position);
    }

    // endregion CLICK

    static class FileViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_icon)
        ImageView mIcon;
        @BindView(R.id.item_title)
        TextView mTitle;

        FileViewHolder(@NonNull View itemView, @NonNull SelectionListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            //Short Click Listener
            itemView.setOnClickListener((view) -> listener.onClick(getAdapterPosition()));

            //Long Click Listener
            itemView.setOnLongClickListener((view) -> {
                listener.onLongClick(getAdapterPosition());
                return true;
            });
        }

        /**
         * Устанавливает отображаемую информацию о файле.
         * @param fileModel - файл, информация которого будет отображаться.
         */
        void bind(FileModel fileModel) {
            if(fileModel.isFolder()) {
                if(fileModel.isUp()) { //Up button
                    mIcon.setImageResource(R.drawable.ic_folder_up);
                }
                mIcon.setColorFilter(ContextCompat.getColor(mIcon.getContext(), R.color.colorFolder));
            } else { //File
                mIcon.setImageResource(R.drawable.ic_file);
                mIcon.setColorFilter(ContextCompat.getColor(mIcon.getContext(), android.R.color.darker_gray));
            }
            if(fileModel.isHidden()) {
                mIcon.setImageAlpha(115);
            }
            mTitle.setText(fileModel.getName());
        }
    }
}
