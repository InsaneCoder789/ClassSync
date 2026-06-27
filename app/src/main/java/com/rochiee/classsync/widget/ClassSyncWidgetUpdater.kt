package com.rochiee.classsync.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.TypedValue
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
            val isDark = themeMode == ThemeMode.DARK
            val rootSurface = if (isDark) R.drawable.widget_root_surface_dark else R.drawable.widget_root_surface
            val neutralCardSurface = if (isDark) R.drawable.widget_panel_surface_dark else R.drawable.widget_panel_surface
            val widgetOptions = appWidgetManager.getAppWidgetOptions(widgetId)
            val widgetHeightDp = widgetOptions.getInt(
                AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 180)
            )
            val contentCardHeightDp = ((widgetHeightDp - 104) * 0.58f).roundToInt().coerceIn(118, 164)
            val views = RemoteViews(context.packageName, R.layout.classsync_widget_layout).apply {
                setInt(R.id.widgetRoot, "setBackgroundResource", rootSurface)
                setViewLayoutHeight(
                    R.id.widgetNextTaskContainer,
                    contentCardHeightDp.toFloat(),
                    TypedValue.COMPLEX_UNIT_DIP
                )
                setImageViewResource(R.id.widgetLogo, R.mipmap.ic_launcher)
                setTextViewText(R.id.widgetTitle, "ClassSync")
                setTextViewText(
                    R.id.widgetSummaryText,
                    formatter.relativeSummary(summary.todayTaskCount, summary.urgentTaskCount)
                )
                setTextViewText(R.id.widgetUpdatedText, formatter.updatedText(summary.updatedAtMillis))
                setTextViewText(R.id.widgetTodayLabel, "Today")
                setTextViewText(R.id.widgetUrgentLabel, "Urgent")
                setTextViewText(R.id.widgetOverdueLabel, "Overdue")
                setTextViewText(R.id.widgetNextTaskLabel, "Primary focus")
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
                    WidgetTaskFormatter.WidgetDeadlineTone.TODAY -> if (isDark) 0xFFFFF1F1.toInt() else 0xFF5D0F17.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.TOMORROW -> if (isDark) 0xFFFFF0F0.toInt() else 0xFF711620.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.SOON -> if (isDark) 0xFFD8E8FF.toInt() else 0xFF1E4F8A.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.UPCOMING -> if (isDark) 0xFFDCEBFF.toInt() else 0xFF285D99.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.SAFE -> if (isDark) 0xFFD9FFE7.toInt() else 0xFF166534.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.NONE -> primaryText
                }
                val mutedAccentText = when (deadlineTone) {
                    WidgetTaskFormatter.WidgetDeadlineTone.OVERDUE,
                    WidgetTaskFormatter.WidgetDeadlineTone.TODAY -> if (isDark) 0xFFFFD0D0.toInt() else 0xFF8F1D2C.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.TOMORROW -> if (isDark) 0xFFFFD7D7.toInt() else 0xFFA12234.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.SOON -> if (isDark) 0xFFAFCFFF.toInt() else 0xFF3B73B9.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.UPCOMING -> if (isDark) 0xFFBDD8FF.toInt() else 0xFF4D7FC2.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.SAFE -> if (isDark) 0xFFA4E0BA.toInt() else 0xFF2F7A4D.toInt()
                    WidgetTaskFormatter.WidgetDeadlineTone.NONE -> secondaryText
                }
                setTextColor(R.id.widgetTitle, primaryText)
                setTextColor(R.id.widgetSummaryText, secondaryText)
                setTextColor(R.id.widgetUpdatedText, secondaryText)
                setTextColor(R.id.widgetTodayLabel, secondaryText)
                setTextColor(R.id.widgetTodayCount, primaryText)
                setTextColor(R.id.widgetUrgentLabel, secondaryText)
                setTextColor(R.id.widgetUrgentCount, primaryText)
                setTextColor(R.id.widgetOverdueLabel, secondaryText)
                setTextColor(R.id.widgetOverdueCount, primaryText)
                setTextColor(R.id.widgetNextTaskLabel, mutedAccentText)
                setTextColor(R.id.widgetNextTaskTitle, accentText)
                setTextColor(R.id.widgetNextTaskCourse, mutedAccentText)
                setTextColor(
                    R.id.widgetNextTaskDue,
                    when (deadlineTone) {
                        WidgetTaskFormatter.WidgetDeadlineTone.OVERDUE,
                        WidgetTaskFormatter.WidgetDeadlineTone.TODAY -> if (isDark) 0xFFFFE3E3.toInt() else 0xFF7A1220.toInt()
                        WidgetTaskFormatter.WidgetDeadlineTone.TOMORROW -> if (isDark) 0xFFFFE8E8.toInt() else 0xFF8C1A2A.toInt()
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
