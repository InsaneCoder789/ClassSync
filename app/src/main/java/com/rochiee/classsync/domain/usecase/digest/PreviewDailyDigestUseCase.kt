package com.rochiee.classsync.domain.usecase.digest

import android.content.Context
import com.rochiee.classsync.digest.DigestNotificationHelper

class PreviewDailyDigestUseCase(
    private val context: Context,
    private val generateDigestSummaryUseCase: GenerateDigestSummaryUseCase
) {
    suspend operator fun invoke() {
        val summary = generateDigestSummaryUseCase()
        DigestNotificationHelper.showDigest(context, summary)
    }
}
