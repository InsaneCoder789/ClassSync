package com.rochiee.classsync.data.repository

import com.rochiee.classsync.data.local.dao.TaskDao
import com.rochiee.classsync.data.local.preferences.TaskSuppressionStore
import com.rochiee.classsync.data.local.mapper.toAcademicTask
import com.rochiee.classsync.data.local.mapper.toTaskEntity
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.reminder.ReminderScheduler
import com.rochiee.classsync.reminder.DueSoonNotificationHelper
import com.rochiee.classsync.taskengine.DuplicateTaskDetector
import com.rochiee.classsync.widget.ClassSyncWidgetUpdater
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val dao: TaskDao,
    private val reminderScheduler: ReminderScheduler,
    private val appContext: Context,
    private val suppressionStore: TaskSuppressionStore
) : TaskRepository {
    override fun observeTasks(): Flow<List<AcademicTask>> {
        return dao.getAllTasks().map { entities ->
            entities.map { it.toAcademicTask() }
        }
    }

    override suspend fun getTasksSnapshot(): List<AcademicTask> {
        return dao.getAllTasksSnapshot().map { it.toAcademicTask() }
    }

    override suspend fun addTask(task: AcademicTask) {
        if (TaskSuppressionStore.shouldCheckSuppression(task) && suppressionStore.isSuppressed(task)) {
            return
        }

        val existingTasks = dao.getAllTasksSnapshot().map { it.toAcademicTask() }
        val duplicate = DuplicateTaskDetector.findBestDuplicate(existingTasks, task)
        val taskWithTimestamps = task.copy(
            createdAtMillis = task.createdAtMillis.takeIf { it > 0 } ?: System.currentTimeMillis(),
            updatedAtMillis = System.currentTimeMillis()
        )

        if (duplicate != null) {
            val mergedTask = DuplicateTaskDetector.merge(duplicate, taskWithTimestamps)
            dao.updateTask(mergedTask.toTaskEntity())
            reminderScheduler.schedule(mergedTask)
            ClassSyncWidgetUpdater.updateAllWidgets(appContext)
            DueSoonNotificationHelper.refresh(appContext, dao.getAllTasksSnapshot().map { it.toAcademicTask() })
        } else {
            val insertedId = dao.insertTask(taskWithTimestamps.toTaskEntity()).toInt()
            reminderScheduler.schedule(taskWithTimestamps.copy(id = insertedId))
            ClassSyncWidgetUpdater.updateAllWidgets(appContext)
            DueSoonNotificationHelper.refresh(appContext, dao.getAllTasksSnapshot().map { it.toAcademicTask() })
        }
    }

    override suspend fun updateTask(task: AcademicTask) {
        val updatedTask = task.copy(updatedAtMillis = System.currentTimeMillis())
        dao.updateTask(updatedTask.toTaskEntity())
        reminderScheduler.schedule(updatedTask)
        ClassSyncWidgetUpdater.updateAllWidgets(appContext)
        DueSoonNotificationHelper.refresh(appContext, dao.getAllTasksSnapshot().map { it.toAcademicTask() })
    }

    override suspend fun deleteTask(task: AcademicTask) {
        reminderScheduler.cancel(task)
        if (TaskSuppressionStore.shouldSuppress(task)) {
            suppressionStore.suppress(task)
        }
        dao.deleteTask(task.toTaskEntity())
        ClassSyncWidgetUpdater.updateAllWidgets(appContext)
        DueSoonNotificationHelper.refresh(appContext, dao.getAllTasksSnapshot().map { it.toAcademicTask() })
    }

    override suspend fun getTaskById(id: Int): AcademicTask? {
        return dao.getTaskById(id)?.toAcademicTask()
    }
}
