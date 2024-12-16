package com.monitoring.heartrate.ui.screen.healthreport

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.text.font.FontWeight

import com.monitoring.heartrate.domain.model.Summaries
import com.monitoring.heartrate.domain.model.Zone
import com.monitoring.heartrate.domain.model.Insight
import com.monitoring.heartrate.domain.model.Reading

import com.monitoring.heartrate.ui.screen.compenets.PieChartView
import com.monitoring.heartrate.ui.screen.compenets.BarChartView
import com.monitoring.heartrate.ui.screen.compenets.InsightItem

@Composable
fun HealthReportContent(
    summaries: Summaries?,
    zonesData: Zone?,
    insights: List<Insight>?,
    navController: NavController,
    trendsData: List<Reading>?
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
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                summaries?.let {
                    SummaryCard("Average", summaries.avgHeartRate.toString(), Icons.Default.Favorite, Modifier.weight(1f))
                    SummaryCard("Max", summaries.maxHeartRate.toString(), Icons.Default.TrendingUp, Modifier.weight(1f))
                    SummaryCard("Min", summaries.minHeartRate.toString(), Icons.Default.TrendingDown, Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Health Zones Section
        item {
            zonesData?.let { zone ->
                Text(
                    "Health Zones",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(8.dp)
                )
                PieChartView(zone)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Trends Overview Section
        item {
            trendsData?.let { readings ->
                Text(
                    "Trends Overview",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    "Heart Rate Trends Over Time",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                BarChartView(
                    data = readings,
                    minThreshold = 60,
                    maxThreshold = 100
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Insights Section with Severity-Based Card Colors
        insights?.let {
            item {
                Text(
                    "Insights",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(8.dp)
                )
            }
            items(it) { insight ->
                InsightItem(insight, getSeverityColor(insight.severity)) // Updated to pass color
            }
        }

        // Threshold Management Button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate("threshold_management") {
                        popUpTo("health_report") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Text("Set Thresholds")
            }
        }
    }
}

/**
 * Utility function to map severity levels to colors.
 */
fun getSeverityColor(severity: String?): Color {
    return when (severity?.lowercase()) {
        "high" -> Color(0xFFFFCDD2) // Light red for High severity
        "medium" -> Color(0xFFFFF9C4) // Light yellow for Medium severity
        "low" -> Color(0xFFC8E6C9) // Light green for Low severity
        else -> Color(0xFFE0E0E0) // Default gray for unknown severity
    }
}
