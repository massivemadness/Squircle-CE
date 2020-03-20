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

package com.lightteam.modpeide.utils.extensions

import android.graphics.drawable.InsetDrawable
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.findFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.tabs.TabLayout

// https://github.com/material-components/material-components-android/commit/560adc655d24f82e3fd866a7840ff7e9db07b301

fun PopupMenu.makeRightPaddingRecursively() {
    if (menu is MenuBuilder) {
        val menuBuilder = menu as MenuBuilder
        menuBuilder.setOptionalIconsVisible(true)
        for (item in menuBuilder.visibleItems) {
            item.makeRightPadding()
            if (item.hasSubMenu()) {
                for (subItem in item.subMenu.iterator()) {
                    subItem.makeRightPadding()
                }
            }
        }
    }
}

fun MenuItem.makeRightPadding() {
    if (icon != null) {
        val iconMargin = 8.toPx() // 8 dp to px
        icon = InsetDrawable(icon, iconMargin, 0, iconMargin, 0)
    }
}

fun Fragment.setSupportActionBar(toolbar: Toolbar) {
    val parentActivity = activity as AppCompatActivity
    parentActivity.setSupportActionBar(toolbar)
}

fun <T : Fragment> FragmentContainerView.fragment(): T {
    return get(0).findFragment()
}

val NavHostFragment.backStackEntryCount: Int
    get() {
        return childFragmentManager.backStackEntryCount
    }

fun NavController.popBackStack(n_times: Int) {
    for (n in 0 until n_times) {
        popBackStack()
    }
}

fun TabLayout.newTab(name: String, resId: Int, callback: (TabLayout.Tab) -> Unit): TabLayout.Tab {
    val tab = newTab()
    tab.text = name
    tab.setCustomView(resId)
    addTab(tab)
    post {
        callback.invoke(tab)
    }
    return tab
}

fun TabLayout.removeLast(n: Int) {
    for (n in 0 until n) {
        val count = tabCount - 1
        removeTabAt(count)
    }
}