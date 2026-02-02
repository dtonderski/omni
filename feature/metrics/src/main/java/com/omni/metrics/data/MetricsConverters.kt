package com.omni.metrics.data

import androidx.room.TypeConverter
import java.time.LocalDate

class MetricsConverters {
    @TypeConverter
    fun localDateToEpochDay(value: LocalDate?): Long? = value?.toEpochDay()

    @TypeConverter
    fun epochDayToLocalDate(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)

    @TypeConverter
    fun metricTypeToString(value: MetricType?): String? = value?.name

    @TypeConverter
    fun stringToMetricType(value: String?): MetricType? = value?.let(MetricType::valueOf)

    @TypeConverter
    fun metricKindToString(value: MetricKind?): String? = value?.name

    @TypeConverter
    fun stringToMetricKind(value: String?): MetricKind? = value?.let(MetricKind::valueOf)

    @TypeConverter
    fun metricResolutionToString(value: MetricResolution?): String? = value?.name

    @TypeConverter
    fun stringToMetricResolution(value: String?): MetricResolution? =
        value?.let(MetricResolution::valueOf)

    @TypeConverter
    fun displayAggregationToString(value: DisplayAggregationType?): String? = value?.name

    @TypeConverter
    fun stringToDisplayAggregation(value: String?): DisplayAggregationType? =
        value?.let(DisplayAggregationType::valueOf)

    @TypeConverter
    fun aggregationTypeToString(value: ObjectiveAggregationType?): String? = value?.name

    @TypeConverter
    fun stringToAggregationType(value: String?): ObjectiveAggregationType? =
        value?.let(ObjectiveAggregationType::valueOf)

    @TypeConverter
    fun polarityToString(value: ObjectivePolarity?): String? = value?.name

    @TypeConverter
    fun stringToPolarity(value: String?): ObjectivePolarity? =
        value?.let(ObjectivePolarity::valueOf)
}
