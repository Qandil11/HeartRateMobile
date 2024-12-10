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
import com.monitoring.heartrate.domain.model.Zone

@Composable
fun PieChartView(zone: Zone) {
    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(Color.WHITE)
                setEntryLabelTextSize(12f)
                setEntryLabelColor(Color.BLACK)
                legend.isEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
                setUsePercentValues(false)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        update = { pieChart ->
            val entries = listOf(
                PieEntry(zone.resting.toFloat(), "Resting"),
                PieEntry(zone.normal.toFloat(), "Normal"),
                PieEntry(zone.high.toFloat(), "High")
            )
            val dataSet = PieDataSet(entries, "Health Zones").apply {
                colors = ColorTemplate.MATERIAL_COLORS.map { it }
                valueTextSize = 12f
                valueTextColor = Color.BLACK
            }

            pieChart.data = PieData(dataSet)
            pieChart.invalidate() // Refresh the chart
        }
    )
}
