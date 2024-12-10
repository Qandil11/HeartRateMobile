package com.monitoring.heartrate.data

import android.util.Log
import com.monitoring.heartrate.domain.model.ApiResponse
import com.monitoring.heartrate.domain.model.DashboardData
import com.monitoring.heartrate.domain.model.Insight
import com.monitoring.heartrate.domain.network.ApiService
import com.monitoring.heartrate.domain.network.RetrofitBuilder
import com.monitoring.heartrate.domain.repository.DashboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import com.monitoring.heartrate.domain.model.Response
import com.monitoring.heartrate.domain.model.Summaries
import com.monitoring.heartrate.domain.model.ThresholdRequest
import com.monitoring.heartrate.domain.model.Thresholds
import com.monitoring.heartrate.domain.model.TrendsData
import com.monitoring.heartrate.domain.model.Zone

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

@Singleton
class DashboardRepositoryImpl @Inject constructor() : DashboardRepository {

    override suspend fun getDashboardData(userId: String): Flow<Response<DashboardData>> = flow {
        try {

            val response = RetrofitBuilder.retrofit.getDashboardData(userId)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    emit(Response.Success(data))
                } ?: emit(Response.Failure(Exception("Dashboard data fetch failed: Empty response body")))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Response.Failure(Exception("Dashboard data fetch failed: ${errorBody ?: "Unknown error"}")))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }.flowOn(Dispatchers.IO)


    override suspend fun getHeartRateTrends(userId: String, granularity: String): Flow<Response<TrendsData>> = flow {
        try {
            val response = RetrofitBuilder.retrofit.getHeartRateTrends(userId, granularity)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    emit(Response.Success(data))
                } ?: emit(Response.Failure(Exception("Trends data fetch failed: Empty response body")))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Response.Failure(Exception("Trends data fetch failed: ${errorBody ?: "Unknown error"}")))
            }

        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getThresholds(userId: String): Flow<Response<Thresholds>> = flow {
        try {
            val response = RetrofitBuilder.retrofit.getThresholds(userId)
            if (response.isSuccessful) {
                response.body()?.data?.let { thresholds ->
                    emit(Response.Success(thresholds))
                } ?: emit(Response.Failure(Exception("Failed to fetch thresholds: Empty response body")))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Response.Failure(Exception("Failed to fetch thresholds: ${errorBody ?: "Unknown error"}")))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun updateThresholds(userId: String, thresholds: ThresholdRequest): Flow<Response<Unit>> = flow {
        try {
            val response = RetrofitBuilder.retrofit.updateThresholds(userId, thresholds)
            if (response.isSuccessful) {
                emit(Response.Success(Unit))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Response.Failure(Exception("Failed to update thresholds: ${errorBody ?: "Unknown error"}")))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }.flowOn(Dispatchers.IO)
    override suspend fun getZones(userId: String): Flow<Response<Zone>> = flow {
        try {
            val response = RetrofitBuilder.retrofit.getZones(userId)
            if (response.isSuccessful) {
                response.body()?.data?.let { zones ->
                    emit(Response.Success(zones))
                } ?: emit(Response.Failure(Exception("Failed to fetch zones: Empty response body")))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Response.Failure(Exception("Failed to fetch zones: ${errorBody ?: "Unknown error"}")))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }.flowOn(Dispatchers.IO)
    override suspend fun getInsights(userId: String): Flow<Response<List<Insight>>> = flow {
        try {
            val response = RetrofitBuilder.retrofit.getInsights(userId)
            if (response.isSuccessful) {
                response.body()?.data?.let { insights ->
                    emit(Response.Success(insights))
                } ?: emit(Response.Failure(Exception("Failed to fetch insights: Empty response body")))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Response.Failure(Exception("Failed to fetch insights: ${errorBody ?: "Unknown error"}")))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }.flowOn(Dispatchers.IO)
    override suspend fun getSummaries(userId: String): Flow<Response<Summaries>> = flow {
        try {
            val response = RetrofitBuilder.retrofit.getSummaries(userId)
            if (response.isSuccessful) {
                response.body()?.data?.let { summaries ->
                    emit(Response.Success(summaries))
                } ?: emit(Response.Failure(Exception("Failed to fetch summaries: Empty response body")))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Response.Failure(Exception("Failed to fetch summaries: ${errorBody ?: "Unknown error"}")))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
