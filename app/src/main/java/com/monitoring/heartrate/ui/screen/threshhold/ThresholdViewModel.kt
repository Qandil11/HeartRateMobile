package com.monitoring.heartrate.ui.screen.threshhold

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monitoring.heartrate.domain.model.Response
import com.monitoring.heartrate.domain.model.ThresholdRequest
import com.monitoring.heartrate.domain.model.Thresholds
import com.monitoring.heartrate.domain.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThresholdViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _thresholds = MutableStateFlow<Thresholds?>(null)
    val thresholds: StateFlow<Thresholds?> get()= _thresholds

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get()= _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get()= _errorMessage
    private val _minThreshold = MutableStateFlow(60) // Default or initial value
    val minThreshold: StateFlow<Int> get()= _minThreshold

    private val _maxThreshold = MutableStateFlow(120) // Default or initial value
    val maxThreshold: StateFlow<Int> get()= _maxThreshold

    fun updateMinThreshold(value: Int) {
        _minThreshold.update { value }
    }

    fun updateMaxThreshold(value: Int) {
        _maxThreshold.update { value }
    }
    private var userId: String = ""


    fun setUserId(id: String) {
        userId = id
    }
    fun fetchThresholds() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                dashboardRepository.getThresholds(userId).collect { response ->
                    when (response) {
                        is Response.Success -> {
                            response.data?.let { thresholds ->
                                _minThreshold.value = thresholds.minThreshold
                                _maxThreshold.value = thresholds.maxThreshold
                            }
                            _errorMessage.value = null
                        }
                        is Response.Failure -> {
                            _errorMessage.value = response.exception.message
                        }
                        else -> {
                            _errorMessage.value = "Unexpected response"
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateThresholds(userId: String, minThreshold: Int, maxThreshold: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = ThresholdRequest(minThreshold, maxThreshold)
                dashboardRepository.updateThresholds(userId, request).collect { response ->
                    when (response) {
                        is Response.Success -> {
                            fetchThresholds() // Refresh thresholds
                        }
                        is Response.Failure -> {
                            _errorMessage.value = response.exception.message
                        }
                        else -> {

                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
