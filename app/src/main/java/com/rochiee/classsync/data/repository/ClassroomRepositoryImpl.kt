package com.rochiee.classsync.data.repository

import com.rochiee.classsync.data.local.dao.CourseDao
import com.rochiee.classsync.data.local.entity.CourseEntity
import com.rochiee.classsync.data.remote.classroom.ClassroomAnnouncementDto
import com.rochiee.classsync.data.remote.classroom.ClassroomCourseDto
import com.rochiee.classsync.data.remote.classroom.ClassroomCourseWorkDto
import com.rochiee.classsync.data.remote.classroom.ClassroomMaterialDto
import com.rochiee.classsync.data.remote.classroom.ClassroomSubmissionDto
import com.rochiee.classsync.data.remote.classroom.ClassroomRemoteDataSource
import com.rochiee.classsync.domain.repository.ClassroomRepository
import kotlinx.coroutines.flow.Flow

class ClassroomRepositoryImpl(
    private val remoteDataSource: ClassroomRemoteDataSource,
    private val courseDao: CourseDao
) : ClassroomRepository {
    override suspend fun fetchRemoteCourses(): List<ClassroomCourseDto> {
        return remoteDataSource.fetchActiveCourses()
    }

    override suspend fun fetchRemoteCourseWork(courseId: String): List<ClassroomCourseWorkDto> {
        return remoteDataSource.fetchCourseWork(courseId)
    }

    override suspend fun fetchRemoteAnnouncements(courseId: String): List<ClassroomAnnouncementDto> {
        return remoteDataSource.fetchAnnouncements(courseId)
    }

    override suspend fun fetchRemoteMaterials(courseId: String): List<ClassroomMaterialDto> {
        return remoteDataSource.fetchMaterials(courseId)
    }

    override suspend fun fetchRemoteSubmissions(courseId: String, courseWorkId: String): List<ClassroomSubmissionDto> {
        return remoteDataSource.fetchSubmissions(courseId, courseWorkId)
    }

    override suspend fun saveCourses(courses: List<CourseEntity>) {
        courseDao.insertCourses(courses)
    }

    override fun observeLocalCourses(): Flow<List<CourseEntity>> {
        return courseDao.getAllCourses()
    }

    override suspend fun getLocalCourseById(courseId: String): CourseEntity? {
        return courseDao.getCourseById(courseId)
    }

    override suspend fun clearLocalCourses() {
        courseDao.clearCourses()
    }
}
