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
