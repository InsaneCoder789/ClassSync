package com.rochiee.classsync.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rochiee.classsync.widget.ClassSyncWidgetUpdater

class WidgetRefreshWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        ClassSyncWidgetUpdater.updateAllWidgets(applicationContext)
        return Result.success()
    }
}
