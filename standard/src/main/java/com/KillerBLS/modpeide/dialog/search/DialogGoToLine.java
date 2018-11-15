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
import android.text.InputType;

import com.afollestad.materialdialogs.MaterialDialog;
import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.utils.commons.EditorDelegate;

public class DialogGoToLine extends MaterialDialog {

    private DialogGoToLine(Builder builder) {
        super(builder);
    }

    public static class Builder extends MaterialDialog.Builder {

        public Builder(@NonNull Context context, EditorDelegate editorDelegate) {
            super(context);
            title(R.string.dialog_title_goto_line);
            negativeText(R.string.action_cancel);
            positiveText(R.string.action_apply);
            cancelable(false);
            autoDismiss(false);
            inputType(InputType.TYPE_CLASS_NUMBER);
            input(context.getString(R.string.hint_line), null, false, ((dialog, input) -> {
                int inputNumber = (int) Double.parseDouble(input.toString());
                editorDelegate.notifyGoToLineClicked(inputNumber);
                dialog.dismiss();
            }));
            onNegative(((dialog, which) -> dialog.dismiss()));
        }

        @Override
        public DialogGoToLine build() {
            return new DialogGoToLine(this);
        }

        @Override
        public DialogGoToLine show() {
            DialogGoToLine dialog = build();
            dialog.show();
            return dialog;
        }
    }
}