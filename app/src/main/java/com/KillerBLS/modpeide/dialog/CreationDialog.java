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

import com.KillerBLS.modpeide.utils.text.StringUtils;
import com.afollestad.materialdialogs.MaterialDialog;
import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.document.commons.FileObject;

import es.dmoral.toasty.Toasty;

public class CreationDialog extends MaterialDialog {

    private static String mValidName;
    private static String mValidPath;

    protected CreationDialog(MaterialDialog.Builder builder) {
        super(builder);
    }

    public static class Builder extends MaterialDialog.Builder {
        public Builder(@NonNull Context context) {
            super(context);
            positiveText(R.string.dialog_create);
            negativeText(R.string.cancel);
        }
    }

    /**
     * Проверка имени файла перед созданием.
     * @param ctx - контекст для получения ресурсов.
     * @param customView - layout диалога для проверки.
     * @return - возвращает true, если имя соответствует нормам.
     */
    public static boolean checkNameField(@NonNull Context ctx, @NonNull View customView) {
        EditText nameField = customView.findViewById(R.id.editName);
        mValidName = nameField.getText().toString();
        if (!StringUtils.isValidFileName(mValidName)){ //Имя пустое или не правильное?
            Toasty.error(ctx, ctx.getString(R.string.dialog_error_name), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    /**
     * Проверка пути файла перед созданием.
     * @param ctx - контекст для получения ресурсов.
     * @param customView - layout диалога для проверки.
     * @return - возвращает true, если путь соответствует нормам.
     */
    public static boolean checkPathField(@NonNull Context ctx, @NonNull View customView) {
        EditText pathField = customView.findViewById(R.id.editPath);
        mValidPath = pathField.getText().toString();
        FileObject mPath = new FileObject(mValidPath);
        if(!mPath.isDirectory() || TextUtils.isEmpty(mValidPath)) { //Путь не правильный или пустой?
            Toasty.error(ctx, ctx.getString(R.string.dialog_error_valid_path), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public static String getValidName() {
        return mValidName;
    }

    public static String getValidPath() {
        return mValidPath;
    }
}
