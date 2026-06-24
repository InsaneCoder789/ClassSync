package com.rochiee.classsync.data.remote.classroom

import com.google.api.client.util.DateTime

class ClassroomRemoteDataSource(private val apiClient: ClassroomApiClient) {

    suspend fun fetchActiveCourses(): List<ClassroomCourseDto> {
        return apiClient.getCourses().map { course ->
            ClassroomCourseDto(
                courseId = course.id,
                name = course.name,
                section = course.section,
                room = course.room,
                descriptionHeading = course.descriptionHeading,
                teacherName = null, // Teacher name requires additional profile fetch, keeping it null for now
                courseState = course.courseState
            )
        }
    }

    suspend fun fetchCourseWork(courseId: String): List<ClassroomCourseWorkDto> {
        return apiClient.getCourseWork(courseId).map { work ->
            ClassroomCourseWorkDto(
                id = work.id,
                courseId = work.courseId,
                title = work.title,
                description = work.description,
                state = work.state,
                workType = work.workType,
                alternateLink = work.alternateLink,
                dueDateMillis = work.dueDate?.let { date ->
                    val calendar = java.util.Calendar.getInstance()
                    calendar.set(date.year, date.month - 1, date.day)
                    work.dueTime?.let { time ->
                        calendar.set(java.util.Calendar.HOUR_OF_DAY, time.hours ?: 0)
                        calendar.set(java.util.Calendar.MINUTE, time.minutes ?: 0)
                    }
                    calendar.timeInMillis
                },
                creationTimeMillis = work.creationTime.toEpochMillis(),
                updateTimeMillis = work.updateTime.toEpochMillis()
            )
        }
    }

    suspend fun fetchSubmissions(courseId: String, courseWorkId: String): List<ClassroomSubmissionDto> {
        return apiClient.getSubmissions(courseId, courseWorkId).map { sub ->
            ClassroomSubmissionDto(
                id = sub.id,
                courseId = sub.courseId,
                courseWorkId = sub.courseWorkId,
                state = sub.state,
                assignedGrade = sub.assignedGrade,
                draftGrade = sub.draftGrade,
                updateTimeMillis = sub.updateTime.toEpochMillis()
            )
        }
    }

    suspend fun fetchAnnouncements(courseId: String): List<ClassroomAnnouncementDto> {
        return apiClient.getAnnouncements(courseId).map { announcement ->
            val materials = announcement.materials.orEmpty()
            ClassroomAnnouncementDto(
                id = announcement.id,
                courseId = announcement.courseId,
                text = announcement.text,
                alternateLink = announcement.alternateLink,
                attachmentTitles = materials.mapNotNull { material ->
                    material.driveFile?.driveFile?.title
                        ?: material.link?.title
                        ?: material.youtubeVideo?.title
                        ?: material.form?.title
                },
                attachmentLinks = materials.mapNotNull { material ->
                    material.driveFile?.driveFile?.alternateLink
                        ?: material.link?.url
                        ?: material.youtubeVideo?.alternateLink
                        ?: material.form?.formUrl
                },
                creationTimeMillis = announcement.creationTime.toEpochMillis(),
                updateTimeMillis = announcement.updateTime.toEpochMillis()
            )
        }
    }

    suspend fun fetchMaterials(courseId: String): List<ClassroomMaterialDto> {
        return apiClient.getCourseWorkMaterials(courseId).map { material ->
            val attachments = material.materials.orEmpty()
            ClassroomMaterialDto(
                id = material.id,
                courseId = material.courseId,
                title = material.title ?: "Untitled material",
                description = material.description,
                alternateLink = material.alternateLink,
                attachmentTitles = attachments.mapNotNull { item ->
                    item.driveFile?.driveFile?.title
                        ?: item.link?.title
                        ?: item.youtubeVideo?.title
                        ?: item.form?.title
                },
                attachmentLinks = attachments.mapNotNull { item ->
                    item.driveFile?.driveFile?.alternateLink
                        ?: item.link?.url
                        ?: item.youtubeVideo?.alternateLink
                        ?: item.form?.formUrl
                },
                creationTimeMillis = material.creationTime.toEpochMillis(),
                updateTimeMillis = material.updateTime.toEpochMillis()
            )
        }
    }

    private fun String?.toEpochMillis(): Long {
        if (this.isNullOrBlank()) return 0L
        return runCatching { DateTime.parseRfc3339(this).value }.getOrDefault(0L)
    }
}
