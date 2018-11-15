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
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.adapter.interfaces.SelectionTransfer;
import com.KillerBLS.modpeide.adapter.model.FileModel;
import com.KillerBLS.modpeide.manager.FileManager;

import java.io.File;

public class DialogDelete extends MaterialDialog {

    private DialogDelete(Builder builder) {
        super(builder);
    }

    public static class Builder extends MaterialDialog.Builder {

        public Builder(@NonNull Context context, SelectionTransfer selectionTransfer, File file) {
            super(context);
            title(file.getName());
            content(R.string.dialog_message_delete);
            negativeText(R.string.action_cancel);
            positiveText(R.string.action_delete);
            onPositive(((dialog, which) -> {
                FileManager.deleteRecursive(file);
                selectionTransfer.onClick(new FileModel(file.getParentFile()));
                Toast.makeText(context, R.string.message_done, Toast.LENGTH_SHORT).show();
            }));
            onNegative(((dialog, which) -> dialog.dismiss()));
        }

        @Override
        public DialogDelete build() {
            return new DialogDelete(this);
        }

        @Override
        public DialogDelete show() {
            DialogDelete dialog = build();
            dialog.show();
            return dialog;
        }
    }
}