package com.monitoring.heartrate.ui.screen.register

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.monitoring.heartrate.data.TokenProvider
import com.monitoring.heartrate.domain.model.RegisterRequest
import com.monitoring.heartrate.domain.model.Response
import com.monitoring.heartrate.domain.model.User
import com.monitoring.heartrate.domain.model.toUserModel
import com.monitoring.heartrate.domain.network.RetrofitBuilder
import com.monitoring.heartrate.domain.repository.AuthRepository
import com.monitoring.heartrate.ui.UserDataViewModel

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _registrationState = MutableStateFlow<Response<User>>(Response.Loading)
    val registrationState: StateFlow<Response<User>> = _registrationState

    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean
        get() = _isLoading.value

    fun register(
        email: String,
        password: String,
        displayName: String,
        photoUri: String,
        navController: NavController,
        userDataViewModel: UserDataViewModel, // Pass UserDataViewModel from the composable
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Call the registerUser method from AuthRepository
                authRepository.registerUser(email, password, displayName, photoUri).collect { response ->
                    when (response) {
                        is Response.Success -> {
                            val user = response.data
                            TokenProvider.token = user.token // Store the token
                            userDataViewModel.setUser(user) // Set the user in UserDataViewModel
                            navController.navigate("dashboard") {
                                popUpTo("register") { inclusive = true } // Clear the registration screen
                            }
                            _registrationState.value = response
                        }
                        is Response.Failure -> {
                            _registrationState.value = response
                        }
                        // Add an else branch if Response can have more cases
                        else -> {
                            _registrationState.value = Response.Failure(Exception("Unexpected response"))
                        }
                    }
                }
            } catch (e: Exception) {
                _registrationState.value = Response.Failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

}
