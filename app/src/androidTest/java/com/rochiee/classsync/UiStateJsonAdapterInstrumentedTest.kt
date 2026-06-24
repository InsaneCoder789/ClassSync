package com.rochiee.classsync

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rochiee.classsync.study.StudyPlan
import com.rochiee.classsync.study.StudyPlanItem
import com.rochiee.classsync.ui.state.UiStateJsonAdapter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UiStateJsonAdapterInstrumentedTest {

    @Test
    fun studyPlan_roundTripsWithoutLosingFields() {
        val plan = StudyPlan(
            generatedAtMillis = 1_717_000_000_000L,
            items = listOf(
                StudyPlanItem(
                    id = "block_1",
                    title = "Review chemistry notes",
                    courseName = "Chemistry",
                    scheduledDateMillis = 1_717_086_400_000L,
                    sourceType = "TASK",
                    priorityExplanation = "Due soon",
                    estimatedEffortLabel = "45 min",
                    isDone = true
                )
            )
        )

        val json = UiStateJsonAdapter.studyPlanToJson(plan)
        val restored = UiStateJsonAdapter.studyPlanFromJson(json)

        assertNotNull(restored)
        assertEquals(plan, restored)
    }

    @Test
    fun examChecklist_roundTripsAsStableSets() {
        val checklist = mapOf(
            "exam_1" to setOf("Review related materials", "Generate a study plan"),
            "exam_2" to setOf("Finish pending course tasks")
        )

        val json = UiStateJsonAdapter.examChecklistToJson(checklist)
        val restored = UiStateJsonAdapter.examChecklistFromJson(json)

        assertEquals(checklist, restored)
    }
}
