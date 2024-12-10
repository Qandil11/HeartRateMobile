package com.monitoring.heartrate.domain.model

data class Insight(
    val message: String,       // Example: "Your heart rate was above normal for 2 hours yesterday."
    val severity: String       // Example: "low", "moderate", "high"
)