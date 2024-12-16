package com.monitoring.heartrate.domain.model

data class Insight(
    val message: String,
    val severity: String,
    val action: String
)