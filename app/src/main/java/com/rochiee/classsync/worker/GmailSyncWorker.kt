package com.rochiee.classsync.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rochiee.classsync.ClassSyncApplication
import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.domain.sync.SyncRetryPolicy

class GmailSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as ClassSyncApplication
        val googleAuthManager = app.container.googleAuthManager
        val syncLogRepository = app.container.syncLogRepository
        val syncGmailTasksUseCase = app.container.syncGmailTasksUseCase

        googleAuthManager.checkAuthState()
        if (!googleAuthManager.isSignedIn()) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "GMAIL_WORKER",
                    status = "SKIPPED",
                    message = "Skipped Gmail sync because no Google account is remembered in ClassSync.",
                    timestamp = System.currentTimeMillis()
                )
            )
            return Result.success()
        }

        return try {
            syncGmailTasksUseCase()
            syncLogRepository.addLog(
                SyncLog(
                    source = "GMAIL_WORKER",
                    status = "SUCCESS",
                    message = "Background Gmail sync completed.",
                    timestamp = System.currentTimeMillis()
                )
            )
            Result.success()
        } catch (e: Exception) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "GMAIL_WORKER",
                    status = "ERROR",
                    message = e.message ?: "Background Gmail sync failed.",
                    timestamp = System.currentTimeMillis()
                )
            )
            if (SyncRetryPolicy.shouldRetryInBackground(e) || runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
