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

package com.blacksquircle.ui.ds.section

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.size2XS
import com.blacksquircle.ui.ds.size5XL
import com.blacksquircle.ui.ds.sizeL

@Composable
fun SectionItem(
    icon: Int,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
    selectedContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    unselectedContainerColor: Color = MaterialTheme.colorScheme.background,
) {
    SectionItemContainer(
        isSelected = isSelected,
        onSelected = onSelected,
        modifier = modifier,
        selectedContainerColor = selectedContainerColor,
        unselectedContainerColor = unselectedContainerColor,
    ) {
        SectionItemContent(
            icon = icon,
            title = title,
            subtitle = subtitle,
            isSelected = isSelected,
        )
    }
}

@Composable
private fun SectionItemContainer(
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier,
    selectedContainerColor: Color,
    unselectedContainerColor: Color,
    content: @Composable () -> Unit,
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) selectedContainerColor else unselectedContainerColor,
        label = "Container Background"
    )
    Card(
        colors = CardDefaults.cardColors(containerColor),
        border = if (isSelected) null else CardDefaults.outlinedCardBorder(),
        onClick = onSelected,
        modifier = modifier,
    ) {
        content()
    }
}

@Composable
private fun SectionItemContent(
    icon: Int,
    title: String,
    subtitle: String,
    isSelected: Boolean,
) {
    Row(modifier = Modifier.padding(sizeL)) {
        SectionIcon(
            icon = icon,
            iconSize = size5XL,
            isSelected = isSelected
        )
        Spacer(modifier = Modifier.size(sizeL))
        Column {
            SectionTitle(title = title)
            Spacer(modifier = Modifier.size(size2XS))
            SectionSubtitle(subtitle = subtitle)
        }
    }
}

@Composable
private fun SectionIcon(
    icon: Int,
    iconSize: Dp,
    isSelected: Boolean,
) {
    val iconBackground = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val iconTint = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(iconSize)
            .clip(CircleShape)
            .background(iconBackground)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(iconSize / 2),
            tint = iconTint,
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SectionSubtitle(subtitle: String) {
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun UnselectedSectionItemPreview() {
    SquircleTheme {
        SectionItem(
            icon = R.drawable.ic_info,
            title = "Application",
            subtitle = "Configure global application settings",
            isSelected = false,
            onSelected = {},
        )
    }
}

@Preview
@Composable
private fun SelectedSectionItemPreview() {
    SquircleTheme {
        SectionItem(
            icon = R.drawable.ic_info,
            title = "Application",
            subtitle = "Configure global application settings",
            isSelected = true,
            onSelected = {},
        )
    }
}