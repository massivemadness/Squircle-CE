package com.lightteam.modpeide.internal.di.explorer

import com.lightteam.modpeide.ui.explorer.fragments.DirectoryFragment
import com.lightteam.modpeide.ui.explorer.fragments.PermissionsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ExplorerFragmentsProvider {

    @ContributesAndroidInjector
    abstract fun bindPermissionsFragment(): PermissionsFragment

    @ContributesAndroidInjector
    abstract fun bindDirectoryFragment(): DirectoryFragment
}