package com.antigravity.antidistraction.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.antigravity.antidistraction.data.local.entity.NotificationEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationEventDao {
    @Insert
    suspend fun insertNotification(event: NotificationEventEntity): Long

    @Query("SELECT * FROM notification_events WHERE isSuppressed = 1 ORDER BY postTimeMs DESC")
    fun getSuppressedNotificationsFlow(): Flow<List<NotificationEventEntity>>

    @Query("DELETE FROM notification_events")
    suspend fun clearAllNotifications()
}
