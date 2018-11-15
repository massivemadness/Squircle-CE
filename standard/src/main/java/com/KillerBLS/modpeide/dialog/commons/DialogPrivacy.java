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

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;

import com.afollestad.materialdialogs.MaterialDialog;
import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.utils.text.StringUtils;

public class DialogPrivacy extends MaterialDialog {

    private DialogPrivacy(Builder builder) {
        super(builder);
    }

    public static class Builder extends MaterialDialog.Builder {

        public Builder(@NonNull Context context) {
            super(context);
            title(R.string.pref_privacy_policy_title);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                content(Html.fromHtml(StringUtils.getRawFileText(context, R.raw.privacy_policy), Html.FROM_HTML_MODE_LEGACY));
            } else {
                content(Html.fromHtml(StringUtils.getRawFileText(context, R.raw.privacy_policy)));
            }
            negativeText(R.string.action_close);
        }

        @Override
        public DialogPrivacy build() {
            return new DialogPrivacy(this);
        }

        @Override
        public DialogPrivacy show() {
            DialogPrivacy dialog = build();
            dialog.show();
            return dialog;
        }
    }
}