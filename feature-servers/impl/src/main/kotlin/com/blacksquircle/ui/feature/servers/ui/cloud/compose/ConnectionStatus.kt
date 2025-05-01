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

package com.blacksquircle.ui.feature.servers.ui.cloud.compose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.feature.servers.R
import com.blacksquircle.ui.feature.servers.domain.model.ServerStatus
import com.blacksquircle.ui.ds.R as UiR

private const val RotateDuration = 750

@Composable
internal fun ConnectionStatus(
    status: ServerStatus,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        when (status) {
            is ServerStatus.Checking -> {
                val infiniteTransition = rememberInfiniteTransition()
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = RotateDuration,
                            easing = LinearEasing,
                        ),
                        repeatMode = RepeatMode.Restart,
                    ),
                    label = "Rotation"
                )
                Icon(
                    painter = painterResource(UiR.drawable.ic_autorenew),
                    contentDescription = null,
                    tint = SquircleTheme.colors.colorTextAndIconSecondary,
                    modifier = Modifier
                        .size(16.dp)
                        .graphicsLayer { rotationZ = rotation }
                )
            }

            is ServerStatus.Available -> {
                Icon(
                    painter = painterResource(UiR.drawable.ic_check),
                    contentDescription = null,
                    tint = SquircleTheme.colors.colorTextAndIconSuccess,
                    modifier = Modifier.size(16.dp)
                )
            }

            is ServerStatus.Unavailable -> {
                Icon(
                    painter = painterResource(UiR.drawable.ic_close),
                    contentDescription = null,
                    tint = SquircleTheme.colors.colorTextAndIconError,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(Modifier.width(4.dp))

        Text(
            text = when (status) {
                is ServerStatus.Checking -> {
                    stringResource(R.string.connection_checking)
                }
                is ServerStatus.Available -> {
                    stringResource(R.string.connection_available, status.latency)
                }
                is ServerStatus.Unavailable -> {
                    stringResource(R.string.connection_unavailable, status.message)
                }
            },
            color = when (status) {
                is ServerStatus.Checking -> SquircleTheme.colors.colorTextAndIconSecondary
                is ServerStatus.Available -> SquircleTheme.colors.colorTextAndIconSuccess
                is ServerStatus.Unavailable -> SquircleTheme.colors.colorTextAndIconError
            },
            style = SquircleTheme.typography.text14Regular,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}