/*
 * Copyright 2023 Squircle CE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.core.extensions

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.view.ViewGroupCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialFadeThrough

fun Fragment.postponeEnterTransition(view: View) {
    postponeEnterTransition()
    view.doOnPreDraw {
        startPostponedEnterTransition()
    }
}

fun Fragment.setFadeTransition(viewGroup: ViewGroup, @IdRes vararg excludeIds: Int) {
    enterTransition = MaterialFadeThrough().apply {
        excludeIds.forEach { excludeTarget(it, true) }
    }
    exitTransition = MaterialFadeThrough().apply {
        excludeIds.forEach { excludeTarget(it, true) }
    }
    ViewGroupCompat.setTransitionGroup(viewGroup, true)
}