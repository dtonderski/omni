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
import java.time.LocalDate
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
                    seedMetrics(
                        database.metricDao(),
                        database.entryDao(),
                        database.objectiveDao(),
                        database.milestoneDao()
                    )
                }
            }
        }
        database = Room.databaseBuilder(
            context,
            MetricsDatabase::class.java,
            "metrics.db"
        ).addCallback(callback)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
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

private suspend fun seedMetrics(
    metricDao: MetricDao,
    entryDao: EntryDao,
    objectiveDao: ObjectiveDao,
    milestoneDao: MilestoneDao
) {
    val now = System.currentTimeMillis()
    val today = LocalDate.now()
    val workoutsId = metricDao.upsert(
        MetricEntity(
            name = "Workouts",
            kind = MetricKind.EVENT,
            type = MetricType.BOOLEAN,
            unit = "days",
            currentResolution = MetricResolution.DAILY,
            displayResolution = MetricResolution.WEEKLY,
            displayAggregation = DisplayAggregationType.TOTAL,
            maxValueForColor = null,
            accentColor = 0xFFFFA24A.toInt(),
            iconKey = "directions_run",
            createdAt = now
        )
    )
    val proteinId = metricDao.upsert(
        MetricEntity(
            name = "Protein Goal Met",
            kind = MetricKind.EVENT,
            type = MetricType.BOOLEAN,
            unit = "days",
            currentResolution = MetricResolution.DAILY,
            displayResolution = MetricResolution.WEEKLY,
            displayAggregation = DisplayAggregationType.TOTAL,
            maxValueForColor = null,
            accentColor = 0xFF7AC6FF.toInt(),
            iconKey = "restaurant",
            createdAt = now
        )
    )
    val sleepId = metricDao.upsert(
        MetricEntity(
            name = "Sleep 8+ Hours",
            kind = MetricKind.EVENT,
            type = MetricType.BOOLEAN,
            unit = "days",
            currentResolution = MetricResolution.DAILY,
            displayResolution = MetricResolution.WEEKLY,
            displayAggregation = DisplayAggregationType.TOTAL,
            maxValueForColor = null,
            accentColor = 0xFF9B8CFF.toInt(),
            iconKey = "nights_stay",
            createdAt = now
        )
    )
    val leetcodeId = metricDao.upsert(
        MetricEntity(
            name = "Leetcode Points",
            kind = MetricKind.EVENT,
            type = MetricType.INT,
            unit = "pts",
            currentResolution = MetricResolution.DAILY,
            displayResolution = MetricResolution.WEEKLY,
            displayAggregation = DisplayAggregationType.TOTAL,
            maxValueForColor = null,
            accentColor = 0xFF4CC7B3.toInt(),
            iconKey = "code",
            createdAt = now
        )
    )
    val emergencyFundId = metricDao.upsert(
        MetricEntity(
            name = "Emergency Fund",
            kind = MetricKind.STATE,
            type = MetricType.INT,
            unit = "USD",
            currentResolution = MetricResolution.MONTHLY,
            displayResolution = MetricResolution.MONTHLY,
            displayAggregation = DisplayAggregationType.LATEST,
            maxValueForColor = null,
            accentColor = 0xFFE8A860.toInt(),
            iconKey = "savings",
            createdAt = now
        )
    )
    val weightId = metricDao.upsert(
        MetricEntity(
            name = "Weight",
            kind = MetricKind.STATE,
            type = MetricType.INT,
            unit = "kg",
            currentResolution = MetricResolution.DAILY,
            displayResolution = MetricResolution.DAILY,
            displayAggregation = DisplayAggregationType.LATEST,
            maxValueForColor = null,
            accentColor = 0xFF67D18C.toInt(),
            iconKey = "monitor_weight",
            createdAt = now
        )
    )
    val familyId = metricDao.upsert(
        MetricEntity(
            name = "Family Visits",
            kind = MetricKind.EVENT,
            type = MetricType.BOOLEAN,
            unit = "weekends",
            currentResolution = MetricResolution.WEEKLY,
            displayResolution = MetricResolution.MONTHLY,
            displayAggregation = DisplayAggregationType.TOTAL,
            maxValueForColor = null,
            accentColor = 0xFFFF8FA3.toInt(),
            iconKey = "people",
            createdAt = now
        )
    )

    seedObjectives(
        objectiveDao = objectiveDao,
        milestoneDao = milestoneDao,
        emergencyFundId = emergencyFundId,
        workoutsId = workoutsId,
        sleepId = sleepId,
        leetcodeId = leetcodeId,
        familyId = familyId,
        today = today,
        now = now
    )

    seedEntries(
        entryDao = entryDao,
        today = today,
        workoutsId = workoutsId,
        proteinId = proteinId,
        sleepId = sleepId,
        leetcodeId = leetcodeId,
        emergencyFundId = emergencyFundId,
        weightId = weightId,
        familyId = familyId
    )
}

private suspend fun seedObjectives(
    objectiveDao: ObjectiveDao,
    milestoneDao: MilestoneDao,
    emergencyFundId: Long,
    workoutsId: Long,
    sleepId: Long,
    leetcodeId: Long,
    familyId: Long,
    today: LocalDate,
    now: Long
) {
    val start = LocalDate.of(today.year, 1, 1)
    val end = LocalDate.of(today.year, 12, 31)
    val objectiveId = objectiveDao.upsert(
        ObjectiveEntity(
            metricId = emergencyFundId,
            name = "Emergency Fund ${today.year}",
            evaluationStart = start,
            evaluationEnd = end,
            aggregationType = ObjectiveAggregationType.LATEST,
            polarity = ObjectivePolarity.HIGHER_IS_BETTER,
            createdAt = now
        )
    )
    milestoneDao.upsertAll(
        listOf(
            MilestoneEntity(
                objectiveId = objectiveId,
                name = "Bronze",
                thresholdValue = 10_000,
                rank = 1
            ),
            MilestoneEntity(
                objectiveId = objectiveId,
                name = "Silver",
                thresholdValue = 15_000,
                rank = 2
            ),
            MilestoneEntity(
                objectiveId = objectiveId,
                name = "Gold",
                thresholdValue = 20_000,
                rank = 3
            ),
            MilestoneEntity(
                objectiveId = objectiveId,
                name = "Diamond",
                thresholdValue = 24_000,
                rank = 4
            )
        )
    )

    seedObjectiveForMetric(
        objectiveDao = objectiveDao,
        milestoneDao = milestoneDao,
        metricId = workoutsId,
        name = "Workouts ${today.year}",
        start = start,
        end = end,
        now = now,
        thresholds = listOf(
            3L to "Bronze",
            4L to "Silver",
            5L to "Gold",
            6L to "Diamond"
        )
    )
    seedObjectiveForMetric(
        objectiveDao = objectiveDao,
        milestoneDao = milestoneDao,
        metricId = sleepId,
        name = "Sleep ${today.year}",
        start = start,
        end = end,
        now = now,
        thresholds = listOf(
            3L to "Bronze",
            4L to "Silver",
            5L to "Gold",
            6L to "Diamond"
        )
    )
    seedObjectiveForMetric(
        objectiveDao = objectiveDao,
        milestoneDao = milestoneDao,
        metricId = leetcodeId,
        name = "Leetcode ${today.year}",
        start = start,
        end = end,
        now = now,
        thresholds = listOf(
            5L to "Bronze",
            10L to "Silver",
            15L to "Gold",
            20L to "Diamond"
        )
    )
    seedObjectiveForMetric(
        objectiveDao = objectiveDao,
        milestoneDao = milestoneDao,
        metricId = familyId,
        name = "Family Visits ${today.year}",
        start = start,
        end = end,
        now = now,
        thresholds = listOf(
            1L to "Bronze",
            2L to "Silver",
            3L to "Gold",
            4L to "Diamond"
        )
    )
}

private suspend fun seedObjectiveForMetric(
    objectiveDao: ObjectiveDao,
    milestoneDao: MilestoneDao,
    metricId: Long,
    name: String,
    start: LocalDate,
    end: LocalDate,
    now: Long,
    thresholds: List<Pair<Long, String>>
) {
    val objectiveId = objectiveDao.upsert(
        ObjectiveEntity(
            metricId = metricId,
            name = name,
            evaluationStart = start,
            evaluationEnd = end,
            aggregationType = ObjectiveAggregationType.TOTAL,
            polarity = ObjectivePolarity.HIGHER_IS_BETTER,
            createdAt = now
        )
    )
    milestoneDao.upsertAll(
        thresholds.mapIndexed { index, (value, label) ->
            MilestoneEntity(
                objectiveId = objectiveId,
                name = label,
                thresholdValue = value,
                rank = index + 1
            )
        }
    )
}
private suspend fun seedEntries(
    entryDao: EntryDao,
    today: LocalDate,
    workoutsId: Long,
    proteinId: Long,
    sleepId: Long,
    leetcodeId: Long,
    emergencyFundId: Long,
    weightId: Long,
    familyId: Long
) {
    val now = System.currentTimeMillis()

    // Daily examples (last week window)
    val lastWeekStart = today.minusWeeks(1).with(java.time.DayOfWeek.MONDAY)
    val lastWeekDays = (0..6).map { lastWeekStart.plusDays(it.toLong()) }

    entryDao.upsert(
        EntryEntity(
            metricId = workoutsId,
            periodStart = lastWeekDays[0],
            periodEnd = lastWeekDays[0],
            valueInt = null,
            valueBool = true,
            createdAt = now
        )
    )
    entryDao.upsert(
        EntryEntity(
            metricId = sleepId,
            periodStart = lastWeekDays[1],
            periodEnd = lastWeekDays[1],
            valueInt = null,
            valueBool = true,
            createdAt = now
        )
    )
    entryDao.upsert(
        EntryEntity(
            metricId = proteinId,
            periodStart = lastWeekDays[2],
            periodEnd = lastWeekDays[2],
            valueInt = null,
            valueBool = true,
            createdAt = now
        )
    )
    entryDao.upsert(
        EntryEntity(
            metricId = leetcodeId,
            periodStart = lastWeekDays[3],
            periodEnd = lastWeekDays[3],
            valueInt = 8,
            valueBool = null,
            createdAt = now
        )
    )
    entryDao.upsert(
        EntryEntity(
            metricId = workoutsId,
            periodStart = lastWeekDays[4],
            periodEnd = lastWeekDays[4],
            valueInt = null,
            valueBool = true,
            createdAt = now
        )
    )
    entryDao.upsert(
        EntryEntity(
            metricId = workoutsId,
            periodStart = lastWeekDays[5],
            periodEnd = lastWeekDays[5],
            valueInt = null,
            valueBool = true,
            createdAt = now
        )
    )
    entryDao.upsert(
        EntryEntity(
            metricId = workoutsId,
            periodStart = lastWeekDays[6],
            periodEnd = lastWeekDays[6],
            valueInt = null,
            valueBool = true,
            createdAt = now
        )
    )
    entryDao.upsert(
        EntryEntity(
            metricId = sleepId,
            periodStart = lastWeekDays[4],
            periodEnd = lastWeekDays[4],
            valueInt = null,
            valueBool = true,
            createdAt = now
        )
    )
    entryDao.upsert(
        EntryEntity(
            metricId = sleepId,
            periodStart = lastWeekDays[5],
            periodEnd = lastWeekDays[5],
            valueInt = null,
            valueBool = true,
            createdAt = now
        )
    )
    entryDao.upsert(
        EntryEntity(
            metricId = sleepId,
            periodStart = lastWeekDays[6],
            periodEnd = lastWeekDays[6],
            valueInt = null,
            valueBool = true,
            createdAt = now
        )
    )
    entryDao.upsert(
        EntryEntity(
            metricId = sleepId,
            periodStart = lastWeekDays[3],
            periodEnd = lastWeekDays[3],
            valueInt = null,
            valueBool = true,
            createdAt = now
        )
    )
    entryDao.upsert(
        EntryEntity(
            metricId = leetcodeId,
            periodStart = lastWeekDays[5],
            periodEnd = lastWeekDays[5],
            valueInt = 10,
            valueBool = null,
            createdAt = now
        )
    )
    entryDao.upsert(
        EntryEntity(
            metricId = leetcodeId,
            periodStart = lastWeekDays[6],
            periodEnd = lastWeekDays[6],
            valueInt = 4,
            valueBool = null,
            createdAt = now
        )
    )

    // Weekly example (last week)
    val lastWeekEnd = lastWeekStart.plusDays(6)
    entryDao.upsert(
        EntryEntity(
            metricId = familyId,
            periodStart = lastWeekStart,
            periodEnd = lastWeekEnd,
            valueInt = null,
            valueBool = true,
            createdAt = now
        )
    )

    // Monthly examples (last month)
    val lastMonthStart = today.minusMonths(1).withDayOfMonth(1)
    val lastMonthEnd = lastMonthStart.plusMonths(1).minusDays(1)
    val lastMonthWeeks = (0..3).map { lastMonthStart.plusDays(it.toLong() * 7L) }
    entryDao.upsert(
        EntryEntity(
            metricId = emergencyFundId,
            periodStart = lastMonthStart,
            periodEnd = lastMonthEnd,
            valueInt = 12000,
            valueBool = null,
            createdAt = now
        )
    )
    lastMonthWeeks.forEach { weekStart ->
        entryDao.upsert(
            EntryEntity(
                metricId = familyId,
                periodStart = weekStart,
                periodEnd = weekStart.plusDays(6),
                valueInt = null,
                valueBool = true,
                createdAt = now
            )
        )
    }
    entryDao.upsert(
        EntryEntity(
            metricId = weightId,
            periodStart = today.minusDays(1),
            periodEnd = today.minusDays(1),
            valueInt = 78,
            valueBool = null,
            createdAt = now
        )
    )
}
