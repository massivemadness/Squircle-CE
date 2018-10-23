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

package com.KillerBLS.modpeide.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.utils.Wrapper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class SplashActivity extends AppCompatActivity {

    private String mSelectedFilePath = null; //Файл который будет открываться из другой программы

    @Inject
    Wrapper mWrapper;

    @BindView(R.id.item_message)
    TextView mMessage;
    @BindView(R.id.item_button)
    Button mDeniedButton;

    // Находим TextView и Button
    // Устанавливаем в TextView первый текст

    // Первый процесс - проверка разрешений
    // Если разрешения не выданы - предлагаем выдать
    // В случае отказа - ждем 300ms и ставим в TextView текст-ошибку и отображаем кнопку
    // Если выданы - ждем 300ms и запускаем второй процесс

    // Второй процесс - проверка ссылки на файл из вне
    // Ставим в TextView второй текст
    // Если ссылка есть - устанавливаем её в mSelectedFilePath, ждем 300ms и запускаем третий процесс
    // Если файла нет - ждем 300ms и запускаем третий процесс

    // Третий процесс
    // Устанавливаем в TextView третий текст
    // Ждем 200ms и открываем MainActivity вместе с переданной ссылкой на файл

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        init();
    }

    /**
     * Запуск приложения со всеми настройками.
     */
    protected void init() {
        //Fullscreen
        if(mWrapper.getFullscreenMode()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        mMessage.setText(R.string.message_checking_permissions);
        mDeniedButton.setOnClickListener(view -> {
            Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(appSettingsIntent, 228);
        });
        process1();
    }

    // region PROCESS_1

    public void process1() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mDeniedButton.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(this::process2, 300);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 1337);
        }
    }

    public void process1_denied() {
        new Handler().postDelayed(() ->
                mMessage.setText(R.string.message_permission_denied), 300);
    }

    // endregion PROCESS_1

    // region PROCESS_2

    /**
     * Проверяем, был-ли файл выбран из какого-либо проводника, если да - ставим ссылку на него.
     */
    public void process2() {
        mMessage.setText(R.string.message_checking_files);
        final Intent intent = getIntent();
        final String action = intent.getAction();
        if(Intent.ACTION_VIEW.equals(action)) {
            if(intent.getData() != null) {
                mSelectedFilePath = intent.getData().getPath();
            }
        }
        new Handler().postDelayed(this::process3, 300);
    }

    // endregion PROCESS_2

    // region PROCESS_3

    public void process3() {
        mMessage.setText(R.string.message_running);
        new Handler().postDelayed(() -> {
            Intent toEditor = new Intent(this, MainActivity.class);
            if(mSelectedFilePath != null) {
                toEditor.putExtra("SELECTED_FILE_PATH", mSelectedFilePath);
            }
            startActivity(toEditor);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 200);
    }

    // endregion PROCESS_3

    // region PERMISSIONS

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == 1337 && grantResults.length == 1) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                mDeniedButton.setVisibility(View.VISIBLE);
                process1_denied();
            } else {
                new Handler().postDelayed(this::process2, 300);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 228) {
            process1();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // endregion PERMISSIONS
}