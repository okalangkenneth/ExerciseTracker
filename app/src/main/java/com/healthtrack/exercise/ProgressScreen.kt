package com.healthtrack.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.healthtrack.exercise.common.CommonBackground
import com.healthtrack.exercise.models.DailyProgress
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ProgressScreen(
    progressHistory: List<DailyProgress>
) {
    val todaySteps = progressHistory.firstOrNull { isSameDay(it.date, LocalDate.now()) }?.steps ?: 0
    val caloriesBurned = calculateCalories(todaySteps)
    val distanceInKm = calculateDistance(todaySteps)

    val last7DaysData = progressHistory
        .filter { daysBetween(it.date, LocalDate.now()) <= 7 }
        .sortedByDescending { it.date }

    CommonBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Progress History",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF3F3F3F)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Today's Statistics",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (todaySteps > 0) {
                            Text(
                                text = "Steps Today: $todaySteps",
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Text(
                                text = "Calories Burned: $caloriesBurned kcal",
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Text(
                                text = "Distance: ${String.format("%.2f", distanceInKm)} km",
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        } else {
                            Text(
                                text = "No steps recorded today",
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Text(
                                text = "Start tracking to see your statistics",
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF3F3F3F)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Last 7 Days",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (last7DaysData.isEmpty()) {
                            Text(
                                text = "No data available yet. Start tracking your steps!",
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            last7DaysData.forEach { data ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = formatDate(data.date),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${data.steps}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper functions
private fun calculateCalories(steps: Int): Int {
    // Average calorie burn is roughly 0.04 calories per step
    return (steps * 0.04).toInt()
}

private fun calculateDistance(steps: Int): Double {
    // Average stride length is roughly 0.762 meters
    // 1 step = 0.762 meters = 0.000762 km
    return steps * 0.000762
}

private fun isSameDay(date1: LocalDate, date2: LocalDate): Boolean {
    return date1.year == date2.year &&
            date1.dayOfYear == date2.dayOfYear
}

private fun daysBetween(date1: LocalDate, date2: LocalDate): Int {
    // Use LocalDate's built-in methods for calculating days between
    return Math.abs(date1.until(date2).days)
}

private fun formatDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd", Locale.getDefault())
    return date.format(formatter)
}
