package com.healthtrack.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.healthtrack.exercise.common.CommonBackground

@Composable
fun HomeScreen(onNavigateToTracker: () -> Unit) {
    CommonBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp), // Added padding for bottom navigation
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Welcome to Daily Strides",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
            )

            // Benefits section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Benefits of Daily Walking",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text("🫀 Improves heart health and circulation",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 2.dp))
                Text("💪 Strengthens muscles and bones",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 2.dp))
                Text("🧠 Enhances mental well-being",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 2.dp))
                Text("⚖️ Helps maintain healthy weight",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 2.dp))
                Text("😴 Improves sleep quality",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 2.dp))
                Text("🌟 Boosts energy levels",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 2.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onNavigateToTracker,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), // Added bottom padding
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                ),
                elevation = ButtonDefaults.buttonElevation(12.dp)
            ) {
                Text(
                    "Start Your Journey",
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.White
                )
            }
        }
    }
}
