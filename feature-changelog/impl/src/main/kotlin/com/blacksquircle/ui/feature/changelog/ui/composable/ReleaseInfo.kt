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

package com.blacksquircle.ui.feature.changelog.ui.composable

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.extensions.buildHtmlAnnotatedString
import com.blacksquircle.ui.ds.extensions.takeUrl

@Composable
internal fun ReleaseInfo(
    versionName: String,
    releaseDate: String,
    releaseNotes: String,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = versionName,
            color = SquircleTheme.colors.colorTextAndIconPrimary,
            style = SquircleTheme.typography.text18Medium,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = releaseDate,
            color = SquircleTheme.colors.colorTextAndIconSecondary,
            style = SquircleTheme.typography.text14Regular,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        val context = LocalContext.current
        val urlColor = SquircleTheme.colors.colorPrimary
        val annotatedString = remember(releaseNotes) {
            releaseNotes.buildHtmlAnnotatedString(urlColor)
        }
        ClickableText(
            text = annotatedString,
            onClick = { pos ->
                val link = annotatedString.takeUrl(pos)
                if (link != null) {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(link)
                    }
                    context.startActivity(intent)
                }
            },
            style = SquircleTheme.typography.text14Regular.copy(
                color = SquircleTheme.colors.colorTextAndIconSecondary,
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun ReleaseInfoPreview() {
    SquircleTheme {
        ReleaseInfo(
            versionName = "v2024.1.0",
            releaseDate = "24 Jan. 2024",
            releaseNotes = "- New UI!<br>- Improved support for tablets and foldables!",
        )
    }
}