package com.monitoring.heartrate.domain.network

import com.google.firebase.auth.UserInfo
import com.monitoring.heartrate.domain.model.ApiResponse
import com.monitoring.heartrate.domain.model.BaseResponse
import com.monitoring.heartrate.domain.model.DashboardData
import com.monitoring.heartrate.domain.model.Insight
import com.monitoring.heartrate.domain.model.LoginRequest
import com.monitoring.heartrate.domain.model.RegisterRequest
import com.monitoring.heartrate.domain.model.Summaries
import com.monitoring.heartrate.domain.model.ThresholdRequest
import com.monitoring.heartrate.domain.model.Thresholds
import com.monitoring.heartrate.domain.model.TrendsData
import com.monitoring.heartrate.domain.model.User
import com.monitoring.heartrate.domain.model.UserDTO
import com.monitoring.heartrate.domain.model.Zone
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {


    @GET("dashboard/{userId}")
    suspend fun getDashboardData(
        @Path("userId") userId: String
    ):  Response<ApiResponse<DashboardData>>

    @GET("trends/{userId}")
    suspend fun getHeartRateTrends(@Path("userId") userId: String,
                                   @Query("granularity") granularity: String

    ): Response<ApiResponse<TrendsData>>

        @GET("zones/{user_id}")
        suspend fun getZones(@Path("user_id") userId: String): Response<ApiResponse<Zone>>

        @GET("insights/{user_id}")
        suspend fun getInsights(@Path("user_id") userId: String): Response<ApiResponse<List<Insight>>>

        @GET("thresholds/{userId}")
        suspend fun getThresholds(@Path("userId") userId: String ): Response<ApiResponse<Thresholds>>

        @POST("thresholds/{userId}")
        suspend fun updateThresholds(
            @Path("userId") userId: String,
            @Body thresholds: ThresholdRequest
    ) : Response<BaseResponse>

    @GET("/healthReport/summaries/{userId}")
    suspend fun getSummaries(@Path("userId") userId: String): Response<ApiResponse<Summaries>>




}



