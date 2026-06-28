package com.rochiee.classsync.taskengine

import com.rochiee.classsync.domain.model.AcademicTask
import java.util.Calendar

object TaskRecencyPolicy {
    private const val MIN_SUPPORTED_YEAR = 2025

    fun shouldKeep(task: AcademicTask, nowMillis: Long = System.currentTimeMillis()): Boolean {
        val floorMillis = releaseFloorMillis(nowMillis)
        val datedAnchor = task.dueDate ?: maxOf(task.createdAtMillis, task.updatedAtMillis)

        if (datedAnchor < floorMillis) return false

        val calendar = Calendar.getInstance().apply {
            timeInMillis = datedAnchor
        }
        return calendar.get(Calendar.YEAR) >= MIN_SUPPORTED_YEAR
    }

    private fun releaseFloorMillis(nowMillis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
