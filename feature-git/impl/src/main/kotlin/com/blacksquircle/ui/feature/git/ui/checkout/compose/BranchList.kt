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

package com.blacksquircle.ui.feature.git.ui.checkout.compose

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.modifier.debounceClickable
import com.blacksquircle.ui.ds.radio.Radio
import com.blacksquircle.ui.ds.radio.RadioStyleDefaults

@Composable
internal fun BranchList(
    currentBranch: String,
    branchList: List<String>,
    onBranchSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedIndex = branchList.indexOf(currentBranch)
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = selectedIndex
    )

    LazyColumn(
        state = lazyListState,
        modifier = modifier,
    ) {
        itemsIndexed(branchList) { index, value ->
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .debounceClickable(
                        interactionSource = interactionSource,
                        indication = ripple(),
                        onClick = { onBranchSelected(value) }
                    )
                    .padding(horizontal = 24.dp)
            ) {
                Radio(
                    title = branchList[index],
                    checked = value == currentBranch,
                    onClick = { onBranchSelected(value) },
                    radioStyle = RadioStyleDefaults.Primary.copy(
                        textStyle = SquircleTheme.typography.text18Regular,
                    ),
                    interactionSource = interactionSource,
                    indication = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
            }
        }
    }
}