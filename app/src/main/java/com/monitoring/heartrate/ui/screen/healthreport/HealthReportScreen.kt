package com.monitoring.heartrate.ui.screen.healthreport
// AndroidX
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.monitoring.heartrate.ui.UserDataViewModel
import com.monitoring.heartrate.ui.screen.heartRateTrends.TrendsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthReportScreen(
    navController: NavController,
    userDataViewModel: UserDataViewModel,
    trendsViewModel: TrendsViewModel,
    viewModel: HealthReportViewModel
) {
    val summaries by viewModel.summariesData.collectAsState()
    val zonesData by viewModel.zonesData.collectAsState()
    val insights by viewModel.insightsData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val trends by trendsViewModel.trendsData.collectAsState()

    val user by userDataViewModel.user.collectAsState()

    LaunchedEffect(user) {
        user?.let {
            viewModel.setUserId(it.uid)
            trendsViewModel.setUserId(it.uid)
            viewModel.fetchSummaries()
            viewModel.fetchZones()
            viewModel.fetchInsights()
            trendsViewModel.fetchTrendsData("daily")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Report", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                errorMessage?.let {
                    Text(
                        text = "Error: $it",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                } ?: run {
                    HealthReportContent(
                        summaries = summaries,
                        zonesData = zonesData,
                        insights = insights,
                        navController = navController,
                        trendsData = trends?.readings
                    )
                }
            }
        }
    }
}
