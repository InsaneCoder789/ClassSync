package com.rochiee.classsync.domain.usecase.gmail

import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.domain.model.TaskSource
import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.repository.GmailRepository
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.domain.repository.SyncLogRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.domain.usecase.settings.SetLastSyncTimeUseCase
import com.rochiee.classsync.domain.usecase.widget.RefreshWidgetsUseCase
import com.rochiee.classsync.eventengine.ClassroomEventParser
import com.rochiee.classsync.eventengine.EventToTaskConverter
import com.rochiee.classsync.eventengine.RawClassroomEventInput
import kotlinx.coroutines.flow.first

class SyncGmailTasksUseCase(
    private val gmailRepository: GmailRepository,
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
            if (!settingsRepository.observeSettings().first().gmailSyncEnabled) {
                syncLogRepository.addLog(
                    SyncLog(
                        source = "GMAIL",
                        status = "SKIPPED",
                        message = "Skipped Gmail sync because Gmail sync is disabled in settings.",
                        timestamp = System.currentTimeMillis()
                    )
                )
                return
            }
            val messages = gmailRepository.fetchRecentAcademicMessages()
            var importedCount = 0
            var eventCount = 0
            messages.forEach { message ->
                val event = classroomEventParser.parse(
                    RawClassroomEventInput(
                        title = message.subject,
                        body = listOfNotNull(message.snippet, message.body).joinToString("\n"),
                        courseId = null,
                        courseName = if (message.from?.contains("classroom.google.com") == true) {
                            message.subject?.split(":")?.firstOrNull()?.trim()
                        } else {
                            null
                        },
                        source = TaskSource.GMAIL,
                        sourceId = message.threadId.ifBlank { message.id },
                        sourcePackageName = null,
                        originalLink = message.link,
                        receivedAtMillis = message.internalDateMillis
                    )
                )

                event?.let {
                    classroomEventRepository.saveEvent(it)
                    eventCount += 1
                    val task = eventToTaskConverter.convert(it)
                    if (task != null) {
                        taskRepository.addTask(task)
                        classroomEventRepository.markConvertedToTask(it.id, true)
                        importedCount += 1
                    }
                }
            }

            syncLogRepository.addLog(
                SyncLog(
                    source = "GMAIL",
                    status = "SUCCESS",
                    message = "Saved $eventCount events and imported $importedCount tasks from ${messages.size} Gmail messages.",
                    timestamp = System.currentTimeMillis()
                )
            )
            setLastSyncTimeUseCase(System.currentTimeMillis())
            refreshWidgetsUseCase()
        } catch (error: Exception) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "GMAIL",
                    status = "ERROR",
                    message = error.message ?: "Gmail sync failed.",
                    timestamp = System.currentTimeMillis()
                )
            )
            throw error
        }
    }
}
