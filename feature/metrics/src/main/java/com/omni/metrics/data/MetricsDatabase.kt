package com.omni.metrics.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        MetricEntity::class,
        EntryEntity::class,
        ObjectiveEntity::class,
        MilestoneEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(MetricsConverters::class)
abstract class MetricsDatabase : RoomDatabase() {
    abstract fun metricDao(): MetricDao
    abstract fun entryDao(): EntryDao
    abstract fun objectiveDao(): ObjectiveDao
    abstract fun milestoneDao(): MilestoneDao
}
