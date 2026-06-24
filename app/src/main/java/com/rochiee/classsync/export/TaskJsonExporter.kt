package com.rochiee.classsync.export

import com.rochiee.classsync.domain.model.AcademicTask
import org.json.JSONArray
import org.json.JSONObject

class TaskJsonExporter {
    fun export(tasks: List<AcademicTask>): String {
        val array = JSONArray()
        tasks.forEach { task ->
            array.put(
                JSONObject().apply {
                    put("id", task.id)
                    put("title", task.title)
                    put("description", task.description)
                    put("courseName", task.courseName)
                    put("isCompleted", task.isCompleted)
                    put("dueDate", task.dueDate)
                    put("priority", task.priority)
                    put("source", task.source)
                    put("sourceId", task.sourceId)
                    put("sourceLink", task.sourceLink)
                    put("createdAtMillis", task.createdAtMillis)
                    put("updatedAtMillis", task.updatedAtMillis)
                }
            )
        }
        return array.toString(2)
    }
}
