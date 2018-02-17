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

package com.KillerBLS.modpeide.document.suggestions;

import android.content.Context;
import android.graphics.Typeface;
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

import java.util.ArrayList;

/**
 * @author Trần Lê Duy
 */
public class SuggestionAdapter extends ArrayAdapter<SuggestionItem> {

    private LayoutInflater inflater;
    private ArrayList<SuggestionItem> clone;
    private ArrayList<SuggestionItem> suggestion;

    @LayoutRes
    private int resourceID;

    private Filter suggestionFilter = new Filter() {

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
            suggestion.clear();
            if (constraint != null) {
                for (SuggestionItem item : clone) {
                    if (item.compareTo(constraint.toString()) == 0) {
                        suggestion.add(item);
                    }
                }
                filterResults.values = suggestion;
                filterResults.count = suggestion.size();
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

    public SuggestionAdapter(@NonNull Context context,
                             @LayoutRes int resource, @NonNull ArrayList<SuggestionItem> objects) {
        super(context, resource, objects);
        inflater = LayoutInflater.from(context);
        clone = (ArrayList<SuggestionItem>) objects.clone();
        suggestion = new ArrayList<>();
        resourceID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resourceID, null);
        }
        final SuggestionItem item = getItem(position);

        TextView suggestionTitle = convertView.findViewById(R.id.suggestion_title);
        assert item != null;
        suggestionTitle.setText(item.getName());
        //Смена типа сделана для будущих обновлений
        //на данный момент ничего особенного не представляет.
        switch (item.getType()) {
            case SuggestionType.TYPE_KEYWORD: //Keyword
                suggestionTitle.setTypeface(Typeface.MONOSPACE);
                break;
            case SuggestionType.TYPE_VARIABLE: //Variable
                suggestionTitle.setTypeface(Typeface.MONOSPACE);
                break;
        }
        return convertView;
    }

    /*public ArrayList<SuggestionItem> getAllItems() {
        return clone;
    }

    public void clearAllData() {
        super.clear();
        clone.clear();
    }

    public void addData(@NonNull Collection<? extends SuggestionItem> collection) {
        addAll(collection);
        clone.addAll(collection);
    }*/

    @NonNull
    @Override
    public Filter getFilter() {
        return suggestionFilter;
    }
}
