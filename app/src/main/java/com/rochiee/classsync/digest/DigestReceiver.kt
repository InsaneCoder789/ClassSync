package com.rochiee.classsync.digest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rochiee.classsync.ClassSyncApplication
import kotlinx.coroutines.runBlocking

class DigestReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as ClassSyncApplication
        runBlocking {
            val summary = app.container.generateDigestSummaryUseCase()
            DigestNotificationHelper.showDigest(context, summary)
        }
    }
}
