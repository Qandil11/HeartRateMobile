package com.monitoring.heartrate.domain.model


data class DashboardData(
    val avgHeartRate: Int,
    val abnormalCount: Int,
    val latestAlert: String,
    val userName: String
)
