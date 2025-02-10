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

package com.blacksquircle.ui.feature.themes.ui.fragment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.OutlinedButton
import com.blacksquircle.ui.ds.button.IconButtonSizeDefaults
import com.blacksquircle.ui.ds.extensions.toHexString
import com.blacksquircle.ui.ds.preference.ColorPreference
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.domain.model.Property
import com.blacksquircle.ui.feature.themes.domain.model.PropertyItem
import com.blacksquircle.ui.feature.themes.ui.viewmodel.EditThemeViewModel
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun EditThemeScreen(viewModel: EditThemeViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    EditThemeScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
        onImportClicked = viewModel::onImportClicked,
        onThemeNameChanged = viewModel::onThemeNameChanged,
        onThemeAuthorChanged = viewModel::onThemeAuthorChanged,
        onSaveClicked = viewModel::onSaveClicked,
        onColorSelected = viewModel::onColorSelected,
    )
}

@Composable
private fun EditThemeScreen(
    viewState: EditThemeViewState,
    onBackClicked: () -> Unit = {},
    onImportClicked: () -> Unit = {},
    onThemeNameChanged: (String) -> Unit = {},
    onThemeAuthorChanged: (String) -> Unit = {},
    onSaveClicked: () -> Unit = {},
    onColorSelected: (Property, String) -> Unit = { _, _ -> },
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(R.string.label_new_theme),
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
                navigationActions = {
                    if (!viewState.isEditMode) {
                        IconButton(
                            iconResId = UiR.drawable.ic_file_import,
                            iconButtonSize = IconButtonSizeDefaults.L,
                            onClick = onImportClicked,
                        )
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.systemBars,
        modifier = Modifier.imePadding()
    ) { contentPadding ->
        LazyColumn(
            contentPadding = contentPadding,
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
                ) {
                    Spacer(Modifier.height(8.dp))

                    TextField(
                        inputText = viewState.name,
                        onInputChanged = onThemeNameChanged,
                        labelText = stringResource(R.string.hint_enter_theme_name),
                        placeholderText = stringResource(R.string.hint_theme_name),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                        ),
                        error = viewState.invalidName,
                    )

                    Spacer(Modifier.height(8.dp))

                    TextField(
                        inputText = viewState.author,
                        onInputChanged = onThemeAuthorChanged,
                        labelText = stringResource(R.string.hint_enter_theme_author),
                        placeholderText = stringResource(R.string.hint_theme_author),
                        error = viewState.invalidAuthor,
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedButton(
                        text = stringResource(UiR.string.common_save),
                        onClick = onSaveClicked,
                        modifier = Modifier.align(Alignment.End),
                    )
                }
            }
            items(
                items = viewState.properties,
                key = PropertyItem::propertyKey,
            ) { propertyItem ->
                ColorPreference(
                    title = propertyItem.propertyKey.key,
                    subtitle = propertyResource(propertyItem.propertyKey),
                    initialColor = Color(propertyItem.propertyValue.toColorInt()),
                    onColorSelected = { color ->
                        onColorSelected(propertyItem.propertyKey, color.toHexString())
                    },
                )
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun propertyResource(property: Property): String {
    return when (property) {
        Property.TEXT_COLOR -> stringResource(R.string.theme_property_text_color)
        Property.CURSOR_COLOR -> stringResource(R.string.theme_property_cursor_color)
        Property.BACKGROUND_COLOR -> stringResource(R.string.theme_property_background_color)
        Property.GUTTER_COLOR -> stringResource(R.string.theme_property_gutter_color)
        Property.GUTTER_DIVIDER_COLOR -> stringResource(R.string.theme_property_gutter_divider_color)
        Property.GUTTER_CURRENT_LINE_NUMBER_COLOR -> stringResource(R.string.theme_property_gutter_current_line_number_color)
        Property.GUTTER_TEXT_COLOR -> stringResource(R.string.theme_property_gutter_text_color)
        Property.SELECTED_LINE_COLOR -> stringResource(R.string.theme_property_selected_line_color)
        Property.SELECTION_COLOR -> stringResource(R.string.theme_property_selection_color)
        Property.SUGGESTION_QUERY_COLOR -> stringResource(R.string.theme_property_suggestion_query_color)
        Property.FIND_RESULT_BACKGROUND_COLOR -> stringResource(R.string.theme_property_find_result_background_color)
        Property.DELIMITER_BACKGROUND_COLOR -> stringResource(R.string.theme_property_delimiter_background_color)
        Property.NUMBER_COLOR -> stringResource(R.string.theme_property_numbers_color)
        Property.OPERATOR_COLOR -> stringResource(R.string.theme_property_operators_color)
        Property.KEYWORD_COLOR -> stringResource(R.string.theme_property_keywords_color)
        Property.TYPE_COLOR -> stringResource(R.string.theme_property_types_color)
        Property.LANG_CONST_COLOR -> stringResource(R.string.theme_property_lang_const_color)
        Property.PREPROCESSOR_COLOR -> stringResource(R.string.theme_property_preprocessor_color)
        Property.VARIABLE_COLOR -> stringResource(R.string.theme_property_variables_color)
        Property.METHOD_COLOR -> stringResource(R.string.theme_property_methods_color)
        Property.STRING_COLOR -> stringResource(R.string.theme_property_strings_color)
        Property.COMMENT_COLOR -> stringResource(R.string.theme_property_comments_color)
        Property.TAG_COLOR -> stringResource(R.string.theme_property_tag_color)
        Property.TAG_NAME_COLOR -> stringResource(R.string.theme_property_tag_name_color)
        Property.ATTR_NAME_COLOR -> stringResource(R.string.theme_property_attr_name_color)
        Property.ATTR_VALUE_COLOR -> stringResource(R.string.theme_property_attr_value_color)
        Property.ENTITY_REF_COLOR -> stringResource(R.string.theme_property_entity_ref_color)
    }
}

@PreviewLightDark
@Composable
private fun EditThemeScreenPreview() {
    PreviewBackground {
        EditThemeScreen(
            viewState = EditThemeViewState(
                name = "Custom theme",
                author = "Author",
            ),
        )
    }
}