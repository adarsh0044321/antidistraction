package com.adarshsingh.antidistraction.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adarshsingh.antidistraction.data.local.entity.ProductivitySnapshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductivitySnapshotDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(snapshot: ProductivitySnapshotEntity)

    @Query("SELECT * FROM productivity_snapshots ORDER BY dateStr DESC LIMIT 30")
    fun getRecentSnapshotsFlow(): Flow<List<ProductivitySnapshotEntity>>
}
