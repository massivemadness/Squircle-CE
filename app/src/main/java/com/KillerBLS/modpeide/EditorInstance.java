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

package com.KillerBLS.modpeide;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.os.StrictMode.VmPolicy.Builder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;

import com.KillerBLS.modpeide.utils.logger.Logger;

import es.dmoral.toasty.Toasty;

public class EditorInstance extends Application {

    private static final String TAG = EditorInstance.class.getSimpleName();

    public static final String APPLICATION_NAME = "ModPE IDE";
    public static final boolean ALLOW_DEBUG = true;

    //region URL'S

    public static final String APP_URL_MARKET =
            "market://details?id=com.KillerBLS.modpeide";
    public static final String APP_URL_NORMAL =
            "https://play.google.com/store/apps/details?id=com.KillerBLS.modpeide";
    public static final String APP_URL_ULTIMATE_MARKET =
            "market://details?id=com.LightTeam.modpeidepro";
    public static final String APP_URL_ULTIMATE_NORMAL =
            "https://play.google.com/store/apps/details?id=com.LightTeam.modpeidepro";
    public static final String APP_URL_TEXTURE_NAMES =
            "http://zhuoweizhang.net/mcpetexturenames";
    public static final String APP_URL_OPEN_SOURCE_CODE =
            "https://github.com/Light-Team/ModPE-IDE-Source";

    //endregion URL'S

    @Override
    public void onCreate() {
        super.onCreate();
        detectLeakedDatabases();
        init();
    }

    /**
     * Проверка утечек в базе данных и их устранение.
     */
    protected void detectLeakedDatabases() {
        if(ALLOW_DEBUG) {
            Builder detectLeakedSqlLiteObjects =
                    new Builder()
                            .penaltyLog()
                            .detectActivityLeaks()
                            .detectLeakedClosableObjects()
                            .detectLeakedSqlLiteObjects();
            if (Build.VERSION.SDK_INT >= 16) {
                detectLeakedSqlLiteObjects = detectLeakedSqlLiteObjects.detectLeakedRegistrationObjects();
            }
            try {
                StrictMode.setVmPolicy(detectLeakedSqlLiteObjects.build());
            } catch(Exception err) {
                Logger.error(TAG, err);
            }
        }
    }

    /**
     * Делаем предварительную настройку приложения, настраиваем библиотеки для дальнейшей работы.
     */
    protected void init() {
        //Toasty Library
        Toasty.Config.getInstance()
                .setErrorColor(ContextCompat.getColor(this, R.color.toastyColor))
                .setSuccessColor(ContextCompat.getColor(this, R.color.toastyColor))
                .setToastTypeface(ResourcesCompat.getFont(this, R.font.main_sans))
                .apply(); //required
    }

    /*public static void doRestart(Context context) {
        Intent launchIntent = new Intent(context, QuantumInstance.class);
        PendingIntent intent = PendingIntent.getActivity(context, 0, launchIntent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, intent);
        System.exit(2);
    }*/
}
