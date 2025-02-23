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

package com.blacksquircle.ui.ds.tabs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.SquircleTheme

@Composable
fun TabNavigation(
    tabs: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selectedIndex: Int = -1,
) {
    if (selectedIndex > -1) {
        ScrollableTabRow(
            backgroundColor = SquircleTheme.colors.colorBackgroundPrimary,
            selectedTabIndex = selectedIndex,
            indicator = { tabPositions ->
                if (tabPositions.isNotEmpty()) {
                    TabIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedIndex]))
                }
            },
            edgePadding = 0.dp,
            divider = {},
            tabs = tabs,
            modifier = modifier
                .fillMaxWidth()
                .height(36.dp),
        )
    } else {
        Spacer(
            modifier
                .fillMaxWidth()
                .height(36.dp)
        )
    }
}

@PreviewLightDark
@Composable
private fun TabNavigationPreview() {
    PreviewBackground {
        TabNavigation(
            selectedIndex = 0,
            tabs = {
                Tab(
                    title = "Hello World",
                    iconResId = R.drawable.ic_close,
                    selected = true,
                    onClick = {},
                    onActionClick = {},
                )
                Tab(
                    title = "Hello World",
                    iconResId = R.drawable.ic_close,
                    selected = false,
                    onClick = {},
                    onActionClick = {},
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}