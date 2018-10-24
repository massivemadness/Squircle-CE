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

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.KillerBLS.modpeide.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogShop extends MaterialDialog {

    private DialogShop(Builder builder) {
        super(builder);
    }

    public static class Builder extends MaterialDialog.Builder {

        @BindView(R.id.button_buy)
        TextView buttonBuy;
        @BindView(R.id.button_continue)
        TextView buttonContinue;

        public Builder(@NonNull Context context) {
            super(context);
            theme(Theme.LIGHT);
            customView(R.layout.dialog_shop, false);
        }

        @Override
        public DialogShop build() {
            return new DialogShop(this);
        }

        @Override
        public DialogShop show() {
            DialogShop dialog = build();
            ButterKnife.bind(this, dialog.getCustomView());
            buttonContinue.setOnClickListener(view1 -> dialog.dismiss());
            buttonBuy.setOnClickListener(view1 -> {
                final String packageName = "com.LightTeam.modpeidepro";
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + packageName)));
                } catch (ActivityNotFoundException e) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                }
                dialog.dismiss();
            });
            dialog.show();
            return dialog;
        }
    }
}