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
                    description.text = "Heart Rate Trends"
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
                    xAxis.textSize = 10f
                    axisLeft.axisMinimum = 50f // Adjusted minimum for clean display
                    axisLeft.axisMaximum = 150f // Adjusted max for reasonable range
                    axisRight.isEnabled = false

                    // Format X-axis based on granularity
                    xAxis.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val date = Date(value.toLong())
                            val format = when (selectedGranularity) {
                                "hourly" -> SimpleDateFormat("HH:mm", Locale.getDefault())
                                "daily" -> SimpleDateFormat("dd MMM", Locale.getDefault())
                                "all" -> SimpleDateFormat("dd MMM", Locale.getDefault())
                                else -> SimpleDateFormat("dd MMM", Locale.getDefault())
                            }
                            return format.format(date)
                        }
                    }


                    this.data = prepareLineData(data.readings, selectedGranularity)
                    invalidate() // Refresh the chart
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

fun prepareLineData(readings: List<Reading>, granularity: String): LineData {
    val groupedEntries = when (granularity) {
        "hourly" -> readings.groupBy { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it.timestamp)) }
        "daily" -> readings.groupBy { SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(it.timestamp)) }
        "all" -> readings.groupBy { SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(it.timestamp)) }
        else -> readings.groupBy { SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(it.timestamp)) }
    }

    val averagedEntries = groupedEntries.entries.mapIndexed { index, entry ->
        val avgHeartRate = entry.value.map { it.heartRate }.average().toFloat()
        Entry(index.toFloat(), avgHeartRate) // Use index for X-axis position
    }

    val dataSet = LineDataSet(averagedEntries, "Heart Rate").apply {
        color = Color.BLUE
        valueTextColor = Color.BLACK
        lineWidth = 2f
        setCircleColor(Color.RED)
        circleRadius = 4f
        mode = LineDataSet.Mode.CUBIC_BEZIER // Smooth lines
    }

    return LineData(dataSet)
}
