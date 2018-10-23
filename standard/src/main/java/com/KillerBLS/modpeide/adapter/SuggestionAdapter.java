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
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.KillerBLS.modpeide.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.adapter.model.SuggestionItem;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Trần Lê Duy
 */
public class SuggestionAdapter extends ArrayAdapter<SuggestionItem> {

    private ArrayList<SuggestionItem> mData; //Полный список
    private ArrayList<SuggestionItem> mDataFiltered; //Отображаемый список

    private Filter mFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            if (resultValue == null) {
                return "";
            }
            return ((SuggestionItem) resultValue).getName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            mDataFiltered.clear();
            if (constraint != null) {
                for (SuggestionItem item : mData) {
                    if (item.compareTo(constraint.toString()) == 0) {
                        mDataFiltered.add(item);
                    }
                }
                filterResults.values = mDataFiltered;
                filterResults.count = mDataFiltered.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<SuggestionItem> filteredList = (ArrayList<SuggestionItem>) results.values;
            clear();
            if (filteredList != null && filteredList.size() > 0) {
                addAll(filteredList);
            }
            notifyDataSetChanged();
        }
    };

    // region BASE

    public SuggestionAdapter(@NonNull Context context,
                             @LayoutRes int resource, @NonNull ArrayList<SuggestionItem> objects) {
        super(context, resource, objects);
        mData = (ArrayList<SuggestionItem>) objects.clone();
        mDataFiltered = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, null);
        }
        final SuggestionItem item = getItem(position);
        if(item != null) {
            TextView title = convertView.findViewById(R.id.item_title);
            title.setText(item.getName());
        }
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return mFilter;
    }

    // endregion BASE

    // region METHODS

    public ArrayList<SuggestionItem> getAllItems() {
        return mData;
    }

    public void clearAllData() {
        super.clear();
        mData.clear();
    }

    public void addData(@NonNull Collection<? extends SuggestionItem> collection) {
        addAll(collection);
        mData.addAll(collection);
    }

    // endregion METHODS
}