package com.rochiee.classsync.data.remote.classroom

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.classroom.Classroom
import com.google.api.services.classroom.model.Announcement
import com.google.api.services.classroom.model.Course
import com.google.api.services.classroom.model.CourseWork
import com.google.api.services.classroom.model.CourseWorkMaterial
import com.google.api.services.classroom.model.StudentSubmission
import com.rochiee.classsync.auth.AuthTokenProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClassroomApiClient(private val authTokenProvider: AuthTokenProvider) {
    private val jsonFactory = GsonFactory.getDefaultInstance()
    private val transport = GoogleNetHttpTransport.newTrustedTransport()

    private suspend fun getClassroomService(): Classroom? {
        val credential = authTokenProvider.getGoogleAccountCredential() ?: return null

        return Classroom.Builder(transport, jsonFactory, credential)
            .setApplicationName("ClassSync")
            .build()
    }

    private fun missingAccessMessage(): String {
        return "Classroom API access is unavailable. Connect a Google account and approve Classroom access on this device."
    }

    suspend fun getCourses(): List<Course> = withContext(Dispatchers.IO) {
        val service = getClassroomService() ?: throw IllegalStateException(missingAccessMessage())
        try {
            val response = service.courses().list()
                .setCourseStates(listOf("ACTIVE"))
                .execute()
            response.courses ?: emptyList()
        } catch (e: Exception) {
            throw IllegalStateException(ClassroomErrorInterpreter.toUserMessage(e, "read your Classroom courses"), e)
        }
    }

    suspend fun getCourseWork(courseId: String): List<CourseWork> = withContext(Dispatchers.IO) {
        val service = getClassroomService() ?: throw IllegalStateException(missingAccessMessage())
        try {
            val response = service.courses().courseWork().list(courseId)
                .execute()
            response.courseWork ?: emptyList()
        } catch (e: Exception) {
            throw IllegalStateException(ClassroomErrorInterpreter.toUserMessage(e, "read your Classroom assignments"), e)
        }
    }

    suspend fun getAnnouncements(courseId: String): List<Announcement> = withContext(Dispatchers.IO) {
        val service = getClassroomService() ?: throw IllegalStateException(missingAccessMessage())
        try {
            val response = service.courses().announcements().list(courseId).execute()
            response.announcements ?: emptyList()
        } catch (e: Exception) {
            throw IllegalStateException(ClassroomErrorInterpreter.toUserMessage(e, "read Classroom announcements"), e)
        }
    }

    suspend fun getCourseWorkMaterials(courseId: String): List<CourseWorkMaterial> = withContext(Dispatchers.IO) {
        val service = getClassroomService() ?: throw IllegalStateException(missingAccessMessage())
        try {
            val response = service.courses().courseWorkMaterials().list(courseId).execute()
            response.courseWorkMaterial ?: emptyList()
        } catch (e: Exception) {
            throw IllegalStateException(ClassroomErrorInterpreter.toUserMessage(e, "read Classroom materials"), e)
        }
    }

    suspend fun getSubmissions(courseId: String, courseWorkId: String): List<StudentSubmission> = withContext(Dispatchers.IO) {
        val service = getClassroomService() ?: throw IllegalStateException(missingAccessMessage())
        try {
            val response = service.courses().courseWork().studentSubmissions().list(courseId, courseWorkId)
                .execute()
            response.studentSubmissions ?: emptyList()
        } catch (e: Exception) {
            throw IllegalStateException(ClassroomErrorInterpreter.toUserMessage(e, "read your submission status"), e)
        }
    }
}
