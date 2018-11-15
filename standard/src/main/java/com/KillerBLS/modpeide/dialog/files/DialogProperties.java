/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.KillerBLS.modpeide.dialog.files;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.SpannableString;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.utils.files.Properties;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogProperties extends MaterialDialog {

    private DialogProperties(Builder builder) {
        super(builder);
    }

    public static class Builder extends MaterialDialog.Builder {

        private Properties.Result result;

        public Builder(@NonNull Context context) {
            super(context);
            title(R.string.menu_file_properties);
            customView(R.layout.dialog_properties, true);
            negativeText(R.string.action_close);
        }

        public Builder withFile(File file) {
            result = Properties.analyze(file);
            return this;
        }

        @Override
        public DialogProperties build() {
            return new DialogProperties(this);
        }

        @BindView(R.id.field_properties)
        TextView mProperties;
        @BindView(R.id.box_read)
        CheckBox mRead;
        @BindView(R.id.box_write)
        CheckBox mWrite;
        @BindView(R.id.box_execute)
        CheckBox mExecute;

        @Override
        public DialogProperties show() {
            DialogProperties dialog = build();
            View customView = dialog.getCustomView();
            assert customView != null;
            ButterKnife.bind(this, customView);

            final Context context = getContext(); //так удобнее
            String properties =
                    String.format(context.getString(R.string.properties_name) + "<br>"
                            + context.getString(R.string.properties_path) + "<br>"
                            + context.getString(R.string.properties_modified) + "<br>"
                            + context.getString(R.string.properties_size) + "<br>"
                            + context.getString(R.string.properties_line_count) + "<br>"
                            + context.getString(R.string.properties_word_count) + "<br>"
                            + context.getString(R.string.properties_char_count) + "<br>",
                            result.name,
                            result.path,
                            result.lastModified,
                            result.size,
                            result.lines,
                            result.words,
                            result.chars);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mProperties.setText(new SpannableString(Html.fromHtml(properties, Html.FROM_HTML_MODE_LEGACY)));
            } else {
                mProperties.setText(new SpannableString(Html.fromHtml(properties)));
            }
            mRead.setChecked(result.read);
            mWrite.setChecked(result.write);
            mExecute.setChecked(result.execute);

            dialog.show();
            return dialog;
        }
    }
}