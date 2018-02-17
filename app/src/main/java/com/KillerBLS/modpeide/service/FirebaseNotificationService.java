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

package com.KillerBLS.modpeide.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.KillerBLS.modpeide.R;
import com.KillerBLS.modpeide.utils.Wrapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseNotificationService extends FirebaseMessagingService {

    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "FIREBASE_CHANNEL";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage.getNotification().getBody());
    }

    public void sendNotification(String messageBody) {
        if(new Wrapper(this).getPushNotifications()) { //Если уведомления включены
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            //Для Android O нужно создавать Notification Channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel =
                        new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                                getString(R.string.notifications_channel),
                                NotificationManager.IMPORTANCE_LOW);

                // Configure the notification channel.
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(R.color.purple);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                if (notificationManager != null)
                    notificationManager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                            .setLargeIcon(
                                    BitmapFactory.decodeResource(
                                            getResources(), R.mipmap.ic_launcher))
                            .setSmallIcon(R.drawable.ic_notification_default)
                            .setContentTitle(getString(R.string.pref_aboutSoftware_title))
                            .setContentText(messageBody)
                            .setColor(getResources().getColor(R.color.purple))
                            .setAutoCancel(true)
                            .setVibrate(new long[]{0, 100, 100, 100})
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));

            assert notificationManager != null;
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }
}
