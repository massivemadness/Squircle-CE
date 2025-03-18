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

package com.blacksquircle.ui.ds.dropdown

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.modifier.debounceClickable
import com.blacksquircle.ui.ds.popupmenu.PopupMenu
import com.blacksquircle.ui.ds.popupmenu.PopupMenuItem

@Composable
fun Dropdown(
    entries: Array<String>,
    entryValues: Array<String>,
    currentValue: String,
    onValueSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    dropdownStyle: DropdownStyle = DropdownStyleDefaults.Default,
    dropdownSize: DropdownSize = DropdownSizeDefaults.M,
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }
    Layout(
        content = {
            Text(
                text = entries[entryValues.indexOf(currentValue)],
                color = dropdownStyle.textColor,
                style = dropdownStyle.textStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.layoutId(DropdownSlot.Text)
            )
            Icon(
                painter = if (expanded) {
                    painterResource(R.drawable.ic_menu_up)
                } else {
                    painterResource(R.drawable.ic_menu_down)
                },
                contentDescription = null,
                tint = dropdownStyle.iconColor,
                modifier = Modifier.layoutId(DropdownSlot.Icon)
            )
            Box(modifier = Modifier.layoutId(DropdownSlot.Popup)) {
                PopupMenu(
                    expanded = expanded,
                    onDismiss = { expanded = false },
                    verticalOffset = -dropdownSize.height,
                ) {
                    entries.forEachIndexed { index, entry ->
                        PopupMenuItem(
                            title = entry,
                            onClick = {
                                onValueSelected(entryValues[index])
                                expanded = false
                            },
                        )
                    }
                }
            }
        },
        modifier = modifier
            .height(dropdownSize.height)
            .clip(RoundedCornerShape(dropdownSize.cornerRadius))
            .debounceClickable { expanded = !expanded },
    ) { measurables, constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight
        val startPadding = dropdownSize.startPadding.roundToPx()
        val endPadding = dropdownSize.endPadding.roundToPx()

        // Detect measurables
        val iconMeasurable = measurables.fastFirst { it.layoutId == DropdownSlot.Icon }
        val textMeasurable = measurables.fastFirst { it.layoutId == DropdownSlot.Text }
        val popupMeasurable = measurables.fastFirstOrNull { it.layoutId == DropdownSlot.Popup }

        // Measure icon
        val iconConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val iconPlaceable = iconMeasurable.measure(iconConstraints)
        val iconWidth = iconPlaceable.measuredWidth
        val iconHeight = iconPlaceable.measuredHeight

        // Measure text
        val textSpacer = dropdownSize.textSpacer.roundToPx()
        val textConstraints = constraints.copy(
            minWidth = 0,
            minHeight = 0,
            maxWidth = layoutWidth - textSpacer - iconWidth,
        )
        val textPlaceable = textMeasurable.measure(textConstraints)
        val textWidth = textPlaceable.measuredWidth
        val textHeight = textPlaceable.measuredHeight

        // Final measure
        val calculatedWidth = if (constraints.hasFixedWidth) {
            layoutWidth
        } else {
            startPadding + textWidth + textSpacer + iconWidth + endPadding
        }

        // Measure popup
        val popupConstraints = constraints.copy(
            minWidth = calculatedWidth,
            minHeight = layoutHeight,
            maxWidth = calculatedWidth,
            maxHeight = layoutHeight,
        )
        val popupPlaceable = popupMeasurable?.measure(popupConstraints)

        // Layout children
        layout(calculatedWidth, layoutHeight) {
            // Place text
            textPlaceable.placeRelative(
                x = startPadding,
                y = layoutHeight / 2 - textHeight / 2
            )

            // Place icon
            iconPlaceable.placeRelative(
                x = calculatedWidth - iconWidth - endPadding,
                y = layoutHeight / 2 - iconHeight / 2
            )

            // Place popup
            popupPlaceable?.placeRelative(0, 0)
        }
    }
}

private enum class DropdownSlot {
    Text,
    Icon,
    Popup,
}

@PreviewLightDark
@Composable
private fun DropdownPreview() {
    PreviewBackground {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Dropdown(
                currentValue = "apple",
                entries = arrayOf("Apple", "Banana", "Orange"),
                entryValues = arrayOf("apple", "banana", "orange"),
                onValueSelected = {},
            )
        }
    }
}