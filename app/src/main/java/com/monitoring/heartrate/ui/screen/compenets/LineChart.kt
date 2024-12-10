package com.monitoring.heartrate.ui.screen.compenets

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeParseException

import java.time.format.DateTimeFormatter


@Composable
fun LineChart(
    data: List<Pair<Long, Int>>, // List of epochMillis and heart rate readings
    minThreshold: Int,
    maxThreshold: Int,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Log.e("LineChart", "No data points to display")
        return
    }

    // Sort data by time
    val sortedData = data.sortedBy { it.first }

    Canvas(modifier = modifier.background(Color.LightGray)) {
        val width = size.width
        val height = size.height

        val minValue = sortedData.minOfOrNull { it.second } ?: 0
        val maxValue = sortedData.maxOfOrNull { it.second } ?: 0

        val normalizedData = sortedData.map { (time, value) ->
            val x = ((time - sortedData.first().first).toFloat() / (sortedData.last().first - sortedData.first().first).toFloat()) * width
            val y = height - ((value - minValue).toFloat() / (maxValue - minValue).toFloat() * height)
            x to y
        }

        Log.d("LineChart", "Normalized Data Points: $normalizedData")

        // Draw min and max threshold lines
        val minThresholdY = height - ((minThreshold - minValue) / (maxValue - minValue).toFloat() * height)
        val maxThresholdY = height - ((maxThreshold - minValue) / (maxValue - minValue).toFloat() * height)

        drawLine(
            color = Color.Red,
            start = androidx.compose.ui.geometry.Offset(0f, maxThresholdY),
            end = androidx.compose.ui.geometry.Offset(width, maxThresholdY),
            strokeWidth = 4.dp.toPx()
        )

        drawLine(
            color = Color.Green,
            start = androidx.compose.ui.geometry.Offset(0f, minThresholdY),
            end = androidx.compose.ui.geometry.Offset(width, minThresholdY),
            strokeWidth = 4.dp.toPx()
        )

        // Draw line chart path
        val path = Path().apply {
            normalizedData.forEachIndexed { index, point ->
                if (index == 0) {
                    moveTo(point.first, point.second)
                } else {
                    lineTo(point.first, point.second)
                }
            }
        }

        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 4.dp.toPx())
        )
    }
}
