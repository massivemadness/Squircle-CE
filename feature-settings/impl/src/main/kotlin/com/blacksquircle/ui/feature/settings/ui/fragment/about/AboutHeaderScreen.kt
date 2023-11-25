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

package com.blacksquircle.ui.feature.settings.ui.fragment.about

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.extensions.adaptiveIconPainterResource
import com.blacksquircle.ui.ds.preference.PreferenceGroup
import com.blacksquircle.ui.ds.preference.Preference
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.settings.BuildConfig
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.data.utils.applicationName
import com.blacksquircle.ui.feature.settings.data.utils.versionCode
import com.blacksquircle.ui.feature.settings.data.utils.versionName
import com.blacksquircle.ui.feature.settings.ui.viewmodel.SettingsViewModel
import com.blacksquircle.ui.ds.R as UiR

@Composable
fun AboutHeaderScreen(viewModel: SettingsViewModel) {
    AboutHeaderContent(
        onPrivacyClicked = {},
        onTranslationClicked = {},
        onContributeClicked = {},
        onBackClicked = viewModel::popBackStack
    )
}

@Composable
private fun AboutHeaderContent(
    onPrivacyClicked: () -> Unit,
    onTranslationClicked: () -> Unit,
    onContributeClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Toolbar(
                title = stringResource(R.string.pref_category_about),
                backIcon = UiR.drawable.ic_back,
                onBackClicked = onBackClicked,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            PreferenceGroup(
                title = stringResource(R.string.pref_category_about)
            )
            Preference(
                title = LocalContext.current.applicationName,
                subtitle = stringResource(
                    R.string.pref_about_summary,
                    LocalContext.current.appVersionName(),
                    LocalContext.current.versionCode,
                ),
                leadingContent = {
                    Image(
                        painter = adaptiveIconPainterResource(UiR.mipmap.ic_launcher),
                        contentDescription = null,
                        modifier = Modifier.size(56.dp)
                    )
                }
            )
            Preference(
                title = stringResource(R.string.pref_privacy_policy_title),
                onClick = onPrivacyClicked,
            )
            HorizontalDivider()
            PreferenceGroup(
                title = stringResource(R.string.pref_category_contribute)
            )
            Preference(
                title = stringResource(R.string.pref_translation_title),
                subtitle = stringResource(R.string.pref_translation_summary),
                onClick = onTranslationClicked,
            )
            Preference(
                title = stringResource(R.string.pref_contribute_title),
                subtitle = stringResource(R.string.pref_contribute_summary),
                onClick = onContributeClicked,
            )
        }
    }
}

private fun Context.appVersionName(): String {
    return if (BuildConfig.DEBUG) {
        this.versionName + getString(R.string.debug_suffix)
    } else {
        this.versionName
    }
}

@Preview
@Composable
fun AboutHeaderScreenPreview() {
    SquircleTheme {
        AboutHeaderContent(
            onPrivacyClicked = {},
            onTranslationClicked = {},
            onContributeClicked = {},
            onBackClicked = {},
        )
    }
}