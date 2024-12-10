package com.monitoring.heartrate.ui.screen.heartRateTrends

    import android.util.Log
    import androidx.compose.foundation.layout.*
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.ArrowBack
    import androidx.compose.material3.*
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import androidx.navigation.NavController
    import com.monitoring.heartrate.domain.model.TrendsData
    import com.monitoring.heartrate.domain.model.Response
    import com.monitoring.heartrate.ui.UserDataViewModel
    import com.monitoring.heartrate.ui.screen.compenets.LineChart
@OptIn(ExperimentalMaterial3Api::class)

@Composable
    fun HeartRateTrendsScreen(
        viewModel: TrendsViewModel,
        navController: NavController,
        userDataViewModel: UserDataViewModel
    ) {
        val trendsData by viewModel.trendsData.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val errorMessage by viewModel.errorMessage.collectAsState()
        val selectedGranularity by viewModel.selectedGranularity.collectAsState("hourly")

        val user by userDataViewModel.user.collectAsState()
        LaunchedEffect(user) {
            user?.let {
                viewModel.setUserId(it.uid)
                viewModel.fetchTrendsData("hourly")
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Heart Rate Trends", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    errorMessage != null -> {
                        Text(
                            text = "Error: $errorMessage",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                    trendsData != null -> {
                        TrendsContent(
                            data = trendsData!!,
                            navController = navController,
                            selectedGranularity = selectedGranularity,
                            onGranularityChange = { granularity ->
                                viewModel.updateGranularity(granularity)
                                Log.d("Granularity", "Selected granularity: $granularity")
                                viewModel.fetchTrendsData(granularity) // Call ViewModel when dropdown changes
                            }
                        )
                    }
                }
            }
        }
    }
