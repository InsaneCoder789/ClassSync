package com.rochiee.classsync.domain.repository

import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventType
import kotlinx.coroutines.flow.Flow

interface ClassroomEventRepository {
    suspend fun getEventsSnapshot(): List<ClassroomEvent>
    fun observeAllEvents(): Flow<List<ClassroomEvent>>
    fun observeEventsByType(type: ClassroomEventType): Flow<List<ClassroomEvent>>
    fun observeRecentEvents(limit: Int): Flow<List<ClassroomEvent>>
    fun observeUnconvertedActionableEvents(): Flow<List<ClassroomEvent>>
    suspend fun getEventById(eventId: String): ClassroomEvent?
    suspend fun saveEvent(event: ClassroomEvent)
    suspend fun saveEvents(events: List<ClassroomEvent>)
    suspend fun markConvertedToTask(eventId: String, converted: Boolean)
    suspend fun deleteEvent(eventId: String)
    suspend fun clearAllEvents()
}
