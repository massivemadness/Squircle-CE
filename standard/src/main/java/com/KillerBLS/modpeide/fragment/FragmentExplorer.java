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

package com.KillerBLS.modpeide.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.Toast;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.adapter.FileAdapter;
import com.KillerBLS.modpeide.adapter.interfaces.SelectionTransfer;
import com.KillerBLS.modpeide.adapter.model.FileModel;
import com.KillerBLS.modpeide.dialog.files.DialogCreate;
import com.KillerBLS.modpeide.dialog.files.DialogDelete;
import com.KillerBLS.modpeide.dialog.files.DialogProperties;
import com.KillerBLS.modpeide.dialog.files.DialogRename;
import com.KillerBLS.modpeide.dialog.files.DialogSortMode;
import com.KillerBLS.modpeide.manager.FileManager;
import com.KillerBLS.modpeide.utils.Wrapper;
import com.KillerBLS.modpeide.manager.interfaces.DocumentsManager;
import com.KillerBLS.modpeide.manager.storage.Filesystem;
import com.KillerBLS.modpeide.utils.files.FileSorter;
import com.KillerBLS.modpeide.utils.files.FileUtils;
import com.KillerBLS.modpeide.utils.commons.MenuHelper;
import com.KillerBLS.modpeide.widget.RecyclerViewStub;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;

public class FragmentExplorer extends Fragment implements SelectionTransfer {

    private static final String TAG = FragmentExplorer.class.getSimpleName();

    private Unbinder unbinder;
    private DocumentsManager mDocumentsManager;
    private FileModel mCurrentPath; //Текущий путь

    @Inject
    Wrapper mWrapper;
    @Inject
    FileAdapter mAdapter;
    @Inject
    Filesystem mFilesystem;

    @BindView(R.id.field_search)
    SearchView mSearchView;
    @BindView(R.id.recycler_view)
    RecyclerViewStub mRecyclerView;
    @BindView(R.id.stub_no_result)
    ViewStub mViewStub;
    @BindView(R.id.action_filter)
    ImageView mImageFilter;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        if(context instanceof Activity) {
            mDocumentsManager = (DocumentsManager) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explorer, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentPath = mFilesystem.getDefaultLocation();

        mRecyclerView.setItemViewCacheSize(30);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setViewStub(mViewStub);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return onQueryTextChange(query);
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mAdapter.getFilter().filter(query);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFilterStatus(); //update filter
        onRefresh();
    }

    // region PANEL

    @OnClick(R.id.action_add)
    public void onAdd(View view) {
        MenuHelper.forceShow(getContext(), view, R.menu.menu_actions_create, item -> {
            switch (item.getItemId()) {
                case R.id.action_create_file:
                    new DialogCreate.Builder(getContext(), this)
                            .setCurrentPath(new File(mCurrentPath.getPath()))
                            .setIsFolder(false)
                            .show();
                    break;
                case R.id.action_create_folder:
                    new DialogCreate.Builder(getContext(), this)
                            .setCurrentPath(new File(mCurrentPath.getPath()))
                            .setIsFolder(true)
                            .show();
                    break;
            }
            return false;
        });
    }

    @OnClick(R.id.action_refresh)
    public void onRefresh() {
        onClick(mCurrentPath);
    }

    @OnClick(R.id.action_home)
    public void onHome() {
        onClick(mFilesystem.getDefaultLocation());
    }

    @OnClick(R.id.action_sort)
    public void onSort() {
        new DialogSortMode.Builder(getContext())
                .setSingleChoiceItems(R.array.sort_mode, mWrapper.getSortMode(), (dialog, which) -> {
                    mWrapper.setSortMode(Integer.toString(which));
                    onRefresh();
                    dialog.dismiss();
                }).show();
    }

    @OnClick(R.id.action_filter)
    public void onFilter() {
        if(mWrapper.getFilterHidden()) {
            mWrapper.setFilterHidden(false);
        } else {
            mWrapper.setFilterHidden(true);
        }
        onResume();
    }

    /**
     * Обновление статуса фильтра файлов.
     */
    private void updateFilterStatus() {
        if(mWrapper.getFilterHidden()) {
            mImageFilter.setImageResource(R.drawable.ic_filter_on);
        } else {
            mImageFilter.setImageResource(R.drawable.ic_filter_off);
        }
    }

    // endregion PANEL

    // region CLICK

    @Override
    public void onClick(FileModel fileModel) {
        if(fileModel.isFolder()) {
            mAdapter.setData(
                    mFilesystem.makeList(fileModel,
                            FileSorter.getComparator(mWrapper.getSortMode()),
                            mWrapper.getFilterHidden()));
            mCurrentPath = fileModel;
        } else {
            if(FileUtils.isExtension(fileModel.getName(), mFilesystem.getUnopenableExtensions())) {
                try { //Открытие файла через соответствующую программу
                    Uri uri = FileProvider.getUriForFile(getContext(),
                            getContext().getPackageName() + ".provider",
                            new File(fileModel.getPath()));

                    String mime = getContext().getContentResolver().getType(uri);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, mime);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, e.getMessage(), e);
                    Toast.makeText(getContext(), R.string.message_cannot_be_opened, Toast.LENGTH_SHORT).show();
                }
            } else {
                if(mDocumentsManager != null) {
                    mDocumentsManager.addDocument(FileManager.convert(fileModel));
                }
            }
        }
    }

    @Override
    public void onLongClick(FileModel fileModel, int position) {
        if(!fileModel.isUp()) {
            final View view = mRecyclerView.getLayoutManager().findViewByPosition(position);
            MenuHelper.forceShow(getContext(), view, R.menu.menu_actions_file, item -> {
                switch (item.getItemId()) {
                    case R.id.action_rename:
                        new DialogRename.Builder(getContext(), this, new File(fileModel.getPath())).show();
                        break;
                    case R.id.action_delete:
                        new DialogDelete.Builder(getContext(), this, new File(fileModel.getPath())).show();
                        break;
                    case R.id.action_properties:
                        new DialogProperties.Builder(getContext()).withFile(new File(fileModel.getPath())).show();
                        break;
                    case R.id.action_copy_path:
                        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("PATH", fileModel.getPath());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getContext(), R.string.message_done, Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            });
        } else {
            onClick(fileModel);
        }
    }

    // endregion CLICK

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
