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