package com.monitoring.heartrate.domain.model

data class ApiResponse<T>(
    val data: T?,
    val success: Boolean
)
