package com.rochiee.classsync.reminder

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
import com.rochiee.classsync.domain.model.AcademicTask
import java.text.DateFormat
import java.util.Date
import kotlin.math.abs

object DueSoonNotificationHelper {
    const val CHANNEL_ID = "due_soon_monitor"
    private const val CHANNEL_NAME = "Due Soon Monitor"
    private const val CHANNEL_DESCRIPTION = "Ongoing reminders for assignments due around today and tomorrow"
    private const val NOTIFICATION_ID = 88021
    private const val DAY_IN_MILLIS = 24L * 60L * 60L * 1000L

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_DESCRIPTION
        }

        context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    fun refresh(context: Context, tasks: List<AcademicTask>) {
        ensureChannel(context)
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val candidateTasks = tasks
            .filter { !it.isCompleted && it.dueDate != null }
            .filter { task -> abs((task.dueDate ?: 0L) - System.currentTimeMillis()) <= DAY_IN_MILLIS }
            .sortedBy { it.dueDate }

        if (candidateTasks.isEmpty()) {
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
            return
        }

        val nextTask = candidateTasks.first()
        val count = candidateTasks.size
        val dueText = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
            .format(Date(nextTask.dueDate ?: System.currentTimeMillis()))
        val body = if (count == 1) {
            "${nextTask.title} for ${nextTask.courseName} is due around $dueText."
        } else {
            "$count assignments need attention. Next up: ${nextTask.title} at $dueText."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Assignments due soon")
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Assignments due soon")
                    .setContentText("Unlock ClassSync to view the affected coursework.")
                    .build()
            )
            .setOngoing(true)
            .setOnlyAlertOnce(false)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}
