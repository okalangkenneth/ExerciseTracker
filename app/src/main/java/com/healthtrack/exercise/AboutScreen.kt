package com.healthtrack.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.healthtrack.exercise.common.CommonBackground

@Composable
fun AboutScreen() {
    CommonBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "About Daily Strides",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // App Features section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "App Features",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Column(
                        modifier = Modifier.padding(start = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("✓ Automatic step tracking with pedometer")
                        Text("✓ Manual step entry option")
                        Text("✓ Comprehensive progress history")
                        Text("✓ Customizable daily goals")
                        Text("✓ Daily reminders")
                        Text("✓ Health insights and statistics")
                    }
                }
            }

            // How to Track Your Steps section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "How to Track Your Steps",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Automatic Tracking
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(
                            text = "Automatic Tracking:",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Column(
                            modifier = Modifier.padding(start = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("• Enable the pedometer in Settings")
                            Text("• Steps are automatically counted as you walk")
                            Text("• All steps are automatically saved to your progress history")
                            Text("• No manual input needed - just carry your phone")
                        }
                    }

                    // Manual Tracking
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(
                            text = "Manual Tracking:",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Column(
                            modifier = Modifier.padding(start = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("• Go to the Tracker screen")
                            Text("• Enter your step count in the input field")
                            Text("• Click 'Add Steps' to record them")
                            Text("• Steps are automatically added to your progress history")
                            Text("• Use 'Reset' to start a new count")
                        }
                    }

                    // Progress History
                    Column {
                        Text(
                            text = "Progress History:",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Column(
                            modifier = Modifier.padding(start = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("• View your daily progress in the Progress screen")
                            Text("• See steps from both automatic and manual tracking")
                            Text("• Track your goal achievement rate")
                            Text("• Monitor calories burned and distance covered")
                        }
                    }
                }
            }

            // Tips for Success section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Tips for Success",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Column(
                        modifier = Modifier.padding(start = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("• Set realistic daily goals in Settings")
                        Text("• Enable reminders to stay consistent")
                        Text("• Check your progress regularly")
                        Text("• Celebrate reaching your daily goals!")
                    }
                }
            }

            // Add padding at the bottom to prevent content from being hidden by navigation bar
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
