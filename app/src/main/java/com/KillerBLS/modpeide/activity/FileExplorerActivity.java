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

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.KillerBLS.modpeide.dialog.RenameDialog;
import com.KillerBLS.modpeide.utils.Converter;
import com.KillerBLS.modpeide.utils.files.SortMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.adapter.files.FileListAdapter;
import com.KillerBLS.modpeide.adapter.files.FileDetail;
import com.KillerBLS.modpeide.dialog.CreationDialog;
import com.KillerBLS.modpeide.document.commons.FileObject;
import com.KillerBLS.modpeide.utils.Wrapper;
import com.KillerBLS.modpeide.utils.logger.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import es.dmoral.toasty.Toasty;

/**
 * Thanks Vlad Mihalachi
 */
public class FileExplorerActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = FileExplorerActivity.class.getSimpleName();

    private SwipeRefreshLayout mSwipeLayout;
    private ListView listView;
    private String currentFolder;

    private Wrapper mWrapper;
    private int mSortMode;
    private String defaultFolder = Environment.getExternalStorageDirectory().getAbsolutePath();

    private MenuItem mSearchViewMenuItem;
    private SearchView mSearchView;
    private Filter filter;

    private TextView mPathTitle;
    private FloatingActionMenu mFloatingMenu;
    private boolean showHiddenFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);
        initToolbar();
        initContent();
        initFabActions();
    }

    private void initToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar_without_tabs);
        setSupportActionBar(mToolbar);
        setTitle(R.string.fexplorer_system_local);
    }

    private void initContent() {
        mWrapper = new Wrapper(this);
        mSortMode = Converter.toSortMode(mWrapper.getSortMode());
        mPathTitle = findViewById(R.id.pathField);
        String workingFolder = mWrapper.getWorkingFolder();
        showHiddenFiles = mWrapper.getShowHiddenFiles();
        listView = findViewById(android.R.id.list);
        listView.setOnItemClickListener(this); //обычное нажатие
        listView.setOnItemLongClickListener(this); //долгое нажатие
        listView.setTextFilterEnabled(true);

        if(mWrapper.getFullScreenMode()) { //Fullscreen
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        FileObject file = new FileObject(workingFolder);
        if(!file.exists()) {
            workingFolder = defaultFolder; //save to prefs
            mWrapper.setWorkingFolder(workingFolder);
            file = new FileObject(workingFolder);
        }
        new UpdateList().execute(file.getAbsolutePath()); //Обновляем список при старте активити
    }

    private void initFabActions() {
        mSwipeLayout = findViewById(R.id.listRefresh);
        mSwipeLayout.setOnRefreshListener(this);
        mFloatingMenu = findViewById(R.id.fabMenu_menu);
        if(!mWrapper.getCreatingFilesAndFolders())
            mFloatingMenu.setVisibility(View.GONE);
        //Кнопки с действиями
        FloatingActionButton mCreateFile = findViewById(R.id.fabMenu_new_file); //File Button
        mCreateFile.setOnClickListener(view -> {
            mFloatingMenu.close(true);
            new CreationDialog.Builder(this)
                    .title(R.string.dialog_file_new)
                    .customView(R.layout.dialog_explorer_new_file, true)
                    .onPositive((dialog, which) -> {
                        View dview = dialog.getCustomView();
                        assert dview != null;
                        if(CreationDialog.checkNameField(this, dview)) {
                            FileObject createdFile = new FileObject(currentFolder + "/" +
                                    CreationDialog.getValidName());
                            try {
                                createdFile.createNewFile();
                            } catch (IOException e) {
                                Logger.error(TAG, Logger.ERROR_OCCURRED, e);
                                Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            finishWithResult(createdFile);
                        }
                    }).show();
        });
        FloatingActionButton mCreateFolder = findViewById(R.id.fabMenu_new_folder); //Folder Button
        mCreateFolder.setOnClickListener(view -> {
            mFloatingMenu.close(true);
            new CreationDialog.Builder(this)
                    .title(R.string.dialog_folder_new)
                    .customView(R.layout.dialog_explorer_new_folder, true)
                    .onPositive((dialog, which) -> {
                        View dview = dialog.getCustomView();
                        assert dview != null;
                        if(CreationDialog.checkNameField(this, dview)) {
                            FileObject createdFolder =
                                    new FileObject(currentFolder + "/" +
                                            CreationDialog.getValidName());
                            createdFolder.mkdir();
                            new UpdateList().execute(currentFolder);
                        }
                    }).show();
        });
    }

    /**
     * Отображение текущего пути.
     * @param pathTitle - путь для отображения.
     */
    private void setPathTitle(String pathTitle) {
        mPathTitle.setText(pathTitle);
    }

    /**
     * Метод для упрощенной передачи выбранного файла в главное активити.
     * @param file - выбранный файл в данном активити.
     */
    private void finishWithResult(FileObject file) {
        Uri fileUri = null;
        if(file != null) {
            fileUri = Uri.fromFile(file);
        }
        if(getCallingActivity() != null) { //Если вызвано через MainActivity
            if(fileUri != null) {
                setResult(RESULT_OK, new Intent().setData(fileUri));
                finish();
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        } else { //Если вызвано через Launcher Shortcut (Android 7)
            if(fileUri != null) {
                startActivity(
                        new Intent(this, MainActivity.class)
                                .setData(fileUri)
                                .setAction(Intent.ACTION_VIEW));
                finish();
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fexplorer_menu, menu);
        mSearchViewMenuItem = menu.findItem(R.id.fexplorer_search);
        mSearchView = (SearchView) mSearchViewMenuItem.getActionView();
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.fexplorer_set_w_folder:
                mWrapper.setWorkingFolder(currentFolder);
                break;
            case R.id.fexplorer_goto_w_folder:
                new UpdateList().execute(mWrapper.getWorkingFolder());
                break;
            case R.id.fexplorer_goto_d_folder:
                new UpdateList().execute(defaultFolder);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mFloatingMenu.isOpened()) {
            mFloatingMenu.close(true);
        } else {
            if (currentFolder.isEmpty() || currentFolder.equals(defaultFolder)) {
                finish();
            } else {
                FileObject file = new FileObject(currentFolder);
                String parentFolder = file.getParent(); //Получаем родительскую папку,
                new UpdateList().execute(parentFolder); //и перемещаемся в неё
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (filter == null)
            return true;
        if (TextUtils.isEmpty(newText)) {
            filter.filter(null);
        } else {
            filter.filter(newText);
        }
        return true;
    }

    /**
     * Обработчик нажатий listView, переход в папки и выбор файла (finishWithResult).
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mFloatingMenu.isOpened())
            mFloatingMenu.close(true);
        final String name = ((TextView) view.findViewById(android.R.id.text1)).getText().toString();
        if (name.equals("..")) {
            if (currentFolder.equals(defaultFolder)) {
                new UpdateList().execute(mWrapper.getWorkingFolder());
            } else {
                File tempFile = new File(currentFolder);
                if (tempFile.isFile()) {
                    tempFile = tempFile.getParentFile()
                            .getParentFile();
                } else {
                    tempFile = tempFile.getParentFile();
                }
                new UpdateList().execute(tempFile.getAbsolutePath());
            }
            return;
        }
        final FileObject selectedFile = new FileObject(currentFolder, name);
        if (selectedFile.isFile()) {
            finishWithResult(selectedFile); //Если выбранный item - файл, завершаем активити и передаём результат
        } else if (selectedFile.isDirectory()) {
            new UpdateList().execute(selectedFile.getAbsolutePath()); //Если это папка - переходим в неё и обновляем список
        }
    }

    /**
     * Обработчик долгих нажатии по listView.
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(mFloatingMenu.isOpened())
            mFloatingMenu.close(true);
        String fileName = ((TextView) view.findViewById(android.R.id.text1)).getText().toString();
        String filePath = currentFolder + "/" + fileName;
        new MaterialDialog.Builder(this) //Диалог при долгом тапе по файлу
                .items(R.array.fexplorer_actions)
                .itemsCallback((dialog, view1, which, text) -> {
                    FileObject selectedFile = new FileObject(filePath);
                    switch (which) {
                        case 0: //Rename
                            EditText editName = new RenameDialog.Builder(this)
                                    .onPositive(((dialog1, which1) -> {
                                        View view2 = dialog1.getCustomView();
                                        assert view2 != null;
                                        if(RenameDialog.checkNameField(this, view2)) {
                                            selectedFile.renameTo(
                                                    new File(selectedFile.getParent()
                                                            + "/" + RenameDialog.getValidName()));
                                            new UpdateList().execute(currentFolder);
                                        }
                                    })).show().getCustomView().findViewById(R.id.editName);
                            editName.setText(selectedFile.getName());
                            break;
                        case 1: //Delete
                            new MaterialDialog.Builder(this)
                                    .title(R.string.fexplorer_delete)
                                    .content(R.string.dialog_delete_confirm)
                                    .positiveText(R.string.yes)
                                    .negativeText(R.string.no)
                                    .onPositive((dialog2, which2) -> {
                                        selectedFile.deleteRecursive();
                                        new UpdateList().execute(currentFolder);
                                    }).show();
                            break;
                    }
                })
                .show();
        return true;
    }

    @Override
    public void onRefresh() {
        new UpdateList().execute(currentFolder);
        new Handler().postDelayed(() -> mSwipeLayout.setRefreshing(false), 500);
    }

    /**
     * Обновление содержимого в текущей папке.
     */
    private class UpdateList extends AsyncTask<String, Void, LinkedList<FileDetail>> {

        String exceptionMessage; //Сообщение об ошибке, если что-то пойдет не так

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mSearchView != null) {
                mSearchView.setIconified(true);
                mSearchViewMenuItem.collapseActionView();
                mSearchView.setQuery("", false);
            }
        }

        @Override
        protected LinkedList<FileDetail> doInBackground(final String... params) {
            try {
                final String path = params[0];
                if (TextUtils.isEmpty(path)) {
                    return null;
                }

                FileObject tempFolder = new FileObject(path);
                if (tempFolder.isFile()) {
                    tempFolder = (FileObject) tempFolder.getParentFile();
                }

                //Неотображаемые форматы
                String[] unopenableExtensions = { "apk", "mp3", "mp4", "wav", "pdf", "avi", "wmv",
                        "m4a", "png", "jpg", "jpeg", "zip", "7z", "rar", "gif" };

                final LinkedList<FileDetail> fileDetails = new LinkedList<>();
                final LinkedList<FileDetail> folderDetails = new LinkedList<>();

                currentFolder = tempFolder.getAbsolutePath();

                FileObject[] files = tempFolder.listFiles();

                //Сортировка файлов
                if(mSortMode == SortMode.SORT_BY_NAME) {
                    Arrays.sort(files, getFileNameComparator());
                } else if(mSortMode == SortMode.SORT_BY_SIZE) {
                    Arrays.sort(files, getFileSizeComparator());
                } else if(mSortMode == SortMode.SORT_BY_DATE) {
                    Arrays.sort(files, getFileDateComparator());
                }

                for (final FileObject f : files) {
                    if (f.isDirectory()) { //Если это папка
                        if(f.isHidden()) { //если папка скрыта
                            if(showHiddenFiles) { //и отображение скрытых файлов вклчючено
                                folderDetails.add(new FileDetail(f.getName(), //добавляем
                                        getString(R.string.fexplorer_folder),
                                        f.getLastModified(), true));
                            } //иначе пропускаем файл
                        } else { //но если папка не скрыта, то сразу добавляем
                            folderDetails.add(new FileDetail(f.getName(),
                                    getString(R.string.fexplorer_folder),
                                    f.getLastModified(), true));
                        }
                    } else if (f.isFile() //Если файл, то информацию о нём
                            && !FilenameUtils.isExtension(f.getName().toLowerCase(), unopenableExtensions)
                            && FileUtils.sizeOf(f) <= 20_000 * FileUtils.ONE_KB) { //20_000 - Max file size
                        if(f.isHidden()) { //та же схема со скрытыми файлами что чуть выше
                            if(showHiddenFiles) {
                                fileDetails.add(new FileDetail(f.getName(),
                                        f.getReadableSize(), f.getLastModified(), false));
                            }
                        } else {
                            fileDetails.add(new FileDetail(f.getName(),
                                    f.getReadableSize(), f.getLastModified(), false));
                        }
                    }
                }
                folderDetails.addAll(fileDetails);
                return folderDetails;
            } catch (Exception e) {
                exceptionMessage = e.getMessage();
                Logger.error(TAG, e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final LinkedList<FileDetail> names) {
            if (names != null) {
                FileListAdapter mAdapter = new FileListAdapter(getBaseContext(), names);
                listView.setAdapter(mAdapter);
                filter = mAdapter.getFilter();
            }
            if (exceptionMessage != null) { //Если произошла ошибка, выводим её на экран
                Toasty.normal(FileExplorerActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
            }
            invalidateOptionsMenu();
            setPathTitle(currentFolder);
            super.onPostExecute(names);
        }
    }

    //region COMPARATORS

    /**
     * Сортировка по имени.
     */
    private Comparator<? super FileObject> getFileNameComparator() {
        return (Comparator<FileObject>) (o1, o2) -> o1.getName().compareTo(o2.getName());
    }

    /**
     * Сортировка по размеру. От большого к малому.
     */
    private Comparator<? super FileObject> getFileSizeComparator() {
        return (Comparator<FileObject>) (o1, o2) -> {
            if (o1.length() == o2.length()) {
                return 0;
            }
            if (o1.length() > o2.length()) {
                return -1;
            } else {
                return 1;
            }
        };
    }

    /**
     * Сортировка по дате. От новых к старым.
     */
    private Comparator<? super FileObject> getFileDateComparator() {
        return (Comparator<FileObject>) (o1, o2) -> {
            if (o1.lastModified() == o2.lastModified()) {
                return 0;
            }
            if (o1.lastModified() > o2.lastModified()) {
                return -1;
            } else {
                return 1;
            }
        };
    }

    //endregion COMPARATORS

}