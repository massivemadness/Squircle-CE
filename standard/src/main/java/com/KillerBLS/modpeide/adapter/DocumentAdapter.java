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

package com.KillerBLS.modpeide.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.KillerBLS.modpeide.fragment.FragmentDocument;
import com.KillerBLS.modpeide.manager.database.Document;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Mark Murphy
 */
public class DocumentAdapter extends /*Array*/PagerAdapter {

    private static final String KEY_DOCUMENTS = "DOCUMENTS";

    private ArrayList<Document> mData = new ArrayList<>();
    private HashMap<Fragment, Integer> mPositionDelta = new HashMap<>();

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private FragmentDocument mCurrentFragment;

    public DocumentAdapter(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    // region METHODS

    /**
     * Добавление фрагмента в конец списка адаптера.
     * @param document - документ (фрагмента) который хотим добавить.
     */
    public void add(Document document) {
        validateDocument(document);

        mPositionDelta.clear();
        mData.add(document);
        notifyDataSetChanged();
    }

    /**
     * Добавление фрагмента на определенную позицию в адаптере.
     * @param document - документ (фрагмента) который хотим добавить.
     * @param position - позиция на которую будет добавлен фрагмент.
     */
    public void insert(Document document, int position) {
        validateDocument(document);

        mPositionDelta.clear();
        for(int i = position; i < mData.size(); i++) {
            Fragment fragment = getExistingFragment(i);
            if(fragment != null) {
                mPositionDelta.put(fragment, i + 1);
            }
        }
        mData.add(position, document);
        notifyDataSetChanged();
    }

    /**
     * Удаление фрагмента из адаптера.
     * @param position - позиция фрагмента для удаления.
     */
    public void remove(int position) {
        mPositionDelta.clear();

        Fragment fragment = getExistingFragment(position);
        if(fragment != null) {
            mPositionDelta.put(fragment, PagerAdapter.POSITION_NONE);
        }
        for(int i = position + 1; i < mData.size(); i++) {
            fragment = getExistingFragment(i);
            if (fragment != null) {
                mPositionDelta.put(fragment, i - 1);
            }
        }
        mData.remove(position);
        notifyDataSetChanged();
    }

    /**
     * Перемещение фрагмента с одной позиции на другую.
     * @param oldPosition - старая позиция фрагмента.
     * @param newPosition - новая позиция фрагмента.
     */
    public void move(int oldPosition, int newPosition) {
        if (oldPosition != newPosition) {
            Document document = getDocument(oldPosition);
            remove(oldPosition);
            insert(document, newPosition);
        }
    }

    // endregion METHODS

    // region ITEM

    @SuppressWarnings("unchecked")
    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        FragmentDocument fragment = (FragmentDocument) object;
        if(fragment != mCurrentFragment) {
            if(mCurrentFragment != null) {
                mCurrentFragment.setMenuVisibility(false);
                mCurrentFragment.setUserVisibleHint(false);
            }
            fragment.setMenuVisibility(true);
            fragment.setUserVisibleHint(true);
            mCurrentFragment = fragment;
        }
    }

    @SuppressLint("CommitTransaction") //See: finishUpdate()
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if(mFragmentTransaction == null) {
            mFragmentTransaction = mFragmentManager.beginTransaction();
        }

        Fragment fragment = getExistingFragment(position);
        if(fragment != null) {
            mFragmentTransaction.attach(fragment);
        } else {
            fragment = FragmentDocument.newInstance(mData.get(position).getUuid());
            mFragmentTransaction.add(container.getId(), fragment, mData.get(position).getUuid());
        }
        //mFragmentTransaction.commit();

        if(fragment != mCurrentFragment) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }
        return fragment;
    }

    @SuppressLint("CommitTransaction") //See: finishUpdate()
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if(mFragmentTransaction == null) {
            mFragmentTransaction = mFragmentManager.beginTransaction();
        }
        mFragmentTransaction.detach((Fragment) object);
        //mFragmentTransaction.commit();
    }

    // endregion ITEM

    // region UPDATE

    @Override
    public void startUpdate(@NonNull ViewGroup container) { }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        if(mFragmentTransaction != null) {
            mFragmentTransaction.commitAllowingStateLoss();
            mFragmentTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    // endregion UPDATE

    // region STATE

    @Override
    public Parcelable saveState() {
        Bundle state = new Bundle();
        state.putParcelableArrayList(KEY_DOCUMENTS, mData);
        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if(state != null) {
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(getClass().getClassLoader());
            mData = ((Bundle) state).getParcelableArrayList(KEY_DOCUMENTS);
            notifyDataSetChanged();
        }
    }

    // endregion STATE

    // region BASE

    /**
     * Получение существующего фрагмента по его позиции.
     * @param position - позиция фрагмента, который хотим получить.
     * @return - возвращает запрошенный фрагмент.
     */
    public FragmentDocument getExistingFragment(int position) {
        return (FragmentDocument) mFragmentManager.findFragmentByTag(mData.get(position).getUuid());
    }

    /**
     * Получение текущего фрагмента.
     * @return - возвращает текущий фрагмент.
     */
    public FragmentDocument getCurrentFragment() {
        return mCurrentFragment;
    }

    /**
     * Проверка является ли указанное View частью View указанного фрагмента.
     * @param view - View для проверки.
     * @param object - Fragment для проверки.
     * @return - вернет true, если указанное View является View указанного фрагмента.
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return ((Fragment) object).getView() == view;
    }

    /**
     * @return - возвращает количество фрагментов в адаптере.
     */
    @Override
    public int getCount() {
        return mData.size();
    }

    /**
     * Проверяет, является ли список с элементами пустым.
     * @return - возвращает true если список пуст.
     */
    public boolean isEmpty() {
        return mData.isEmpty();
    }

    /**
     * Получение заголовка указанного фрагмента.
     * @param position - позиция фрагмента, заголовок которого хотим получить.
     * @return - возвращает заголовок указанного фрагмента.
     */
    @Override
    public String getPageTitle(int position) {
        return getDocument(position).getName();
    }

    /**
     * Получение позиции указанного фрагмента.
     * @param object - фрагмент, позицию которого хотим узнать.
     * @return - возвращает позицию указанного фрагмента.
     */
    @Override
    public int getItemPosition(@NonNull Object object) {
        Integer result = mPositionDelta.get(object);
        if(result == null) {
            return PagerAdapter.POSITION_UNCHANGED;
        }
        return result;
    }

    /**
     * Получение документа по его позиции.
     * @param position - позиция фрагмента, документ которого хотим получить.
     * @return - возвращает документ.
     */
    public Document getDocument(int position) {
        return mData.get(position);
    }

    /**
     * Получение позиции фрагмента по его расположению.
     * @param path - путь к документу, позицию которого хотим получить.
     * @return - возвращает позицию фрагмента.
     */
    public int findPosition/*ByPath*/(String path) {
        for (int i = 0; i < mData.size(); i++) {
            Document document = mData.get(i);
            if (document.getPath().equals(path)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Проверка добавляемого документа. UUID каждого документа должен быть уникален.
     * @param document - добавляемый документ.
     */
    private void validateDocument(Document document) {
        for(Document model : mData) {
            if(document.getUuid().equals(model.getUuid())) {
                throw new IllegalArgumentException("UUID not unique: " + document.getUuid());
            }
        }
    }

    // endregion BASE
}