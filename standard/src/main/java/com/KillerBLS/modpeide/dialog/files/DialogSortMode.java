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
