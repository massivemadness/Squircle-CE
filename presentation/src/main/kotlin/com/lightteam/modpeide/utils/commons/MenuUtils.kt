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

package com.lightteam.modpeide.utils.commons

import android.graphics.drawable.InsetDrawable
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.iterator

object MenuUtils {

    // https://github.com/material-components/material-components-android/commit/560adc655d24f82e3fd866a7840ff7e9db07b301
    fun makeRightPaddingRecursively(view: View, popupMenu: PopupMenu) {
        if(popupMenu.menu is MenuBuilder) {
            val menuBuilder = popupMenu.menu as MenuBuilder
            menuBuilder.setOptionalIconsVisible(true)
            for(item in menuBuilder.visibleItems) {
                makeRightPadding(view, item)
                if(item.hasSubMenu()) {
                    for(subItem in item.subMenu.iterator()) {
                        makeRightPadding(view, subItem)
                    }
                }
            }
        }
    }

    private fun makeRightPadding(view: View, item: MenuItem) {
        val iconMarginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 8F, view.resources.displayMetrics
        ).toInt()
        item.icon = InsetDrawable(item.icon, iconMarginPx, 0, iconMarginPx, 0)
    }
}