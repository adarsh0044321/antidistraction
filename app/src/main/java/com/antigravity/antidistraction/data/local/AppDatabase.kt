package com.antigravity.antidistraction.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.antigravity.antidistraction.data.local.dao.BlockedAppDao
import com.antigravity.antidistraction.data.local.dao.DistractionAttemptDao
import com.antigravity.antidistraction.data.local.dao.FocusProfileDao
import com.antigravity.antidistraction.data.local.dao.FocusSessionDao
import com.antigravity.antidistraction.data.local.dao.NotificationEventDao
import com.antigravity.antidistraction.data.local.dao.ScheduleDao
import com.antigravity.antidistraction.data.local.entity.BlockedAppEntity
import com.antigravity.antidistraction.data.local.entity.DistractionAttemptEntity
import com.antigravity.antidistraction.data.local.entity.FocusProfileEntity
import com.antigravity.antidistraction.data.local.entity.FocusSessionEntity
import com.antigravity.antidistraction.data.local.entity.NotificationEventEntity
import com.antigravity.antidistraction.data.local.entity.ScheduleEntity
import com.antigravity.antidistraction.domain.model.FocusMode
import com.antigravity.antidistraction.domain.model.FocusState
import com.antigravity.antidistraction.domain.model.IntentionType
import com.antigravity.antidistraction.domain.model.InterventionLevel

class Converters {
    @TypeConverter
    fun fromFocusState(value: FocusState): String = value.name

    @TypeConverter
    fun toFocusState(value: String): FocusState = enumValueOf(value)

    @TypeConverter
    fun fromFocusMode(value: FocusMode): String = value.name

    @TypeConverter
    fun toFocusMode(value: String): FocusMode = enumValueOf(value)

    @TypeConverter
    fun fromInterventionLevel(value: InterventionLevel?): String? = value?.name

    @TypeConverter
    fun toInterventionLevel(value: String?): InterventionLevel? = value?.let { enumValueOf<InterventionLevel>(it) }

    @TypeConverter
    fun fromIntentionType(value: IntentionType?): String? = value?.name

    @TypeConverter
    fun toIntentionType(value: String?): IntentionType? = value?.let { enumValueOf<IntentionType>(it) }
}

@Database(
    entities = [
        FocusSessionEntity::class,
        BlockedAppEntity::class,
        DistractionAttemptEntity::class,
        FocusProfileEntity::class,
        NotificationEventEntity::class,
        ScheduleEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun blockedAppDao(): BlockedAppDao
    abstract fun distractionAttemptDao(): DistractionAttemptDao
    abstract fun focusProfileDao(): FocusProfileDao
    abstract fun notificationEventDao(): NotificationEventDao
    abstract fun scheduleDao(): ScheduleDao
}
