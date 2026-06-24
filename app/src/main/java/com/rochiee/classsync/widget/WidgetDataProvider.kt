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
        val upcomingTasks = taskRepository.getTasksSnapshot()
            .filter { !it.isCompleted && it.dueDate != null && it.dueDate >= System.currentTimeMillis() }
            .sortedBy { it.dueDate }
        val nextTask = upcomingTasks.getOrNull(0)
        val secondTask = upcomingTasks.getOrNull(1)
        return WidgetSummary(
            todayTaskCount = todayTasks.size,
            urgentTaskCount = urgentTasks.size,
            overdueTaskCount = overdueTasks.size,
            nextTaskTitle = nextTask?.title,
            nextTaskCourseName = nextTask?.courseName,
            nextTaskDueMillis = nextTask?.dueDate,
            secondTaskTitle = secondTask?.title,
            secondTaskDueMillis = secondTask?.dueDate,
            lastUpdatedMillis = System.currentTimeMillis()
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
