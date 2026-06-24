package com.rochiee.classsync.domain.usecase.worker

import android.content.Context
import com.rochiee.classsync.worker.WorkScheduler

class CancelBackgroundSyncUseCase(
    private val context: Context
) {
    operator fun invoke() {
        WorkScheduler.cancelAll(context)
    }
}
