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

package com.blacksquircle.ui.ds.button

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.SquircleTheme

@Composable
fun FloatingButton(
    iconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(iconResId),
            contentDescription = null,
            tint = SquircleTheme.colors.colorTextAndIconPrimary,
        )
    }
}

@Preview
@Composable
private fun FloatingButtonPreview() {
    SquircleTheme {
        FloatingButton(
            iconResId = R.drawable.ic_edit,
            onClick = {},
        )
    }
}