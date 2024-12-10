package com.monitoring.heartrate.ui.screen.login

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.monitoring.heartrate.data.TokenProvider
import com.monitoring.heartrate.domain.model.LoginRequest
import com.monitoring.heartrate.domain.model.Response
import com.monitoring.heartrate.domain.model.User
import com.monitoring.heartrate.domain.model.toUserModel
import com.monitoring.heartrate.domain.network.RetrofitBuilder
import com.monitoring.heartrate.domain.repository.AuthRepository
import com.monitoring.heartrate.ui.UserDataViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository

) : ViewModel() {
    private val _loginState = MutableStateFlow<Response<User>>(Response.Loading)
    val loginState: StateFlow<Response<User>> = _loginState
    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean
        get() = _isLoading.value

    fun login(
        email: String,
        password: String,
        navController: NavController,
        userDataViewModel: UserDataViewModel
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                authRepository.loginUser(email, password).collect { response ->
                    when (response) {
                        is Response.Success -> {
                            val user = response.data
                            TokenProvider.token = user.token // Store the token
                            Log.d("token", user.token+"")
                            userDataViewModel.setUser(user) // Pass user to UserDataViewModel
                            navController.navigate("dashboard") // Navigate to dashboard
                            _loginState.value = response
                        }
                        is Response.Failure -> {
                            _loginState.value = response
                        }
                        else -> {
                            _loginState.value = Response.Failure(Exception("Unexpected response"))
                        }
                    }
                }
            } catch (e: Exception) {
                _loginState.value = Response.Failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
