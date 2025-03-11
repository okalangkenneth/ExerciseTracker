package com.healthtrack.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onShowTimePickerDialog: () -> Unit
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val steps by viewModel.steps.collectAsState()
    val stepInput by viewModel.stepInput.collectAsState()
    val dailyGoal by viewModel.dailyGoal.collectAsState()
    val showCongratulations by viewModel.showCongratulations.collectAsState()
    val usePedometer by viewModel.usePedometer.collectAsState()
    val progressHistory by viewModel.progressHistory.collectAsState()
    val reminderTime by viewModel.reminderTime.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Daily Strides") },
                actions = {
                    IconButton(
                        onClick = { viewModel.updateCurrentScreen("settings") }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentScreen == "home",
                    onClick = { viewModel.updateCurrentScreen("home") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Outlined.DirectionsWalk, contentDescription = "Tracker") },
                    label = { Text("Tracker") },
                    selected = currentScreen == "tracker",
                    onClick = { viewModel.updateCurrentScreen("tracker") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = "Progress") },
                    label = { Text("Progress") },
                    selected = currentScreen == "progress",
                    onClick = { viewModel.updateCurrentScreen("progress") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "About") },
                    label = { Text("About") },
                    selected = currentScreen == "about",
                    onClick = { viewModel.updateCurrentScreen("about") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentScreen) {
                "home" -> HomeScreen(
                    onNavigateToTracker = { viewModel.updateCurrentScreen("tracker") }
                )
                "tracker" -> TrackerScreen(
                    steps = steps,
                    stepInput = stepInput,
                    dailyGoal = dailyGoal,
                    showCongratulations = showCongratulations,
                    onStepInputChange = { viewModel.updateStepInput(it) },
                    onAddSteps = { viewModel.addSteps() },
                    onReset = { viewModel.resetSteps() }
                )
                "progress" -> ProgressScreen(
                    progressHistory = progressHistory
                )
                "about" -> AboutScreen()
                "settings" -> SettingsScreen(
                    dailyGoal = dailyGoal,
                    usePedometer = usePedometer,
                    reminderTime = reminderTime,
                    onDailyGoalChange = { viewModel.updateDailyGoal(it) },
                    onUsePedometerChange = { viewModel.updateUsePedometer(it) },
                    onSetReminder = onShowTimePickerDialog
                )
                else -> HomeScreen(
                    onNavigateToTracker = { viewModel.updateCurrentScreen("tracker") }
                )
            }
        }
    }
}
