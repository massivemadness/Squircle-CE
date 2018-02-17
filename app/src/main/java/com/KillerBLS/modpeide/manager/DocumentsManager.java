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

package com.KillerBLS.modpeide.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.KillerBLS.modpeide.adapter.LockableViewPager;
import com.commonsware.cwac.pager.PageDescriptor;
import com.commonsware.cwac.pager.SimplePageDescriptor;
import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.adapter.DocumentAdapter;
import com.KillerBLS.modpeide.document.Document;
import com.KillerBLS.modpeide.document.commons.FileObject;
import com.KillerBLS.modpeide.utils.Wrapper;
import com.KillerBLS.modpeide.utils.files.TabFileUtils;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class DocumentsManager {

    private static DocumentAdapter mAdapter;
    private TabLayout mTabLayout;
    private LockableViewPager mViewPager;
    private Wrapper mWrapper;
    private FileManager mFileManager;
    private Context mContext;

    public DocumentsManager(Context context, Wrapper wrapper, FileManager fileManager) {
        mContext = context;
        mWrapper = wrapper;
        mFileManager = fileManager;
    }

    public void setTabLayout(TabLayout tabLayout) {
        mTabLayout = tabLayout;
    }

    public void setViewPager(LockableViewPager viewPager) {
        mViewPager = viewPager;
        mViewPager.setOffscreenPageLimit(mWrapper.getMaxTabsCount()); //Кол-во страниц (максимум)
        mViewPager.setSwipeLocked(mWrapper.getDisableSwipeGesture()); //Блокировка свайпа между вкладками
    }

    public DocumentAdapter getAdapter() {
        return mAdapter;
    }

    public void setupViewPager(FragmentManager fragmentManager) {
        ArrayList<FileObject> listFile = TabFileUtils.getTabFiles(mContext);
        ArrayList<PageDescriptor> pages = new ArrayList<>();
        if(mWrapper.getResumeSession()) { //открываем файлы на старте
            for (FileObject file : listFile) {
                pages.add(new SimplePageDescriptor(file.getPath(), file.getName()));
            }
        } else { //удаляем из Database если открывать не нужно
            DatabaseManager mDatabaseManager = new DatabaseManager(mContext);
            for (FileObject file : listFile) {
                mDatabaseManager.removeFile(file.getPath());
            }
        }
        mAdapter = new DocumentAdapter(fragmentManager, pages);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        invalidateTabs();
        if (mAdapter.isEmpty()) { //Если нет открытых документов, открываем стартовую страницу
            String fileName = mContext.getString(R.string.start_page_name);
            addNewPageEditor(new FileObject(fileName), true, true); //выбираем документ
        }
    }

    /**
     * Устанавливаем кастомный layout для файловых вкладок и добавляем соответствующие действия
     * для кнопки закрытия документа и TextView с именем выбранного файла.
     */
    private void invalidateTabs() {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            final TabLayout.Tab tab = mTabLayout.getTabAt(i);
            View view = null;
            if (tab != null) {
                tab.setCustomView(R.layout.item_tab_file);
                view = tab.getCustomView();
            }
            if (view != null) {
                View vClose = view.findViewById(R.id.img_close);
                final int position = i;
                vClose.setOnClickListener(v ->
                        removePage(position)
                );
                TextView txtTitle = view.findViewById(R.id.txt_name);
                txtTitle.setText(mAdapter.getPageTitle(i));
                txtTitle.setOnClickListener(v ->
                        mViewPager.setCurrentItem(position)
                );
            }
            if (i == mViewPager.getCurrentItem()) {
                if (tab != null) {
                    tab.select();
                }
            }
        }
    }

    /**
     * Метод удаляет страницу из списка открытых файлов.
     * @param position - позиция документа.
     */
    private void removePage(int position) {
        Fragment existingFragment = mAdapter.getExistingFragment(position);
        if (existingFragment == null) {
            return;
        }

        //сообщение при закрытии
        String currentFileName = mAdapter.getPageTitle(position);
        Toasty.success(mContext,
                mContext.getString(R.string.closed)
                        + currentFileName, Toast.LENGTH_SHORT, true).show();

        //удаляем из базы данных
        String filePath = existingFragment.getTag();
        mFileManager.removeTabFile(filePath);

        //удаляем страницу
        mAdapter.remove(position);
        invalidateTabs();
    }

    /**
     * Удаление текущей страницы.
     */
    public void removeSelectedPage() {
        removePage(mTabLayout.getSelectedTabPosition());
    }

    /**
     * Добавление документа в список открытых файлов.
     * @param file - файл из памяти.
     * @param selectNewPage - перейти на открытую вкладку? или просто открыть?
     * @param isStartPage - стартовая страница-ли это?
     */
    private void addNewPageEditor(@NonNull FileObject file, boolean selectNewPage, boolean isStartPage) {
        int position = mAdapter.getPositionForTag(file.getPath());
        if (position != -1) { //если этот документ есть в списке файлов
            //проверяем нужно-ли выбрать открытый документ
            if (selectNewPage) {
                TabLayout.Tab tab = mTabLayout.getTabAt(position);
                if (tab != null) {
                    tab.select();
                    mViewPager.setCurrentItem(position);
                }
            }
        } else { //при создании нового файла
            if(isStartPage) { //если создаем стартовую страницу, то...
                //добавляем особый фрагмент стартовой страницы
                mAdapter.add(new SimplePageDescriptor("", file.getName())); //"Start Page"
                invalidateTabs();
            } else {
                //добавляем в базу данных
                mFileManager.addNewPath(file.getPath());
                //новая страница
                mAdapter.add(new SimplePageDescriptor(file.getPath(), file.getName()));
                invalidateTabs();
                if (selectNewPage) {
                    int indexOfNewPage = mAdapter.getCount() - 1;
                    TabLayout.Tab tab = mTabLayout.getTabAt(indexOfNewPage);
                    if (tab != null) {
                        tab.select();
                        mViewPager.setCurrentItem(indexOfNewPage);
                    }
                }
            }
        }
    }

    /**
     * Обновление редактора при перехода в главное активити после изменения настроек.
     */
    public void onResumeActivity() {
        if(mAdapter != null && !mAdapter.isEmpty()) { //if tabs count != 0 or null
            for(int i = 0; i < mAdapter.getCount(); i++) {
                if(mAdapter.getExistingFragment(i) != null) { //fix
                    mAdapter.getExistingFragment(i).refreshEditor();
                }
            }
        }
    }

    /**
     * Метод для получения текущего документа вне класса.
     * @return - возвращает текущий фрагмент.
     */
    @Nullable
    public static Document getDisplayedDocument() {
        return mAdapter.getCurrentFragment();
    }

    /**
     * Создание и добавление файла в список.
     * @param filePath - путь создаваемого файла.
     * @see #addNewPageEditor(FileObject, boolean, boolean).
     */
    public void newFileToTabs(String filePath) {
        String newFilePath = mFileManager.createNewFile(filePath);
        addNewPageEditor(new FileObject(newFilePath), true, false); //выбираем документ
    }

    /**
     * Аналог метода #addNewFileToTabs, сделан для того чтобы не путаться когда нужно создать
     * файл, а когда просто загружать из памяти.
     * @param file - файл для открытия из памяти.
     * @see #addNewPageEditor(FileObject, boolean, boolean).
     * @see #newFileToTabs(String).
     */
    public void loadFileToTabs(FileObject file) {
        addNewPageEditor(file, true, false);
    }

}
