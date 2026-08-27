package com.antigravity.antidistraction.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.antigravity.antidistraction.data.local.AppDatabase
import com.antigravity.antidistraction.data.local.dao.BlockedAppDao
import com.antigravity.antidistraction.data.local.dao.DistractionAttemptDao
import com.antigravity.antidistraction.data.local.dao.FocusProfileDao
import com.antigravity.antidistraction.data.local.dao.FocusSessionDao
import com.antigravity.antidistraction.data.local.dao.NotificationEventDao
import com.antigravity.antidistraction.data.local.dao.ScheduleDao
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
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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
}
