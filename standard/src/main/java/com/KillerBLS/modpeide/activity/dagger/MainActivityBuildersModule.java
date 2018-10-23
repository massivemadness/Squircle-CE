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