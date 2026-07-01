package com.rochiee.classsync.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.widget.RemoteViews
import com.rochiee.classsync.ClassSyncApplication
import com.rochiee.classsync.MainActivity
import com.rochiee.classsync.R
import com.rochiee.classsync.domain.model.ThemeMode
import com.rochiee.classsync.ui.navigation.AppDestination
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

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
            val deadlineTone = formatter.deadlineTone(summary.primaryTaskDueMillis)
            val systemIsDark =
                (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            val isDark = when (themeMode) {
                ThemeMode.SYSTEM -> systemIsDark
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
            }
            val rootSurface = if (isDark) R.drawable.widget_root_surface_dark else R.drawable.widget_root_surface
            val neutralCardSurface = if (isDark) R.drawable.widget_panel_surface_dark else R.drawable.widget_panel_surface
            val widgetOptions = appWidgetManager.getAppWidgetOptions(widgetId)
            val widgetWidthDp = widgetOptions.getInt(
                AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
                widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 180)
            )
            val widgetHeightDp = widgetOptions.getInt(
                AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 140)
            )
            val isWideWidget = widgetWidthDp >= 250
            val isTallWidget = widgetHeightDp >= 228
            val isCompactWidget = widgetHeightDp < 220
            val views = RemoteViews(context.packageName, R.layout.classsync_widget_layout).apply {
                setInt(R.id.widgetRoot, "setBackgroundResource", rootSurface)
                setViewVisibility(
                    R.id.widgetStatsRow,
                    if (isCompactWidget) android.view.View.GONE else android.view.View.VISIBLE
                )
                setViewVisibility(
                    R.id.widgetUpdatedText,
                    if (isCompactWidget) android.view.View.GONE else android.view.View.VISIBLE
                )
                setInt(R.id.widgetNextTaskTitle, "setMaxLines", when {
                    isCompactWidget -> 1
                    isTallWidget -> 3
                    else -> 2
                })
                setInt(R.id.widgetNextTaskCourse, "setMaxLines", if (isTallWidget && !isCompactWidget) 2 else 1)
                setFloat(R.id.widgetNextTaskTitle, "setTextSize", if (isCompactWidget) 16f else 18f)
                setFloat(R.id.widgetNextTaskCourse, "setTextSize", if (isCompactWidget) 10f else 11f)
                setFloat(R.id.widgetNextTaskDue, "setTextSize", if (isCompactWidget) 8f else 9f)
                setImageViewResource(R.id.widgetLogo, R.mipmap.ic_launcher)
                setTextViewText(R.id.widgetTitle, "ClassSync")
                setTextViewText(
                    R.id.widgetSummaryText,
                    formatter.relativeSummary(summary.todayTaskCount, summary.urgentTaskCount)
                )
                setTextViewText(R.id.widgetUpdatedText, formatter.updatedText(summary.updatedAtMillis))
                setTextViewText(R.id.widgetTodayLabel, "Today")
                setTextViewText(R.id.widgetUrgentLabel, "Urgent")
                setTextViewText(R.id.widgetStatusPill, "Overdue ${summary.overdueTaskCount}")
                setTextViewText(R.id.widgetNextTaskLabel, "Primary focus")
                setInt(R.id.widgetTodayCard, "setBackgroundResource", neutralCardSurface)
                setInt(R.id.widgetUrgentCard, "setBackgroundResource", neutralCardSurface)
                setTextViewText(R.id.widgetTodayCount, "${summary.todayTaskCount}")
                setTextViewText(R.id.widgetUrgentCount, "${summary.urgentTaskCount}")
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
                    formatter.compactDueText(summary.primaryTaskDueMillis)
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
                val primaryText = if (isDark) 0xFFF7FAFF.toInt() else 0xFF101828.toInt()
                val secondaryText = if (isDark) 0xFFAEB8C4.toInt() else 0xFF667085.toInt()
                val accentText = when (deadlineTone) {
                    WidgetTaskFormatter.WidgetDeadlineTone.OVERDUE,
                    WidgetTaskFormatter.WidgetDeadlineTone.TODAY -> if (isDark) 0xFFFFF1F1.toInt() else 0xFFFFF3F3.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.TOMORROW -> if (isDark) 0xFFFFF0F0.toInt() else 0xFFFFF5F5.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.SOON -> if (isDark) 0xFFD8E8FF.toInt() else 0xFF1E4F8A.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.UPCOMING -> if (isDark) 0xFFDCEBFF.toInt() else 0xFF285D99.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.SAFE -> if (isDark) 0xFFD9FFE7.toInt() else 0xFF166534.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.NONE -> primaryText
                }
                val mutedAccentText = when (deadlineTone) {
                    WidgetTaskFormatter.WidgetDeadlineTone.OVERDUE,
                    WidgetTaskFormatter.WidgetDeadlineTone.TODAY -> if (isDark) 0xFFFFD0D0.toInt() else 0xFFFFDFDF.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.TOMORROW -> if (isDark) 0xFFFFD7D7.toInt() else 0xFFFFE6E6.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.SOON -> if (isDark) 0xFFAFCFFF.toInt() else 0xFF3B73B9.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.UPCOMING -> if (isDark) 0xFFBDD8FF.toInt() else 0xFF4D7FC2.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.SAFE -> if (isDark) 0xFFA4E0BA.toInt() else 0xFF2F7A4D.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.NONE -> secondaryText
                }
                setTextColor(R.id.widgetTitle, primaryText)
                setTextColor(R.id.widgetSummaryText, secondaryText)
                setTextColor(R.id.widgetUpdatedText, secondaryText)
                setTextColor(
                    R.id.widgetStatusPill,
                    if (summary.overdueTaskCount > 0) {
                        if (isDark) 0xFFF0B9B9.toInt() else 0xFF9B5555.toInt()
                    } else {
                        if (isDark) 0xFF9DBAA7.toInt() else 0xFF5C7263.toInt()
                    }
                )
                setTextColor(R.id.widgetTodayLabel, secondaryText)
                setTextColor(R.id.widgetTodayCount, primaryText)
                setTextColor(R.id.widgetUrgentLabel, secondaryText)
                setTextColor(R.id.widgetUrgentCount, primaryText)
                setTextColor(R.id.widgetNextTaskLabel, mutedAccentText)
                setTextColor(R.id.widgetNextTaskTitle, accentText)
                setTextColor(R.id.widgetNextTaskCourse, mutedAccentText)
                setTextColor(
                    R.id.widgetNextTaskDue,
                    when (deadlineTone) {
                        WidgetTaskFormatter.WidgetDeadlineTone.OVERDUE,
                        WidgetTaskFormatter.WidgetDeadlineTone.TODAY -> if (isDark) 0xFFFFE3E3.toInt() else 0xFFFFEEEE.toInt()
                        WidgetTaskFormatter.WidgetDeadlineTone.TOMORROW -> if (isDark) 0xFFFFE8E8.toInt() else 0xFFFFF0F0.toInt()
                        WidgetTaskFormatter.WidgetDeadlineTone.SOON -> if (isDark) 0xFFDCEBFF.toInt() else 0xFF295C99.toInt()
                        WidgetTaskFormatter.WidgetDeadlineTone.UPCOMING -> if (isDark) 0xFFE3F0FF.toInt() else 0xFF376EAF.toInt()
                        WidgetTaskFormatter.WidgetDeadlineTone.SAFE -> if (isDark) 0xFFD9FFE7.toInt() else 0xFF1F7A42.toInt()
                        WidgetTaskFormatter.WidgetDeadlineTone.NONE -> secondaryText
                    }
                )
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
