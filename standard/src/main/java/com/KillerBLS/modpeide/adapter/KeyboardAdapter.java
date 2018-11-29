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
            text.setOnClickListener(v ->
                    listener.onKeyClick(v, ((TextView) v).getText().toString()));
        }
    }
}