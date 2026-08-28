package com.adarshsingh.antidistraction.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.adarshsingh.antidistraction.data.local.entity.BlockedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedAppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateApp(app: BlockedAppEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apps: List<BlockedAppEntity>)

    @Update
    suspend fun updateApp(app: BlockedAppEntity)

    @Delete
    suspend fun deleteApp(app: BlockedAppEntity)

    @Query("SELECT * FROM blocked_apps WHERE packageName = :packageName")
    suspend fun getAppByPackageName(packageName: String): BlockedAppEntity?

    @Query("SELECT * FROM blocked_apps ORDER BY appName ASC")
    fun getAllAppsFlow(): Flow<List<BlockedAppEntity>>

    @Query("SELECT * FROM blocked_apps WHERE isEmergencyApp = 1")
    fun getEmergencyAppsFlow(): Flow<List<BlockedAppEntity>>

    @Query("UPDATE blocked_apps SET distractionScore = :score WHERE packageName = :packageName")
    suspend fun updateDistractionScore(packageName: String, score: Int)
}
