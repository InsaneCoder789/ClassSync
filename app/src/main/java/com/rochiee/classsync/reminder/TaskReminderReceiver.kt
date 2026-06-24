package com.rochiee.classsync.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra(EXTRA_TASK_ID, 0)
        val title = intent.getStringExtra(EXTRA_TASK_TITLE).orEmpty()
        val courseName = intent.getStringExtra(EXTRA_TASK_COURSE).orEmpty()

        val body = if (courseName.isBlank()) {
            "Task reminder: $title"
        } else {
            "Reminder for $courseName: $title"
        }

        ReminderNotificationHelper.showReminderNotification(
            context = context,
            notificationId = taskId,
            title = title.ifBlank { "Task Reminder" },
            message = body
        )
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_TASK_COURSE = "extra_task_course"
    }
}
