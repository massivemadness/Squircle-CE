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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.KillerBLS.modpeide.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.commonsware.cwac.pager.PageDescriptor;
import com.commonsware.cwac.pager.v4.ArrayPagerAdapter;
import com.KillerBLS.modpeide.document.Document;
import com.KillerBLS.modpeide.utils.Wrapper;

import java.util.ArrayList;

public class DocumentAdapter extends ArrayPagerAdapter<Document> {

    public DocumentAdapter(@NonNull FragmentManager fragmentManager,
                           @NonNull ArrayList<PageDescriptor> desc) {
        super(fragmentManager, desc);
    }

    @Override
    protected Document createFragment(@Nullable PageDescriptor desc) {
        if(desc.getFragmentTag().equals("") && desc.getTitle().equals("Start Page")) {
            return(Document.newInstance("Start Page", true)); //Создание стартовой страницы
        } else { //иначе, это создание нормального файла
            return(Document.newInstance(desc.getFragmentTag(), false));
        }
    }

    /**
     * Проверка на наличие элементов.
     * @return - возвращает true если количество фрагментов равно нулю.
     */
    public boolean isEmpty() {
        return this.getCount() == 0;
    }

    /**
     * Проверка на наличие элементов.
     * @return - возвращает true если количество фрагментов больше или равно
     * максимально допустимому количеству фрагментов.
     */
    public boolean isFull(Wrapper wrapper) {
        return this.getCount() >= wrapper.getMaxTabsCount();
    }
}
