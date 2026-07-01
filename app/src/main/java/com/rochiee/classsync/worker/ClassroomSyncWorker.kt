package com.rochiee.classsync.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rochiee.classsync.ClassSyncApplication
import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.domain.sync.SyncRetryPolicy

class ClassroomSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as ClassSyncApplication
        val googleAuthManager = app.container.googleAuthManager
        val syncLogRepository = app.container.syncLogRepository
        val syncClassroomCourseworkUseCase = app.container.syncClassroomCourseworkUseCase

        if (!googleAuthManager.isSignedIn()) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "CLASSROOM_WORKER",
                    status = "SKIPPED",
                    message = "Skipped Classroom sync because the user is not signed in.",
                    timestamp = System.currentTimeMillis()
                )
            )
            return Result.success()
        }

        return try {
            syncClassroomCourseworkUseCase()
            syncLogRepository.addLog(
                SyncLog(
                    source = "CLASSROOM_WORKER",
                    status = "SUCCESS",
                    message = "Background Classroom sync completed.",
                    timestamp = System.currentTimeMillis()
                )
            )
            Result.success()
        } catch (e: Exception) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "CLASSROOM_WORKER",
                    status = "ERROR",
                    message = e.message ?: "Background Classroom sync failed.",
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
