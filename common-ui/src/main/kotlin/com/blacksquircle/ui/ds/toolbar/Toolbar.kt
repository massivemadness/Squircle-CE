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

package com.blacksquircle.ui.ds.toolbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.size2XS

@Composable
fun Toolbar(
    title: String,
    backIcon: Int? = null,
    onBackClicked: () -> Unit = {},
    menuItems: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            if (backIcon != null) {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        painter = painterResource(backIcon),
                        contentDescription = null
                    )
                }
            }
        },
        actions = menuItems,
        modifier = Modifier.shadow(size2XS)
    )
}

@Preview
@Composable
private fun ToolbarPreview() {
    SquircleTheme {
        Toolbar(
            title = "Title",
            backIcon = R.drawable.ic_back
        )
    }
}