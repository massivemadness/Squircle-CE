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

package com.blacksquircle.ui.ds.dropdown

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.popupmenu.PopupMenu
import com.blacksquircle.ui.ds.popupmenu.PopupMenuItem

@Composable
fun Dropdown(
    entries: Array<String>,
    entryValues: Array<String>,
    currentValue: String,
    onValueSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable { expanded = !expanded }
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = entries[entryValues.indexOf(currentValue)],
            color = SquircleTheme.colors.colorTextAndIconPrimary,
            style = SquircleTheme.typography.text16Regular,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.weight(1f, fill = false)
        )
        Spacer(Modifier.width(16.dp))
        Icon(
            painter = if (expanded) {
                painterResource(R.drawable.ic_menu_up)
            } else {
                painterResource(R.drawable.ic_menu_down)
            },
            contentDescription = null,
            tint = SquircleTheme.colors.colorTextAndIconPrimary,
        )

        PopupMenu(
            expanded = expanded,
            onDismiss = { expanded = false },
            verticalOffset = (-56).dp,
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
}

@Preview
@Composable
private fun DropdownPreview() {
    SquircleTheme {
        Dropdown(
            currentValue = "apple",
            entries = arrayOf("Apple", "Banana", "Orange"),
            entryValues = arrayOf("apple", "banana", "orange"),
            onValueSelected = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}