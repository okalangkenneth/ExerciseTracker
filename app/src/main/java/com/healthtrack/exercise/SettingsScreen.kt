package com.healthtrack.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.healthtrack.exercise.common.CommonBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    dailyGoal: Int,
    usePedometer: Boolean,
    reminderTime: String,
    onDailyGoalChange: (Int) -> Unit,
    onUsePedometerChange: (Boolean) -> Unit,
    onSetReminder: () -> Unit
) {
    CommonBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Daily Goal Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = dailyGoal.toString(),
                    onValueChange = {
                        val newValue = it.toIntOrNull() ?: return@OutlinedTextField
                        if (newValue > 0) onDailyGoalChange(newValue)
                    },
                    label = { Text("Daily Step Goal", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Text(
                    text = "Preset Goals",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { onDailyGoalChange(5000) },
                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                    ) {
                        Text("5,000")
                    }
                    Button(
                        onClick = { onDailyGoalChange(7500) },
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Text("7,500")
                    }
                    Button(
                        onClick = { onDailyGoalChange(10000) },
                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                    ) {
                        Text("10,000")
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Pedometer Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Use Pedometer", color = Color.White)
                    Switch(
                        checked = usePedometer,
                        onCheckedChange = onUsePedometerChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Reminder Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Daily Reminder", color = Color.White)
                        Text(
                            text = reminderTime,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    Button(
                        onClick = onSetReminder
                    ) {
                        Text("Set Time")
                    }
                }
            }
        }
    }
}
