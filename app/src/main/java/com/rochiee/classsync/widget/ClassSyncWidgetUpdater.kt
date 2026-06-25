package com.rochiee.classsync.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.rochiee.classsync.ClassSyncApplication
import com.rochiee.classsync.MainActivity
import com.rochiee.classsync.R
import com.rochiee.classsync.domain.model.ThemeMode
import com.rochiee.classsync.ui.navigation.AppDestination
import kotlinx.coroutines.flow.first
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
        val (summary, themeMode) = runBlocking {
            app.container.widgetDataProvider.getWidgetSummary() to
                app.container.settingsRepository.observeSettings().first().themeMode
        }
        val formatter = app.container.widgetTaskFormatter

        appWidgetIds.forEach { widgetId ->
            val options = appWidgetManager.getAppWidgetOptions(widgetId)
            val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
            val expanded = minHeight >= 170
            val deadlineTone = formatter.deadlineTone(summary.primaryTaskDueMillis)
            val isDark = themeMode == ThemeMode.DARK
            val rootSurface = if (isDark) R.drawable.widget_root_surface_dark else R.drawable.widget_root_surface
            val neutralCardSurface = if (isDark) R.drawable.widget_card_surface_dark else R.drawable.widget_card_surface
            val views = RemoteViews(context.packageName, R.layout.classsync_widget_layout).apply {
                setInt(R.id.widgetRoot, "setBackgroundResource", rootSurface)
                setImageViewResource(R.id.widgetLogo, R.mipmap.ic_launcher)
                setTextViewText(R.id.widgetTitle, "ClassSync")
                setTextViewText(
                    R.id.widgetSummaryText,
                    formatter.relativeSummary(summary.todayTaskCount, summary.urgentTaskCount)
                )
                setInt(R.id.widgetTodayCard, "setBackgroundResource", neutralCardSurface)
                setInt(R.id.widgetUrgentCard, "setBackgroundResource", neutralCardSurface)
                setInt(R.id.widgetOverdueCard, "setBackgroundResource", neutralCardSurface)
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
                setInt(
                    R.id.widgetNextTaskContainer,
                    "setBackgroundResource",
                    when (deadlineTone) {
                        WidgetTaskFormatter.WidgetDeadlineTone.OVERDUE -> if (isDark) R.drawable.widget_card_overdue_dark else R.drawable.widget_card_overdue
                        WidgetTaskFormatter.WidgetDeadlineTone.TODAY -> if (isDark) R.drawable.widget_card_today_dark else R.drawable.widget_card_today
                        WidgetTaskFormatter.WidgetDeadlineTone.TOMORROW -> if (isDark) R.drawable.widget_card_tomorrow_dark else R.drawable.widget_card_tomorrow
                        WidgetTaskFormatter.WidgetDeadlineTone.SOON -> if (isDark) R.drawable.widget_card_soon_dark else R.drawable.widget_card_soon
                        WidgetTaskFormatter.WidgetDeadlineTone.UPCOMING -> if (isDark) R.drawable.widget_card_upcoming_dark else R.drawable.widget_card_upcoming
                        WidgetTaskFormatter.WidgetDeadlineTone.SAFE -> if (isDark) R.drawable.widget_card_safe_dark else R.drawable.widget_card_safe
                        WidgetTaskFormatter.WidgetDeadlineTone.NONE -> neutralCardSurface
                    }
                )
                val primaryText = if (isDark) 0xFFF7FAFF.toInt() else 0xFF122033.toInt()
                val secondaryText = if (isDark) 0xFFA9B7CF.toInt() else 0xFF64748B.toInt()
                val accentText = when (deadlineTone) {
                    WidgetTaskFormatter.WidgetDeadlineTone.OVERDUE,
                    WidgetTaskFormatter.WidgetDeadlineTone.TODAY -> if (isDark) 0xFFFFD7D7.toInt() else 0xFF7E2323.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.TOMORROW -> if (isDark) 0xFFFFDFDF.toInt() else 0xFF962E2E.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.SOON -> if (isDark) 0xFFFFE1C8.toInt() else 0xFF8C4E16.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.UPCOMING -> if (isDark) 0xFFFFEFA8.toInt() else 0xFF8C6A13.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.SAFE -> if (isDark) 0xFFD8FFE7.toInt() else 0xFF1E6D45.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.NONE -> primaryText
                }
                val mutedAccentText = when (deadlineTone) {
                    WidgetTaskFormatter.WidgetDeadlineTone.OVERDUE,
                    WidgetTaskFormatter.WidgetDeadlineTone.TODAY,
                    WidgetTaskFormatter.WidgetDeadlineTone.TOMORROW -> if (isDark) 0xFFFFAAAA.toInt() else 0xFFB14A4A.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.SOON -> if (isDark) 0xFFFFC887.toInt() else 0xFFB86C2A.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.UPCOMING -> if (isDark) 0xFFFFDB73.toInt() else 0xFFA5832E.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.SAFE -> if (isDark) 0xFF88E7B0.toInt() else 0xFF347F5A.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.NONE -> secondaryText
                }
                setTextColor(R.id.widgetTitle, primaryText)
                setTextColor(R.id.widgetSummaryText, secondaryText)
                setTextColor(R.id.widgetTodayLabel, secondaryText)
                setTextColor(R.id.widgetTodayCount, primaryText)
                setTextColor(R.id.widgetUrgentLabel, if (isDark) 0xFFFFC887.toInt() else 0xFFA06A22.toInt())
                setTextColor(R.id.widgetUrgentCount, if (isDark) 0xFFFFD08D.toInt() else 0xFFED9B40.toInt())
                setTextColor(R.id.widgetOverdueLabel, if (isDark) 0xFFFFAAAA.toInt() else 0xFF9A4B4B.toInt())
                setTextColor(R.id.widgetOverdueCount, if (isDark) 0xFFFFB4B4.toInt() else 0xFFD95D5D.toInt())
                setTextColor(R.id.widgetNextTaskLabel, mutedAccentText)
                setTextColor(R.id.widgetNextTaskTitle, accentText)
                setTextColor(R.id.widgetNextTaskCourse, mutedAccentText)
                setTextColor(
                    R.id.widgetNextTaskDue,
                    when (deadlineTone) {
                        WidgetTaskFormatter.WidgetDeadlineTone.OVERDUE,
                        WidgetTaskFormatter.WidgetDeadlineTone.TODAY -> if (isDark) 0xFFFFD7D7.toInt() else 0xFF9D3A3A.toInt()
                        WidgetTaskFormatter.WidgetDeadlineTone.TOMORROW -> if (isDark) 0xFFFFE3E3.toInt() else 0xFFD95D5D.toInt()
                        WidgetTaskFormatter.WidgetDeadlineTone.SOON -> if (isDark) 0xFFFFDEB5.toInt() else 0xFFE68A3A.toInt()
                        WidgetTaskFormatter.WidgetDeadlineTone.UPCOMING -> if (isDark) 0xFFFFEFA8.toInt() else 0xFFE0B84C.toInt()
                        WidgetTaskFormatter.WidgetDeadlineTone.SAFE -> if (isDark) 0xFFD8FFE7.toInt() else 0xFF39A66A.toInt()
                        WidgetTaskFormatter.WidgetDeadlineTone.NONE -> secondaryText
                    }
                )
                setViewVisibility(R.id.widgetNextTaskContainer, if (expanded) View.VISIBLE else View.GONE)
                setOnClickPendingIntent(
                    R.id.widgetRoot,
                    openAppPendingIntent(context, AppDestination.Home.route)
                )
                setOnClickPendingIntent(
                    R.id.widgetNextTaskContainer,
                    openAppPendingIntent(context, AppDestination.Tasks.route)
                )
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
