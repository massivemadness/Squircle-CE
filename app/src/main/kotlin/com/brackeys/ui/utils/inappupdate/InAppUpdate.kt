package com.brackeys.ui.utils.inappupdate

import android.app.Activity

interface InAppUpdate {
    fun checkForUpdates(activity: Activity, onComplete: () -> Unit)
    fun completeUpdate()
}