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

package com.KillerBLS.modpeide.utils.commons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.View;

import com.KillerBLS.modpeide.R;

public class MenuHelper {

    /**
     * Отображение всплывающего меню вместе с иконкой.
     * @param context - контекст для показа.
     * @param view - View по которому было нажатие.
     * @param menuRes - меню которое будет открыто.
     * @param listener - слушатель для определения нажатий.
     */
    @SuppressLint("RestrictedApi")
    public static void forceShow(Context context, View view,
                                 int menuRes, PopupMenu.OnMenuItemClickListener listener) {
        ContextThemeWrapper wrapper =
                new ContextThemeWrapper(context, R.style.Theme_Platform_PopupMenu); //Светлая тема
        PopupMenu menu = new PopupMenu(wrapper, view);
        menu.inflate(menuRes);
        menu.setOnMenuItemClickListener(listener);
        MenuPopupHelper menuHelper = new MenuPopupHelper(wrapper, (MenuBuilder) menu.getMenu(), view);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }
}
