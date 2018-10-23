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

package com.KillerBLS.modpeide.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.adapter.DocumentAdapter;
import com.KillerBLS.modpeide.adapter.model.FileModel;
import com.KillerBLS.modpeide.dialog.commons.DialogExit;
import com.KillerBLS.modpeide.dialog.commons.DialogShop;
import com.KillerBLS.modpeide.dialog.files.DialogProperties;
import com.KillerBLS.modpeide.dialog.search.DialogFind;
import com.KillerBLS.modpeide.dialog.search.DialogGoToLine;
import com.KillerBLS.modpeide.dialog.search.DialogReplaceAll;
import com.KillerBLS.modpeide.manager.FileManager;
import com.KillerBLS.modpeide.manager.ToolbarManager;
import com.KillerBLS.modpeide.manager.database.AppData;
import com.KillerBLS.modpeide.manager.database.Document;
import com.KillerBLS.modpeide.manager.interfaces.DocumentsManager;
import com.KillerBLS.modpeide.manager.interfaces.OnPanelClickListener;
import com.KillerBLS.modpeide.utils.Wrapper;
import com.KillerBLS.modpeide.utils.commons.EditorDelegate;
import com.KillerBLS.modpeide.utils.commons.MenuHelper;
import com.KillerBLS.modpeide.widget.ExtendedKeyboard;
import com.KillerBLS.modpeide.widget.LockableViewPager;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MainActivity extends AppCompatActivity implements OnPanelClickListener,
        DocumentsManager, ExtendedKeyboard.OnKeyListener, HasSupportFragmentInjector {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    DispatchingAndroidInjector<Fragment> mFragmentInjector;

    @Inject
    Wrapper mWrapper;
    @Inject
    AppData mDatabase;
    @Inject
    DocumentAdapter mAdapter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    LockableViewPager mViewPager;
    @BindView(R.id.stub_no_open_files)
    ViewStub mViewStub;
    @BindView(R.id.extended_keyboard)
    ExtendedKeyboard mExtendedKeyboard;

    private ToolbarManager mToolbarManager;
    private ActionBarDrawerToggle mDrawerToggle;
    private long mPressTime;

    // region BASE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        //Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbarManager = new ToolbarManager(this);

        //Drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.description_open, R.string.description_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                try {
                    closeKeyboard();
                } catch (NullPointerException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        onConfigurationChanged(getResources().getConfiguration()); //Обновляем конфиругацию

        //Tabs
        mViewPager.setSwipeLocked(mWrapper.getDisableSwipeGesture());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        //Keyboard
        mExtendedKeyboard.setHasFixedSize(true);
        mExtendedKeyboard.init(this);
        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
            if(isOpen && mWrapper.getExtendedKeyboard()) {
                mExtendedKeyboard.setVisibility(View.VISIBLE);
            } else {
                mExtendedKeyboard.setVisibility(View.GONE);
            }
        });

        //Load documents on start
        if(mWrapper.getResumeSession()) {
            for(Document document : mDatabase.getDao().getAll()) {
                mAdapter.add(document);
            }
            invalidateTabs();
        } else {
            mDatabase.clearAllTables(); //Т.к не хотим оставлять старые файлы в базе данных
            FileManager.deleteRecursive(FileManager.getCachedFilesDir()); //и в кеше
        }

        //Check file receiver
        String filePath = getIntent().getStringExtra("SELECTED_FILE_PATH");
        if(filePath != null) {
            addDocument(FileManager.convert(new FileModel(new File(filePath))));
        }
    }

    // endregion BASE

    // region DOCUMENTS

    @SuppressLint("RtlHardcoded")
    @Override
    public void addDocument(Document document) {
        if(mAdapter.getCount() < mWrapper.getMaxTabsCount()) {
            int position = mAdapter.findPosition(document.getPath());
            if(position != -1) { //Если документ уже есть в списке файлов
                TabLayout.Tab tab = mTabLayout.getTabAt(position);
                if (tab != null) { //и вкладка этого документа существует
                    tab.select(); //переключаемся на него
                    mViewPager.setCurrentItem(position);
                }
            } else { //Если файла еще нет в списке файлов
                mDatabase.getDao().insert(document);
                mAdapter.add(document); //добавляем его
                invalidateTabs();
                int indexOfNewPage = mAdapter.getCount() - 1;
                TabLayout.Tab tab = mTabLayout.getTabAt(indexOfNewPage);
                if (tab != null) {
                    tab.select(); //переключаемся на него
                    mViewPager.setCurrentItem(indexOfNewPage);
                }
            }
            if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        } else {
            Toast.makeText(this, R.string.message_too_much_tabs, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void removePosition(int position) {
        String uuid = mAdapter.getDocument(position).getUuid();
        mDatabase.getDao().delete(mAdapter.getDocument(position)); //Удаляем из базы данных
        mAdapter.remove(position); //Удаляем из адаптера
        FileManager.clearCache(uuid); //Удаляем из кеша (ПОСЛЕ удаления из адаптера, ибо автосейв)
        invalidateTabs();
    }

    private void invalidateTabs() {
        for(int i = 0; i < mAdapter.getCount(); i++) {
            final int position = i;
            TabLayout.Tab tab = mTabLayout.getTabAt(position);
            View tabView = null;
            if(tab != null) {
                tab.setCustomView(R.layout.item_tab);
                tabView = tab.getCustomView();
            }
            if(tabView != null) {
                TextView title = tabView.findViewById(R.id.item_title);
                ImageView icon = tabView.findViewById(R.id.item_icon);

                title.setText(mAdapter.getPageTitle(position));
                icon.setOnClickListener(view -> removePosition(position));
                ((View) tabView.getParent()).setOnLongClickListener((view -> {
                    MenuHelper.forceShow(this, view, R.menu.menu_actions_tab, item -> {
                        switch (item.getItemId()) {
                            case R.id.action_close: //Close
                                removePosition(position);
                                break;
                            case R.id.action_close_others: //Close Others
                                for(int pos = mAdapter.getCount() - 1; pos >= 0; pos--) {
                                    if(pos != position) {
                                        removePosition(pos);
                                    }
                                }
                                break;
                            case R.id.action_close_all: //Close All
                                for(int pos = mAdapter.getCount() - 1; pos >= 0; pos--) {
                                    removePosition(pos);
                                }
                                break;
                        }
                        return false;
                    });
                    return true;
                }));
            }
        }
        if(mAdapter.isEmpty()) {
            if(mViewStub.getParent() != null) {
                mViewStub.inflate();
            }
            mViewStub.setVisibility(View.VISIBLE);
        } else {
            mViewStub.setVisibility(View.GONE);
        }
    }

    // endregion DOCUMENTS

    // region MENU

    @Override
    public void onKeyClick(View view, String text) { //Extended Keyboard listener
        if(!mAdapter.isEmpty()) {
            EditorDelegate editorDelegate = mAdapter.getCurrentFragment();
            editorDelegate.notifyInsertClicked(text);
        } else {
            Toast.makeText(this, R.string.message_editor_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onNewButton() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
        Toast.makeText(this, R.string.message_new_file, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onOpenButton() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
        Toast.makeText(this, R.string.message_select_file, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveButton() {
        if(!mAdapter.isEmpty()) {
            EditorDelegate editorDelegate = mAdapter.getCurrentFragment();
            editorDelegate.notifySaveClicked();
            Toast.makeText(this, String.format(getString(R.string.message_document_saved),
                    mAdapter.getPageTitle(mTabLayout.getSelectedTabPosition())), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.message_editor_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPropertiesButton() {
        if(!mAdapter.isEmpty()) {
            new DialogProperties.Builder(this)
                    .withFile(new File(mAdapter.getDocument(mTabLayout.getSelectedTabPosition()).getPath()))
                    .show();
        } else {
            Toast.makeText(this, R.string.message_editor_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCloseButton() {
        if(!mAdapter.isEmpty()) {
            removePosition(mTabLayout.getSelectedTabPosition());
        } else {
            Toast.makeText(this, R.string.message_editor_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCutButton() {
        if(!mAdapter.isEmpty()) {
            EditorDelegate editorDelegate = mAdapter.getCurrentFragment();
            editorDelegate.notifyCutClicked();
        } else {
            Toast.makeText(this, R.string.message_editor_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCopyButton() {
        if(!mAdapter.isEmpty()) {
            EditorDelegate editorDelegate = mAdapter.getCurrentFragment();
            editorDelegate.notifyCopyClicked();
        } else {
            Toast.makeText(this, R.string.message_editor_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPasteButton() {
        if(!mAdapter.isEmpty()) {
            EditorDelegate editorDelegate = mAdapter.getCurrentFragment();
            editorDelegate.notifyPasteClicked();
        } else {
            Toast.makeText(this, R.string.message_editor_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSelectAllButton() {
        if(!mAdapter.isEmpty()) {
            EditorDelegate editorDelegate = mAdapter.getCurrentFragment();
            editorDelegate.notifySelectAllClicked();
        } else {
            Toast.makeText(this, R.string.message_editor_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSelectLineButton() {
        if(!mAdapter.isEmpty()) {
            EditorDelegate editorDelegate = mAdapter.getCurrentFragment();
            editorDelegate.notifySelectLineClicked();
        } else {
            Toast.makeText(this, R.string.message_editor_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteLineButton() {
        if(!mAdapter.isEmpty()) {
            EditorDelegate editorDelegate = mAdapter.getCurrentFragment();
            editorDelegate.notifyDeleteLineClicked();
        } else {
            Toast.makeText(this, R.string.message_editor_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDuplicateLineButton() {
        if(!mAdapter.isEmpty()) {
            EditorDelegate editorDelegate = mAdapter.getCurrentFragment();
            editorDelegate.notifyDuplicateLineClicked();
        } else {
            Toast.makeText(this, R.string.message_editor_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFindButton() {
        if(!mAdapter.isEmpty()) {
            EditorDelegate editorDelegate = mAdapter.getCurrentFragment();
            new DialogFind.Builder(this, editorDelegate).show();
        } else {
            Toast.makeText(this, R.string.message_editor_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReplaceAllButton() {
        if(!mAdapter.isEmpty()) {
            EditorDelegate editorDelegate = mAdapter.getCurrentFragment();
            new DialogReplaceAll.Builder(this, editorDelegate).show();
        } else {
            Toast.makeText(this, R.string.message_editor_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGoToLineButton() {
        if(!mAdapter.isEmpty()) {
            EditorDelegate editorDelegate = mAdapter.getCurrentFragment();
            new DialogGoToLine.Builder(this, editorDelegate).show();
        } else {
            Toast.makeText(this, R.string.message_editor_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSyntaxValidatorButton() {
        new DialogShop.Builder(this).show();
    }

    @Override
    public void onInsertColorButton() {
        new DialogShop.Builder(this).show();
    }

    @Override
    public void onUndoButton() {
        if(!mAdapter.isEmpty()) {
            EditorDelegate editorDelegate = mAdapter.getCurrentFragment();
            editorDelegate.notifyUndoClicked();
        } else {
            Toast.makeText(this, R.string.message_editor_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRedoButton() {
        if(!mAdapter.isEmpty()) {
            EditorDelegate editorDelegate = mAdapter.getCurrentFragment();
            editorDelegate.notifyRedoClicked();
        } else {
            Toast.makeText(this, R.string.message_editor_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSettingsButton() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    // endregion MENU

    // region METHODS

    /**
     * Закрытие клавиатуры.
     */
    protected void closeKeyboard() throws NullPointerException {
        InputMethodManager inputManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        final View focusedView = getCurrentFocus();
        if (focusedView != null) {
            IBinder windowToken = focusedView.getWindowToken();
            int hideType = InputMethodManager.HIDE_NOT_ALWAYS;
            assert inputManager != null;
            inputManager.hideSoftInputFromWindow(windowToken, hideType);
        } else {
            Log.e(TAG, "closeKeyboard(): focusedView = null", null);
        }
    }

    // endregion METHODS

    // region POST_BASE

    @Override
    public void onResume() {
        super.onResume();
        if(mWrapper.getFullscreenMode()) { //Fullscreen
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if(mToolbarManager != null) {
                mToolbarManager.landscape(); //Обновление меню
            }
        } else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if(mToolbarManager != null) {
                mToolbarManager.portrait(); //Обновление меню
            }
        }
    }

    // endregion POST_BASE

    @SuppressLint("RtlHardcoded")
    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawers();
        } else {
            if(mWrapper.getConfirmExit()) {
                new DialogExit.Builder(this)
                        .onPositive(((dialog, which) -> finish()))
                        .show();
            } else {
                if (mPressTime + 2000 > System.currentTimeMillis()) {
                    finish();
                } else {
                    Toast.makeText(this, R.string.message_one_more, Toast.LENGTH_SHORT).show();
                    mPressTime = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return mFragmentInjector;
    }
}
