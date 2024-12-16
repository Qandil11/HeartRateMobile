package com.monitoring.heartrate.ui.screen.login



import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable


import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.monitoring.heartrate.domain.model.Response
import com.monitoring.heartrate.ui.UserDataViewModel

import com.monitoring.heartrate.ui.screen.compenets.DisplayError
import com.monitoring.heartrate.ui.screen.compenets.EmailAndPasswordInputs

import com.monitoring.heartrate.ui.screen.compenets.NavigationText


import com.monitoring.heartrate.util.Button.LoadingButton

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    navController: NavController,
    userDataViewModel: UserDataViewModel
) {

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val loginResult by loginViewModel.loginState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Heart Rate App",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Please log in to continue",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

            EmailAndPasswordInputs(
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                isError = loginResult is Response.Failure,
                showNameField = false,
                name = "",
                onNameChange = { },
            )

            // Display error message if loginResult is a failure
            DisplayError(loginResult)

            // Login Button
            LoadingButton(
                text = "Login", isLoading = loginViewModel.isLoading,
                enabled = !(email.isBlank() || password.isBlank()),
                textloading = "Login...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                onClick = {
                    loginViewModel.login(email, password, navController,userDataViewModel)
                },
            )

            // Navigation button to registration screen
            NavigationText(
                text = "Don't have an account? Register",
                onClick = {
                    navController.navigate("register")
                }
            )
        }
    }
}
