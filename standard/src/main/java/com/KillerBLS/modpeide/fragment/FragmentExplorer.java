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

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.adapter.DirectoryAdapter;
import com.KillerBLS.modpeide.adapter.interfaces.SelectionTransfer;
import com.KillerBLS.modpeide.adapter.model.FileModel;
import com.KillerBLS.modpeide.dialog.files.DialogCreate;
import com.KillerBLS.modpeide.manager.FileManager;
import com.KillerBLS.modpeide.manager.interfaces.DocumentsManager;
import com.KillerBLS.modpeide.manager.storage.Filesystem;
import com.KillerBLS.modpeide.utils.Wrapper;
import com.KillerBLS.modpeide.utils.files.FileUtils;
import com.KillerBLS.modpeide.widget.LockableViewPager;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;

public class FragmentExplorer extends DaggerFragment
        implements SelectionTransfer, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = FragmentExplorer.class.getSimpleName();

    private Unbinder unbinder;
    private DocumentsManager mDocumentsManager;

    @Inject
    Wrapper mWrapper;
    @Inject
    Filesystem mFilesystem;
    //@Inject
    DirectoryAdapter mAdapter;

    @BindView(R.id.dir_layout)
    TabLayout mDirLayout;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeLayout;
    @BindView(R.id.floating_button)
    FloatingActionButton mCreateButton;
    @BindView(R.id.dir_view_pager)
    LockableViewPager mViewPager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mDocumentsManager = (DocumentsManager) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new DirectoryAdapter(getChildFragmentManager());
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
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setSwipeLocked(mWrapper.getDisableSwipeGesture());
        mViewPager.setAdapter(mAdapter);
        mDirLayout.setupWithViewPager(mViewPager);
        mSwipeLayout.setOnRefreshListener(this);

        mCreateButton.setOnClickListener((view1) ->
                new DialogCreate.Builder(getContext(), this)
                        .setCurrentPath(mAdapter.getCurrentFragment().getTag())
                        .show());

        //https://stackoverflow.com/a/29946734
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) { }
            @Override
            public void onPageSelected(int position) { }
            @Override
            public void onPageScrollStateChanged(int state) {
                mSwipeLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });
        if(mAdapter.isEmpty()) {
            onClick(mFilesystem.getDefaultLocation());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_explorer, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return onQueryTextChange(query);
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mAdapter.getCurrentFragment().filter(query);
                return true;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem showHiddenItem = menu.findItem(R.id.action_show_hidden);
        showHiddenItem.setChecked(mWrapper.getFilterHidden());

        MenuItem sortByName = menu.findItem(R.id.sort_by_name);
        MenuItem sortBySize = menu.findItem(R.id.sort_by_size);
        MenuItem sortByDate = menu.findItem(R.id.sort_by_date);
        switch(mWrapper.getSortMode()) {
            case 0: //FileSorter.SORT_BY_NAME
                sortByName.setChecked(true);
                break;
            case 1: //FileSorter.SORT_BY_SIZE
                sortBySize.setChecked(true);
                break;
            case 2: //FileSorter.SORT_BY_DATE
                sortByDate.setChecked(true);
                break;
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_hidden:
                mWrapper.setFilterHidden(!item.isChecked());
                break;
            case R.id.sort_by_name:
                mWrapper.setSortMode("0");
                break;
            case R.id.sort_by_size:
                mWrapper.setSortMode("1");
                break;
            case R.id.sort_by_date:
                mWrapper.setSortMode("2");
                break;
        }
        onRefresh();
        return false;
    }

    // region PANEL

    @Override
    public void onRefresh() {
        mAdapter.getCurrentFragment().refreshList();
        mSwipeLayout.setRefreshing(false);
    }

    @OnClick(R.id.action_home)
    public void onHome() {
        for(int pos = mAdapter.getCount() - 1; pos > 0; pos--) { //leave the first fragment
            mAdapter.remove(pos);
        }
        invalidateTabs();
    }

    private void invalidateTabs() {
        for(int i = 0; i < mAdapter.getCount(); i++) {
            TabLayout.Tab tab = mDirLayout.getTabAt(i);
            if(tab != null) {
                tab.setCustomView(R.layout.item_directory);
            }
        }
    }

    // endregion PANEL

    // region CLICK

    @Override
    public void onClick(FileModel fileModel) {
        if(fileModel.isFolder()) {
            int currPos = mViewPager.getCurrentItem();
            int nextPos = currPos + 1;
            int pathPos = mAdapter.contains(fileModel.getPath());

            if(pathPos == nextPos) {
                mViewPager.setCurrentItem(nextPos, true);
            } else {
                if(pathPos != currPos) {
                    for(int pos = mAdapter.getCount() - 1; pos > currPos; pos--) {
                        mAdapter.remove(pos);
                    }
                    mAdapter.addToStack(fileModel.getPath());
                    mViewPager.setCurrentItem(mAdapter.getCount() - 1, true);
                }
                invalidateTabs();
            }
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
        /*final View view = mRecyclerView.getLayoutManager().findViewByPosition(position);
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
        });*/
    }

    // endregion CLICK
}