/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.plugin.pinchzoom

import com.blacksquircle.ui.plugin.base.PluginContainer
import com.blacksquircle.ui.plugin.base.PluginSupplier

var PluginContainer.minTextSize: Float
    get() = findPlugin<PinchZoomPlugin>(PinchZoomPlugin.PLUGIN_ID)
        ?.minTextSize ?: PinchZoomPlugin.DEFAULT_MIN_TEXT_SIZE
    set(value) {
        findPlugin<PinchZoomPlugin>(PinchZoomPlugin.PLUGIN_ID)?.minTextSize = value
    }

var PluginContainer.maxTextSize: Float
    get() = findPlugin<PinchZoomPlugin>(PinchZoomPlugin.PLUGIN_ID)
        ?.maxTextSize ?: PinchZoomPlugin.DEFAULT_MAX_TEXT_SIZE
    set(value) {
        findPlugin<PinchZoomPlugin>(PinchZoomPlugin.PLUGIN_ID)?.maxTextSize = value
    }

fun PluginSupplier.pinchZoom(block: PinchZoomPlugin.() -> Unit = {}) {
    plugin(PinchZoomPlugin(), block)
}