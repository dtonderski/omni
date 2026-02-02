package com.omni.metrics.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MetricDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(metric: MetricEntity): Long

    @Update
    suspend fun update(metric: MetricEntity)

    @Delete
    suspend fun delete(metric: MetricEntity)

    @Query("SELECT * FROM metrics ORDER BY name ASC")
    fun observeAll(): Flow<List<MetricEntity>>

    @Query("SELECT * FROM metrics WHERE id = :id")
    suspend fun getById(id: Long): MetricEntity?
}

@Dao
interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: EntryEntity): Long

    @Query(
        "SELECT * FROM entries " +
            "WHERE metricId = :metricId " +
            "ORDER BY periodEnd DESC " +
            "LIMIT 1"
    )
    fun observeLatest(metricId: Long): Flow<EntryEntity?>

    @Query(
        "SELECT * FROM entries " +
            "WHERE metricId = :metricId " +
            "AND periodStart >= :from " +
            "AND periodEnd <= :to " +
            "ORDER BY periodStart ASC"
    )
    fun observeEntriesForRange(
        metricId: Long,
        from: LocalDate,
        to: LocalDate
    ): Flow<List<EntryEntity>>
}

@Dao
interface ObjectiveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(objective: ObjectiveEntity): Long

    @Query("SELECT * FROM objectives WHERE metricId = :metricId")
    fun observeByMetric(metricId: Long): Flow<List<ObjectiveEntity>>
}

@Dao
interface MilestoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(milestones: List<MilestoneEntity>)

    @Query("SELECT * FROM milestones WHERE objectiveId = :objectiveId ORDER BY rank DESC")
    fun observeByObjective(objectiveId: Long): Flow<List<MilestoneEntity>>
}
