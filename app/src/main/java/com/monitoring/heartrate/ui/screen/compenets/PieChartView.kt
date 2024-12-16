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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

// MPAndroidChart Templates
import com.github.mikephil.charting.utils.ColorTemplate

// Kotlin Collections
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.components.Legend
import com.monitoring.heartrate.domain.model.Zone

@Composable
fun PieChartView(zone: Zone?) {
    // Check if zone is null or contains all zero values
    if (zone == null || (zone.resting == 0 && zone.normal == 0 && zone.high == 0)) {
        Text(
            text = "No data available for Pie Chart",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )
        return
    }

    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(Color.WHITE)
                setEntryLabelTextSize(12f)
                setEntryLabelColor(Color.BLACK)

                // Configure legend
                legend.apply {
                    isEnabled = true
                    verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    setDrawInside(false)
                }
                setUsePercentValues(false)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        update = { pieChart ->
            // Safely prepare entries with non-zero values
            val entries = mutableListOf<PieEntry>()

            if (zone.resting > 0) entries.add(PieEntry(zone.resting.toFloat(), "Resting"))
            if (zone.normal > 0) entries.add(PieEntry(zone.normal.toFloat(), "Normal"))
            if (zone.high > 0) entries.add(PieEntry(zone.high.toFloat(), "High"))

            if (entries.isNotEmpty()) {
                val dataSet = PieDataSet(entries, "Health Zones").apply {
                    colors = listOf(Color.GREEN, Color.BLUE, Color.RED)
                    valueTextSize = 12f
                    valueTextColor = Color.BLACK
                }

                pieChart.data = PieData(dataSet)
                pieChart.invalidate() // Refresh the chart
            } else {
                // Clear chart if entries are empty
                pieChart.data = null
                pieChart.invalidate()
            }
        }
    )
}
