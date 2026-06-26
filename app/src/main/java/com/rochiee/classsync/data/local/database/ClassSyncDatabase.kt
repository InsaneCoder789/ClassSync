package com.rochiee.classsync.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rochiee.classsync.data.local.dao.CourseDao
import com.rochiee.classsync.data.local.dao.ClassroomEventDao
import com.rochiee.classsync.data.local.dao.SyncLogDao
import com.rochiee.classsync.data.local.dao.TaskDao
import com.rochiee.classsync.data.local.entity.ClassroomEventEntity
import com.rochiee.classsync.data.local.entity.CourseEntity
import com.rochiee.classsync.data.local.entity.SyncLogEntity
import com.rochiee.classsync.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class, CourseEntity::class, SyncLogEntity::class, ClassroomEventEntity::class],
    version = 7,
    exportSchema = false
)
abstract class ClassSyncDatabase : RoomDatabase() {
    abstract val taskDao: TaskDao
    abstract val courseDao: CourseDao
    abstract val syncLogDao: SyncLogDao
    abstract val classroomEventDao: ClassroomEventDao

    companion object {
        const val DATABASE_NAME = "classsync_db"
    }
}
