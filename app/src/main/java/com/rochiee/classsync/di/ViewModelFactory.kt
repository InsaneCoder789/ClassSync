package com.rochiee.classsync.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rochiee.classsync.bloc.auth.AuthBlocViewModel
import com.rochiee.classsync.bloc.classroom.ClassroomScreenViewModel
import com.rochiee.classsync.bloc.eventdetail.EventDetailViewModel
import com.rochiee.classsync.bloc.exam.ExamModeBlocViewModel
import com.rochiee.classsync.bloc.event.EventBlocViewModel
import com.rochiee.classsync.bloc.planner.PlannerBlocViewModel
import com.rochiee.classsync.bloc.settings.SettingsBlocViewModel
import com.rochiee.classsync.bloc.study.StudyPlanBlocViewModel
import com.rochiee.classsync.bloc.sync.SyncBlocViewModel
import com.rochiee.classsync.bloc.task.TaskBlocViewModel

class ViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TaskBlocViewModel::class.java) -> {
                TaskBlocViewModel(
                    container.observeTasksUseCase,
                    container.addManualTaskUseCase,
                    container.markTaskCompletedUseCase,
                    container.deleteTaskUseCase,
                    container.syncGmailTasksUseCase,
                    container.syncClassroomCoursesUseCase,
                    container.syncClassroomCourseworkUseCase,
                    container.scheduleBackgroundSyncUseCase,
                    container.cancelBackgroundSyncUseCase,
                    container.runOneTimeFullSyncUseCase,
                    container.addSyncLogUseCase,
                    container.exportTasksCsvUseCase,
                    container.exportTasksJsonUseCase
                ) as T
            }
            modelClass.isAssignableFrom(AuthBlocViewModel::class.java) -> {
                AuthBlocViewModel(
                    container.googleAuthManager,
                    container.clearLocalAcademicDataUseCase
                ) as T
            }
            modelClass.isAssignableFrom(SyncBlocViewModel::class.java) -> {
                SyncBlocViewModel(
                    container.observeSyncLogsUseCase,
                    container.clearSyncLogsUseCase,
                    container.syncGmailTasksUseCase,
                    container.syncClassroomCoursesUseCase,
                    container.syncClassroomCourseworkUseCase,
                    container.runOneTimeFullSyncUseCase
                ) as T
            }
            modelClass.isAssignableFrom(SettingsBlocViewModel::class.java) -> {
                SettingsBlocViewModel(
                    container.observeSettingsUseCase,
                    container.setBackgroundSyncEnabledUseCase,
                    container.setGmailSyncEnabledUseCase,
                    container.setClassroomSyncEnabledUseCase,
                    container.setDefaultReminderHoursUseCase,
                    container.setOnboardingCompletedUseCase,
                    container.setClassroomPermissionExplainedUseCase,
                    container.setGmailPermissionExplainedUseCase,
                    container.setDigestEnabledUseCase,
                    container.setDigestHourOfDayUseCase,
                    container.setDigestIncludeAnnouncementsUseCase,
                    container.setDigestIncludeMaterialsUseCase,
                    container.setThemeModeUseCase,
                    container.setLastAppOpenTimeUseCase,
                    container.scheduleDailyDigestUseCase,
                    container.cancelDailyDigestUseCase,
                    container.previewDailyDigestUseCase,
                    container.scheduleBackgroundSyncUseCase,
                    container.cancelBackgroundSyncUseCase,
                    container.refreshWidgetsUseCase
                ) as T
            }
            modelClass.isAssignableFrom(EventBlocViewModel::class.java) -> {
                EventBlocViewModel(
                    container.observeAllEventsUseCase,
                    container.observeRecentEventsUseCase,
                    container.convertEventToTaskUseCase,
                    container.deleteClassroomEventUseCase
                ) as T
            }
            modelClass.isAssignableFrom(PlannerBlocViewModel::class.java) -> {
                PlannerBlocViewModel(
                    container.getTodayPlannerUseCase,
                    container.getWeekPlannerUseCase,
                    container.getMonthPlannerUseCase,
                    container.getPlannerRangeUseCase
                ) as T
            }
            modelClass.isAssignableFrom(EventDetailViewModel::class.java) -> {
                EventDetailViewModel(
                    container.classroomEventRepository,
                    container.summaryUseCase
                ) as T
            }
            modelClass.isAssignableFrom(StudyPlanBlocViewModel::class.java) -> {
                StudyPlanBlocViewModel(
                    container.generateStudyPlanUseCase,
                    container.settingsRepository
                ) as T
            }
            modelClass.isAssignableFrom(ExamModeBlocViewModel::class.java) -> {
                ExamModeBlocViewModel(
                    container.getExamModeUseCase,
                    container.settingsRepository
                ) as T
            }
            modelClass.isAssignableFrom(ClassroomScreenViewModel::class.java) -> {
                ClassroomScreenViewModel(
                    container.getClassroomCatalogUseCase
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
