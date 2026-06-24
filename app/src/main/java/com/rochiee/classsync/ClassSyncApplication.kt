package com.rochiee.classsync

import android.app.Application
import com.rochiee.classsync.di.AppContainer
import com.rochiee.classsync.di.AppContainerImpl
import com.rochiee.classsync.digest.DigestNotificationHelper
import com.rochiee.classsync.reminder.ReminderNotificationHelper
import com.rochiee.classsync.worker.WorkScheduler

class ClassSyncApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
        ReminderNotificationHelper.ensureChannel(this)
        DigestNotificationHelper.ensureChannel(this)
        WorkScheduler.scheduleWidgetRefresh(this)
    }
}
