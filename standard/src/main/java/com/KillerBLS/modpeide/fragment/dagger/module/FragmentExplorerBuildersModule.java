package com.KillerBLS.modpeide.fragment.dagger.module;

import android.support.v4.app.Fragment;

import com.KillerBLS.modpeide.fragment.FragmentDirectory;
import com.KillerBLS.modpeide.fragment.dagger.component.FragmentDirectoryComponent;

import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;

@Module(subcomponents = {FragmentDirectoryComponent.class})
public abstract class FragmentExplorerBuildersModule {

    @Binds
    @IntoMap
    @FragmentKey(FragmentDirectory.class)
    abstract AndroidInjector.Factory<? extends Fragment>
    bindFragmentDirectoryInjectorFactory(FragmentDirectoryComponent.Builder builder);
}