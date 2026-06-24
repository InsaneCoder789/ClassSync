package com.rochiee.classsync.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rochiee.classsync.data.local.entity.ClassroomEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassroomEventDao {
    @Query("SELECT * FROM classroom_events ORDER BY eventTimeMillis DESC")
    suspend fun getAllEventsSnapshot(): List<ClassroomEventEntity>

    @Query("SELECT * FROM classroom_events ORDER BY eventTimeMillis DESC")
    fun observeAllEvents(): Flow<List<ClassroomEventEntity>>

    @Query("SELECT * FROM classroom_events WHERE eventType = :eventType ORDER BY eventTimeMillis DESC")
    fun observeEventsByType(eventType: String): Flow<List<ClassroomEventEntity>>

    @Query("SELECT * FROM classroom_events ORDER BY eventTimeMillis DESC LIMIT :limit")
    fun observeRecentEvents(limit: Int): Flow<List<ClassroomEventEntity>>

    @Query("""
        SELECT * FROM classroom_events
        WHERE convertedToTask = 0
        AND actionType IN ('TASK_REQUIRED', 'OPTIONAL_READING', 'DEADLINE_UPDATE')
        ORDER BY eventTimeMillis DESC
    """)
    fun observeUnconvertedActionableEvents(): Flow<List<ClassroomEventEntity>>

    @Query("SELECT * FROM classroom_events WHERE id = :eventId LIMIT 1")
    suspend fun getEventById(eventId: String): ClassroomEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEvent(event: ClassroomEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEvents(events: List<ClassroomEventEntity>)

    @Query("UPDATE classroom_events SET convertedToTask = :converted, updatedAtMillis = :updatedAtMillis WHERE id = :eventId")
    suspend fun markConvertedToTask(eventId: String, converted: Boolean, updatedAtMillis: Long)

    @Query("DELETE FROM classroom_events WHERE id = :eventId")
    suspend fun deleteEventById(eventId: String)

    @Query("DELETE FROM classroom_events")
    suspend fun clearEvents()
}
