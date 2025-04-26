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

package com.blacksquircle.ui.application.splash

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun SplashScreen() {
    Surface(
        color = Color.Black,
        modifier = Modifier.fillMaxSize(),
    ) {
        Icon(
            painter = painterResource(UiR.drawable.ic_splash_screen),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@PreviewLightDark
@Composable
private fun SplashScreenPreview() {
    PreviewBackground {
        SplashScreen()
    }
}