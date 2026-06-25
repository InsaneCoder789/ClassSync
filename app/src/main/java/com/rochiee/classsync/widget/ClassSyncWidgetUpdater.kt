package com.rochiee.classsync.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.rochiee.classsync.ClassSyncApplication
import com.rochiee.classsync.MainActivity
import com.rochiee.classsync.R
import com.rochiee.classsync.ui.navigation.AppDestination
import kotlinx.coroutines.runBlocking

object ClassSyncWidgetUpdater {
    fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, ClassSyncWidgetProvider::class.java)
        val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
        if (widgetIds.isNotEmpty()) {
            updateWidgets(context, appWidgetManager, widgetIds)
        }
    }

    fun updateWidgets(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val app = context.applicationContext as ClassSyncApplication
        val summary = runBlocking {
            app.container.widgetDataProvider.getWidgetSummary()
        }
        val formatter = app.container.widgetTaskFormatter

        appWidgetIds.forEach { widgetId ->
            val options = appWidgetManager.getAppWidgetOptions(widgetId)
            val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
            val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
            val expanded = minHeight >= 170
            val wide = minWidth >= 250
            val deadlineTone = formatter.deadlineTone(summary.primaryTaskDueMillis)
            val views = RemoteViews(context.packageName, R.layout.classsync_widget_layout).apply {
                setImageViewResource(R.id.widgetLogo, R.mipmap.ic_launcher)
                setTextViewText(R.id.widgetTitle, "ClassSync")
                setTextViewText(
                    R.id.widgetSummaryText,
                    formatter.relativeSummary(summary.todayTaskCount, summary.urgentTaskCount)
                )
                setTextViewText(R.id.widgetTodayCount, "${summary.todayTaskCount}")
                setTextViewText(R.id.widgetUrgentCount, "${summary.urgentTaskCount}")
                setTextViewText(R.id.widgetOverdueCount, "${summary.overdueTaskCount}")
                setTextViewText(
                    R.id.widgetNextTaskTitle,
                    summary.primaryTaskTitle ?: "No academic tasks yet"
                )
                setTextViewText(
                    R.id.widgetNextTaskCourse,
                    summary.primaryTaskCourseName ?: "Academic focus"
                )
                setTextViewText(
                    R.id.widgetNextTaskDue,
                    formatter.dueText(summary.primaryTaskDueMillis)
                )
                setTextViewText(
                    R.id.widgetNextTaskOverflow,
                    formatter.overflowText(summary.redZoneOverflowCount)
                )
                setTextViewText(
                    R.id.widgetFocusLine,
                    formatter.focusText(summary.secondTaskTitle, summary.secondTaskDueMillis)
                )
                setTextViewText(
                    R.id.widgetLastUpdated,
                    formatter.updatedText(summary.lastUpdatedMillis)
                )
                setInt(
                    R.id.widgetNextTaskContainer,
                    "setBackgroundResource",
                    when (deadlineTone) {
                        WidgetTaskFormatter.WidgetDeadlineTone.OVERDUE -> R.drawable.widget_card_overdue
                        WidgetTaskFormatter.WidgetDeadlineTone.TODAY -> R.drawable.widget_card_today
                        WidgetTaskFormatter.WidgetDeadlineTone.TOMORROW -> R.drawable.widget_card_tomorrow
                        WidgetTaskFormatter.WidgetDeadlineTone.SOON -> R.drawable.widget_card_soon
                        WidgetTaskFormatter.WidgetDeadlineTone.UPCOMING -> R.drawable.widget_card_upcoming
                        WidgetTaskFormatter.WidgetDeadlineTone.SAFE -> R.drawable.widget_card_safe
                        WidgetTaskFormatter.WidgetDeadlineTone.NONE -> R.drawable.widget_card_surface
                    }
                )
                val accentText = when (deadlineTone) {
                    WidgetTaskFormatter.WidgetDeadlineTone.OVERDUE,
                    WidgetTaskFormatter.WidgetDeadlineTone.TODAY -> 0xFF7E2323.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.TOMORROW -> 0xFF962E2E.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.SOON -> 0xFF8C4E16.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.UPCOMING -> 0xFF8C6A13.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.SAFE -> 0xFF1E6D45.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.NONE -> 0xFF122033.toInt()
                }
                val mutedAccentText = when (deadlineTone) {
                    WidgetTaskFormatter.WidgetDeadlineTone.OVERDUE,
                    WidgetTaskFormatter.WidgetDeadlineTone.TODAY,
                    WidgetTaskFormatter.WidgetDeadlineTone.TOMORROW -> 0xFFB14A4A.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.SOON -> 0xFFB86C2A.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.UPCOMING -> 0xFFA5832E.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.SAFE -> 0xFF347F5A.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.NONE -> 0xFF64748B.toInt()
                }
                setTextColor(R.id.widgetNextTaskLabel, mutedAccentText)
                setTextColor(R.id.widgetNextTaskOverflow, accentText)
                setTextColor(R.id.widgetNextTaskTitle, accentText)
                setTextColor(R.id.widgetNextTaskCourse, mutedAccentText)
                setTextColor(
                    R.id.widgetNextTaskDue,
                    when (deadlineTone) {
                        WidgetTaskFormatter.WidgetDeadlineTone.OVERDUE,
                        WidgetTaskFormatter.WidgetDeadlineTone.TODAY -> 0xFF9D3A3AL.toInt()
                        WidgetTaskFormatter.WidgetDeadlineTone.TOMORROW -> 0xFFD95D5DL.toInt()
                        WidgetTaskFormatter.WidgetDeadlineTone.SOON -> 0xFFE68A3AL.toInt()
                        WidgetTaskFormatter.WidgetDeadlineTone.UPCOMING -> 0xFFE0B84CL.toInt()
                        WidgetTaskFormatter.WidgetDeadlineTone.SAFE -> 0xFF39A66AL.toInt()
                        WidgetTaskFormatter.WidgetDeadlineTone.NONE -> 0xFF7B8794.toInt()
                    }
                )
                setTextColor(R.id.widgetFocusLine, 0xFF122033.toInt())
                setViewVisibility(
                    R.id.widgetNextTaskOverflow,
                    if (summary.redZoneOverflowCount > 0) View.VISIBLE else View.GONE
                )
                setViewVisibility(R.id.widgetNextTaskContainer, if (expanded) View.VISIBLE else View.GONE)
                setViewVisibility(R.id.widgetFocusContainer, if (expanded && wide) View.VISIBLE else View.GONE)
                setViewVisibility(R.id.widgetLastUpdated, if (expanded) View.VISIBLE else View.GONE)
                setOnClickPendingIntent(
                    R.id.widgetRoot,
                    openAppPendingIntent(context, AppDestination.Home.route)
                )
                setOnClickPendingIntent(
                    R.id.widgetNextTaskContainer,
                    openAppPendingIntent(context, AppDestination.Tasks.route)
                )
                setOnClickPendingIntent(R.id.widgetFocusContainer, openAppPendingIntent(context, AppDestination.Planner.route))
            }
            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }

    private fun openAppPendingIntent(context: Context, route: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_START_DESTINATION, route)
        }
        return PendingIntent.getActivity(
            context,
            route.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
