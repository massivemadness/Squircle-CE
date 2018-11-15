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

package com.KillerBLS.modpeide.dialog.commons;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.KillerBLS.modpeide.R;

public class DialogExit extends MaterialDialog {

    private DialogExit(Builder builder) {
        super(builder);
    }

    public static class Builder extends MaterialDialog.Builder {

        public Builder(@NonNull Context context) {
            super(context);
            title(R.string.dialog_title_exit);
            content(R.string.dialog_message_exit);
            negativeText(R.string.action_no);
            positiveText(R.string.action_yes);
        }

        @Override
        public DialogExit build() {
            return new DialogExit(this);
        }

        @Override
        public DialogExit show() {
            DialogExit dialog = build();
            dialog.show();
            return dialog;
        }
    }
}