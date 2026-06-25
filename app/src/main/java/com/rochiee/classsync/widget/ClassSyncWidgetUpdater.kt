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
            val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
            val expanded = minHeight >= 170
            val deadlineTone = formatter.deadlineTone(summary.nextTaskDueMillis)
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
                    summary.nextTaskTitle ?: "No academic tasks yet"
                )
                setTextViewText(
                    R.id.widgetNextTaskCourse,
                    summary.nextTaskCourseName ?: "Academic focus"
                )
                setTextViewText(
                    R.id.widgetNextTaskDue,
                    formatter.dueText(summary.nextTaskDueMillis)
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
                        WidgetTaskFormatter.WidgetDeadlineTone.OVERDUE,
                        WidgetTaskFormatter.WidgetDeadlineTone.TODAY -> R.drawable.widget_card_today
                        WidgetTaskFormatter.WidgetDeadlineTone.TOMORROW -> R.drawable.widget_card_tomorrow
                        WidgetTaskFormatter.WidgetDeadlineTone.SOON -> R.drawable.widget_card_soon
                        WidgetTaskFormatter.WidgetDeadlineTone.UPCOMING -> R.drawable.widget_card_upcoming
                        WidgetTaskFormatter.WidgetDeadlineTone.SAFE -> R.drawable.widget_card_safe
                        WidgetTaskFormatter.WidgetDeadlineTone.NONE -> R.drawable.widget_card_surface
                    }
                )
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
                setViewVisibility(R.id.widgetNextTaskContainer, if (expanded) View.VISIBLE else View.GONE)
                setViewVisibility(R.id.widgetFocusContainer, if (expanded) View.VISIBLE else View.GONE)
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
