package com.monitoring.heartrate.domain.model


data class Thresholds(
    val minThreshold: Int,
                      val maxThreshold: Int)

data class ThresholdRequest(val minThreshold: Int, val maxThreshold: Int)