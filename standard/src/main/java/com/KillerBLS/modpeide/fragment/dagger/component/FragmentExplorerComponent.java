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

package com.KillerBLS.modpeide.fragment.dagger.component;

import com.KillerBLS.modpeide.fragment.FragmentExplorer;
import com.KillerBLS.modpeide.fragment.dagger.module.FragmentExplorerBuildersModule;
import com.KillerBLS.modpeide.fragment.dagger.module.FragmentExplorerModule;
import com.KillerBLS.modpeide.utils.scopes.PerFragment;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@PerFragment
@Subcomponent(modules = {FragmentExplorerModule.class, FragmentExplorerBuildersModule.class})
public interface FragmentExplorerComponent extends AndroidInjector<FragmentExplorer> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<FragmentExplorer> {

    }
}