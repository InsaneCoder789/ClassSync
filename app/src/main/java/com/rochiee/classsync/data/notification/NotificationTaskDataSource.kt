package com.rochiee.classsync.data.notification

import com.rochiee.classsync.data.local.dao.NotificationDao
import com.rochiee.classsync.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

class NotificationTaskDataSource(
    private val notificationDao: NotificationDao
) {
    fun observeUnprocessedNotifications(): Flow<List<NotificationEntity>> =
        notificationDao.observeUnprocessedNotifications()

    suspend fun insertNotification(notification: NotificationEntity) {
        notificationDao.insertNotification(notification)
    }

    suspend fun markProcessed(id: Int) {
        notificationDao.markProcessed(id)
    }
}