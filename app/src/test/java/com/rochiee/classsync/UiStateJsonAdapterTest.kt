package com.rochiee.classsync

import com.rochiee.classsync.widget.WidgetTaskFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UiStateJsonAdapterTest {

    @Test
    fun widgetDeadlineTone_marksTodayAndOverdueCorrectly() {
        val formatter = WidgetTaskFormatter()
        val now = 1_717_000_000_000L

        val overdueTone = formatter.deadlineTone(now - 1_000L, now)
        val todayTone = formatter.deadlineTone(now + 60_000L, now)

        assertEquals(WidgetTaskFormatter.WidgetDeadlineTone.OVERDUE, overdueTone)
        assertTrue(
            todayTone == WidgetTaskFormatter.WidgetDeadlineTone.TODAY ||
                todayTone == WidgetTaskFormatter.WidgetDeadlineTone.SOON
        )
    }
}
