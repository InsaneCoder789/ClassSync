package com.rochiee.classsync.data.local.dao

import androidx.room.*
import com.rochiee.classsync.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notifications ORDER BY postedAtMillis DESC")
    fun observeAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE processed = 0 ORDER BY postedAtMillis DESC")
    fun observeUnprocessedNotifications(): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET processed = 1 WHERE id = :id")
    suspend fun markProcessed(id: Int)

    @Query("DELETE FROM notifications WHERE postedAtMillis < :timestamp")
    suspend fun deleteOldNotifications(timestamp: Long)
}