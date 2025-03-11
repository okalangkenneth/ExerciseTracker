package com.healthtrack.exercise

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import com.healthtrack.exercise.models.DailyProgress

class MainViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepDetector: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    private val stepCounter: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private var initialStepCount = -1f
    private var lastSensorUpdate = 0L

    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    private val _stepInput = MutableStateFlow("")
    val stepInput: StateFlow<String> = _stepInput.asStateFlow()

    private val _dailyGoal = MutableStateFlow(5000)
    val dailyGoal: StateFlow<Int> = _dailyGoal.asStateFlow()

    private val _showCongratulations = MutableStateFlow(false)
    val showCongratulations: StateFlow<Boolean> = _showCongratulations.asStateFlow()

    private val _usePedometer = MutableStateFlow(false)
    val usePedometer: StateFlow<Boolean> = _usePedometer.asStateFlow()

    private val _reminderTime = MutableStateFlow("08:00")
    val reminderTime: StateFlow<String> = _reminderTime.asStateFlow()

    private val _progressHistory = MutableStateFlow<List<DailyProgress>>(listOf(
        DailyProgress(
            date = LocalDate.now(),
            steps = 0,
            goalAchieved = false
        )
    ))
    val progressHistory: StateFlow<List<DailyProgress>> = _progressHistory.asStateFlow()

    init {
        Log.d("MainViewModel", "Step detector available: ${stepDetector != null}")
        Log.d("MainViewModel", "Step counter available: ${stepCounter != null}")
        // Initialize with empty list
        _progressHistory.value = emptyList()
    }

    private fun generateSampleHistory(): List<DailyProgress> {
        // Return an empty list instead of sample data
        return emptyList()
    }

    fun updateCurrentScreen(screen: String) {
        _currentScreen.value = screen
    }

    fun updateStepInput(input: String) {
        _stepInput.value = input
    }

    fun addSteps() {
        _stepInput.value.toIntOrNull()?.let { newSteps ->
            _steps.value += newSteps
            _stepInput.value = ""
            updateProgress()
        }
    }

    fun resetSteps() {
        _steps.value = 0
        _showCongratulations.value = false
        initialStepCount = -1f
        updateProgress()
    }

    fun updateDailyGoal(goal: Int) {
        _dailyGoal.value = goal
        updateProgress()
    }

    fun updateUsePedometer(enabled: Boolean) {
        if (_usePedometer.value == enabled) return

        _usePedometer.value = enabled
        if (enabled) {
            registerSensors()
        } else {
            unregisterSensors()
        }
        Log.d("MainViewModel", "Pedometer ${if (enabled) "enabled" else "disabled"}")
    }

    fun updateReminderTime(time: String) {
        _reminderTime.value = time
    }

    private fun registerSensors() {
        stepCounter?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        stepDetector?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    private fun unregisterSensors() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!_usePedometer.value) return

        event?.let {
            val currentTime = System.currentTimeMillis()
            when (event.sensor.type) {
                Sensor.TYPE_STEP_DETECTOR -> {
                    if (currentTime - lastSensorUpdate > 200) { // Debounce threshold
                        _steps.value += 1
                        lastSensorUpdate = currentTime
                        updateProgress()
                    }
                }
                Sensor.TYPE_STEP_COUNTER -> {
                    val newSteps = event.values[0]
                    if (initialStepCount < 0) {
                        initialStepCount = newSteps
                    }
                    val stepCount = (newSteps - initialStepCount).toInt()
                    if (stepCount > 0) {
                        _steps.value = stepCount
                        updateProgress()
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("MainViewModel", "Sensor accuracy changed: $accuracy")
    }

    private fun updateProgress() {
        _showCongratulations.value = _steps.value >= _dailyGoal.value

        // Update progress history
        val today = LocalDate.now()
        val updatedHistory = _progressHistory.value.filterNot { it.date == today } +
                DailyProgress(
                    date = today,
                    steps = _steps.value,
                    goalAchieved = _steps.value >= _dailyGoal.value
                )
        _progressHistory.value = updatedHistory.sortedByDescending { it.date }
    }

    override fun onCleared() {
        super.onCleared()
        unregisterSensors()
    }
}
