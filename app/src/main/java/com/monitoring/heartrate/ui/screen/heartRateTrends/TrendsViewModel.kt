package com.monitoring.heartrate.ui.screen.heartRateTrends

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monitoring.heartrate.domain.model.Reading
import com.monitoring.heartrate.domain.model.TrendsData
import com.monitoring.heartrate.domain.model.Response
import com.monitoring.heartrate.domain.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class TrendsViewModel @Inject constructor(
    private val trendsRepository: DashboardRepository
) : ViewModel() {

    private var userId: String? = null

    private val _trendsData = MutableStateFlow<TrendsData?>(null)
    val trendsData: StateFlow<TrendsData?> get() = _trendsData

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _selectedGranularity = MutableStateFlow("hourly")
    val selectedGranularity: StateFlow<String> = _selectedGranularity
    fun setUserId(id: String) {
        userId = id
    }
    fun updateGranularity(granularity: String) {
        _selectedGranularity.value = granularity
    }
    fun fetchTrendsData(granularity: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val id = userId
                if (id != null) {
                    trendsRepository.getHeartRateTrends(id, granularity).collect { response ->
                        when (response) {
                            is Response.Success -> {
                                val parsedData = when (granularity) {
                                    "hourly", "daily" -> response.data // For grouped data
                                    "all" -> TrendsData( // Pass all readings directly
                                        readings = response.data.readings,
                                        minThreshold = response.data.minThreshold,
                                        maxThreshold = response.data.maxThreshold
                                    )
                                    else -> response.data // Default case
                                }
                                _trendsData.value = parsedData
                                _errorMessage.value = null
                            }
                            is Response.Failure -> {
                                _errorMessage.value = response.exception.message
                                _trendsData.value = null
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

}
