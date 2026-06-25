package com.rochiee.classsync.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkScheduler {
    const val GMAIL_SYNC_WORK = "GMAIL_SYNC_WORK"
    const val CLASSROOM_SYNC_WORK = "CLASSROOM_SYNC_WORK"
    const val FULL_SYNC_WORK = "FULL_SYNC_WORK"
    const val WIDGET_REFRESH_WORK = "WIDGET_REFRESH_WORK"

    fun scheduleGmailSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<GmailSyncWorker>(3, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            GMAIL_SYNC_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun scheduleClassroomSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<ClassroomSyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            CLASSROOM_SYNC_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun scheduleFullSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<FullSyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            FULL_SYNC_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun scheduleAll(context: Context) {
        scheduleGmailSync(context)
        scheduleClassroomSync(context)
        scheduleFullSync(context)
        scheduleWidgetRefresh(context)
    }

    fun scheduleWidgetRefresh(context: Context) {
        val request = PeriodicWorkRequestBuilder<WidgetRefreshWorker>(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WIDGET_REFRESH_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancelAll(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(GMAIL_SYNC_WORK)
        workManager.cancelUniqueWork(CLASSROOM_SYNC_WORK)
        workManager.cancelUniqueWork(FULL_SYNC_WORK)
        workManager.cancelUniqueWork(WIDGET_REFRESH_WORK)
    }

    fun runOneTimeFullSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<FullSyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }
}
