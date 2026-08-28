package com.adarshsingh.antidistraction.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.adarshsingh.antidistraction.data.local.entity.FocusProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: FocusProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: FocusProfileEntity)

    @Query("SELECT * FROM focus_profiles WHERE id = :id")
    suspend fun getProfileById(id: Long): FocusProfileEntity?

    @Query("SELECT * FROM focus_profiles WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultProfile(): FocusProfileEntity?

    @Query("SELECT * FROM focus_profiles ORDER BY name ASC")
    fun getAllProfilesFlow(): Flow<List<FocusProfileEntity>>
}
