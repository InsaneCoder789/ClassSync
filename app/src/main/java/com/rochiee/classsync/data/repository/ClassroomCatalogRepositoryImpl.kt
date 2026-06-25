package com.rochiee.classsync.data.repository

import android.content.Context
import com.rochiee.classsync.domain.model.ClassroomCatalog
import com.rochiee.classsync.domain.model.ClassroomDaySchedule
import com.rochiee.classsync.domain.model.ClassroomScheduleEntry
import com.rochiee.classsync.domain.model.ClassroomSection
import com.rochiee.classsync.domain.model.ClassroomSemester
import com.rochiee.classsync.domain.repository.ClassroomCatalogRepository
import org.json.JSONArray
import org.json.JSONObject

class ClassroomCatalogRepositoryImpl(
    private val context: Context
) : ClassroomCatalogRepository {

    private var cachedCatalog: ClassroomCatalog? = null

    override suspend fun getCatalog(): ClassroomCatalog {
        cachedCatalog?.let { return it }
        val rawJson = context.assets.open(CATALOG_ASSET_PATH).bufferedReader().use { it.readText() }
        return parseCatalog(JSONObject(rawJson)).also { cachedCatalog = it }
    }

    private fun parseCatalog(root: JSONObject): ClassroomCatalog {
        val semesters = root.getJSONArray("semesters").map { semesterJson ->
            ClassroomSemester(
                semesterNumber = semesterJson.getInt("semester"),
                sections = semesterJson.getJSONArray("sections").map { sectionJson ->
                    ClassroomSection(
                        sectionId = sectionJson.getString("sectionId"),
                        studentCount = sectionJson.optInt("studentCount", 0),
                        days = sectionJson.getJSONArray("days").map { dayJson ->
                            ClassroomDaySchedule(
                                dayKey = dayJson.getString("dayKey"),
                                label = dayJson.getString("label"),
                                entries = dayJson.getJSONArray("entries").map { entryJson ->
                                    ClassroomScheduleEntry(
                                        slotIndex = entryJson.optInt("slotIndex", 0),
                                        time = entryJson.getString("time"),
                                        subject = entryJson.getString("subject"),
                                        room = entryJson.getString("room"),
                                        variant = entryJson.optString("variant").takeIf { it.isNotBlank() }
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
        return ClassroomCatalog(semesters = semesters)
    }

    private fun <T> JSONArray.map(transform: (JSONObject) -> T): List<T> {
        return buildList(length()) {
            for (index in 0 until length()) {
                add(transform(getJSONObject(index)))
            }
        }
    }

    companion object {
        private const val CATALOG_ASSET_PATH = "classroom/classroom_catalog.json"
    }
}
