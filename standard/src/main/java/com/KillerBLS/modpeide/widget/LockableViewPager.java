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
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * ViewPager с возможностью убрать скроллинг фрагментов свайпами.
 * { https://stackoverflow.com/a/34609762/4405457 }
 */
public class LockableViewPager extends ViewPager {

    private boolean isSwipeLocked = false;

    // region CONSTRUCTOR

    public LockableViewPager(Context context) {
        super(context);
    }

    public LockableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // endregion CONSTRUCTOR

    // region BASE

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !isSwipeLocked && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return !isSwipeLocked && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return !isSwipeLocked && super.canScrollHorizontally(direction);
    }

    // endregion BASE

    // region METHODS

    /**
     * @return - возвращает значение, заблокированы ли свайпы ViewPager.
     */
    public boolean getSwipeLocked() {
        return isSwipeLocked;
    }

    /**
     * Устанавливает блокировку свайпов на ViewPager.
     * @param swipeLocked - отвечает за блокировку свайпов.
     */
    public void setSwipeLocked(boolean swipeLocked) {
        isSwipeLocked = swipeLocked;
    }

    // endregion METHODS
}