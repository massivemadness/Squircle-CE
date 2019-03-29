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

import com.KillerBLS.modpeide.fragment.FragmentDirectory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DirectoryAdapter extends PagerAdapter {

    private static final String KEY_DIRECTORIES = "DIRECTORIES";

    private ArrayList<String> mData = new ArrayList<>(); //String - путь к папке
    private HashMap<Fragment, Integer> mPositionDelta = new HashMap<>();

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private FragmentDirectory mCurrentFragment;

    public DirectoryAdapter(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    // region METHODS

    /**
     * Добавление фрагмента в конец.
     * @param path - путь к добавляемой директории.
     */
    public void addToStack(String path) {
        mPositionDelta.clear();
        mData.add(path);
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
     * Проверка, существует ли добавляемый путь уже в списке.
     * @param path - добавляемый путь.
     * @return - вернет позицию фрагмента если он существует, и -1 если не существует.
     */
    public int contains(String path) {
        for(int i = 0; i < mData.size(); i++) {
            if(mData.get(i).equals(path)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Получение директории по позиции.
     * @param pos - индекс позиции.
     * @return - вернет путь к директории.
     */
    public String get(int pos) {
        return mData.get(pos);
    }

    // endregion METHODS

    // region ITEM

    @SuppressWarnings("unchecked")
    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        FragmentDirectory fragment = (FragmentDirectory) object;
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
            fragment = FragmentDirectory.newInstance(mData.get(position));
            mFragmentTransaction.add(container.getId(), fragment, mData.get(position));
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
        state.putStringArrayList(KEY_DIRECTORIES, mData);
        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if(state != null) {
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(getClass().getClassLoader());
            mData = ((Bundle) state).getStringArrayList(KEY_DIRECTORIES);
            notifyDataSetChanged();
        }
    }

    // endregion STATE

    /**
     * Получение существующего фрагмента по его позиции.
     * @param position - позиция фрагмента, который хотим получить.
     * @return - возвращает запрошенный фрагмент.
     */
    public FragmentDirectory getExistingFragment(int position) {
        return (FragmentDirectory) mFragmentManager.findFragmentByTag(mData.get(position));
    }

    /**
     * Получение текущего фрагмента.
     * @return - возвращает текущий фрагмент.
     */
    public FragmentDirectory getCurrentFragment() {
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
        return new File(getDirectory(position)).getName();
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
     * Получение директории по позиции.
     * @param position - позиция фрагмента, директорию которого хотим получить.
     * @return - возвращает директорию.
     */
    public String getDirectory(int position) {
        return mData.get(position);
    }
}