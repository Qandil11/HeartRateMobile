package com.monitoring.heartrate.ui.screen.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.monitoring.heartrate.data.TokenProvider
import com.monitoring.heartrate.domain.model.DashboardData
import com.monitoring.heartrate.domain.model.Response
import com.monitoring.heartrate.domain.model.User
import com.monitoring.heartrate.domain.network.RetrofitBuilder
import com.monitoring.heartrate.domain.repository.AuthRepository
import com.monitoring.heartrate.domain.repository.DashboardRepository
import com.monitoring.heartrate.ui.UserDataViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private var userId: String? = null

    private val _dashboardData = MutableStateFlow<DashboardData?>(null)
    val dashboardData: StateFlow<DashboardData?> get() = _dashboardData

    private val _minThreshold = MutableStateFlow(60) // Default threshold
    private val _maxThreshold = MutableStateFlow(120) // Default threshold

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun setUserId(id: String) {
        userId = id
    }

    fun fetchDashboardData() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val id = userId

                if (id != null) {
                    dashboardRepository.getDashboardData(id).collect { response ->
                        when (response) {
                            is Response.Success -> {
                                val dashboard = response.data
                                fetchThresholdsAndSetAlert(dashboard)
                            }
                            is Response.Failure -> {
                                _errorMessage.value = response.exception.message
                                _dashboardData.value = null
                            }
                            else -> {
                                _errorMessage.value = "Unexpected response"
                            }
                        }
                    }
                } else {
                    _errorMessage.value = "User ID is null"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchThresholdsAndSetAlert(dashboard: DashboardData) {
        try {
            dashboardRepository.getThresholds(userId!!).collect { response ->
                when (response) {
                    is Response.Success -> {
                        val thresholds = response.data
                        _minThreshold.value = thresholds.minThreshold
                        _maxThreshold.value = thresholds.maxThreshold

                        // Check for alerts
                        val latestAlert = if (dashboard.avgHeartRate < _minThreshold.value ||
                            dashboard.avgHeartRate > _maxThreshold.value
                        ) {
                            "Heart rate ${dashboard.avgHeartRate}bpm is outside the threshold!"
                        } else {
                            "Heart rate is within normal range."
                        }

                        // Update dashboard data with the alert
                        _dashboardData.value = dashboard.copy(latestAlert = latestAlert)
                    }
                    is Response.Failure -> {
                        _errorMessage.value = response.exception.message
                        _dashboardData.value = dashboard // Fallback to the dashboard without alerts
                    }
                    else -> {
                        _errorMessage.value = "Unexpected response"
                    }
                }
            }
        } catch (e: Exception) {
            _errorMessage.value = e.message
        }
    }
}
