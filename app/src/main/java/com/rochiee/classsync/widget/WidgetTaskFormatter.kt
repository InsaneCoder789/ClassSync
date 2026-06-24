package com.rochiee.classsync.widget

import java.text.DateFormat
import java.util.Date

class WidgetTaskFormatter {
    enum class WidgetDeadlineTone {
        OVERDUE,
        TODAY,
        TOMORROW,
        SOON,
        NORMAL,
        NONE
    }

    fun dueText(dueMillis: Long?): String {
        return if (dueMillis == null) {
            "No due time"
        } else {
            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(dueMillis))
        }
    }

    fun compactDueText(dueMillis: Long?): String {
        return if (dueMillis == null) {
            "No due date"
        } else {
            DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(dueMillis))
        }
    }

    fun relativeSummary(todayCount: Int, urgentCount: Int): String {
        return when {
            urgentCount > 0 -> "$urgentCount urgent task${if (urgentCount == 1) "" else "s"} need attention"
            todayCount > 0 -> "$todayCount task${if (todayCount == 1) "" else "s"} due today"
            else -> "All clear for now"
        }
    }

    fun focusText(taskTitle: String?, dueMillis: Long?): String {
        return if (taskTitle == null) {
            "No upcoming tasks"
        } else {
            "$taskTitle • ${compactDueText(dueMillis)}"
        }
    }

    fun updatedText(updatedAtMillis: Long): String {
        return "Updated ${DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(updatedAtMillis))}"
    }

    fun deadlineTone(dueMillis: Long?, nowMillis: Long = System.currentTimeMillis()): WidgetDeadlineTone {
        if (dueMillis == null) return WidgetDeadlineTone.NONE
        val todayStart = java.util.Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
        val tomorrowStart = todayStart + 24L * 60L * 60L * 1000L
        val dayAfterTomorrowStart = tomorrowStart + 24L * 60L * 60L * 1000L
        return when {
            dueMillis < nowMillis -> WidgetDeadlineTone.OVERDUE
            dueMillis < tomorrowStart -> WidgetDeadlineTone.TODAY
            dueMillis < dayAfterTomorrowStart -> WidgetDeadlineTone.TOMORROW
            dueMillis < nowMillis + 3L * 24L * 60L * 60L * 1000L -> WidgetDeadlineTone.SOON
            else -> WidgetDeadlineTone.NORMAL
        }
    }
}
