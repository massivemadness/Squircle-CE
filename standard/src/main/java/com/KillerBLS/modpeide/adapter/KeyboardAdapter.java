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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.widget.ExtendedKeyboard;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KeyboardAdapter extends RecyclerView.Adapter<KeyboardAdapter.KeyViewHolder> {

    private String[] mList;
    private ExtendedKeyboard.OnKeyListener mListener;

    public KeyboardAdapter(ExtendedKeyboard.OnKeyListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public KeyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_key, parent, false);
        return new KeyViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyViewHolder holder, int position) {
        holder.text.setText(mList[position]);
    }

    @Override
    public int getItemCount() {
        return mList.length;
    }

    /**
     * Установка отображаемых символов.
     * @param list - массив с символами.
     */
    public void setListKey(String[] list) {
        mList = list;
    }

    static class KeyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_title)
        TextView text;

        KeyViewHolder(@NonNull View itemView, @NonNull ExtendedKeyboard.OnKeyListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            text.setOnClickListener(v -> {
                listener.onKeyClick(v, ((TextView) v).getText().toString());
            });
        }
    }
}