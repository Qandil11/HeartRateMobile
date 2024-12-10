package com.monitoring.heartrate.domain.repository

import com.monitoring.heartrate.domain.model.DashboardData
import com.monitoring.heartrate.domain.model.Insight
import com.monitoring.heartrate.domain.model.Response
import com.monitoring.heartrate.domain.model.Summaries
import com.monitoring.heartrate.domain.model.ThresholdRequest
import com.monitoring.heartrate.domain.model.Thresholds
import com.monitoring.heartrate.domain.model.TrendsData
import com.monitoring.heartrate.domain.model.Zone
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
     suspend fun getDashboardData(userId: String): Flow<Response<DashboardData>>
     suspend fun getHeartRateTrends(userId: String, granularity: String): Flow<Response<TrendsData>>
     suspend fun getThresholds(userId: String): Flow<Response<Thresholds>>
     suspend fun updateThresholds(userId: String, thresholds: ThresholdRequest): Flow<Response<Unit>>
     suspend fun getZones(userId: String): Flow<Response<Zone>>
     suspend fun getInsights(userId: String): Flow<Response<List<Insight>>>
     suspend fun getSummaries(userId: String): Flow<Response<Summaries>>
}
