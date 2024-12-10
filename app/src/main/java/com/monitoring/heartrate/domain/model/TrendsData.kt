package com.monitoring.heartrate.domain.model

data class TrendsData(
    val readings: List<Reading>,
    val minThreshold: Int,
    val maxThreshold: Int
)

data class Reading(
    val timestamp: Long,
    val heartRate: Int
)
