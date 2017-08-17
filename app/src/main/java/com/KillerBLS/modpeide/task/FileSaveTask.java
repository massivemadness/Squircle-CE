/*
 * Copyright (C) 2017 Light Team Software
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

package com.KillerBLS.modpeide.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.KillerBLS.modpeide.LModActivity;
import com.KillerBLS.modpeide.R;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.FileOutputStream;

public class FileSaveTask extends AsyncTask<Void, String, String> {
    private String file_text;
    private MaterialDialog.Builder mdb;
    private MaterialDialog dialog;

    public FileSaveTask(String text, Context ctx) {
        file_text = text;
        mdb = new MaterialDialog.Builder(ctx);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mdb.title(R.string.dialog_loading)
                .content(R.string.dialog_loading_desc)
                .cancelable(false)
                .autoDismiss(true)
                .progress(true, 0);
        dialog = mdb.build();
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            File file = new File(LModActivity.FILE_PATH);
            FileOutputStream fOut = new FileOutputStream(file);
            fOut.write(file_text.getBytes());
            fOut.close();
        } catch (Exception e) {
            dialog.dismiss();
            LModActivity.showException(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 1500);
    }
}
