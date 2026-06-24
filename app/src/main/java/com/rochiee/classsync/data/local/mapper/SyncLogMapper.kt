package com.rochiee.classsync.data.local.mapper

import com.rochiee.classsync.data.local.entity.SyncLogEntity
import com.rochiee.classsync.domain.model.SyncLog

fun SyncLogEntity.toDomain(): SyncLog {
    return SyncLog(
        id = id,
        source = source,
        status = status,
        message = message,
        timestamp = timestamp
    )
}

fun SyncLog.toEntity(): SyncLogEntity {
    return SyncLogEntity(
        id = id,
        source = source,
        status = status,
        message = message,
        timestamp = timestamp
    )
}
