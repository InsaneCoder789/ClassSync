package com.rochiee.classsync.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

class ReminderScheduler(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {
    private val alarmManager: AlarmManager? by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    }

    suspend fun schedule(task: AcademicTask) {
        if (task.id <= 0) return
        if (task.isCompleted || task.dueDate == null) {
            cancel(task)
            return
        }

        val settings = settingsRepository.observeSettings().first()
        if (!settings.notificationParsingEnabled) {
            cancel(task)
            return
        }
        val reminderAtMillis = task.dueDate - (settings.defaultReminderHours * 60L * 60L * 1000L)
        if (reminderAtMillis <= System.currentTimeMillis()) {
            cancel(task)
            return
        }

        val pendingIntent = createPendingIntent(task)
        alarmManager?.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderAtMillis,
            pendingIntent
        )
    }

    fun cancel(task: AcademicTask) {
        if (task.id <= 0) return
        val pendingIntent = createPendingIntent(task)
        alarmManager?.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun createPendingIntent(task: AcademicTask): PendingIntent {
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra(TaskReminderReceiver.EXTRA_TASK_ID, task.id)
            putExtra(TaskReminderReceiver.EXTRA_TASK_TITLE, task.title)
            putExtra(TaskReminderReceiver.EXTRA_TASK_COURSE, task.courseName)
        }

        return PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
