package com.rochiee.classsync.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rochiee.classsync.ClassSyncApplication
import com.rochiee.classsync.domain.model.SyncLog
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class FullSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = coroutineScope {
        val app = applicationContext as ClassSyncApplication
        val googleAuthManager = app.container.googleAuthManager
        val syncLogRepository = app.container.syncLogRepository
        val syncGmailTasksUseCase = app.container.syncGmailTasksUseCase
        val syncClassroomCourseworkUseCase = app.container.syncClassroomCourseworkUseCase

        if (!googleAuthManager.isSignedIn()) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "FULL_SYNC_WORKER",
                    status = "SKIPPED",
                    message = "Skipped full sync because the user is not signed in.",
                    timestamp = System.currentTimeMillis()
                )
            )
            return@coroutineScope Result.success()
        }

        try {
            val gmailJob = async { syncGmailTasksUseCase() }
            val classroomJob = async { syncClassroomCourseworkUseCase() }

            gmailJob.await()
            classroomJob.await()

            syncLogRepository.addLog(
                SyncLog(
                    source = "FULL_SYNC_WORKER",
                    status = "SUCCESS",
                    message = "Background full sync completed.",
                    timestamp = System.currentTimeMillis()
                )
            )
            Result.success()
        } catch (e: Exception) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "FULL_SYNC_WORKER",
                    status = "ERROR",
                    message = e.message ?: "Background full sync failed.",
                    timestamp = System.currentTimeMillis()
                )
            )
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
