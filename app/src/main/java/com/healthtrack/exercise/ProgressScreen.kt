package com.healthtrack.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.healthtrack.exercise.models.DailyProgress

@Composable
fun ProgressScreen(
    progressHistory: List<DailyProgress>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Progress History",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Statistics Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Today's Statistics",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val todayProgress = progressHistory.find { it.date == LocalDate.now() }
                val steps = todayProgress?.steps ?: 0
                val caloriesBurned = (steps * 0.04).toInt()
                val distanceKm = (steps * 0.0008).toFloat()

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Steps Today: $steps",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        "Calories Burned: $caloriesBurned kcal",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        "Distance: %.2f km".format(distanceKm),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Progress History Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Last 7 Days",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                progressHistory.take(7).forEach { progress ->
                    ProgressItem(progress)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ProgressItem(progress: DailyProgress) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = progress.date.format(dateFormatter),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(80.dp)
        )

        LinearProgressIndicator(
            progress = progress.steps.toFloat() / 10000, // Using 10k as max
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .padding(horizontal = 8.dp),
            color = if (progress.goalAchieved) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
        )

        Text(
            text = "${progress.steps}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(60.dp),
            textAlign = TextAlign.End
        )
    }
}
