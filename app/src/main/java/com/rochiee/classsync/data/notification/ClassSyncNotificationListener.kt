package com.rochiee.classsync.data.notification

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.rochiee.classsync.ClassSyncApplication
import com.rochiee.classsync.data.local.entity.NotificationEntity
import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.domain.model.TaskSource
import com.rochiee.classsync.eventengine.RawClassroomEventInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ClassSyncNotificationListener : NotificationListenerService() {
    private companion object {
        const val MAX_TITLE_LENGTH = 180
        const val MAX_TEXT_LENGTH = 1_200
        private val ALLOWED_PACKAGES = setOf(
            "com.google.android.gm",
            "com.google.android.apps.classroom"
        )
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let {
            val packageName = it.packageName
            if (packageName in ALLOWED_PACKAGES) {
                val extras = it.notification.extras
                val title = extras.getString("android.title").sanitizeForStorage(MAX_TITLE_LENGTH)
                val text = extras.getCharSequence("android.text")?.toString().sanitizeForStorage(MAX_TEXT_LENGTH)
                val postedAtMillis = it.postTime

                if (title.isNotBlank() || text.isNotBlank()) {
                    processNotification(packageName, title, text, postedAtMillis)
                }
            }
        }
    }

    private fun processNotification(packageName: String, title: String, text: String, postedAtMillis: Long) {
        val app = application as ClassSyncApplication
        val container = app.container
        val notificationDao = container.database.notificationDao
        val repository = container.taskRepository
        val syncLogRepository = container.syncLogRepository
        val settingsRepository = container.settingsRepository
        val classroomEventRepository = container.classroomEventRepository
        val classroomEventParser = container.classroomEventParser
        val eventToTaskConverter = container.eventToTaskConverter

        serviceScope.launch {
            val settings = settingsRepository.observeSettings().first()
            if (!settings.notificationParsingEnabled) {
                syncLogRepository.addLog(
                    SyncLog(
                        source = "NOTIFICATION",
                        status = "SKIPPED",
                        message = "Skipped notification parsing because notification parsing is disabled in settings.",
                        timestamp = System.currentTimeMillis()
                    )
                )
                return@launch
            }
            val entity = NotificationEntity(
                packageName = packageName,
                title = title,
                text = text,
                postedAtMillis = postedAtMillis
            )
            notificationDao.insertNotification(entity)

            val event = classroomEventParser.parse(
                RawClassroomEventInput(
                    title = title,
                    body = text,
                    courseId = null,
                    courseName = if (packageName == "com.google.android.apps.classroom") {
                        title.split(":").firstOrNull()?.trim()
                    } else {
                        null
                    },
                    source = TaskSource.NOTIFICATION,
                    sourceId = "$packageName:$postedAtMillis",
                    sourcePackageName = packageName,
                    originalLink = null,
                    receivedAtMillis = postedAtMillis
                )
            )

            event?.let {
                classroomEventRepository.saveEvent(it)
                val task = eventToTaskConverter.convert(it)
                task?.let { convertedTask ->
                    repository.addTask(convertedTask)
                    classroomEventRepository.markConvertedToTask(it.id, true)
                }
                syncLogRepository.addLog(
                    SyncLog(
                        source = "NOTIFICATION",
                        status = "SUCCESS",
                        message = "Saved event from notification: ${it.title}",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } ?: syncLogRepository.addLog(
                SyncLog(
                    source = "NOTIFICATION",
                    status = "SKIPPED",
                    message = "Notification did not match academic task rules.",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    private fun String?.sanitizeForStorage(maxLength: Int): String {
        return this
            .orEmpty()
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(maxLength)
    }
}
