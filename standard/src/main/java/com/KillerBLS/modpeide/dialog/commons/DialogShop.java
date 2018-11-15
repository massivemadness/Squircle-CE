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