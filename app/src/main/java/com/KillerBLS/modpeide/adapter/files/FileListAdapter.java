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

import com.KillerBLS.modpeide.R;

import java.util.LinkedList;

/**
 * Thanks Vlad Mihalachi
 */
public class FileListAdapter extends ArrayAdapter<FileDetail> {

    // Layout Inflater
    private final LayoutInflater inflater;
    private final LinkedList<FileDetail> orig;
    private FileFilter customFilter;
    // List of file details
    private LinkedList<FileDetail> fileDetails;

    private Resources mRes;

    public FileListAdapter(final Context context, final LinkedList<FileDetail> fileDetails) {
        super(context, R.layout.item_list_file, fileDetails);
        mRes = context.getResources();
        this.fileDetails = fileDetails;
        orig = fileDetails;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_file, null);
            final FileViewHolder hold = new FileViewHolder();

            hold.icon = convertView.findViewById(android.R.id.icon);
            hold.nameLabel = convertView.findViewById(android.R.id.text1);
            hold.sizeLabel = convertView.findViewById(android.R.id.text2);
            hold.lastChangeLabel = convertView.findViewById(R.id.text3);
            convertView.setTag(hold);

            final FileDetail fileDetail = fileDetails.get(position);
            setIcon(hold, fileDetail);
            hold.nameLabel.setText(fileDetail.getName());
            hold.sizeLabel.setText(fileDetail.getSize());
            hold.lastChangeLabel.setText(fileDetail.getDateModified());
        } else {
            final FileViewHolder hold = ((FileViewHolder) convertView.getTag());
            final FileDetail fileDetail = fileDetails.get(position);
            setIcon(hold, fileDetail);
            hold.nameLabel.setText(fileDetail.getName());
            hold.sizeLabel.setText(fileDetail.getSize());
            hold.lastChangeLabel.setText(fileDetail.getDateModified());
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return fileDetails.size();
    }

    private void setIcon(final FileViewHolder viewHolder, final FileDetail fileDetail) {
        //final String fileName = fileDetail.getName();
        //final String ext = FilenameUtils.getExtension(fileName);
        if (fileDetail.isFolder()) {
            Drawable mIcon = mRes.getDrawable(R.drawable.ic_fexplorer_folder);
            DrawableCompat.setTint(mIcon, mRes.getColor(R.color.fexplorer_color_folder));
            viewHolder.icon.setImageDrawable(mIcon);
        } else {
            Drawable mIcon = mRes.getDrawable(R.drawable.ic_fexplorer_document);
            DrawableCompat.setTint(mIcon, mRes.getColor(R.color.fexplorer_color_file));
            viewHolder.icon.setImageDrawable(mIcon);
        }
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (customFilter == null) {
            customFilter = new FileFilter();
        }
        return customFilter;
    }

    private class FileFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = orig;
                results.count = orig.size();
            } else {
                LinkedList<FileDetail> nHolderList = new LinkedList<>();
                for (FileDetail h : orig) {
                    if (h.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
                        nHolderList.add(h);
                }
                results.values = nHolderList;
                results.count = nHolderList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            fileDetails = (LinkedList<FileDetail>) results.values;
            notifyDataSetChanged();
        }
    }
}
