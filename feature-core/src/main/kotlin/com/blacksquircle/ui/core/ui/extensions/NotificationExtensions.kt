package com.blacksquircle.ui.core.ui.extensions

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
    @StringRes channelDescription: Int
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

fun Context.createNotification(
    channelId: String,
    notificationTitle: String? = null,
    notificationMessage: String? = null,
    pendingIntent: PendingIntent? = null,
    @DrawableRes smallIcon: Int? = null,
    priority: Int? = null,
    indeterminate: Boolean? = null,
    autoCancel: Boolean? = null,
    ongoing: Boolean? = null,
    silent: Boolean? = null,
    actions: List<NotificationCompat.Action>? = null
): Notification {
    val notificationBuilder = NotificationCompat.Builder(this, channelId)

    notificationTitle?.let(notificationBuilder::setContentTitle)
    notificationMessage?.let(notificationBuilder::setContentText)
    pendingIntent?.let(notificationBuilder::setContentIntent)
    smallIcon?.let(notificationBuilder::setSmallIcon)
    priority?.let(notificationBuilder::setPriority)
    indeterminate?.let { notificationBuilder.setProgress(-1, -1, indeterminate) }
    autoCancel?.let(notificationBuilder::setAutoCancel)
    ongoing?.let(notificationBuilder::setOngoing)
    silent?.let(notificationBuilder::setSilent)
    actions?.forEach(notificationBuilder::addAction)

    return notificationBuilder.build()
}