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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.R
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.ds.progress.CircularProgressStyleDefaults
import com.blacksquircle.ui.ds.R as UiR

/** https://developer.android.com/develop/ui/views/launch/splash-screen#dimensions */
private val SplashIconSize = 288.dp

@Composable
internal fun SplashScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        Icon(
            painter = painterResource(UiR.drawable.ic_splash_screen),
            contentDescription = null,
            tint = Color.Unspecified,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(SplashIconSize))

            CircularProgress(
                circularProgressStyle = CircularProgressStyleDefaults.Monochrome,
            )

            Text(
                text = stringResource(R.string.loading),
                style = SquircleTheme.typography.text16Regular,
                color = Color.White,
            )
        }
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    PreviewBackground {
        SplashScreen()
    }
}