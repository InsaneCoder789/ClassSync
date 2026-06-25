package com.rochiee.classsync.widget

import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.repository.TaskRepository
import java.util.Calendar

class WidgetDataProvider(
    private val taskRepository: TaskRepository
) {
    suspend fun getOverdueTasks(): List<AcademicTask> {
        val now = System.currentTimeMillis()
        return taskRepository.getTasksSnapshot().filter { task ->
            !task.isCompleted && task.dueDate?.let { it < now } == true
        }.sortedBy { it.dueDate }
    }

    suspend fun getTodayTasks(): List<AcademicTask> {
        val bounds = todayBounds()
        return taskRepository.getTasksSnapshot().filter { task ->
            task.dueDate?.let { it in bounds.first..bounds.second } == true && !task.isCompleted
        }
    }

    suspend fun getUrgentTasks(): List<AcademicTask> {
        val now = System.currentTimeMillis()
        val urgentCutoff = now + 24L * 60L * 60L * 1000L
        return taskRepository.getTasksSnapshot().filter { task ->
            !task.isCompleted && task.dueDate?.let { it in now..urgentCutoff } == true
        }.sortedBy { it.dueDate }
    }

    suspend fun getNextUpcomingTask(): AcademicTask? {
        val now = System.currentTimeMillis()
        return taskRepository.getTasksSnapshot()
            .filter { !it.isCompleted && it.dueDate != null && it.dueDate >= now }
            .minByOrNull { it.dueDate ?: Long.MAX_VALUE }
    }

    suspend fun getWidgetSummary(): WidgetSummary {
        val todayTasks = getTodayTasks()
        val urgentTasks = getUrgentTasks()
        val overdueTasks = getOverdueTasks()
        val now = System.currentTimeMillis()
        val allDatedTasks = taskRepository.getTasksSnapshot()
            .filter { !it.isCompleted && it.dueDate != null }
            .sortedBy { it.dueDate }
        val upcomingTasks = allDatedTasks.filter { (it.dueDate ?: 0L) >= now }
        val redZoneTasks = allDatedTasks.filter { task ->
            task.dueDate?.let { dueMillis -> isRedZone(dueMillis, now) } == true
        }
        val primaryTask = redZoneTasks.firstOrNull() ?: upcomingTasks.firstOrNull()
        return WidgetSummary(
            todayTaskCount = todayTasks.size,
            urgentTaskCount = urgentTasks.size,
            overdueTaskCount = overdueTasks.size,
            primaryTaskTitle = primaryTask?.title,
            primaryTaskCourseName = primaryTask?.courseName,
            primaryTaskDueMillis = primaryTask?.dueDate,
            redZoneOverflowCount = (redZoneTasks.size - 1).coerceAtLeast(0)
        )
    }

    private fun isRedZone(dueMillis: Long, now: Long): Boolean {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val dayAfterTomorrowStart = calendar.timeInMillis + (2L * 24L * 60L * 60L * 1000L)
        return dueMillis < dayAfterTomorrowStart
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
