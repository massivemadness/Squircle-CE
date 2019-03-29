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
                if(adapter.getItemCount() == 0) { //Если в списке 0 элементов
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