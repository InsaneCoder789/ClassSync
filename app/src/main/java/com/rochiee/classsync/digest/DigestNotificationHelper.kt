package com.rochiee.classsync.digest

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.rochiee.classsync.R

object DigestNotificationHelper {
    const val CHANNEL_ID = "daily_digest"
    private const val CHANNEL_NAME = "Daily Digest"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Daily academic digest"
        }
        context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    fun showDigest(context: Context, summary: DigestSummary) {
        ensureChannel(context)
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val body = buildString {
            append("${summary.dueTodayCount} due today, ${summary.overdueCount} overdue")
            append(". ${summary.upcomingQuizExamCount} quizzes/exams ahead.")
            if (summary.latestAnnouncementTitles.isNotEmpty()) {
                append(" Latest announcement: ${summary.latestAnnouncementTitles.first()}.")
            }
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Today's ClassSync Digest")
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Today's ClassSync Digest")
                    .setContentText("Unlock ClassSync to view your academic digest.")
                    .build()
            )
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(4041, notification)
    }
}
