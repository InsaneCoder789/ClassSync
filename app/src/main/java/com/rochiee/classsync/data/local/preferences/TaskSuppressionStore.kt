package com.rochiee.classsync.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rochiee.classsync.domain.model.AcademicTask
import kotlinx.coroutines.flow.first
import java.util.Locale
import java.security.MessageDigest

private val Context.taskSuppressionDataStore by preferencesDataStore(name = "classsync_task_suppression")

class TaskSuppressionStore(private val context: Context) {

    suspend fun suppress(task: AcademicTask) {
        val keys = suppressionKeysFor(task)
        if (keys.isEmpty()) return

        context.taskSuppressionDataStore.edit { preferences ->
            val existing = preferences[Keys.suppressedTaskKeys].orEmpty()
            preferences[Keys.suppressedTaskKeys] = (existing + keys).toList().takeLast(MAX_KEYS).toSet()
        }
    }

    suspend fun isSuppressed(task: AcademicTask): Boolean {
        val keys = suppressionKeysFor(task)
        if (keys.isEmpty()) return false
        val suppressed = context.taskSuppressionDataStore.data.first()[Keys.suppressedTaskKeys].orEmpty()
        return keys.any(suppressed::contains)
    }

    companion object {
        private const val MAX_KEYS = 800

        fun shouldSuppress(task: AcademicTask): Boolean {
            return task.isCompleted && task.source.isSyncedSource()
        }

        fun shouldCheckSuppression(task: AcademicTask): Boolean {
            return task.source.isSyncedSource()
        }

        private fun suppressionKeysFor(task: AcademicTask): Set<String> {
            if (!task.source.isSyncedSource()) return emptySet()

            val normalizedSource = task.source.trim().lowercase(Locale.getDefault())
            val keys = linkedSetOf<String>()

            task.sourceId
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?.let { keys += "source_id:$normalizedSource:${obfuscateKeyMaterial(it.lowercase(Locale.getDefault()))}" }

            task.sourceLink
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?.let { keys += "source_link:$normalizedSource:${obfuscateKeyMaterial(it.lowercase(Locale.getDefault()))}" }

            val title = task.title.normalizeForKey()
            val course = task.courseName.normalizeForKey()
            val due = task.dueDate?.toString().orEmpty()
            if (title.isNotBlank() || course.isNotBlank()) {
                keys += "fingerprint:$normalizedSource:${obfuscateKeyMaterial("$course:$title:$due")}"
                keys += "fingerprint:$normalizedSource:${obfuscateKeyMaterial("$course:$title")}"
            }

            if (title.isNotBlank()) {
                keys += "title:$normalizedSource:${obfuscateKeyMaterial(title)}"
            }

            if (title.isNotBlank() && course.isNotBlank()) {
                keys += "course_title:$normalizedSource:${obfuscateKeyMaterial("$course:$title")}"
            }

            return keys
        }

        private fun String.normalizeForKey(): String {
            return lowercase(Locale.getDefault())
                .replace(Regex("\\s+"), " ")
                .trim()
        }

        private fun String.isSyncedSource(): Boolean {
            return when (trim().lowercase(Locale.getDefault())) {
                "classroom", "google classroom", "gmail", "notification" -> true
                else -> false
            }
        }

        private fun obfuscateKeyMaterial(raw: String): String {
            return MessageDigest.getInstance("SHA-256")
                .digest(raw.toByteArray())
                .joinToString("") { "%02x".format(it) }
        }
    }

    private object Keys {
        val suppressedTaskKeys = stringSetPreferencesKey("suppressed_task_keys")
    }
}
