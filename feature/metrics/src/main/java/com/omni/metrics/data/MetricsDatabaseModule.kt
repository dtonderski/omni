package com.omni.metrics.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MetricsDatabaseModule {
    @Provides
    @Singleton
    fun provideMetricsDatabase(
        @ApplicationContext context: Context
    ): MetricsDatabase {
        lateinit var database: MetricsDatabase
        val callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    seedMetrics(database.metricDao())
                }
            }
        }
        database = Room.databaseBuilder(
            context,
            MetricsDatabase::class.java,
            "metrics.db"
        ).addCallback(callback).build()
        return database
    }

    @Provides
    fun provideMetricDao(database: MetricsDatabase): MetricDao = database.metricDao()

    @Provides
    fun provideEntryDao(database: MetricsDatabase): EntryDao = database.entryDao()

    @Provides
    fun provideObjectiveDao(database: MetricsDatabase): ObjectiveDao = database.objectiveDao()

    @Provides
    fun provideMilestoneDao(database: MetricsDatabase): MilestoneDao = database.milestoneDao()
}

private suspend fun seedMetrics(metricDao: MetricDao) {
    val now = System.currentTimeMillis()
    metricDao.upsert(
        MetricEntity(
            name = "Workouts",
            type = MetricType.BOOLEAN,
            unit = "days",
            currentResolution = MetricResolution.DAILY,
            maxValueForColor = null,
            accentColor = 0xFFFFA24A.toInt(),
            iconKey = "directions_run",
            createdAt = now
        )
    )
    metricDao.upsert(
        MetricEntity(
            name = "Protein Goal Met",
            type = MetricType.BOOLEAN,
            unit = "days",
            currentResolution = MetricResolution.DAILY,
            maxValueForColor = null,
            accentColor = 0xFF7AC6FF.toInt(),
            iconKey = "restaurant",
            createdAt = now
        )
    )
    metricDao.upsert(
        MetricEntity(
            name = "Sleep 8+ Hours",
            type = MetricType.BOOLEAN,
            unit = "days",
            currentResolution = MetricResolution.DAILY,
            maxValueForColor = null,
            accentColor = 0xFF9B8CFF.toInt(),
            iconKey = "nights_stay",
            createdAt = now
        )
    )
    metricDao.upsert(
        MetricEntity(
            name = "Leetcode Points",
            type = MetricType.INT,
            unit = "pts",
            currentResolution = MetricResolution.DAILY,
            maxValueForColor = null,
            accentColor = 0xFF4CC7B3.toInt(),
            iconKey = "code",
            createdAt = now
        )
    )
    metricDao.upsert(
        MetricEntity(
            name = "Emergency Fund",
            type = MetricType.INT,
            unit = "USD",
            currentResolution = MetricResolution.MONTHLY,
            maxValueForColor = null,
            accentColor = 0xFFE8A860.toInt(),
            iconKey = "savings",
            createdAt = now
        )
    )
    metricDao.upsert(
        MetricEntity(
            name = "Weight",
            type = MetricType.INT,
            unit = "kg",
            currentResolution = MetricResolution.DAILY,
            maxValueForColor = null,
            accentColor = 0xFF67D18C.toInt(),
            iconKey = "monitor_weight",
            createdAt = now
        )
    )
    metricDao.upsert(
        MetricEntity(
            name = "Family Visits",
            type = MetricType.BOOLEAN,
            unit = "weekends",
            currentResolution = MetricResolution.WEEKLY,
            maxValueForColor = null,
            accentColor = 0xFFFF8FA3.toInt(),
            iconKey = "people",
            createdAt = now
        )
    )
}
