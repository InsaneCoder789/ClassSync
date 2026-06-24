package com.rochiee.classsync.bloc.sync

sealed class SyncEvent {
    object ObserveLogs : SyncEvent()
    object RunManualFullSync : SyncEvent()
    object RunGmailSync : SyncEvent()
    object RunClassroomSync : SyncEvent()
    object ClearLogs : SyncEvent()
    object ClearError : SyncEvent()
}
