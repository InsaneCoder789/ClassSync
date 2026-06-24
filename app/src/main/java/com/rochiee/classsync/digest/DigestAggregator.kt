package com.rochiee.classsync.digest

import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.model.SettingsPreferences
import com.rochiee.classsync.domain.model.SyncLog
import java.util.Calendar

class DigestAggregator {
    fun buildSummary(
        tasks: List<AcademicTask>,
        events: List<ClassroomEvent>,
        latestSyncLog: SyncLog?,
        settings: SettingsPreferences
    ): DigestSummary {
        val now = System.currentTimeMillis()
        val today = todayBounds()
        val dueToday = tasks.count { !it.isCompleted && (it.dueDate ?: Long.MIN_VALUE) in today.first..today.second }
        val overdue = tasks.count { !it.isCompleted && (it.dueDate ?: Long.MAX_VALUE) < now }
        val upcomingQuizzesExams = events.count {
            (it.eventType == ClassroomEventType.QUIZ || it.eventType == ClassroomEventType.EXAM) &&
                (it.dueDateMillis ?: Long.MAX_VALUE) >= now
        }
        val latestAnnouncements = if (settings.digestIncludeAnnouncements) {
            events.filter { it.eventType == ClassroomEventType.ANNOUNCEMENT }.take(3).map { it.title }
        } else {
            emptyList()
        }
        val importantMaterials = if (settings.digestIncludeMaterials) {
            events.filter { it.eventType == ClassroomEventType.MATERIAL }.take(3).map { it.title }
        } else {
            emptyList()
        }

        return DigestSummary(
            dueTodayCount = dueToday,
            overdueCount = overdue,
            upcomingQuizExamCount = upcomingQuizzesExams,
            latestAnnouncementTitles = latestAnnouncements,
            importantMaterialTitles = importantMaterials,
            syncStatus = latestSyncLog?.let { "${it.source}: ${it.status}" } ?: "No sync history yet"
        )
    }

    private fun todayBounds(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        return start to calendar.timeInMillis
    }
}
