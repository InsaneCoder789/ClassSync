package com.rochiee.classsync.domain.repository

import com.rochiee.classsync.domain.model.AcademicTask
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<AcademicTask>>
    suspend fun getTasksSnapshot(): List<AcademicTask>
    suspend fun addTask(task: AcademicTask)
    suspend fun updateTask(task: AcademicTask)
    suspend fun deleteTask(task: AcademicTask)
    suspend fun getTaskById(id: Int): AcademicTask?
}
