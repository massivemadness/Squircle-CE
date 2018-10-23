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