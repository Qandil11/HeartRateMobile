package com.monitoring.heartrate.ui.screen.healthreport

// AndroidX
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Jetpack Compose UI
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.text.font.FontWeight

// Data Model
import com.monitoring.heartrate.domain.model.Summaries
import com.monitoring.heartrate.domain.model.Zone
import com.monitoring.heartrate.domain.model.Insight
import com.monitoring.heartrate.domain.model.Reading

// Utilities
import com.monitoring.heartrate.ui.screen.compenets.PieChartView
import com.monitoring.heartrate.ui.screen.compenets.BarChartView
import com.monitoring.heartrate.ui.screen.compenets.InsightItem

@Composable
fun HealthReportContent(
    summaries: Summaries?,
    zonesData: Zone?, // Ensure this is passed correctly
    insights: List<Insight>?,
    navController: NavController,
    trendsData: List<Reading>? // Updated to pass list of readings for trends
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Heart Rate Summary Section
        item {
            Text(
                "Heart Rate Summary",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), // Bold heading
                modifier = Modifier.padding(8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                summaries?.let {
                    SummaryCard("Average", summaries.avgHeartRate.toString(), Icons.Default.Favorite, modifier = Modifier.weight(1f))
                    SummaryCard("Max", summaries.maxHeartRate.toString(), Icons.Default.TrendingUp, modifier = Modifier.weight(1f))
                    SummaryCard("Min", summaries.minHeartRate.toString(), Icons.Default.TrendingDown, modifier = Modifier.weight(1f))

                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Health Zones Section
        item {
            zonesData?.let { zone ->
                Text(
                    "Health Zones",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), // Bold heading
                    modifier = Modifier.padding(8.dp)
                )
                PieChartView(zone)
                Spacer(modifier = Modifier.height(16.dp))
            }


        }

        // Trends Overview Section
        item {
            trendsData?.let { readings ->
                Text("Trends Overview", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(8.dp))
                Text("Heart Rate Trends Over Time", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
                BarChartView(readings) // Pass a list of readings for BarChartView
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Insights Section
        insights?.let {
            item {
                Text("Insights", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(8.dp))
            }
            items(it) { insight ->
                InsightItem(insight) // Render each Insight item
            }

        }

        // Threshold Management Button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate("threshold_management") {
                        popUpTo("health_report") { inclusive = true } // Clear HealthReportScreen from stack
                    }
                          },
                modifier = Modifier.fillMaxWidth()
                    .navigationBarsPadding() // Avoid navigation bar overlap

            ) {
                Text("Set Thresholds")
            }
        }
    }
}
