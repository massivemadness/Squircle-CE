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

package com.KillerBLS.modpeide.dialog.commons;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.StackingBehavior;
import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.utils.text.StringUtils;

public class DialogChangeLog extends MaterialDialog {

    private DialogChangeLog(Builder builder) {
        super(builder);
    }

    public static class Builder extends MaterialDialog.Builder {

        public Builder(@NonNull Context context) {
            super(context);
            title(R.string.dialog_title_changelog);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                content(Html.fromHtml(StringUtils.getRawFileText(context, R.raw.changelog), Html.FROM_HTML_MODE_LEGACY));
            } else {
                content(Html.fromHtml(StringUtils.getRawFileText(context, R.raw.changelog)));
            }
            stackingBehavior(StackingBehavior.ALWAYS);
            negativeText(R.string.action_close);
            positiveText(R.string.action_open_source);
            onPositive((dialog, which) -> {
                Intent toSource = new Intent(Intent.ACTION_VIEW);
                toSource.setData(Uri.parse("https://github.com/Light-Team/ModPE-IDE-Source"));
                context.startActivity(toSource);
            });
        }

        @Override
        public DialogChangeLog build() {
            return new DialogChangeLog(this);
        }

        @Override
        public DialogChangeLog show() {
            DialogChangeLog dialog = build();
            dialog.show();
            return dialog;
        }
    }
}