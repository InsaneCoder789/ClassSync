package com.rochiee.classsync.data.repository

import com.rochiee.classsync.data.local.dao.ClassroomEventDao
import com.rochiee.classsync.data.local.mapper.toDomain
import com.rochiee.classsync.data.local.mapper.toEntity
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClassroomEventRepositoryImpl(
    private val dao: ClassroomEventDao
) : ClassroomEventRepository {
    override suspend fun getEventsSnapshot(): List<ClassroomEvent> {
        return dao.getAllEventsSnapshot().map { it.toDomain() }
    }

    override fun observeAllEvents(): Flow<List<ClassroomEvent>> {
        return dao.observeAllEvents().map { items -> items.map { it.toDomain() } }
    }

    override fun observeEventsByType(type: ClassroomEventType): Flow<List<ClassroomEvent>> {
        return dao.observeEventsByType(type.name).map { items -> items.map { it.toDomain() } }
    }

    override fun observeRecentEvents(limit: Int): Flow<List<ClassroomEvent>> {
        return dao.observeRecentEvents(limit).map { items -> items.map { it.toDomain() } }
    }

    override fun observeUnconvertedActionableEvents(): Flow<List<ClassroomEvent>> {
        return dao.observeUnconvertedActionableEvents().map { items -> items.map { it.toDomain() } }
    }

    override suspend fun getEventById(eventId: String): ClassroomEvent? {
        return dao.getEventById(eventId)?.toDomain()
    }

    override suspend fun saveEvent(event: ClassroomEvent) {
        dao.upsertEvent(event.toEntity())
    }

    override suspend fun saveEvents(events: List<ClassroomEvent>) {
        if (events.isNotEmpty()) {
            dao.upsertEvents(events.map { it.toEntity() })
        }
    }

    override suspend fun markConvertedToTask(eventId: String, converted: Boolean) {
        dao.markConvertedToTask(eventId, converted, System.currentTimeMillis())
    }

    override suspend fun deleteEvent(eventId: String) {
        dao.deleteEventById(eventId)
    }

    override suspend fun clearAllEvents() {
        dao.clearEvents()
    }
}
