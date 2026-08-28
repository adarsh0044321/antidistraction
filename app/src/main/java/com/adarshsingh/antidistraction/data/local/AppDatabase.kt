package com.adarshsingh.antidistraction.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.adarshsingh.antidistraction.data.local.dao.BlockedAppDao
import com.adarshsingh.antidistraction.data.local.dao.DailyGoalDao
import com.adarshsingh.antidistraction.data.local.dao.DistractionAttemptDao
import com.adarshsingh.antidistraction.data.local.dao.FocusProfileDao
import com.adarshsingh.antidistraction.data.local.dao.FocusSessionDao
import com.adarshsingh.antidistraction.data.local.dao.NotificationEventDao
import com.adarshsingh.antidistraction.data.local.dao.ProductivitySnapshotDao
import com.adarshsingh.antidistraction.data.local.dao.ScheduleDao
import com.adarshsingh.antidistraction.data.local.dao.WakeAlarmDao
import com.adarshsingh.antidistraction.data.local.entity.BlockedAppEntity
import com.adarshsingh.antidistraction.data.local.entity.DailyGoalEntity
import com.adarshsingh.antidistraction.data.local.entity.DistractionAttemptEntity
import com.adarshsingh.antidistraction.data.local.entity.FocusProfileEntity
import com.adarshsingh.antidistraction.data.local.entity.FocusSessionEntity
import com.adarshsingh.antidistraction.data.local.entity.NotificationEventEntity
import com.adarshsingh.antidistraction.data.local.entity.ProductivitySnapshotEntity
import com.adarshsingh.antidistraction.data.local.entity.ScheduleEntity
import com.adarshsingh.antidistraction.data.local.entity.WakeAlarmEntity
import com.adarshsingh.antidistraction.domain.model.FocusMode
import com.adarshsingh.antidistraction.domain.model.FocusState
import com.adarshsingh.antidistraction.domain.model.IntentionType
import com.adarshsingh.antidistraction.domain.model.InterventionLevel

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
        ScheduleEntity::class,
        DailyGoalEntity::class,
        WakeAlarmEntity::class,
        ProductivitySnapshotEntity::class
    ],
    version = 4,
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
    abstract fun dailyGoalDao(): DailyGoalDao
    abstract fun wakeAlarmDao(): WakeAlarmDao
    abstract fun productivitySnapshotDao(): ProductivitySnapshotDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `focus_sessions` ADD COLUMN `totalInterventions` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `focus_sessions` ADD COLUMN `totalBypasses` INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `schedules` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `startHour` INTEGER NOT NULL,
                        `startMinute` INTEGER NOT NULL,
                        `endHour` INTEGER NOT NULL,
                        `endMinute` INTEGER NOT NULL,
                        `daysOfWeek` TEXT NOT NULL,
                        `profileId` INTEGER NOT NULL,
                        `isEnabled` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `daily_goals` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `targetDurationMs` INTEGER NOT NULL,
                        `completedDurationMs` INTEGER NOT NULL,
                        `category` TEXT NOT NULL,
                        `priority` TEXT NOT NULL,
                        `isCompleted` INTEGER NOT NULL,
                        `createdDateStr` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `wake_alarms` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `timeHour` INTEGER NOT NULL,
                        `timeMinute` INTEGER NOT NULL,
                        `isEnabled` INTEGER NOT NULL,
                        `repeatDays` TEXT NOT NULL,
                        `plannedBedtimeHour` INTEGER NOT NULL,
                        `plannedBedtimeMinute` INTEGER NOT NULL,
                        `minimumSleepDurationHours` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `productivity_snapshots` (
                        `dateStr` TEXT PRIMARY KEY NOT NULL,
                        `score` INTEGER NOT NULL,
                        `focusTimeMinutes` INTEGER NOT NULL,
                        `goalsCompletedCount` INTEGER NOT NULL,
                        `totalGoalsCount` INTEGER NOT NULL,
                        `distractionResistanceRate` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
