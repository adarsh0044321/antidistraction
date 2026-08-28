package com.adarshsingh.antidistraction.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.adarshsingh.antidistraction.data.local.AppDatabase
import com.adarshsingh.antidistraction.data.local.dao.BlockedAppDao
import com.adarshsingh.antidistraction.data.local.dao.DailyGoalDao
import com.adarshsingh.antidistraction.data.local.dao.DistractionAttemptDao
import com.adarshsingh.antidistraction.data.local.dao.FocusProfileDao
import com.adarshsingh.antidistraction.data.local.dao.FocusSessionDao
import com.adarshsingh.antidistraction.data.local.dao.NotificationEventDao
import com.adarshsingh.antidistraction.data.local.dao.ProductivitySnapshotDao
import com.adarshsingh.antidistraction.data.local.dao.ScheduleDao
import com.adarshsingh.antidistraction.data.local.dao.WakeAlarmDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `notification_events` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `packageName` TEXT NOT NULL,
                `title` TEXT,
                `text` TEXT,
                `postTimeMs` INTEGER NOT NULL,
                `isSuppressed` INTEGER NOT NULL DEFAULT 1
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `schedules` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `profileId` INTEGER NOT NULL,
                `mode` TEXT NOT NULL,
                `daysOfWeekMask` INTEGER NOT NULL,
                `startMinuteOfDay` INTEGER NOT NULL,
                `endMinuteOfDay` INTEGER NOT NULL,
                `isEnabled` INTEGER NOT NULL DEFAULT 1
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

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "anti_distraction_db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideFocusSessionDao(database: AppDatabase): FocusSessionDao = database.focusSessionDao()

    @Provides
    fun provideBlockedAppDao(database: AppDatabase): BlockedAppDao = database.blockedAppDao()

    @Provides
    fun provideDistractionAttemptDao(database: AppDatabase): DistractionAttemptDao = database.distractionAttemptDao()

    @Provides
    fun provideFocusProfileDao(database: AppDatabase): FocusProfileDao = database.focusProfileDao()

    @Provides
    fun provideNotificationEventDao(database: AppDatabase): NotificationEventDao = database.notificationEventDao()

    @Provides
    fun provideScheduleDao(database: AppDatabase): ScheduleDao = database.scheduleDao()

    @Provides
    fun provideDailyGoalDao(database: AppDatabase): DailyGoalDao = database.dailyGoalDao()

    @Provides
    fun provideWakeAlarmDao(database: AppDatabase): WakeAlarmDao = database.wakeAlarmDao()

    @Provides
    fun provideProductivitySnapshotDao(database: AppDatabase): ProductivitySnapshotDao = database.productivitySnapshotDao()
}
