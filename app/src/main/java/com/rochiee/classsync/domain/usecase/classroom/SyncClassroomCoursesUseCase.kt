package com.rochiee.classsync.domain.usecase.classroom

import com.rochiee.classsync.data.local.entity.CourseEntity
import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.domain.repository.ClassroomRepository
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.domain.repository.SyncLogRepository
import com.rochiee.classsync.domain.usecase.settings.SetLastSyncTimeUseCase
import kotlinx.coroutines.flow.first

class SyncClassroomCoursesUseCase(
    private val classroomRepository: ClassroomRepository,
    private val syncLogRepository: SyncLogRepository,
    private val settingsRepository: SettingsRepository,
    private val setLastSyncTimeUseCase: SetLastSyncTimeUseCase
) {
    suspend operator fun invoke() {
        try {
            if (!settingsRepository.observeSettings().first().classroomSyncEnabled) {
                syncLogRepository.addLog(
                    SyncLog(
                        source = "CLASSROOM_COURSES",
                        status = "SKIPPED",
                        message = "Skipped Classroom course sync because Classroom sync is disabled in settings.",
                        timestamp = System.currentTimeMillis()
                    )
                )
                return
            }
            val remoteCourses = classroomRepository.fetchRemoteCourses()
            val entities = remoteCourses.map { dto ->
                CourseEntity(
                    courseId = dto.courseId,
                    name = dto.name,
                    section = dto.section,
                    room = dto.room,
                    descriptionHeading = dto.descriptionHeading,
                    teacherName = dto.teacherName,
                    courseState = dto.courseState
                )
            }
            classroomRepository.saveCourses(entities)
            syncLogRepository.addLog(
                SyncLog(
                    source = "CLASSROOM_COURSES",
                    status = "SUCCESS",
                    message = "Saved ${entities.size} active courses.",
                    timestamp = System.currentTimeMillis()
                )
            )
            setLastSyncTimeUseCase(System.currentTimeMillis())
        } catch (error: Exception) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "CLASSROOM_COURSES",
                    status = "ERROR",
                    message = error.message ?: "Classroom course sync failed.",
                    timestamp = System.currentTimeMillis()
                )
            )
            throw error
        }
    }
}
