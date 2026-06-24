package com.rochiee.classsync.dashboard

import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.SyncLog
import java.util.Calendar

class DashboardAggregator {
    fun buildSummary(
        tasks: List<AcademicTask>,
        events: List<ClassroomEvent>,
        syncLogs: List<SyncLog>
    ): DashboardSummary {
        val todayWindow = todayWindow()
        val now = System.currentTimeMillis()

        return DashboardSummary(
            todayTaskCount = tasks.count { task -> task.dueDate?.let { it in todayWindow.first..todayWindow.second } == true },
            upcomingTaskCount = tasks.count { task -> task.dueDate?.let { it >= now } == true && !task.isCompleted },
            overdueTaskCount = tasks.count { task -> task.dueDate?.let { it < now } == true && !task.isCompleted },
            announcementCount = events.count { it.eventType == ClassroomEventType.ANNOUNCEMENT },
            materialCount = events.count { it.eventType == ClassroomEventType.MATERIAL },
            quizCount = events.count { it.eventType == ClassroomEventType.QUIZ },
            examCount = events.count { it.eventType == ClassroomEventType.EXAM },
            recentEventCount = events.size,
            lastSyncMillis = syncLogs.maxOfOrNull { it.timestamp }
        )
    }

    private fun todayWindow(): Pair<Long, Long> {
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
