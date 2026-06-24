package com.rochiee.classsync.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rochiee.classsync.data.local.dao.CourseDao
import com.rochiee.classsync.data.local.dao.ClassroomEventDao
import com.rochiee.classsync.data.local.dao.NotificationDao
import com.rochiee.classsync.data.local.dao.SyncLogDao
import com.rochiee.classsync.data.local.dao.TaskDao
import com.rochiee.classsync.data.local.entity.ClassroomEventEntity
import com.rochiee.classsync.data.local.entity.CourseEntity
import com.rochiee.classsync.data.local.entity.NotificationEntity
import com.rochiee.classsync.data.local.entity.SyncLogEntity
import com.rochiee.classsync.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class, NotificationEntity::class, CourseEntity::class, SyncLogEntity::class, ClassroomEventEntity::class],
    version = 6,
    exportSchema = false
)
abstract class ClassSyncDatabase : RoomDatabase() {
    abstract val taskDao: TaskDao
    abstract val notificationDao: NotificationDao
    abstract val courseDao: CourseDao
    abstract val syncLogDao: SyncLogDao
    abstract val classroomEventDao: ClassroomEventDao

    companion object {
        const val DATABASE_NAME = "classsync_db"
    }
}
