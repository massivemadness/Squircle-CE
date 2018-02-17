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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.KillerBLS.modpeide.dialog;

import android.content.Context;
import android.support.annotation.NonNull;

import com.KillerBLS.modpeide.R;
import com.afollestad.materialdialogs.MaterialDialog;

public class ReplaceAllDialog extends MaterialDialog {

    protected ReplaceAllDialog(Builder builder) {
        super(builder);
    }

    public static class Builder extends MaterialDialog.Builder {
        public Builder(@NonNull Context context) {
            super(context);
            title(R.string.dialog_replace_all);
            positiveText(R.string.apply);
            negativeText(R.string.cancel);
            customView(R.layout.dialog_replace_all, true);
        }
    }
}
