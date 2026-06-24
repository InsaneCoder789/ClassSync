package com.rochiee.classsync.domain.usecase.digest

import com.rochiee.classsync.digest.DigestScheduler

class CancelDailyDigestUseCase(
    private val digestScheduler: DigestScheduler
) {
    operator fun invoke() {
        digestScheduler.cancel()
    }
}
