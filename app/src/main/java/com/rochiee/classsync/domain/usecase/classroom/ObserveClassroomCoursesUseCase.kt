package com.rochiee.classsync.domain.usecase.classroom

import com.rochiee.classsync.data.local.entity.CourseEntity
import com.rochiee.classsync.domain.repository.ClassroomRepository
import kotlinx.coroutines.flow.Flow

class ObserveClassroomCoursesUseCase(
    private val classroomRepository: ClassroomRepository
) {
    operator fun invoke(): Flow<List<CourseEntity>> {
        return classroomRepository.observeLocalCourses()
    }
}
