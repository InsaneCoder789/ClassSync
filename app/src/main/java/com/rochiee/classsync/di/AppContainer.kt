package com.rochiee.classsync.di

import android.content.Context
import androidx.room.Room
import com.rochiee.classsync.auth.AuthTokenProvider
import com.rochiee.classsync.auth.GoogleAuthManager
import com.rochiee.classsync.ai.AiSummaryProvider
import com.rochiee.classsync.ai.AnnouncementSummarizer
import com.rochiee.classsync.ai.NoOpAiSummaryProvider
import com.rochiee.classsync.data.local.database.ClassSyncDatabase
import com.rochiee.classsync.data.local.preferences.SettingsDataStore
import com.rochiee.classsync.data.local.preferences.TaskSuppressionStore
import com.rochiee.classsync.data.remote.classroom.ClassroomApiClient
import com.rochiee.classsync.data.remote.classroom.ClassroomRemoteDataSource
import com.rochiee.classsync.data.remote.gmail.GmailApiClient
import com.rochiee.classsync.data.remote.gmail.GmailRemoteDataSource
import com.rochiee.classsync.data.repository.ClassroomCatalogRepositoryImpl
import com.rochiee.classsync.data.repository.ClassroomEventRepositoryImpl
import com.rochiee.classsync.data.repository.ClassroomRepositoryImpl
import com.rochiee.classsync.data.repository.GmailRepositoryImpl
import com.rochiee.classsync.data.repository.SettingsRepositoryImpl
import com.rochiee.classsync.data.repository.SyncLogRepositoryImpl
import com.rochiee.classsync.data.repository.TaskRepositoryImpl
import com.rochiee.classsync.dashboard.DashboardAggregator
import com.rochiee.classsync.dashboard.CourseDashboardAggregator
import com.rochiee.classsync.digest.DigestAggregator
import com.rochiee.classsync.digest.DigestScheduler
import com.rochiee.classsync.exam.ExamModeAggregator
import com.rochiee.classsync.domain.repository.ClassroomCatalogRepository
import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.repository.ClassroomRepository
import com.rochiee.classsync.domain.repository.GmailRepository
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.domain.repository.SyncLogRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.domain.usecase.planner.GetMonthPlannerUseCase
import com.rochiee.classsync.domain.usecase.planner.GetPlannerRangeUseCase
import com.rochiee.classsync.domain.usecase.planner.GetTodayPlannerUseCase
import com.rochiee.classsync.domain.usecase.planner.GetWeekPlannerUseCase
import com.rochiee.classsync.domain.usecase.ai.SummaryUseCase
import com.rochiee.classsync.domain.usecase.auth.ClearLocalAcademicDataUseCase
import com.rochiee.classsync.domain.usecase.digest.CancelDailyDigestUseCase
import com.rochiee.classsync.domain.usecase.digest.GenerateDigestSummaryUseCase
import com.rochiee.classsync.domain.usecase.digest.PreviewDailyDigestUseCase
import com.rochiee.classsync.domain.usecase.digest.ScheduleDailyDigestUseCase
import com.rochiee.classsync.domain.usecase.exam.GetExamModeUseCase
import com.rochiee.classsync.domain.usecase.classroom.GetClassroomCatalogUseCase
import com.rochiee.classsync.domain.usecase.classroom.ObserveClassroomCoursesUseCase
import com.rochiee.classsync.domain.usecase.classroom.SyncClassroomCoursesUseCase
import com.rochiee.classsync.domain.usecase.classroom.SyncClassroomCourseworkUseCase
import com.rochiee.classsync.domain.usecase.event.ConvertEventToTaskUseCase
import com.rochiee.classsync.domain.usecase.event.DeleteClassroomEventUseCase
import com.rochiee.classsync.domain.usecase.event.ObserveAllEventsUseCase
import com.rochiee.classsync.domain.usecase.event.ObserveEventsByTypeUseCase
import com.rochiee.classsync.domain.usecase.event.ObserveRecentEventsUseCase
import com.rochiee.classsync.domain.usecase.event.SaveClassroomEventUseCase
import com.rochiee.classsync.domain.usecase.export.ExportTasksCsvUseCase
import com.rochiee.classsync.domain.usecase.export.ExportTasksJsonUseCase
import com.rochiee.classsync.domain.usecase.gmail.SyncGmailTasksUseCase
import com.rochiee.classsync.domain.usecase.settings.ObserveSettingsUseCase
import com.rochiee.classsync.domain.usecase.settings.SetBackgroundSyncEnabledUseCase
import com.rochiee.classsync.domain.usecase.settings.SetClassroomSyncEnabledUseCase
import com.rochiee.classsync.domain.usecase.settings.SetClassroomPermissionExplainedUseCase
import com.rochiee.classsync.domain.usecase.settings.SetDefaultReminderHoursUseCase
import com.rochiee.classsync.domain.usecase.settings.SetDigestEnabledUseCase
import com.rochiee.classsync.domain.usecase.settings.SetDigestHourOfDayUseCase
import com.rochiee.classsync.domain.usecase.settings.SetDigestIncludeAnnouncementsUseCase
import com.rochiee.classsync.domain.usecase.settings.SetDigestIncludeMaterialsUseCase
import com.rochiee.classsync.domain.usecase.settings.SetGmailSyncEnabledUseCase
import com.rochiee.classsync.domain.usecase.settings.SetGmailPermissionExplainedUseCase
import com.rochiee.classsync.domain.usecase.settings.SetLastAppOpenTimeUseCase
import com.rochiee.classsync.domain.usecase.settings.SetLastSyncTimeUseCase
import com.rochiee.classsync.domain.usecase.settings.SetOnboardingCompletedUseCase
import com.rochiee.classsync.domain.usecase.settings.SetThemeModeUseCase
import com.rochiee.classsync.domain.usecase.synclog.AddSyncLogUseCase
import com.rochiee.classsync.domain.usecase.synclog.ClearSyncLogsUseCase
import com.rochiee.classsync.domain.usecase.synclog.ObserveSyncLogsUseCase
import com.rochiee.classsync.domain.usecase.task.AddManualTaskUseCase
import com.rochiee.classsync.domain.usecase.task.DeleteTaskUseCase
import com.rochiee.classsync.domain.usecase.task.MarkTaskCompletedUseCase
import com.rochiee.classsync.domain.usecase.task.ObserveTasksUseCase
import com.rochiee.classsync.domain.usecase.study.GenerateStudyPlanUseCase
import com.rochiee.classsync.domain.usecase.worker.CancelBackgroundSyncUseCase
import com.rochiee.classsync.domain.usecase.worker.RunOneTimeFullSyncUseCase
import com.rochiee.classsync.domain.usecase.worker.ScheduleBackgroundSyncUseCase
import com.rochiee.classsync.domain.usecase.widget.RefreshWidgetsUseCase
import com.rochiee.classsync.export.TaskCsvExporter
import com.rochiee.classsync.export.TaskExportManager
import com.rochiee.classsync.export.TaskJsonExporter
import com.rochiee.classsync.eventengine.ClassroomEventParser
import com.rochiee.classsync.eventengine.EventToTaskConverter
import com.rochiee.classsync.ml.classifier.HybridEventClassifier
import com.rochiee.classsync.ml.classifier.RuleBasedEventClassifier
import com.rochiee.classsync.ml.classifier.TfLiteEventClassifier
import com.rochiee.classsync.planner.PlannerAggregator
import com.rochiee.classsync.reminder.ReminderScheduler
import com.rochiee.classsync.study.StudyPlanGenerator
import com.rochiee.classsync.widget.WidgetDataProvider
import com.rochiee.classsync.widget.WidgetTaskFormatter

interface AppContainer {
    val database: ClassSyncDatabase
    val aiSummaryProvider: AiSummaryProvider
    val announcementSummarizer: AnnouncementSummarizer
    val taskRepository: TaskRepository
    val classroomEventRepository: ClassroomEventRepository
    val settingsRepository: SettingsRepository
    val syncLogRepository: SyncLogRepository
    val classroomEventParser: ClassroomEventParser
    val eventToTaskConverter: EventToTaskConverter
    val dashboardAggregator: DashboardAggregator
    val courseDashboardAggregator: CourseDashboardAggregator
    val digestAggregator: DigestAggregator
    val examModeAggregator: ExamModeAggregator
    val studyPlanGenerator: StudyPlanGenerator
    val plannerAggregator: PlannerAggregator
    val widgetDataProvider: WidgetDataProvider
    val widgetTaskFormatter: WidgetTaskFormatter
    val observeTasksUseCase: ObserveTasksUseCase
    val addManualTaskUseCase: AddManualTaskUseCase
    val markTaskCompletedUseCase: MarkTaskCompletedUseCase
    val deleteTaskUseCase: DeleteTaskUseCase
    val exportTasksCsvUseCase: ExportTasksCsvUseCase
    val exportTasksJsonUseCase: ExportTasksJsonUseCase
    val observeSettingsUseCase: ObserveSettingsUseCase
    val setBackgroundSyncEnabledUseCase: SetBackgroundSyncEnabledUseCase
    val setGmailSyncEnabledUseCase: SetGmailSyncEnabledUseCase
    val setClassroomSyncEnabledUseCase: SetClassroomSyncEnabledUseCase
    val setDefaultReminderHoursUseCase: SetDefaultReminderHoursUseCase
    val setLastSyncTimeUseCase: SetLastSyncTimeUseCase
    val setLastAppOpenTimeUseCase: SetLastAppOpenTimeUseCase
    val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase
    val setClassroomPermissionExplainedUseCase: SetClassroomPermissionExplainedUseCase
    val setGmailPermissionExplainedUseCase: SetGmailPermissionExplainedUseCase
    val setDigestEnabledUseCase: SetDigestEnabledUseCase
    val setDigestHourOfDayUseCase: SetDigestHourOfDayUseCase
    val setDigestIncludeAnnouncementsUseCase: SetDigestIncludeAnnouncementsUseCase
    val setDigestIncludeMaterialsUseCase: SetDigestIncludeMaterialsUseCase
    val setThemeModeUseCase: SetThemeModeUseCase
    val observeAllEventsUseCase: ObserveAllEventsUseCase
    val observeEventsByTypeUseCase: ObserveEventsByTypeUseCase
    val observeRecentEventsUseCase: ObserveRecentEventsUseCase
    val saveClassroomEventUseCase: SaveClassroomEventUseCase
    val convertEventToTaskUseCase: ConvertEventToTaskUseCase
    val deleteClassroomEventUseCase: DeleteClassroomEventUseCase
    val summaryUseCase: SummaryUseCase
    val getTodayPlannerUseCase: GetTodayPlannerUseCase
    val getWeekPlannerUseCase: GetWeekPlannerUseCase
    val getMonthPlannerUseCase: GetMonthPlannerUseCase
    val getPlannerRangeUseCase: GetPlannerRangeUseCase
    val refreshWidgetsUseCase: RefreshWidgetsUseCase
    val generateDigestSummaryUseCase: GenerateDigestSummaryUseCase
    val generateStudyPlanUseCase: GenerateStudyPlanUseCase
    val getExamModeUseCase: GetExamModeUseCase
    val scheduleDailyDigestUseCase: ScheduleDailyDigestUseCase
    val cancelDailyDigestUseCase: CancelDailyDigestUseCase
    val previewDailyDigestUseCase: PreviewDailyDigestUseCase
    val observeSyncLogsUseCase: ObserveSyncLogsUseCase
    val addSyncLogUseCase: AddSyncLogUseCase
    val clearSyncLogsUseCase: ClearSyncLogsUseCase
    val googleAuthManager: GoogleAuthManager
    val clearLocalAcademicDataUseCase: ClearLocalAcademicDataUseCase
    val gmailRepository: GmailRepository
    val syncGmailTasksUseCase: SyncGmailTasksUseCase
    val classroomCatalogRepository: ClassroomCatalogRepository
    val getClassroomCatalogUseCase: GetClassroomCatalogUseCase
    val classroomRepository: ClassroomRepository
    val observeClassroomCoursesUseCase: ObserveClassroomCoursesUseCase
    val syncClassroomCoursesUseCase: SyncClassroomCoursesUseCase
    val syncClassroomCourseworkUseCase: SyncClassroomCourseworkUseCase
    val scheduleBackgroundSyncUseCase: ScheduleBackgroundSyncUseCase
    val cancelBackgroundSyncUseCase: CancelBackgroundSyncUseCase
    val runOneTimeFullSyncUseCase: RunOneTimeFullSyncUseCase
}

class AppContainerImpl(private val context: Context) : AppContainer {

    override val database: ClassSyncDatabase by lazy {
        Room.databaseBuilder(
            context,
            ClassSyncDatabase::class.java,
            ClassSyncDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    override val aiSummaryProvider: AiSummaryProvider by lazy {
        NoOpAiSummaryProvider()
    }

    override val announcementSummarizer: AnnouncementSummarizer by lazy {
        AnnouncementSummarizer(aiSummaryProvider)
    }

    private val taskCsvExporter: TaskCsvExporter by lazy {
        TaskCsvExporter()
    }

    private val taskJsonExporter: TaskJsonExporter by lazy {
        TaskJsonExporter()
    }

    private val taskExportManager: TaskExportManager by lazy {
        TaskExportManager(
            context = context.applicationContext,
            csvExporter = taskCsvExporter,
            jsonExporter = taskJsonExporter
        )
    }

    override val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(
            database.taskDao,
            reminderScheduler,
            context.applicationContext,
            taskSuppressionStore
        )
    }

    private val settingsDataStore: SettingsDataStore by lazy {
        SettingsDataStore(context.applicationContext)
    }

    private val taskSuppressionStore: TaskSuppressionStore by lazy {
        TaskSuppressionStore(context.applicationContext)
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(settingsDataStore)
    }

    override val classroomCatalogRepository: ClassroomCatalogRepository by lazy {
        ClassroomCatalogRepositoryImpl(context.applicationContext)
    }

    override val classroomEventRepository: ClassroomEventRepository by lazy {
        ClassroomEventRepositoryImpl(database.classroomEventDao)
    }

    override val syncLogRepository: SyncLogRepository by lazy {
        SyncLogRepositoryImpl(database.syncLogDao)
    }

    private val ruleBasedEventClassifier: RuleBasedEventClassifier by lazy {
        RuleBasedEventClassifier()
    }

    private val tfLiteEventClassifier: TfLiteEventClassifier by lazy {
        TfLiteEventClassifier(context.applicationContext)
    }

    private val hybridEventClassifier: HybridEventClassifier by lazy {
        HybridEventClassifier(
            ruleBasedEventClassifier = ruleBasedEventClassifier,
            tfLiteEventClassifier = tfLiteEventClassifier
        )
    }

    override val classroomEventParser: ClassroomEventParser by lazy {
        ClassroomEventParser(
            settingsRepository = settingsRepository,
            hybridEventClassifier = hybridEventClassifier
        )
    }

    override val eventToTaskConverter: EventToTaskConverter by lazy {
        EventToTaskConverter()
    }

    override val dashboardAggregator: DashboardAggregator by lazy {
        DashboardAggregator()
    }

    override val courseDashboardAggregator: CourseDashboardAggregator by lazy {
        CourseDashboardAggregator()
    }

    override val digestAggregator: DigestAggregator by lazy {
        DigestAggregator()
    }

    override val examModeAggregator: ExamModeAggregator by lazy {
        ExamModeAggregator()
    }

    override val studyPlanGenerator: StudyPlanGenerator by lazy {
        StudyPlanGenerator()
    }

    override val plannerAggregator: PlannerAggregator by lazy {
        PlannerAggregator()
    }

    private val digestScheduler: DigestScheduler by lazy {
        DigestScheduler(context.applicationContext)
    }

    override val widgetDataProvider: WidgetDataProvider by lazy {
        WidgetDataProvider(taskRepository)
    }

    override val widgetTaskFormatter: WidgetTaskFormatter by lazy {
        WidgetTaskFormatter()
    }

    override val observeTasksUseCase: ObserveTasksUseCase by lazy {
        ObserveTasksUseCase(taskRepository)
    }

    override val addManualTaskUseCase: AddManualTaskUseCase by lazy {
        AddManualTaskUseCase(taskRepository)
    }

    override val markTaskCompletedUseCase: MarkTaskCompletedUseCase by lazy {
        MarkTaskCompletedUseCase(taskRepository)
    }

    override val deleteTaskUseCase: DeleteTaskUseCase by lazy {
        DeleteTaskUseCase(taskRepository)
    }

    override val exportTasksCsvUseCase: ExportTasksCsvUseCase by lazy {
        ExportTasksCsvUseCase(taskRepository, taskExportManager)
    }

    override val exportTasksJsonUseCase: ExportTasksJsonUseCase by lazy {
        ExportTasksJsonUseCase(taskRepository, taskExportManager)
    }

    override val observeSettingsUseCase: ObserveSettingsUseCase by lazy {
        ObserveSettingsUseCase(settingsRepository)
    }

    override val setBackgroundSyncEnabledUseCase: SetBackgroundSyncEnabledUseCase by lazy {
        SetBackgroundSyncEnabledUseCase(settingsRepository)
    }

    override val setGmailSyncEnabledUseCase: SetGmailSyncEnabledUseCase by lazy {
        SetGmailSyncEnabledUseCase(settingsRepository)
    }

    override val setClassroomSyncEnabledUseCase: SetClassroomSyncEnabledUseCase by lazy {
        SetClassroomSyncEnabledUseCase(settingsRepository)
    }

    override val setDefaultReminderHoursUseCase: SetDefaultReminderHoursUseCase by lazy {
        SetDefaultReminderHoursUseCase(settingsRepository)
    }

    override val setLastSyncTimeUseCase: SetLastSyncTimeUseCase by lazy {
        SetLastSyncTimeUseCase(settingsRepository)
    }

    override val setLastAppOpenTimeUseCase: SetLastAppOpenTimeUseCase by lazy {
        SetLastAppOpenTimeUseCase(settingsRepository)
    }

    override val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase by lazy {
        SetOnboardingCompletedUseCase(settingsRepository)
    }

    override val setClassroomPermissionExplainedUseCase: SetClassroomPermissionExplainedUseCase by lazy {
        SetClassroomPermissionExplainedUseCase(settingsRepository)
    }

    override val setGmailPermissionExplainedUseCase: SetGmailPermissionExplainedUseCase by lazy {
        SetGmailPermissionExplainedUseCase(settingsRepository)
    }

    override val setDigestEnabledUseCase: SetDigestEnabledUseCase by lazy {
        SetDigestEnabledUseCase(settingsRepository)
    }

    override val setDigestHourOfDayUseCase: SetDigestHourOfDayUseCase by lazy {
        SetDigestHourOfDayUseCase(settingsRepository)
    }

    override val setDigestIncludeAnnouncementsUseCase: SetDigestIncludeAnnouncementsUseCase by lazy {
        SetDigestIncludeAnnouncementsUseCase(settingsRepository)
    }

    override val setDigestIncludeMaterialsUseCase: SetDigestIncludeMaterialsUseCase by lazy {
        SetDigestIncludeMaterialsUseCase(settingsRepository)
    }

    override val setThemeModeUseCase: SetThemeModeUseCase by lazy {
        SetThemeModeUseCase(settingsRepository)
    }

    override val observeAllEventsUseCase: ObserveAllEventsUseCase by lazy {
        ObserveAllEventsUseCase(classroomEventRepository)
    }

    override val observeEventsByTypeUseCase: ObserveEventsByTypeUseCase by lazy {
        ObserveEventsByTypeUseCase(classroomEventRepository)
    }

    override val observeRecentEventsUseCase: ObserveRecentEventsUseCase by lazy {
        ObserveRecentEventsUseCase(classroomEventRepository)
    }

    override val saveClassroomEventUseCase: SaveClassroomEventUseCase by lazy {
        SaveClassroomEventUseCase(classroomEventRepository)
    }

    override val convertEventToTaskUseCase: ConvertEventToTaskUseCase by lazy {
        ConvertEventToTaskUseCase(classroomEventRepository, taskRepository, eventToTaskConverter)
    }

    override val deleteClassroomEventUseCase: DeleteClassroomEventUseCase by lazy {
        DeleteClassroomEventUseCase(classroomEventRepository)
    }

    override val summaryUseCase: SummaryUseCase by lazy {
        SummaryUseCase(classroomEventRepository, announcementSummarizer)
    }

    override val getTodayPlannerUseCase: GetTodayPlannerUseCase by lazy {
        GetTodayPlannerUseCase(taskRepository, classroomEventRepository, plannerAggregator)
    }

    override val getWeekPlannerUseCase: GetWeekPlannerUseCase by lazy {
        GetWeekPlannerUseCase(taskRepository, classroomEventRepository, plannerAggregator)
    }

    override val getMonthPlannerUseCase: GetMonthPlannerUseCase by lazy {
        GetMonthPlannerUseCase(taskRepository, classroomEventRepository, plannerAggregator)
    }

    override val getPlannerRangeUseCase: GetPlannerRangeUseCase by lazy {
        GetPlannerRangeUseCase(taskRepository, classroomEventRepository, plannerAggregator)
    }

    override val refreshWidgetsUseCase: RefreshWidgetsUseCase by lazy {
        RefreshWidgetsUseCase(context.applicationContext)
    }

    override val generateDigestSummaryUseCase: GenerateDigestSummaryUseCase by lazy {
        GenerateDigestSummaryUseCase(
            taskRepository,
            classroomEventRepository,
            syncLogRepository,
            settingsRepository,
            digestAggregator
        )
    }

    override val generateStudyPlanUseCase: GenerateStudyPlanUseCase by lazy {
        GenerateStudyPlanUseCase(
            taskRepository,
            classroomEventRepository,
            studyPlanGenerator
        )
    }

    override val getExamModeUseCase: GetExamModeUseCase by lazy {
        GetExamModeUseCase(
            taskRepository,
            classroomEventRepository,
            examModeAggregator
        )
    }

    override val scheduleDailyDigestUseCase: ScheduleDailyDigestUseCase by lazy {
        ScheduleDailyDigestUseCase(digestScheduler)
    }

    override val cancelDailyDigestUseCase: CancelDailyDigestUseCase by lazy {
        CancelDailyDigestUseCase(digestScheduler)
    }

    override val previewDailyDigestUseCase: PreviewDailyDigestUseCase by lazy {
        PreviewDailyDigestUseCase(context.applicationContext, generateDigestSummaryUseCase)
    }

    private val reminderScheduler: ReminderScheduler by lazy {
        ReminderScheduler(context.applicationContext, settingsRepository)
    }

    override val observeSyncLogsUseCase: ObserveSyncLogsUseCase by lazy {
        ObserveSyncLogsUseCase(syncLogRepository)
    }

    override val addSyncLogUseCase: AddSyncLogUseCase by lazy {
        AddSyncLogUseCase(syncLogRepository)
    }

    override val clearSyncLogsUseCase: ClearSyncLogsUseCase by lazy {
        ClearSyncLogsUseCase(syncLogRepository)
    }

    override val googleAuthManager: GoogleAuthManager by lazy {
        GoogleAuthManager(context)
    }

    private val authTokenProvider: AuthTokenProvider by lazy {
        AuthTokenProvider(context.applicationContext, googleAuthManager)
    }

    // Gmail
    private val gmailApiClient: GmailApiClient by lazy {
        GmailApiClient(authTokenProvider)
    }

    private val gmailRemoteDataSource: GmailRemoteDataSource by lazy {
        GmailRemoteDataSource(gmailApiClient)
    }

    override val gmailRepository: GmailRepository by lazy {
        GmailRepositoryImpl(gmailRemoteDataSource)
    }

    override val syncGmailTasksUseCase: SyncGmailTasksUseCase by lazy {
        SyncGmailTasksUseCase(
            gmailRepository,
            taskRepository,
            syncLogRepository,
            classroomEventRepository,
            classroomEventParser,
            eventToTaskConverter,
            settingsRepository,
            setLastSyncTimeUseCase,
            refreshWidgetsUseCase
        )
    }

    override val clearLocalAcademicDataUseCase: ClearLocalAcademicDataUseCase by lazy {
        ClearLocalAcademicDataUseCase(
            taskRepository = taskRepository,
            classroomEventRepository = classroomEventRepository,
            classroomRepository = classroomRepository,
            syncLogRepository = syncLogRepository,
            settingsRepository = settingsRepository,
            taskSuppressionStore = taskSuppressionStore
        )
    }

    override val getClassroomCatalogUseCase: GetClassroomCatalogUseCase by lazy {
        GetClassroomCatalogUseCase(classroomCatalogRepository)
    }

    // Classroom
    private val classroomApiClient: ClassroomApiClient by lazy {
        ClassroomApiClient(authTokenProvider)
    }

    private val classroomRemoteDataSource: ClassroomRemoteDataSource by lazy {
        ClassroomRemoteDataSource(classroomApiClient)
    }

    override val classroomRepository: ClassroomRepository by lazy {
        ClassroomRepositoryImpl(classroomRemoteDataSource, database.courseDao)
    }

    override val syncClassroomCoursesUseCase: SyncClassroomCoursesUseCase by lazy {
        SyncClassroomCoursesUseCase(
            classroomRepository,
            syncLogRepository,
            settingsRepository,
            setLastSyncTimeUseCase
        )
    }

    override val observeClassroomCoursesUseCase: ObserveClassroomCoursesUseCase by lazy {
        ObserveClassroomCoursesUseCase(classroomRepository)
    }

    override val syncClassroomCourseworkUseCase: SyncClassroomCourseworkUseCase by lazy {
        SyncClassroomCourseworkUseCase(
            classroomRepository,
            taskRepository,
            syncLogRepository,
            classroomEventRepository,
            classroomEventParser,
            eventToTaskConverter,
            settingsRepository,
            setLastSyncTimeUseCase,
            refreshWidgetsUseCase
        )
    }

    override val scheduleBackgroundSyncUseCase: ScheduleBackgroundSyncUseCase by lazy {
        ScheduleBackgroundSyncUseCase(context.applicationContext, settingsRepository)
    }

    override val cancelBackgroundSyncUseCase: CancelBackgroundSyncUseCase by lazy {
        CancelBackgroundSyncUseCase(context.applicationContext)
    }

    override val runOneTimeFullSyncUseCase: RunOneTimeFullSyncUseCase by lazy {
        RunOneTimeFullSyncUseCase(context.applicationContext)
    }
}
