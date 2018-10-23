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
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.adapter.interfaces.SelectionTransfer;
import com.KillerBLS.modpeide.adapter.model.FileModel;
import com.KillerBLS.modpeide.utils.text.StringUtils;

import java.io.File;
import java.io.IOException;

public class DialogCreate extends MaterialDialog {

    private static final String TAG = DialogCreate.class.getSimpleName();

    private DialogCreate(Builder builder) {
        super(builder);
    }

    public static class Builder extends MaterialDialog.Builder {

        private File mCurrentPath;
        private boolean mIsFolder;

        public Builder(@NonNull Context context, SelectionTransfer selectionTransfer) {
            super(context);
            title(R.string.dialog_title_create);
            negativeText(R.string.action_cancel);
            positiveText(R.string.action_create);
            cancelable(false);
            autoDismiss(false);
            inputType(InputType.TYPE_CLASS_TEXT);
            input(context.getString(R.string.hint_enter_file_name), null, false, (dialog, input) -> {
                if(StringUtils.isValidFileName(input.toString())) {
                    File mNewFile = new File(mCurrentPath, input.toString());
                    if(mIsFolder) { //is folder
                        mNewFile.mkdir();
                        selectionTransfer.onClick(new FileModel(mNewFile)); //open folder
                    } else { //is file
                        try {
                            mNewFile.createNewFile();
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage(), e);
                            Toast.makeText(context, R.string.message_error, Toast.LENGTH_SHORT).show();
                        } finally {
                            if(mNewFile.exists()) {
                                selectionTransfer.onClick(new FileModel(mCurrentPath)); //update list
                                selectionTransfer.onClick(new FileModel(mNewFile)); //open file
                            }
                        }
                    }
                    dialog.dismiss();
                    Toast.makeText(context, R.string.message_done, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.message_invalid_file_name, Toast.LENGTH_SHORT).show();
                }
            });
            onNegative(((dialog, which) -> dialog.dismiss()));
        }

        public Builder setCurrentPath(File currentPath) {
            mCurrentPath = currentPath;
            return this;
        }

        public Builder setIsFolder(boolean isFolder) {
            mIsFolder = isFolder;
            return this;
        }

        @Override
        public DialogCreate build() {
            return new DialogCreate(this);
        }

        @Override
        public DialogCreate show() {
            DialogCreate dialog = build();
            dialog.show();
            return dialog;
        }
    }
}