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

package com.lightteam.modpeide.presentation.main.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.lightteam.modpeide.domain.model.FileModel
import com.lightteam.modpeide.presentation.main.fragments.FragmentDirectory

class DirectoryAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val data: MutableList<FragmentDirectory> = mutableListOf()

    fun add(path: FileModel) {

        data.add(
            FragmentDirectory().apply {
                this.path = path
            }
        )
        notifyDataSetChanged()
    }

    private fun internalRemove(position: Int) {
        data.removeAt(position)
        //notifyDataSetChanged()
    }

    override fun getPageTitle(position: Int): CharSequence = data[position].path.name
    override fun getItem(position: Int): Fragment = data[position]
    override fun getCount(): Int = data.size
}