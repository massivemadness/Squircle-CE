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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.extensions.buildHtmlAnnotatedString
import com.blacksquircle.ui.ds.extensions.takeUrl
import com.blacksquircle.ui.ds.size2XS
import com.blacksquircle.ui.ds.sizeM
import com.blacksquircle.ui.ds.sizeXS

@Composable
fun ReleaseInfo(
    versionName: String,
    releaseDate: String,
    releaseNotes: String,
) {
    Column(modifier = Modifier.padding(sizeM)) {
        VersionName(versionName)
        Spacer(modifier = Modifier.size(size2XS))
        ReleaseDate(releaseDate)
        Spacer(modifier = Modifier.size(sizeXS))
        ReleaseNotes(releaseNotes)
    }
}

@Composable
private fun VersionName(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.headlineSmall,
    )
}

@Composable
private fun ReleaseDate(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ReleaseNotes(text: String) {
    val context = LocalContext.current
    val urlColor = MaterialTheme.colorScheme.primary
    val annotatedString = remember(text) {
        text.buildHtmlAnnotatedString(urlColor)
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
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        modifier = Modifier.fillMaxWidth()
    )
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