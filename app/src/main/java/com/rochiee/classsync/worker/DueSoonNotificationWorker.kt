package com.rochiee.classsync.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rochiee.classsync.ClassSyncApplication
import com.rochiee.classsync.reminder.DueSoonNotificationHelper

class DueSoonNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val app = applicationContext as ClassSyncApplication
        val tasks = app.container.taskRepository.getTasksSnapshot()
        DueSoonNotificationHelper.refresh(applicationContext, tasks)
        return Result.success()
    }
}
