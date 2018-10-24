/*
 * Copyright (C) 2018 Light Team Software
 *
 * This file is part of ModPE IDE.
 *
 * ModPE IDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ModPE IDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
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