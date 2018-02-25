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

package com.KillerBLS.modpeide.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.KillerBLS.modpeide.EditorInstance;
import com.KillerBLS.modpeide.dialog.ReplaceAllDialog;
import com.KillerBLS.modpeide.keyboard.ExtendedKeyboard;
import com.afollestad.materialdialogs.MaterialDialog;
import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.dialog.CreationDialog;
import com.KillerBLS.modpeide.dialog.FindDialog;
import com.KillerBLS.modpeide.dialog.GotoLineDialog;
import com.KillerBLS.modpeide.document.Document;
import com.KillerBLS.modpeide.manager.DocumentsManager;
import com.KillerBLS.modpeide.document.commons.FileObject;
import com.KillerBLS.modpeide.manager.FileManager;
import com.KillerBLS.modpeide.utils.Wrapper;
import com.KillerBLS.modpeide.utils.files.Properties;
import com.KillerBLS.modpeide.utils.logger.Logger;

import es.dmoral.toasty.Toasty;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ExtendedKeyboard.OnKeyListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int FEXPLORER_CODE = 1001;
    private static final String PERMISSION_WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String PERMISSION_READ = Manifest.permission.READ_EXTERNAL_STORAGE;

    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private FileManager mFileManager;
    private DocumentsManager mDocumentsManager;
    private Wrapper mWrapper;
    private ExtendedKeyboard mExtendedKeyboard;

    protected long mPressTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWrapper = new Wrapper(this);
        setContentView(R.layout.activity_main);
        initToolbar();
        initDrawerAndStuff();
        initTabs();
        checkFileReceiver();
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private void initDrawerAndStuff() {
        final NavigationView mNavigationLeftView = findViewById(R.id.navigation_left_view);
        final NavigationView mNavigationRightView = findViewById(R.id.navigation_right_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open,
                R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if(drawerView.equals(mNavigationRightView))
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(mDrawerLayout.isDrawerOpen(GravityCompat.END)
                        && !drawerView.equals(mNavigationRightView))
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                if(mDrawerLayout.isDrawerOpen(GravityCompat.END))
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
                try {
                    closeKeyboard();
                } catch (NullPointerException e) {
                    Logger.error(TAG, e);
                }
            }
            @Override
            public void onDrawerStateChanged(int state) {
                super.onDrawerStateChanged(state);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
        mNavigationLeftView.setNavigationItemSelectedListener(this);
        mNavigationRightView.setNavigationItemSelectedListener(this);

        //добавляем действия на Switch-кнопки в Navigation View (Left)
        initNavigationSwitch(mNavigationLeftView);

        //расширенная клавиатура
        mExtendedKeyboard = findViewById(R.id.recycler_view);
        mExtendedKeyboard.setListener(this);
    }

    /**
     * P.S Если для смены состояния Switch использовать OnCheckedChangeListener, то будет вылет
     * из-за того что документ = null, то есть ещё не создан.
     * @param navigationView - наш NavigationView в котором используются Switch-кнопки.
     */
    private void initNavigationSwitch(NavigationView navigationView) {
        final Menu menu = navigationView.getMenu();
        final SwitchCompat mReadOnlySwitch = //Read Only
                (SwitchCompat) menu.findItem(R.id.drawer_read_only).getActionView();
        mReadOnlySwitch.setOnClickListener(v -> {
            mWrapper.setReadOnly(mReadOnlySwitch.isChecked());
            for(int i = 0; i < mDocumentsManager.getAdapter().getCount(); i++) { //для всех документов
                mDocumentsManager.getAdapter().getExistingFragment(i).setReadOnly(mReadOnlySwitch.isChecked());
            }
        });
        final SwitchCompat mSyntaxSwitch = //Syntax Highlighting
                (SwitchCompat) menu.findItem(R.id.drawer_syntax_highlighting).getActionView();
        mSyntaxSwitch.setOnClickListener(v -> {
            mWrapper.setSyntaxHighlight(mSyntaxSwitch.isChecked());
            for(int i = 0; i < mDocumentsManager.getAdapter().getCount(); i++) { //для всех документов
                mDocumentsManager.getAdapter().getExistingFragment(i).setSyntaxHighlight(mSyntaxSwitch.isChecked());
            }
        });

        //метод на включение при создании и открытии файла в Document.onCreateView
        mReadOnlySwitch.setChecked(mWrapper.getReadOnly());
        mSyntaxSwitch.setChecked(mWrapper.getSyntaxHighlight());
    }

    private void initTabs() {
        mFileManager = new FileManager(this);
        mDocumentsManager = new DocumentsManager(this, mWrapper, mFileManager);
        mDocumentsManager.setTabLayout(findViewById(R.id.tab_layout));
        mDocumentsManager.setViewPager(findViewById(R.id.viewpager));
        mDocumentsManager.setupViewPager(getSupportFragmentManager());
    }

    /**
     * Проверяем, был-ли файл выбран из какого-либо проводника, если да - открываем его.
     */
    private void checkFileReceiver() {
        final Intent intent = getIntent();
        final String action = intent.getAction();
        if(Intent.ACTION_VIEW.equals(action)) {
            if(mDocumentsManager.getAdapter().isFull(mWrapper)) { //Если полностью загружен файлами
                Toasty.error(this,
                        getString(R.string.too_much_files),
                        Toast.LENGTH_SHORT, true).show();
            } else {
                if(intent.getData() != null) {
                    mDocumentsManager.loadFileToTabs(new FileObject(intent.getData().getPath())); //загружаем файл
                    Logger.debug(TAG, "checkFileReceiver(): file loaded");
                }
            }
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mWrapper.getFullScreenMode()) //Fullscreen
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(mWrapper.getExtendedKeyboard()) //Extended Keyboard
            mExtendedKeyboard.setVisibility(View.VISIBLE);
        else
            mExtendedKeyboard.setVisibility(View.GONE);

        if(mDocumentsManager != null)
            mDocumentsManager.onResumeActivity();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Закрытие клавиатуры. Срабатывает без ошибок.
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
            Logger.error(TAG, "closeKeyboard(): focusedView = null", null);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();
        switch(id) {
            case R.id.drawer_new_file:
                MainActivityPermissionsDispatcher.newFileWithPermissionCheck(this);
                break;
            case R.id.drawer_open_file:
                MainActivityPermissionsDispatcher.openFileWithPermissionCheck(this);
                break;
            case R.id.drawer_open_right:
                mDrawerLayout.openDrawer(GravityCompat.END);
                break;
            case R.id.drawer_settings:
                startActivity(new Intent(this, EditorPreferences.class));
                break;
            case R.id.drawer_templates:
                startActivity(new Intent(this, TemplatesActivity.class));
                break;
            case R.id.drawer_texture_names:
                MaterialDialog browserDialog = new MaterialDialog.Builder(this)
                        .title(R.string.drawer_texture_names)
                        .positiveText(R.string.close)
                        .customView(R.layout.dialog_textures, false)
                        .build();
                View view = browserDialog.getCustomView();
                assert view != null;
                WebView mTextures = view.findViewById(R.id.web_textures);
                mTextures.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return false;
                    }
                });
                mTextures.getSettings().setJavaScriptEnabled(true);
                mTextures.getSettings().setBuiltInZoomControls(true);
                mTextures.getSettings().setDisplayZoomControls(false);
                mTextures.getSettings().setUseWideViewPort(true);
                mTextures.loadUrl(EditorInstance.APP_URL_TEXTURE_NAMES);
                browserDialog.show();
                break;
            case R.id.drawer_buy_ultimate:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(EditorInstance.APP_URL_ULTIMATE_MARKET)));
                } catch (ActivityNotFoundException e) {
                    Logger.error(TAG, e);
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(EditorInstance.APP_URL_ULTIMATE_NORMAL)));
                }
                break;
            case R.id.drawer_rate_app:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(EditorInstance.APP_URL_MARKET)));
                } catch (ActivityNotFoundException e) {
                    Logger.error(TAG, e);
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(EditorInstance.APP_URL_NORMAL)));
                }
                break;
        }
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        }
        return true;
    }

    /**
     * Вызываем диалог для создания нового файла, если проверка прошла успешно, создаем файл.
     */
    @NeedsPermission({PERMISSION_WRITE, PERMISSION_READ})
    public void newFile() {
        if(mDocumentsManager.getAdapter().isFull(mWrapper)) {
            Toasty.error(this,
                    getString(R.string.too_much_files), Toast.LENGTH_SHORT, true).show();
        } else {
            EditText path = new CreationDialog.Builder(this)
                    .title(R.string.dialog_file_new)
                    .customView(R.layout.dialog_create_new, true)
                    .onPositive((dialog, which) -> {
                        View view = dialog.getCustomView();
                        assert view != null;
                        if(CreationDialog.checkNameField(this, view)
                                && CreationDialog.checkPathField(this, view)) {
                            mDocumentsManager.newFileToTabs(
                                    CreationDialog.getValidPath() +
                                            CreationDialog.getValidName()); //создаем и открываем файл
                        }
                    }).show().getCustomView().findViewById(R.id.editPath);
            path.setText(String.format("%s/", mWrapper.getWorkingFolder())); //устанавливаем изначальный путь
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FEXPLORER_CODE && resultCode == RESULT_OK) {
            if(intent.getData() != null) {
                mDocumentsManager.loadFileToTabs(new FileObject(intent.getData().getPath()));
            } else {
                Logger.debug(TAG, "getData(): null");
                Toasty.error(this, "getData(): null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Проверяем требования перед открытием файла.
     */
    @NeedsPermission({PERMISSION_WRITE, PERMISSION_READ})
    public void openFile() {
        if(mDocumentsManager.getAdapter().isFull(mWrapper)) {
            Toasty.error(this,
                    getString(R.string.too_much_files), Toast.LENGTH_SHORT, true).show();
        } else { //Открываем Activity для выбора файла
            Intent toExplorer = new Intent(this, FileExplorerActivity.class);
            startActivityForResult(toExplorer, FEXPLORER_CODE);
        }
    }

    private void openFileProperties(FileObject file) {
        Properties.PropertiesResult result = Properties.analyze(file);

        MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(this)
                .title(R.string.menu_file_properties)
                .customView(R.layout.dialog_file_properties, true)
                .positiveText(R.string.close);
        MaterialDialog mDialog = mBuilder.build();

        View customView = mDialog.getCustomView();
        assert customView != null;

        TextView mName = customView.findViewById(R.id.props_fileName);
        mName.setText(result.mName);
        TextView mPath = customView.findViewById(R.id.props_filePath);
        mPath.setText(result.mPath);
        TextView mModified = customView.findViewById(R.id.props_lastModified);
        mModified.setText(result.mLastModified);
        TextView mSize = customView.findViewById(R.id.props_size);
        mSize.setText(result.mSize);
        TextView mWords = customView.findViewById(R.id.props_wordCount);
        mWords.setText(result.mWords);
        TextView mChars = customView.findViewById(R.id.props_charCount);
        mChars.setText(result.mCharacters);
        TextView mLines = customView.findViewById(R.id.props_lineCount);
        mLines.setText(result.mLines);

        CheckBox mRead = customView.findViewById(R.id.props_read);
        mRead.setChecked(result.mRead);
        CheckBox mWrite = customView.findViewById(R.id.props_write);
        mWrite.setChecked(result.mWrite);
        CheckBox mExecute = customView.findViewById(R.id.props_execute);
        mExecute.setChecked(result.mExecute);

        mDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        changeMenuItemsState(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Используется для скрытия item'ов при открытии {@link DrawerLayout}.
     */
    private void changeMenuItemsState(@NonNull Menu menu) {
        boolean isOpen = mDrawerLayout.isDrawerOpen(GravityCompat.START)
                || mDrawerLayout.isDrawerOpen(GravityCompat.END);
        for(int i = 0; i < menu.size(); i++){
            menu.getItem(i).setVisible(!isOpen); //скрываем все item'ы
        }
        getSupportActionBar().setDisplayShowTitleEnabled(isOpen);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final Document editorFragment = DocumentsManager.getDisplayedDocument();
        switch(id) {
            case R.id.file_new:
                //Вывоз метода newFile(), но с проверкой разрешений
                MainActivityPermissionsDispatcher.newFileWithPermissionCheck(this);
                break;
            case R.id.file_open:
                //Вывоз метода openFile(), но с проверкой разрешений
                MainActivityPermissionsDispatcher.openFileWithPermissionCheck(this);
                break;
            case R.id.file_save:
                if(editorFragment != null)
                    editorFragment.saveFile();
                else
                    editorNotFound();
                break;
            case R.id.file_properties:
                if(editorFragment != null && !editorFragment.getFilePath().equals(""))
                    openFileProperties(new FileObject(editorFragment.getFilePath()));
                else
                    editorNotFound();
                break;
            case R.id.file_close:
                if(!mDocumentsManager.getAdapter().isEmpty()) { //Если кол-во фрагментов не равно нулю
                    mDocumentsManager.removeSelectedPage();
                }
                break;
            case R.id.main_menu_undo:
                if(editorFragment != null)
                    editorFragment.undo();
                else
                    editorNotFound();
                break;
            case R.id.main_menu_redo:
                if(editorFragment != null)
                    editorFragment.redo();
                else
                    editorNotFound();
                break;
            case R.id.edit_cut:
                if(editorFragment != null)
                    editorFragment.cut();
                else
                    editorNotFound();
                break;
            case R.id.edit_copy:
                if(editorFragment != null)
                    editorFragment.copy();
                else
                    editorNotFound();
                break;
            case R.id.edit_paste:
                if(editorFragment != null)
                    editorFragment.paste();
                else
                    editorNotFound();
                break;
            case R.id.edit_selectAll:
                if(editorFragment != null)
                    editorFragment.selectAll();
                else
                    editorNotFound();
                break;
            case R.id.edit_selectLine:
                if(editorFragment != null)
                    editorFragment.selectLine();
                else
                    editorNotFound();
                break;
            case R.id.edit_deleteLine:
                if(editorFragment != null)
                    editorFragment.deleteLine();
                else
                    editorNotFound();
                break;
            case R.id.edit_duplicateLine:
                if(editorFragment != null)
                    editorFragment.duplicateLine();
                else
                    editorNotFound();
                break;
            case R.id.search_find:
                new FindDialog.Builder(this)
                        .onPositive((dialog, which) -> {
                            View view = dialog.getCustomView();
                            assert view != null;
                            String textToFind = ((EditText) view.findViewById(R.id.findField))
                                    .getText().toString();
                            boolean matchCase =
                                    ((CheckBox) view.findViewById(R.id.matchCase)).isChecked();
                            boolean regExp =
                                    ((CheckBox) view.findViewById(R.id.regExp)).isChecked();
                            boolean wordOnly =
                                    ((CheckBox) view.findViewById(R.id.wordOnly)).isChecked();
                            if(editorFragment != null)
                                editorFragment.find(textToFind, matchCase, regExp, wordOnly);
                            else
                                editorNotFound();
                        }).show();
                break;
            case R.id.search_replace_all:
                new ReplaceAllDialog.Builder(this)
                        .onPositive((dialog, which) -> {
                            View view = dialog.getCustomView();
                            assert view != null;
                            String textToReplace =
                                    ((EditText) view.findViewById(R.id.replaceTextField)).getText().toString();
                            String replaceWith =
                                    ((EditText) view.findViewById(R.id.replaceWithField)).getText().toString();
                            if(editorFragment != null)
                                editorFragment.replaceAll(textToReplace, replaceWith);
                            else
                                editorNotFound();
                        }).show();
                break;
            case R.id.search_goToLine:
                new GotoLineDialog.Builder(this)
                        .onPositive((dialog, which) -> {
                            View view = dialog.getCustomView();
                            assert view != null;
                            int lineValue = GotoLineDialog.checkValue(
                                    (EditText) dialog.findViewById(R.id.editLine));
                            if(editorFragment != null)
                                editorFragment.gotoLine(lineValue);
                            else
                                editorNotFound();
                        }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onKeyClick(View view, String text) {
        final Document editorFragment = DocumentsManager.getDisplayedDocument();
        if (editorFragment != null) {
            editorFragment.insert(text);
        }
    }

    private void editorNotFound() {
        Toasty.error(this, getString(R.string.editor_not_found), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)
                || mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawers();
        } else {
            if(mWrapper.getConfirmExit()) {
                new MaterialDialog.Builder(this)
                        .title(R.string.exit)
                        .content(R.string.dialog_exit)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .onPositive((dialog, which) -> {
                            finish(); //Выход
                        }).show();
            } else {
                if (mPressTime + 2000 > System.currentTimeMillis()) {
                    super.onBackPressed();
                } else {
                    Toasty.normal(this,
                            getString(R.string.one_more)).show();
                    mPressTime = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        mFileManager.closeDatabase();
        super.onDestroy();
    }
}
