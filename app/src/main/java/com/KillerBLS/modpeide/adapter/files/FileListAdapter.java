/*
 * Copyright (C) 2018 Light Team Software
 *
 * This file is part of ModPE IDE.
 *
 * ModPE IDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ModPE IDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.KillerBLS.modpeide.adapter.files;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.KillerBLS.modpeide.R;

import java.util.LinkedList;

/**
 * Thanks Vlad Mihalachi
 */
public class FileListAdapter extends ArrayAdapter<FileDetail> implements Filterable {

    private LayoutInflater mLayoutInflater;

    private LinkedList<FileDetail> mCollection;
    private LinkedList<FileDetail> mCollectionFiltered;

    private FileFilter mFilter;
    private Resources mResources;

    public FileListAdapter(final Context context, final LinkedList<FileDetail> fileDetails) {
        super(context, R.layout.item_list_file, fileDetails);
        mResources = context.getResources();
        mCollectionFiltered = fileDetails;
        mCollection = fileDetails;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_list_file, null);
            final FileViewHolder holder = new FileViewHolder();

            holder.icon = convertView.findViewById(android.R.id.icon);
            holder.nameLabel = convertView.findViewById(android.R.id.text1);
            holder.sizeLabel = convertView.findViewById(android.R.id.text2);
            holder.lastChangeLabel = convertView.findViewById(R.id.text3);

            convertView.setTag(holder);
            final FileDetail fileDetail = mCollectionFiltered.get(position);

            setIcon(holder, fileDetail);
            holder.nameLabel.setText(fileDetail.getName());
            holder.sizeLabel.setText(fileDetail.getSize());
            holder.lastChangeLabel.setText(fileDetail.getDateModified());
        } else {
            final FileViewHolder hold = ((FileViewHolder) convertView.getTag());
            final FileDetail fileDetail = mCollectionFiltered.get(position);

            setIcon(hold, fileDetail);
            hold.nameLabel.setText(fileDetail.getName());
            hold.sizeLabel.setText(fileDetail.getSize());
            hold.lastChangeLabel.setText(fileDetail.getDateModified());
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return mCollectionFiltered.size();
    }

    /**
     * Устанавливает иконку взависимости от файла.
     * @param viewHolder - ViewHolder содержащий ImageView для иконки.
     * @param fileDetail - Обьект с информацией о файле.
     */
    private void setIcon(final FileViewHolder viewHolder, final FileDetail fileDetail) {
        if (fileDetail.isFolder()) { //Если папка
            Drawable mIcon = mResources.getDrawable(R.drawable.ic_fexplorer_folder);
            DrawableCompat.setTint(mIcon, mResources.getColor(R.color.fexplorer_color_folder));
            viewHolder.icon.setImageDrawable(mIcon); //Ставим иконку папки
        } else { //Иначе, если файл
            Drawable mIcon = mResources.getDrawable(R.drawable.ic_fexplorer_document);
            DrawableCompat.setTint(mIcon, mResources.getColor(R.color.fexplorer_color_file));
            viewHolder.icon.setImageDrawable(mIcon); //Ставим иконку файла
        }
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if(mFilter == null) {
            mFilter = new FileFilter();
        }
        return mFilter;
    }

    /**
     * Кастомный фильтр для поиска файлов.
     */
    private class FileFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint == null || constraint.length() == 0) {
                results.values = mCollection;
                results.count = mCollection.size();
            } else {
                LinkedList<FileDetail> nHolderList = new LinkedList<>();
                for(FileDetail fileDetail : mCollection) {
                    if(fileDetail.getName().toLowerCase()
                            .contains(constraint.toString().toLowerCase()))
                        nHolderList.add(fileDetail);
                }
                results.values = nHolderList;
                results.count = nHolderList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mCollectionFiltered = (LinkedList<FileDetail>) results.values;
            notifyDataSetChanged();
        }
    }
}
