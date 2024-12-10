package com.monitoring.heartrate.ui.screen.threshhold

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monitoring.heartrate.ui.UserDataViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ThresholdManagementScreen(
    navController: NavController,
    userDataViewModel: UserDataViewModel,
    viewModel: ThresholdViewModel
) {
    val minThreshold by viewModel.minThreshold.collectAsState()
    val maxThreshold by viewModel.maxThreshold.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var newMinThreshold by remember { mutableStateOf(minThreshold.toString()) }
    var newMaxThreshold by remember { mutableStateOf(maxThreshold.toString()) }

    val user by userDataViewModel.user.collectAsState()

    LaunchedEffect(user) {
        user?.let {
            viewModel.setUserId(it.uid)
            viewModel.fetchThresholds() // Fetch thresholds on user login
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Threshold Management", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    errorMessage?.let {
                        Text(
                            text = "Error: $it",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    // Input for Min Threshold
                    OutlinedTextField(
                        value = newMinThreshold,
                        onValueChange = { newMinThreshold = it },
                        label = { Text("Min Threshold") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input for Max Threshold
                    OutlinedTextField(
                        value = newMaxThreshold,
                        onValueChange = { newMaxThreshold = it },
                        label = { Text("Max Threshold") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save Button
                    Button(
                        onClick = {
                            userDataViewModel.getUser()?.let {
                                viewModel.updateThresholds(
                                    userId = it.uid,
                                    minThreshold = newMinThreshold.toIntOrNull() ?: 60,
                                    maxThreshold = newMaxThreshold.toIntOrNull() ?: 120
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Changes")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reset Button
                    Button(
                        onClick = {
                            newMinThreshold = "60"
                            newMaxThreshold = "120"
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("Reset to Default")
                    }
                }
            }
        }
    }
}
