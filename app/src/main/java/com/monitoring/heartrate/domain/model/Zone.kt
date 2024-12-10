package com.monitoring.heartrate.domain.model

data class Zone(
    val resting: Int,    // Number of readings in the resting zone
    val normal: Int,     // Number of readings in the normal zone
    val high: Int        // Number of readings in the high zone
)
