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
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.KillerBLS.modpeide.R;
import com.afollestad.materialdialogs.MaterialDialog;

import es.dmoral.toasty.Toasty;

public class RenameDialog extends MaterialDialog {

    private static String mValidName;

    protected RenameDialog(MaterialDialog.Builder builder) {
        super(builder);
    }

    public static class Builder extends MaterialDialog.Builder {
        public Builder(@NonNull Context context) {
            super(context);
            title(R.string.fexplorer_rename);
            customView(R.layout.dialog_explorer_rename, true);
            positiveText(R.string.apply);
            negativeText(R.string.cancel);
        }
    }

    /**
     * Проверка имени файла перед переименованием.
     * @param ctx - контекст для получения ресурсов.
     * @param customView - layout диалога для проверки.
     * @return - возвращает true, если имя соответствует нормам.
     */
    public static boolean checkNameField(@NonNull Context ctx, @NonNull View customView) {
        EditText nameField = customView.findViewById(R.id.editName);
        mValidName = nameField.getText().toString();
        if (TextUtils.isEmpty(mValidName) || mValidName.matches("^.*[^a-zA-Z0-9._-].*$")){ //Имя пустое или не правильное?
            Toasty.error(ctx, ctx.getString(R.string.dialog_error_name), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public static String getValidName() {
        return mValidName;
    }
}
