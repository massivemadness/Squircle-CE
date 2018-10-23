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

package com.KillerBLS.modpeide.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;

public class RecyclerViewStub extends RecyclerView {

    private ViewStub mViewStub;

    private RecyclerView.AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            RecyclerView.Adapter<?> adapter = getAdapter();
            if(adapter != null) {
                if(adapter.getItemCount() <= 1) { //Если в списке меньше 1-го элемента (или 1)
                    if(mViewStub != null) {
                        if(mViewStub.getParent() != null) { //И ViewStub всё еще не добавил InflatedView
                            mViewStub.inflate(); //Добавляем InflatedView
                        }
                        mViewStub.setVisibility(View.VISIBLE);
                    }
                } else {
                    mViewStub.setVisibility(View.GONE);
                }
            }
        }
    };

    // region CONSTRUCTOR

    public RecyclerViewStub(Context context) {
        super(context);
    }

    public RecyclerViewStub(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewStub(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // endregion CONSTRUCTOR

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if(adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }
        mObserver.onChanged();
    }

    /**
     * Устанавливает ViewStub для отображения при отсутствии элементов.
     * @param viewStub - ViewStub для отображения.
     */
    public void setViewStub(ViewStub viewStub) {
        mViewStub = viewStub;
    }
}