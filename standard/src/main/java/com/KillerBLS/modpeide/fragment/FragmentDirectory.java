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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.adapter.FileAdapter;
import com.KillerBLS.modpeide.adapter.model.FileModel;
import com.KillerBLS.modpeide.manager.storage.Filesystem;
import com.KillerBLS.modpeide.utils.Wrapper;
import com.KillerBLS.modpeide.utils.files.FileSorter;
import com.KillerBLS.modpeide.widget.RecyclerViewStub;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.DaggerFragment;

public class FragmentDirectory extends DaggerFragment {

    private Unbinder unbinder;
    private String mPath;

    @BindView(R.id.recycler_view)
    RecyclerViewStub mRecyclerView;
    @BindView(R.id.stub_no_result)
    ViewStub mViewStub;

    @Inject
    Wrapper mWrapper;
    @Inject
    Filesystem mFilesystem;
    @Inject
    FileAdapter mAdapter;

    /**
     * Создание нового фрагмента.
     * @param path - путь к папке, в которой будут отображаться файлы.
     * @return - возвращает фрагмент с заполненными данными.
     */
    public static FragmentDirectory newInstance(String path) {
        FragmentDirectory fragmentDirectory = new FragmentDirectory();
        Bundle bundle = new Bundle();
        bundle.putString("PATH", path);
        fragmentDirectory.setArguments(bundle);
        return fragmentDirectory;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(getParentFragment()); //inject as a FragmentExplorer.class
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPath = getArguments().getString("PATH");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_directory, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setItemViewCacheSize(30);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setViewStub(mViewStub);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    /**
     * Обновление списка файлов.
     */
    public void refreshList() {
        mAdapter.setData(mFilesystem.makeList(new FileModel(mPath),
                FileSorter.getComparator(mWrapper.getSortMode()), mWrapper.getFilterHidden()));
    }

    /**
     * Поиск по списку.
     * @param query - поисковой запрос.
     */
    public void filter(CharSequence query) {
        mAdapter.getFilter().filter(query);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}