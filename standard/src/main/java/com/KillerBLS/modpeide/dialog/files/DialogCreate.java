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