package com.omni.metrics.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "metrics"
)
data class MetricEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: MetricType,
    val unit: String?,
    val currentResolution: MetricResolution,
    val maxValueForColor: Int?,
    val accentColor: Int?,
    val iconKey: String?,
    val createdAt: Long
)

@Entity(
    tableName = "entries",
    foreignKeys = [
        ForeignKey(
            entity = MetricEntity::class,
            parentColumns = ["id"],
            childColumns = ["metricId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["metricId", "periodStart", "periodEnd"], unique = true),
        Index(value = ["metricId", "periodStart"])
    ]
)
data class EntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val metricId: Long,
    val periodStart: LocalDate,
    val periodEnd: LocalDate,
    val valueInt: Long?,
    val valueBool: Boolean?,
    val createdAt: Long
)

@Entity(
    tableName = "objectives",
    foreignKeys = [
        ForeignKey(
            entity = MetricEntity::class,
            parentColumns = ["id"],
            childColumns = ["metricId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["metricId"])
    ]
)
data class ObjectiveEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val metricId: Long,
    val name: String,
    val evaluationStart: LocalDate,
    val evaluationEnd: LocalDate,
    val aggregationType: ObjectiveAggregationType,
    val polarity: ObjectivePolarity,
    val createdAt: Long
)

@Entity(
    tableName = "milestones",
    foreignKeys = [
        ForeignKey(
            entity = ObjectiveEntity::class,
            parentColumns = ["id"],
            childColumns = ["objectiveId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["objectiveId"])
    ]
)
data class MilestoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val objectiveId: Long,
    val name: String,
    val thresholdValue: Long,
    val rank: Int
)

enum class MetricType {
    BOOLEAN,
    INT
}

enum class MetricResolution {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

enum class ObjectiveAggregationType {
    TOTAL,
    AVERAGE,
    LATEST
}

enum class ObjectivePolarity {
    HIGHER_IS_BETTER,
    LOWER_IS_BETTER
}
