package com.monitoring.heartrate.ui.screen.healthreport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monitoring.heartrate.domain.model.Insight
import com.monitoring.heartrate.domain.model.Response
import com.monitoring.heartrate.domain.model.Summaries
import com.monitoring.heartrate.domain.model.Zone
import com.monitoring.heartrate.domain.repository.DashboardRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
    class HealthReportViewModel @Inject constructor(
        private val dashboardRepository: DashboardRepository
    ) : ViewModel() {
    private val _zonesData = MutableStateFlow<Zone?>(null)
    val zonesData: StateFlow<Zone?> get() = _zonesData

    private val _insightsData = MutableStateFlow<List<Insight>?>(null)
    val insightsData: StateFlow<List<Insight>?> get()= _insightsData

    private val _summariesData = MutableStateFlow<Summaries?>(null)
    val summariesData: StateFlow<Summaries?> get()= _summariesData

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get()= _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get()= _isLoading

    private var userId: String? = null
    private val _isSummariesLoading = MutableStateFlow(false)

    private val _isZonesLoading = MutableStateFlow(false)

    private val _isInsightsLoading = MutableStateFlow(false)

    fun setUserId(id: String) {
        userId = id
    }


    fun fetchZones() {
        _isZonesLoading.value = true
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val id = userId
                if (id != null) {
                    dashboardRepository.getZones(id).collect { response ->
                        when (response) {
                            is Response.Success -> {
                                _zonesData.value = response.data
                                _errorMessage.value = null
                            }
                            is Response.Failure -> {
                                _errorMessage.value = response.exception.message
                                _zonesData.value = null
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
                _isZonesLoading.value = false
                _isLoading.value = false
            }
        }
    }
    fun fetchInsights() {
        _isInsightsLoading.value = true
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val id = userId
                if (id != null) {
                    dashboardRepository.getInsights(id).collect { response ->
                        when (response) {
                            is Response.Success -> {
                                _insightsData.value = response.data
                                _errorMessage.value = null
                            }
                            is Response.Failure -> {
                                _errorMessage.value = response.exception.message
                                _insightsData.value = null
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
                _isInsightsLoading.value = false
                _isLoading.value = false
            }
        }
    }
    fun fetchSummaries() {
        _isLoading.value = true
        _isSummariesLoading.value = true

        viewModelScope.launch {
            try {
                val id = userId
                if (id != null) {
                    dashboardRepository.getSummaries(id).collect { response ->
                        when (response) {
                            is Response.Success -> {
                                _summariesData.value = response.data
                                _errorMessage.value = null
                            }
                            is Response.Failure -> {
                                _errorMessage.value = response.exception.message
                                _summariesData.value = null
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
                _isSummariesLoading.value = false
            }
        }
    }


}