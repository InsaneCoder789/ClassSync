package com.rochiee.classsync.ui.state

import com.rochiee.classsync.study.StudyPlan
import com.rochiee.classsync.study.StudyPlanItem
import org.json.JSONArray
import org.json.JSONObject

object UiStateJsonAdapter {
    fun studyPlanToJson(plan: StudyPlan?): String? {
        if (plan == null) return null
        val items = JSONArray()
        plan.items.forEach { item ->
            items.put(
                JSONObject()
                    .put("id", item.id)
                    .put("title", item.title)
                    .put("courseName", item.courseName)
                    .put("scheduledDateMillis", item.scheduledDateMillis)
                    .put("sourceType", item.sourceType)
                    .put("priorityExplanation", item.priorityExplanation)
                    .put("estimatedEffortLabel", item.estimatedEffortLabel)
                    .put("notes", item.notes)
                    .put("isManual", item.isManual)
                    .put("isDone", item.isDone)
            )
        }
        return JSONObject()
            .put("generatedAtMillis", plan.generatedAtMillis)
            .put("items", items)
            .toString()
    }

    fun studyPlanFromJson(json: String?): StudyPlan? {
        if (json.isNullOrBlank()) return null
        return runCatching {
            val root = JSONObject(json)
            val items = root.optJSONArray("items") ?: JSONArray()
            StudyPlan(
                generatedAtMillis = root.optLong("generatedAtMillis"),
                items = buildList {
                    for (index in 0 until items.length()) {
                        val item = items.getJSONObject(index)
                        add(
                            StudyPlanItem(
                                id = item.optString("id"),
                                title = item.optString("title"),
                                courseName = item.optString("courseName"),
                                scheduledDateMillis = item.optLong("scheduledDateMillis"),
                                sourceType = item.optString("sourceType"),
                                priorityExplanation = item.optString("priorityExplanation"),
                                estimatedEffortLabel = item.optString("estimatedEffortLabel"),
                                notes = item.optString("notes"),
                                isManual = item.optBoolean("isManual"),
                                isDone = item.optBoolean("isDone")
                            )
                        )
                    }
                }
            )
        }.getOrNull()
    }

    fun examChecklistToJson(checklist: Map<String, Set<String>>): String {
        val root = JSONObject()
        checklist.forEach { (eventId, labels) ->
            root.put(eventId, JSONArray(labels.toList()))
        }
        return root.toString()
    }

    fun examChecklistFromJson(json: String?): Map<String, Set<String>> {
        if (json.isNullOrBlank()) return emptyMap()
        return runCatching {
            val root = JSONObject(json)
            root.keys().asSequence().associateWith { eventId ->
                val labels = root.optJSONArray(eventId) ?: JSONArray()
                buildSet {
                    for (index in 0 until labels.length()) {
                        add(labels.optString(index))
                    }
                }
            }
        }.getOrDefault(emptyMap())
    }
}
