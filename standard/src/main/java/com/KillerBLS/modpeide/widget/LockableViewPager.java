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