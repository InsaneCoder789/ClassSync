package com.rochiee.classsync.domain.usecase.auth

import com.rochiee.classsync.data.local.preferences.TaskSuppressionStore
import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.repository.ClassroomRepository
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.domain.repository.SyncLogRepository
import com.rochiee.classsync.domain.repository.TaskRepository

class ClearLocalAcademicDataUseCase(
    private val taskRepository: TaskRepository,
    private val classroomEventRepository: ClassroomEventRepository,
    private val classroomRepository: ClassroomRepository,
    private val syncLogRepository: SyncLogRepository,
    private val settingsRepository: SettingsRepository,
    private val taskSuppressionStore: TaskSuppressionStore
) {
    suspend operator fun invoke() {
        taskRepository.clearAllTasks()
        classroomEventRepository.clearAllEvents()
        classroomRepository.clearLocalCourses()
        syncLogRepository.clearLogs()
        settingsRepository.setPersistedStudyPlanJson(null)
        settingsRepository.setPersistedExamChecklistJson(null)
        taskSuppressionStore.clear()
    }
}
