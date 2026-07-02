package com.rochiee.classsync

import android.app.Application
import com.rochiee.classsync.di.AppContainer
import com.rochiee.classsync.di.AppContainerImpl
import com.rochiee.classsync.digest.DigestNotificationHelper
import com.rochiee.classsync.reminder.DueSoonNotificationHelper
import com.rochiee.classsync.reminder.ReminderNotificationHelper
import com.rochiee.classsync.worker.WorkScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ClassSyncApplication : Application() {
    lateinit var container: AppContainer
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
        ReminderNotificationHelper.ensureChannel(this)
        DueSoonNotificationHelper.ensureChannel(this)
        DigestNotificationHelper.ensureChannel(this)
        WorkScheduler.scheduleWidgetRefresh(this)
        WorkScheduler.scheduleDueSoonNotificationRefresh(this)
        WorkScheduler.runOneTimeDueSoonNotificationRefresh(this)
        applicationScope.launch {
            container.scheduleBackgroundSyncUseCase()
        }
    }
}
