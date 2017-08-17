/*
 * Copyright (C) 2017 Light Team Software
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

package com.KillerBLS.modpeide.adapter;

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
import com.KillerBLS.modpeide.manager.SuggestionManager;

import java.util.ArrayList;
import java.util.Collection;

public class BasicCompletionAdapter extends ArrayAdapter<CompletionItem> {
    private final int colorKeyWord;
    private final int colorNormal;
    private LayoutInflater inflater;
    private ArrayList<CompletionItem> clone;
    private ArrayList<CompletionItem> suggestion;
    private int resourceID;
    private Filter codeFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            if (resultValue == null) {
                return "";
            }
            return ((CompletionItem) resultValue).getName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            suggestion.clear();
            if (constraint != null) {
                for (CompletionItem item : clone) {
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
        @SuppressWarnings("unchecked")

        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<CompletionItem> filteredList = (ArrayList<CompletionItem>) results.values;
            clear();
            if (filteredList != null && filteredList.size() > 0) {
                addAll(filteredList);
            }
            notifyDataSetChanged();
        }
    };

    @SuppressWarnings({"unchecked", "deprecation"})
    public BasicCompletionAdapter(@NonNull Context context,
                                  @LayoutRes int resource, @NonNull ArrayList<CompletionItem> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
        this.clone = (ArrayList<CompletionItem>) objects.clone();
        this.suggestion = new ArrayList<>();
        this.resourceID = resource;
        colorKeyWord = context.getResources().getColor(android.R.color.primary_text_dark);
        colorNormal = context.getResources().getColor(android.R.color.primary_text_dark);
    }

    public ArrayList<CompletionItem> getAllItems() {
        return clone;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resourceID, null);
        }

        final CompletionItem item = getItem(position);

        TextView txtName = convertView.findViewById(R.id.txt_title);
        assert item != null;
        txtName.setText(item.getShow() != null ? item.getShow() : item.getName());
        switch (item.getType()) {
            case SuggestionManager.TYPE_KEYWORD: //keyword
                txtName.setTextColor(colorKeyWord);
                txtName.setTypeface(Typeface.DEFAULT_BOLD);
                break;
            case SuggestionManager.TYPE_VARIABLE: //variable
                int colorVariable = 0xffFFB74D;
                txtName.setTextColor(colorVariable);
                txtName.setTypeface(Typeface.DEFAULT);
                break;
            default:
                txtName.setTextColor(colorNormal);
                txtName.setTypeface(Typeface.DEFAULT);
                break;
        }
        return convertView;
    }

    public void clearAllData() {
        super.clear();
        clone.clear();
    }

    public void addData(@NonNull Collection<? extends CompletionItem> collection) {
        addAll(collection);
        clone.addAll(collection);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return codeFilter;
    }
}
