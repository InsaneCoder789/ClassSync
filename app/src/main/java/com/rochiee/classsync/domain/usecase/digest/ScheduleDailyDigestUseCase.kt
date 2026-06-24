package com.rochiee.classsync.domain.usecase.digest

import com.rochiee.classsync.digest.DigestScheduler

class ScheduleDailyDigestUseCase(
    private val digestScheduler: DigestScheduler
) {
    operator fun invoke(hourOfDay: Int) {
        digestScheduler.schedule(hourOfDay)
    }
}
