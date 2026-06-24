package com.rochiee.classsync.planner

import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.model.TaskPriority
import java.util.Calendar

class PlannerAggregator {
    fun buildTodayPlanner(
        tasks: List<AcademicTask>,
        events: List<ClassroomEvent>,
        filter: PlannerFilter = PlannerFilter(),
        nowMillis: Long = System.currentTimeMillis()
    ): PlannerDay {
        val bounds = dayBounds(nowMillis)
        return buildPlannerDay(tasks, events, bounds.first, bounds.second, filter, nowMillis)
    }

    fun buildWeekPlanner(
        tasks: List<AcademicTask>,
        events: List<ClassroomEvent>,
        filter: PlannerFilter = PlannerFilter(),
        nowMillis: Long = System.currentTimeMillis()
    ): PlannerWeek {
        val calendar = calendarAt(nowMillis)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val weekStart = dayBounds(calendar.timeInMillis).first
        val days = (0 until 7).map { offset ->
            val dayStart = weekStart + offset * DAY_MILLIS
            val dayEnd = dayStart + DAY_MILLIS - 1
            buildPlannerDay(tasks, events, dayStart, dayEnd, filter, nowMillis)
        }
        val weekEnd = days.last().dateEndMillis
        return PlannerWeek(
            weekStartMillis = weekStart,
            weekEndMillis = weekEnd,
            days = days,
            totalTaskCount = days.sumOf { it.tasks.size },
            completedTaskCount = days.sumOf { day -> day.tasks.count { it.isCompleted } },
            overdueTaskCount = days.sumOf { day ->
                day.dueItems.count { item -> !item.isCompleted && item.dueDateMillis?.let { it < nowMillis } == true }
            },
            quizExamCount = days.sumOf { day ->
                day.events.count { it.itemType == PlannerItemType.QUIZ || it.itemType == PlannerItemType.EXAM }
            }
        )
    }

    fun buildMonthPlanner(
        tasks: List<AcademicTask>,
        events: List<ClassroomEvent>,
        filter: PlannerFilter = PlannerFilter(),
        nowMillis: Long = System.currentTimeMillis()
    ): PlannerMonth {
        val calendar = calendarAt(nowMillis)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val monthStart = dayBounds(calendar.timeInMillis).first
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val monthEnd = calendar.timeInMillis

        val weeks = mutableListOf<PlannerWeek>()
        var cursor = monthStart
        while (cursor <= monthEnd) {
            weeks += buildWeekPlanner(tasks, events, filter, cursor)
            cursor += 7 * DAY_MILLIS
        }

        val monthItems = weeks.flatMap { it.days }.flatMap { it.tasks + it.events }
        return PlannerMonth(
            monthStartMillis = monthStart,
            monthEndMillis = monthEnd,
            weeks = weeks,
            totalTaskCount = weeks.sumOf { it.totalTaskCount },
            completedTaskCount = weeks.sumOf { it.completedTaskCount },
            overdueTaskCount = weeks.sumOf { it.overdueTaskCount },
            announcementCount = monthItems.count { it.itemType == PlannerItemType.ANNOUNCEMENT },
            materialCount = monthItems.count { it.itemType == PlannerItemType.MATERIAL },
            quizExamCount = monthItems.count { it.itemType == PlannerItemType.QUIZ || it.itemType == PlannerItemType.EXAM }
        )
    }

    fun buildRangePlanner(
        tasks: List<AcademicTask>,
        events: List<ClassroomEvent>,
        startMillis: Long,
        endMillis: Long,
        filter: PlannerFilter = PlannerFilter(),
        nowMillis: Long = System.currentTimeMillis()
    ): List<PlannerDay> {
        val start = dayBounds(startMillis).first
        val end = dayBounds(endMillis).second
        val result = mutableListOf<PlannerDay>()
        var cursor = start
        while (cursor <= end) {
            result += buildPlannerDay(tasks, events, cursor, cursor + DAY_MILLIS - 1, filter, nowMillis)
            cursor += DAY_MILLIS
        }
        return result
    }

    private fun buildPlannerDay(
        tasks: List<AcademicTask>,
        events: List<ClassroomEvent>,
        dayStart: Long,
        dayEnd: Long,
        filter: PlannerFilter,
        nowMillis: Long
    ): PlannerDay {
        val taskItems = tasks.mapNotNull { task -> task.toPlannerItem() }
        val eventItems = events.mapNotNull { event -> event.toPlannerItem() }
        val items = (taskItems + eventItems)
            .filter { item -> item.dateMillis in dayStart..dayEnd || item.dueDateMillis?.let { it in dayStart..dayEnd } == true }
            .filter { item -> includeItem(item, filter) }

        val tasksForDay = items.filter { it.itemType == PlannerItemType.TASK || it.itemType in setOf(PlannerItemType.ASSIGNMENT, PlannerItemType.COURSEWORK, PlannerItemType.QUIZ, PlannerItemType.EXAM) }
        val eventsForDay = items - tasksForDay.toSet()
        val dueItems = items.filter { it.dueDateMillis?.let { due -> due in dayStart..dayEnd || (!it.isCompleted && due < nowMillis) } == true }
        val highPriorityItems = items.filter { it.priority == TaskPriority.HIGH || it.priority == TaskPriority.URGENT }

        return PlannerDay(
            dateStartMillis = dayStart,
            dateEndMillis = dayEnd,
            tasks = tasksForDay.sortedBy { it.dueDateMillis ?: it.dateMillis },
            events = eventsForDay.sortedBy { it.dateMillis },
            dueItems = dueItems.sortedBy { it.dueDateMillis ?: it.dateMillis },
            highPriorityItems = highPriorityItems.sortedByDescending { it.priority.score }
        )
    }

    private fun includeItem(item: PlannerItem, filter: PlannerFilter): Boolean {
        if (!filter.showCompleted && item.isCompleted) return false
        if (filter.courseId != null && filter.courseId != item.courseId) return false
        return when (item.itemType) {
            PlannerItemType.TASK, PlannerItemType.COURSEWORK -> filter.showTasks
            PlannerItemType.ASSIGNMENT -> filter.showAssignments
            PlannerItemType.QUIZ -> filter.showQuizzes
            PlannerItemType.EXAM -> filter.showExams
            PlannerItemType.ANNOUNCEMENT -> filter.showAnnouncements
            PlannerItemType.MATERIAL -> filter.showMaterials
            else -> true
        }
    }

    private fun AcademicTask.toPlannerItem(): PlannerItem? {
        val date = dueDate ?: createdAtMillis
        return PlannerItem(
            id = "task_$id",
            title = title,
            description = description,
            courseName = courseName,
            itemType = PlannerItemType.TASK,
            sourceId = sourceId,
            sourceType = source,
            dateMillis = date,
            dueDateMillis = dueDate,
            priority = TaskPriority.fromScore(priority),
            isCompleted = isCompleted,
            originalLink = sourceLink
        )
    }

    private fun ClassroomEvent.toPlannerItem(): PlannerItem? {
        return PlannerItem(
            id = id,
            title = title,
            description = description,
            courseId = courseId,
            courseName = courseName,
            itemType = eventType.toPlannerItemType(),
            sourceId = sourceId,
            sourceType = source.name,
            dateMillis = dueDateMillis ?: eventTimeMillis,
            dueDateMillis = dueDateMillis,
            priority = priority,
            isCompleted = convertedToTask,
            originalLink = originalLink
        )
    }

    private fun ClassroomEventType.toPlannerItemType(): PlannerItemType {
        return when (this) {
            ClassroomEventType.ASSIGNMENT -> PlannerItemType.ASSIGNMENT
            ClassroomEventType.COURSEWORK -> PlannerItemType.COURSEWORK
            ClassroomEventType.QUIZ -> PlannerItemType.QUIZ
            ClassroomEventType.EXAM -> PlannerItemType.EXAM
            ClassroomEventType.ANNOUNCEMENT -> PlannerItemType.ANNOUNCEMENT
            ClassroomEventType.MATERIAL -> PlannerItemType.MATERIAL
            ClassroomEventType.REMINDER -> PlannerItemType.REMINDER
            ClassroomEventType.COMMENT,
            ClassroomEventType.TEACHER_FEEDBACK -> PlannerItemType.COMMENT
            ClassroomEventType.GRADE_UPDATE -> PlannerItemType.GRADE_UPDATE
            else -> PlannerItemType.UNKNOWN
        }
    }

    private fun dayBounds(timeMillis: Long): Pair<Long, Long> {
        val calendar = calendarAt(timeMillis)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        return start to (start + DAY_MILLIS - 1)
    }

    private fun calendarAt(timeMillis: Long): Calendar = Calendar.getInstance().apply { timeInMillis = timeMillis }

    companion object {
        private const val DAY_MILLIS = 24L * 60L * 60L * 1000L
    }
}
