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
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.KillerBLS.modpeide.R;

public class DialogSortMode extends AlertDialog {

    // region CONSTRUCTOR

    protected DialogSortMode(@NonNull Context context) {
        super(context);
    }

    protected DialogSortMode(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DialogSortMode(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    // endregion CONSTRUCTOR

    public static class Builder extends AlertDialog.Builder {

        public Builder(@NonNull Context context) {
            super(context);
            init();
        }

        private void init() {
            setTitle(R.string.pref_sortMode_title);
            setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.dismiss());
        }
    }
}
