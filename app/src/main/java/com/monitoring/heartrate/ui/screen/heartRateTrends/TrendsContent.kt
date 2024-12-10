package com.monitoring.heartrate.ui.screen.heartRateTrends

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import androidx.navigation.NavController
import com.github.mikephil.charting.formatter.ValueFormatter
import com.monitoring.heartrate.domain.model.TrendsData
import com.monitoring.heartrate.domain.model.Reading
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrendsContent(
    data: TrendsData,
    navController: NavController,
    selectedGranularity: String, // Granularity passed as parameter
    onGranularityChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Heart Rate Trends",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Debug: Check recomposition and granularity state
        Log.d("Recomposition", "TrendsContent recomposed with granularity: $selectedGranularity")

        // Radio buttons for selecting granularity
        val options = listOf("hourly", "daily", "all")
        options.forEach { granularity ->
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        if (selectedGranularity != granularity) {
                            onGranularityChange(granularity) // Notify ViewModel
                        }
                    }
            ) {
                RadioButton(
                    selected = selectedGranularity == granularity,
                    onClick = {
                        if (selectedGranularity != granularity) {
                            onGranularityChange(granularity) // Notify ViewModel
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = granularity.capitalize(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Line Chart displaying trends
        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    description.text = "Heart Rate Over Time"
                    legend.isEnabled = false

                    // Configure zoom and scroll
                    setPinchZoom(true)
                    isDoubleTapToZoomEnabled = true
                    setScaleEnabled(true)
                    isDragEnabled = true
                    isScaleXEnabled = true
                    isScaleYEnabled = false

                    // Configure axes
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.granularity = 1f
                    axisLeft.axisMinimum = data.readings.minOfOrNull { it.heartRate.toFloat() } ?: 0f
                    axisLeft.axisMaximum = data.readings.maxOfOrNull { it.heartRate.toFloat() } ?: 120f
                    axisRight.isEnabled = false

                    xAxis.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val date = Date(value.toLong())
                            val format = when (selectedGranularity) {
                                "hourly" -> SimpleDateFormat("HH:mm", Locale.getDefault())
                                "daily" -> SimpleDateFormat("dd MMM", Locale.getDefault())
                                else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            }
                            return format.format(date)
                        }
                    }

                    this.data = prepareLineData(data.readings)
                    invalidate() // Refresh the chart
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Back to Dashboard Button
        Button(
            onClick = { navController.navigate("dashboard") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Dashboard")
        }
    }
}

fun prepareLineData(readings: List<Reading>): LineData {
    val entries = readings.mapNotNull {
        try {
            Entry(it.timestamp.toFloat(), it.heartRate.toFloat())
        } catch (e: Exception) {
            Log.e("ChartData", "Invalid data point: $it", e)
            null
        }
    }

    if (entries.isEmpty()) {
        Log.e("ChartData", "No valid data points to display")
        return LineData()
    }

    val lineDataSet = LineDataSet(entries, "Heart Rate").apply {
        color = Color.BLUE
        valueTextColor = Color.BLACK
        lineWidth = 2f
        setCircleColor(Color.RED)
        circleRadius = 4f
        mode = LineDataSet.Mode.LINEAR
    }
    return LineData(lineDataSet)
}
