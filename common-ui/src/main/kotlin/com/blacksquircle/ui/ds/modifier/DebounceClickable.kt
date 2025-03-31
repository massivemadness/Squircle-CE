/*
 * Copyright 2025 Squircle CE contributors.
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

package com.blacksquircle.ui.ds.modifier

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role

fun Modifier.debounceClickable(
    role: Role? = null,
    enabled: Boolean = true,
    debounce: Boolean = true,
    debounceMs: Long = DefaultMs,
    onDoubleClick: (() -> Unit)? = null,
    onLongClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    onClick: (() -> Unit)? = null,
): Modifier {
    return composed(
        inspectorInfo = debugInspectorInfo {
            name = "debounceClickable"
            properties["role"] = role
            properties["enabled"] = enabled
            properties["debounce"] = debounce
            properties["debounceMs"] = debounceMs
            properties["onDoubleClick"] = onDoubleClick
            properties["onLongClickLabel"] = onLongClickLabel
            properties["onLongClick"] = onLongClick
            properties["onClickLabel"] = onClickLabel
            properties["onClick"] = onClick
        },
        factory = {
            val localIndication = LocalIndication.current
            val interactionSource = if (localIndication is IndicationNodeFactory) {
                // We can fast path here as it will be created inside clickable lazily
                null
            } else {
                // We need an interaction source to pass between the indication modifier and clickable, so
                // by creating here we avoid another composed down the line
                remember { MutableInteractionSource() }
            }
            Modifier.debounceClickable(
                interactionSource = interactionSource,
                indication = localIndication,
                role = role,
                enabled = enabled,
                debounce = debounce,
                debounceMs = debounceMs,
                onDoubleClick = onDoubleClick,
                onLongClickLabel = onLongClickLabel,
                onLongClick = onLongClick,
                onClickLabel = onClickLabel,
                onClick = onClick,
            )
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.debounceClickable(
    interactionSource: MutableInteractionSource?,
    indication: Indication?,
    role: Role? = null,
    enabled: Boolean = true,
    debounce: Boolean = true,
    debounceMs: Long = DefaultMs,
    onDoubleClick: (() -> Unit)? = null,
    onLongClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    onClick: (() -> Unit)? = null,
): Modifier {
    val onClickLambda = onClick?.let { lambda ->
        if (debounce) {
            debounceLambda(lambda, debounceMs)
        } else {
            lambda
        }
    }
    val onLongClickLambda = onLongClick?.let { lambda ->
        if (debounce) {
            debounceLambda(lambda, debounceMs)
        } else {
            lambda
        }
    }
    val onDoubleClickLambda = onDoubleClick?.let { lambda ->
        if (debounce) {
            debounceLambda(lambda, debounceMs)
        } else {
            lambda
        }
    }
    if (onClickLambda == null && onLongClickLambda == null && onDoubleClickLambda == null) {
        return this
    }
    return this.combinedClickable(
        interactionSource = interactionSource,
        indication = indication,
        role = role,
        enabled = enabled,
        onDoubleClick = onDoubleClickLambda,
        onLongClickLabel = onLongClickLabel,
        onLongClick = onLongClickLambda,
        onClickLabel = onClickLabel,
        onClick = onClickLambda ?: {},
    )
}