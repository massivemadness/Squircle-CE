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

package com.KillerBLS.modpeide.activity.dagger;

import android.support.v4.app.Fragment;

import com.KillerBLS.modpeide.fragment.FragmentDocument;
import com.KillerBLS.modpeide.fragment.FragmentExplorer;
import com.KillerBLS.modpeide.fragment.dagger.FragmentExplorerComponent;
import com.KillerBLS.modpeide.fragment.dagger.FragmentDocumentComponent;

import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;

@Module(subcomponents = {FragmentExplorerComponent.class, FragmentDocumentComponent.class})
public abstract class MainActivityBuildersModule {

    @Binds
    @IntoMap
    @FragmentKey(FragmentExplorer.class)
    abstract AndroidInjector.Factory<? extends Fragment>
    bindFragmentExplorerInjectorFactory(FragmentExplorerComponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(FragmentDocument.class)
    abstract AndroidInjector.Factory<? extends Fragment>
    bindFragmentDocumentInjectorFactory(FragmentDocumentComponent.Builder builder);

}