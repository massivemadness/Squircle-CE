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

package com.KillerBLS.modpeide.dialog.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.utils.commons.EditorDelegate;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogFind extends MaterialDialog {

    private DialogFind(Builder builder) {
        super(builder);
    }

    public static class Builder extends MaterialDialog.Builder {

        @BindView(R.id.field_input)
        TextInputEditText input;
        @BindView(R.id.matchCase)
        CheckBox matchCase;
        @BindView(R.id.regExp)
        CheckBox regExp;
        @BindView(R.id.wordOnly)
        CheckBox wordOnly;

        public Builder(@NonNull Context context, EditorDelegate editorDelegate) {
            super(context);
            title(R.string.dialog_title_find);
            positiveText(R.string.action_apply);
            negativeText(R.string.action_cancel);
            customView(R.layout.dialog_find, true);
            onPositive((dialog, which) -> {
                String inputSearch = input.getText().toString();
                if(!TextUtils.isEmpty(inputSearch)) {
                    editorDelegate.notifyFindClicked(inputSearch, matchCase.isChecked(),
                            regExp.isChecked(), wordOnly.isChecked());
                }
                Toast.makeText(context, R.string.message_done, Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public DialogFind build() {
            return new DialogFind(this);
        }

        @Override
        public DialogFind show() {
            DialogFind dialog = build();
            View customView = dialog.getCustomView();
            assert customView != null;
            ButterKnife.bind(this, customView);
            dialog.show();
            return dialog;
        }
    }
}