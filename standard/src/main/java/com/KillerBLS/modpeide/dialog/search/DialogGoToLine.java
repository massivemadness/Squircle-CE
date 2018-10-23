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