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

package com.KillerBLS.modpeide.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.keyboard.ExtendedKeyboard;

/**
 * @author Trần Lê Duy
 */
public class SymbolAdapter extends RecyclerView.Adapter<SymbolAdapter.ViewHolder> {

    private String[] mList;
    private ExtendedKeyboard.OnKeyListener mListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_key, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text.setText(mList[position]);
        holder.text.setOnClickListener(v -> {
            if (mListener != null)
                mListener.onKeyClick(v,((TextView) v).getText().toString());
        });
    }

    @Override
    public int getItemCount() {
        return mList.length;
    }


    public void setListKey(String[] list) {
        mList = list;
    }

    public void setListener(ExtendedKeyboard.OnKeyListener listener) {
        mListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;

        ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text_view);
        }
    }
}
