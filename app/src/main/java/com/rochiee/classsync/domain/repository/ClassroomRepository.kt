package com.rochiee.classsync.domain.repository

import com.rochiee.classsync.data.local.entity.CourseEntity
import com.rochiee.classsync.data.remote.classroom.ClassroomCourseDto
import com.rochiee.classsync.data.remote.classroom.ClassroomCourseWorkDto
import com.rochiee.classsync.data.remote.classroom.ClassroomAnnouncementDto
import com.rochiee.classsync.data.remote.classroom.ClassroomMaterialDto
import com.rochiee.classsync.data.remote.classroom.ClassroomSubmissionDto
import kotlinx.coroutines.flow.Flow

interface ClassroomRepository {
    suspend fun fetchRemoteCourses(): List<ClassroomCourseDto>
    suspend fun fetchRemoteCourseWork(courseId: String): List<ClassroomCourseWorkDto>
    suspend fun fetchRemoteAnnouncements(courseId: String): List<ClassroomAnnouncementDto>
    suspend fun fetchRemoteMaterials(courseId: String): List<ClassroomMaterialDto>
    suspend fun fetchRemoteSubmissions(courseId: String, courseWorkId: String): List<ClassroomSubmissionDto>
    
    suspend fun saveCourses(courses: List<CourseEntity>)
    fun observeLocalCourses(): Flow<List<CourseEntity>>
    suspend fun getLocalCourseById(courseId: String): CourseEntity?
    suspend fun clearLocalCourses()
}
