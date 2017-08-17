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
import android.widget.TextView;

import com.KillerBLS.modpeide.LModActivity;
import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.widget.LModEditor;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileOpenTask extends AsyncTask<String, Integer, String> {
    private File file = null;
    private MaterialDialog.Builder mdb;
    private MaterialDialog dialog;
    private LModEditor editor;

    public FileOpenTask(File fileOpen, Context ctx, LModEditor code_editor){
        file = fileOpen;
        mdb = new MaterialDialog.Builder(ctx);
        editor = code_editor;
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
    protected String doInBackground(String... params) {
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            LModActivity.showException(e);
        }
        return text.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        editor.setText(result, TextView.BufferType.SPANNABLE);
        editor.clearHistory();
        LModActivity.FILE_PATH = file.getAbsolutePath();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 1500);
    }
}
