package com.rochiee.classsync.domain.usecase.classroom

import com.rochiee.classsync.data.remote.classroom.ClassroomCourseDto
import com.rochiee.classsync.data.remote.classroom.ClassroomCourseWorkDto
import com.rochiee.classsync.data.remote.classroom.ClassroomSubmissionDto
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.domain.model.TaskSource
import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.repository.ClassroomRepository
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.domain.repository.SyncLogRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.domain.usecase.settings.SetLastSyncTimeUseCase
import com.rochiee.classsync.domain.usecase.widget.RefreshWidgetsUseCase
import com.rochiee.classsync.eventengine.ClassroomEventParser
import com.rochiee.classsync.eventengine.EventToTaskConverter
import com.rochiee.classsync.eventengine.RawClassroomEventInput
import kotlinx.coroutines.flow.first

class SyncClassroomCourseworkUseCase(
    private val classroomRepository: ClassroomRepository,
    private val taskRepository: TaskRepository,
    private val syncLogRepository: SyncLogRepository,
    private val classroomEventRepository: ClassroomEventRepository,
    private val classroomEventParser: ClassroomEventParser,
    private val eventToTaskConverter: EventToTaskConverter,
    private val settingsRepository: SettingsRepository,
    private val setLastSyncTimeUseCase: SetLastSyncTimeUseCase,
    private val refreshWidgetsUseCase: RefreshWidgetsUseCase
) {
    suspend operator fun invoke() {
        try {
            if (!settingsRepository.observeSettings().first().classroomSyncEnabled) {
                syncLogRepository.addLog(
                    SyncLog(
                        source = "CLASSROOM_TASKS",
                        status = "SKIPPED",
                        message = "Skipped Classroom coursework sync because Classroom sync is disabled in settings.",
                        timestamp = System.currentTimeMillis()
                    )
                )
                return
            }

            val remoteCourses = classroomRepository.fetchRemoteCourses()
            var importedTaskCount = 0
            var courseworkEventCount = 0
            var announcementEventCount = 0
            var materialEventCount = 0
            var submissionEventCount = 0
            var gradeEventCount = 0
            val partialFailures = mutableListOf<String>()

            remoteCourses.forEach { courseDto ->
                runCatching { syncCourseWork(courseDto) }
                    .onSuccess { courseWorkSync ->
                        courseworkEventCount += courseWorkSync.eventCount
                        importedTaskCount += courseWorkSync.importedTasks
                    }
                    .onFailure { error ->
                        partialFailures += "${courseDto.name}: ${error.message ?: "Coursework access failed."}"
                    }

                runCatching { syncAnnouncements(courseDto) }
                    .onSuccess { announcementCount ->
                        announcementEventCount += announcementCount
                    }
                    .onFailure { error ->
                        partialFailures += "${courseDto.name}: ${error.message ?: "Announcement access failed."}"
                    }

                runCatching { syncMaterials(courseDto) }
                    .onSuccess { materialSync ->
                        materialEventCount += materialSync.eventCount
                        importedTaskCount += materialSync.importedTasks
                    }
                    .onFailure { error ->
                        partialFailures += "${courseDto.name}: ${error.message ?: "Material access failed."}"
                    }

                // Student submission endpoints are commonly restricted or much slower on
                // institution-managed accounts. The main ClassSync flow prioritizes courses,
                // coursework, announcements, and materials so onboarding can complete reliably.
            }

            val totalEvents = courseworkEventCount + announcementEventCount + materialEventCount + submissionEventCount + gradeEventCount
            if (totalEvents == 0 && importedTaskCount == 0 && partialFailures.isNotEmpty()) {
                throw IllegalStateException(partialFailures.first())
            }

            val savedEventsMessage = "Saved $totalEvents Classroom events ($courseworkEventCount coursework, $announcementEventCount announcements, $materialEventCount materials, $submissionEventCount submission updates, $gradeEventCount grade updates) and imported $importedTaskCount tasks from ${remoteCourses.size} courses."

            syncLogRepository.addLog(
                SyncLog(
                    source = "CLASSROOM_TASKS",
                    status = "SUCCESS",
                    message = savedEventsMessage,
                    timestamp = System.currentTimeMillis()
                )
            )
            if (partialFailures.isNotEmpty()) {
                syncLogRepository.addLog(
                    SyncLog(
                        source = "CLASSROOM_TASKS",
                        status = "WARNING",
                        message = "Classroom sync completed with some skipped endpoints. ${partialFailures.first()}",
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
            if (totalEvents == 0 && importedTaskCount == 0 && remoteCourses.isNotEmpty()) {
                syncLogRepository.addLog(
                    SyncLog(
                        source = "CLASSROOM_TASKS",
                        status = "WARNING",
                        message = "Classroom courses were found, but no coursework, announcements, or materials were imported yet.",
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
            setLastSyncTimeUseCase(System.currentTimeMillis())
            refreshWidgetsUseCase()
        } catch (error: Exception) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "CLASSROOM_TASKS",
                    status = "ERROR",
                    message = error.message ?: "Classroom coursework sync failed.",
                    timestamp = System.currentTimeMillis()
                )
            )
            throw error
        }
    }

    private suspend fun syncCourseWork(courseDto: ClassroomCourseDto): ImportResult {
        var eventCount = 0
        var importedTasks = 0
        val courseWorkList = classroomRepository.fetchRemoteCourseWork(courseDto.courseId)
        courseWorkList.forEach { workDto ->
            val event = classroomEventParser.parse(
                RawClassroomEventInput(
                    title = workDto.title,
                    body = workDto.description,
                    courseId = workDto.courseId,
                    courseName = courseDto.name,
                    source = TaskSource.CLASSROOM,
                    sourceId = sourceIdForCourseWork(workDto),
                    sourcePackageName = null,
                    originalLink = workDto.alternateLink,
                    receivedAtMillis = workDto.updateTimeMillis.takeIf { it > 0 } ?: workDto.creationTimeMillis,
                    dueDateMillisOverride = workDto.dueDateMillis
                )
            )
            event?.let {
                classroomEventRepository.saveEvent(it)
                eventCount += 1
                val task = eventToTaskConverter.convert(it)
                if (task != null) {
                    taskRepository.addTask(task)
                    classroomEventRepository.markConvertedToTask(it.id, true)
                    importedTasks += 1
                }
            }
        }
        return ImportResult(eventCount, importedTasks)
    }

    private suspend fun syncAnnouncements(courseDto: ClassroomCourseDto): Int {
        var eventCount = 0
        classroomRepository.fetchRemoteAnnouncements(courseDto.courseId).forEach { announcement ->
            val body = buildBody(
                baseText = announcement.text,
                attachmentTitles = announcement.attachmentTitles,
                attachmentLinks = announcement.attachmentLinks
            )
            val event = classroomEventParser.parse(
                RawClassroomEventInput(
                    title = "Announcement: ${body.lineSequence().firstOrNull()?.take(60) ?: courseDto.name}",
                    body = body,
                    courseId = courseDto.courseId,
                    courseName = courseDto.name,
                    source = TaskSource.CLASSROOM,
                    sourceId = "${courseDto.courseId}:announcement:${announcement.id}",
                    sourcePackageName = null,
                    originalLink = announcement.alternateLink ?: announcement.attachmentLinks.firstOrNull(),
                    receivedAtMillis = announcement.updateTimeMillis.takeIf { it > 0 } ?: announcement.creationTimeMillis
                )
            )
            event?.let {
                classroomEventRepository.saveEvent(it)
                eventCount += 1
            }
        }
        return eventCount
    }

    private suspend fun syncMaterials(courseDto: ClassroomCourseDto): ImportResult {
        var eventCount = 0
        var importedTasks = 0
        classroomRepository.fetchRemoteMaterials(courseDto.courseId).forEach { material ->
            val body = buildBody(
                baseText = material.description,
                attachmentTitles = material.attachmentTitles,
                attachmentLinks = material.attachmentLinks
            )
            val event = classroomEventParser.parse(
                RawClassroomEventInput(
                    title = "Material: ${material.title}",
                    body = body,
                    courseId = courseDto.courseId,
                    courseName = courseDto.name,
                    source = TaskSource.CLASSROOM,
                    sourceId = "${courseDto.courseId}:material:${material.id}",
                    sourcePackageName = null,
                    originalLink = material.alternateLink ?: material.attachmentLinks.firstOrNull(),
                    receivedAtMillis = material.updateTimeMillis.takeIf { it > 0 } ?: material.creationTimeMillis
                )
            )
            event?.let {
                classroomEventRepository.saveEvent(it)
                eventCount += 1
                val task = eventToTaskConverter.convert(it)
                if (task != null) {
                    taskRepository.addTask(task)
                    classroomEventRepository.markConvertedToTask(it.id, true)
                    importedTasks += 1
                }
            }
        }
        return ImportResult(eventCount, importedTasks)
    }

    private suspend fun syncSubmissions(courseDto: ClassroomCourseDto): SubmissionResult {
        var submissionEventCount = 0
        var gradeEventCount = 0
        val courseWorkList = classroomRepository.fetchRemoteCourseWork(courseDto.courseId)

        courseWorkList.forEach { workDto ->
            val submissions = classroomRepository.fetchRemoteSubmissions(courseDto.courseId, workDto.id)
            submissions.forEach { submission ->
                val sourceId = sourceIdForCourseWork(workDto)
                val submissionEvent = classroomEventParser.parse(
                    RawClassroomEventInput(
                        title = "Submission update: ${workDto.title}",
                        body = buildSubmissionBody(submission),
                        courseId = courseDto.courseId,
                        courseName = courseDto.name,
                        source = TaskSource.CLASSROOM,
                        sourceId = "$sourceId:submission:${submission.id}",
                        sourcePackageName = null,
                        originalLink = workDto.alternateLink,
                        receivedAtMillis = submission.updateTimeMillis
                    )
                )
                submissionEvent?.let {
                    classroomEventRepository.saveEvent(it)
                    submissionEventCount += 1
                }

                if (submission.assignedGrade != null || submission.draftGrade != null) {
                    val gradeEvent = classroomEventParser.parse(
                        RawClassroomEventInput(
                            title = "Grade update: ${workDto.title}",
                            body = buildGradeBody(submission),
                            courseId = courseDto.courseId,
                            courseName = courseDto.name,
                            source = TaskSource.CLASSROOM,
                            sourceId = "$sourceId:grade:${submission.id}",
                            sourcePackageName = null,
                            originalLink = workDto.alternateLink,
                            receivedAtMillis = submission.updateTimeMillis
                        )
                    )
                    gradeEvent?.let {
                        classroomEventRepository.saveEvent(it)
                        gradeEventCount += 1
                    }
                }

                updateMatchingTaskFromSubmission(workDto, submission)
            }
        }

        return SubmissionResult(submissionEventCount, gradeEventCount)
    }

    private suspend fun updateMatchingTaskFromSubmission(
        workDto: ClassroomCourseWorkDto,
        submission: ClassroomSubmissionDto
    ) {
        val sourceId = sourceIdForCourseWork(workDto)
        val existingTasks = taskRepository.getTasksSnapshot()
        val matchedTask = existingTasks.firstOrNull { it.sourceId == sourceId } ?: return
        val shouldBeCompleted = when (submission.state.uppercase()) {
            "TURNED_IN", "RETURNED" -> true
            "CREATED", "NEW", "RECLAIMED_BY_STUDENT", "MISSING" -> false
            else -> matchedTask.isCompleted
        }

        if (matchedTask.isCompleted != shouldBeCompleted) {
            taskRepository.updateTask(matchedTask.copy(isCompleted = shouldBeCompleted))
        }
    }

    private fun buildBody(
        baseText: String?,
        attachmentTitles: List<String>,
        attachmentLinks: List<String>
    ): String {
        val sections = mutableListOf<String>()
        if (!baseText.isNullOrBlank()) sections += baseText.trim()
        if (attachmentTitles.isNotEmpty()) sections += "Attachments: ${attachmentTitles.joinToString()}"
        if (attachmentLinks.isNotEmpty()) sections += "Links: ${attachmentLinks.joinToString()}"
        return sections.joinToString("\n")
    }

    private fun buildSubmissionBody(submission: ClassroomSubmissionDto): String {
        return buildString {
            append("Submission state: ${submission.state}.")
            if (submission.draftGrade != null) append(" Draft grade: ${submission.draftGrade}.")
            if (submission.assignedGrade != null) append(" Assigned grade: ${submission.assignedGrade}.")
        }
    }

    private fun buildGradeBody(submission: ClassroomSubmissionDto): String {
        return buildString {
            append("Grade update received.")
            if (submission.draftGrade != null) append(" Draft grade: ${submission.draftGrade}.")
            if (submission.assignedGrade != null) append(" Assigned grade: ${submission.assignedGrade}.")
            append(" Submission state: ${submission.state}.")
        }
    }

    private fun sourceIdForCourseWork(workDto: ClassroomCourseWorkDto): String {
        return "${workDto.courseId}:${workDto.id}"
    }

    private data class ImportResult(
        val eventCount: Int,
        val importedTasks: Int
    )

    private data class SubmissionResult(
        val submissionEvents: Int,
        val gradeEvents: Int
    )
}
