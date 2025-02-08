/*
 * Copyright 2025 Squircle CE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.core.extensions

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService

fun Context.createChannel(
    channelId: String,
    @StringRes channelName: Int,
    @StringRes channelDescription: Int,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationManager = applicationContext.getSystemService<NotificationManager>()
        val name = applicationContext.getString(channelName)
        val description = applicationContext.getString(channelDescription)
        val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT)
            .also { it.description = description }

        notificationManager?.createNotificationChannel(channel)
    }
}

fun Context.buildNotification(
    channelId: String,
    notificationTitle: String? = null,
    notificationMessage: String? = null,
    pendingIntent: PendingIntent? = null,
    @DrawableRes smallIcon: Int? = null,
    priority: Int? = null,
    progress: Int = -1,
    progressMax: Int = -1,
    indeterminate: Boolean? = null,
    autoCancel: Boolean? = null,
    ongoing: Boolean? = null,
    silent: Boolean? = null,
    actions: List<NotificationCompat.Action>? = null,
): Notification {
    val notificationBuilder = NotificationCompat.Builder(this, channelId)

    notificationTitle?.let(notificationBuilder::setContentTitle)
    notificationMessage?.let(notificationBuilder::setContentText)
    pendingIntent?.let(notificationBuilder::setContentIntent)
    smallIcon?.let(notificationBuilder::setSmallIcon)
    priority?.let(notificationBuilder::setPriority)
    indeterminate?.let { notificationBuilder.setProgress(progressMax, progress, indeterminate) }
    autoCancel?.let(notificationBuilder::setAutoCancel)
    ongoing?.let(notificationBuilder::setOngoing)
    silent?.let(notificationBuilder::setSilent)
    actions?.forEach(notificationBuilder::addAction)

    return notificationBuilder.build()
}