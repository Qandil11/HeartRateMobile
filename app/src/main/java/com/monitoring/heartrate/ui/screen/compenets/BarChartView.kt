package com.monitoring.heartrate.ui.screen.compenets

// Jetpack Compose
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

// MPAndroidChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

// Android Colors
import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

// MPAndroidChart Templates
import com.github.mikephil.charting.utils.ColorTemplate

// Kotlin Collections
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.monitoring.heartrate.domain.model.Reading
import com.monitoring.heartrate.domain.model.Zone
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun BarChartView(data: List<Reading>) {
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawGridLines(false)
                    textColor = Color.BLACK
                    textSize = 12f
                }

                axisLeft.apply {
                    axisMinimum = 0f
                    axisMaximum = data.maxOf { it.heartRate.toFloat() } + 20f // Add padding
                    textColor = Color.BLACK
                    textSize = 12f
                    granularity = 10f
                }

                axisRight.isEnabled = false // Disable right Y-axis

                legend.apply {
                    isEnabled = true
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    textColor = Color.BLACK
                    textSize = 12f
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        update = { barChart ->
            val entries = data.mapIndexed { index, reading ->
                BarEntry(index.toFloat(), reading.heartRate.toFloat())
            }

            val dataSet = BarDataSet(entries, "Heart Rate Trends").apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList()
                valueTextSize = 12f
                valueTextColor = Color.BLACK
            }

            val dateFormatter = DateTimeFormatter.ofPattern("MMM dd")
            val xLabels = data.map { reading ->
                Instant.ofEpochMilli(reading.timestamp).atZone(ZoneOffset.UTC).format(dateFormatter)
            }

            barChart.xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)

            barChart.data = BarData(dataSet)
            barChart.invalidate() // Refresh the chart
        }
    )
}

