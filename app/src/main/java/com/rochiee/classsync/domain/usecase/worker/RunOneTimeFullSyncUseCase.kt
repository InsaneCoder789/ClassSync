package com.rochiee.classsync.domain.usecase.worker

import android.content.Context
import com.rochiee.classsync.worker.WorkScheduler

class RunOneTimeFullSyncUseCase(
    private val context: Context
) {
    operator fun invoke() {
        WorkScheduler.runOneTimeFullSync(context)
    }
}
