package com.healthtrack.exercise

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.healthtrack.exercise.models.DailyProgress
import kotlin.math.sqrt

class MainViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private val tag = "MainViewModel"
    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepDetector: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    private val stepCounter: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var initialStepCount = -1f
    private var lastSensorUpdate = 0L
    private var sensorRegistered = false
    private var debugCounter = 0

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

    private val _progressHistory = MutableStateFlow<List<DailyProgress>>(emptyList())
    val progressHistory: StateFlow<List<DailyProgress>> = _progressHistory.asStateFlow()

    // Create a custom accelerometer-based step counter if hardware sensors aren't available
    private val accelerometerStepCounter = AccelerometerStepCounter {
        viewModelScope.launch {
            _steps.value += 1
            updateProgress()
            Log.d(tag, "Accelerometer step detected, total: ${_steps.value}")
        }
    }

    // Add this function to print all available sensors (for debugging)
    private fun logAvailableSensors() {
        val allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
        Log.d(tag, "Available sensors (${allSensors.size}):")
        allSensors.forEach { sensor ->
            Log.d(tag, "Sensor: ${sensor.name}, Type: ${sensor.type}")
        }
    }

    init {
        // Log detailed sensor information
        logAvailableSensors()
        Log.d(tag, "Step detector available: ${stepDetector?.name ?: "Not available"}")
        Log.d(tag, "Step counter available: ${stepCounter?.name ?: "Not available"}")
        Log.d(tag, "Accelerometer available: ${accelerometer?.name ?: "Not available"}")

        // Initialize with empty progress history
        _progressHistory.value = emptyList()
        updateProgress() // Add the current day with 0 steps to history

        // Uncomment for debugging
        // debugStepSimulation()
    }

    private fun debugStepSimulation() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(5000)
                if (_usePedometer.value) {
                    debugCounter++
                    _steps.value += 100
                    Log.d(tag, "DEBUG: Simulated 100 steps, total: ${_steps.value}")
                    updateProgress()
                }
            }
        }
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
        Log.d(tag, "Pedometer enabled: $enabled")

        if (enabled) {
            registerSensors()
        } else {
            unregisterSensors()
        }
    }

    fun updateReminderTime(time: String) {
        _reminderTime.value = time
    }

    private fun registerSensors() {
        // Unregister first to avoid duplicates
        unregisterSensors()
        sensorRegistered = false

        // Try different sensor types with different sampling rates
        stepDetector?.let {
            // Try to register with multiple sensitivity levels
            val registered = sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST
            )
            if (registered) {
                sensorRegistered = true
                Log.d(tag, "Step detector registered with FASTEST delay")
            }
        }

        stepCounter?.let {
            val registered = sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
            if (registered) {
                sensorRegistered = true
                Log.d(tag, "Step counter registered with GAME delay")
            }
        }

        // Last resort: use the accelerometer for custom step detection
        if (!sensorRegistered && accelerometer != null) {
            val registered = sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME
            )
            if (registered) {
                sensorRegistered = true
                Log.d(tag, "Using accelerometer for step detection")
            }
        }

        if (!sensorRegistered) {
            Log.e(tag, "Failed to register any step sensors!")
        }
    }

    private fun unregisterSensors() {
        sensorManager.unregisterListener(this)
        accelerometerStepCounter.stop(sensorManager)
        Log.d(tag, "All sensors unregistered")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!_usePedometer.value) return

        event?.let {
            val currentTime = System.currentTimeMillis()
            val sensor = event.sensor

            when (sensor.type) {
                Sensor.TYPE_STEP_DETECTOR -> {
                    // Step detector directly reports steps (usually 1.0 per step)
                    // Add debounce to avoid counting every minor movement
                    if (currentTime - lastSensorUpdate > 500) { // 500ms debounce period
                        val steps = event.values[0].toInt()
                        _steps.value += steps
                        lastSensorUpdate = currentTime
                        Log.d(tag, "Step detector registered $steps steps, total: ${_steps.value}")
                        updateProgress()
                    } else {

                    }
                }

                Sensor.TYPE_STEP_COUNTER -> {
                    // Step counter gives the total steps since reboot
                    val totalSteps = event.values[0]
                    Log.d(tag, "Step counter raw value: $totalSteps")

                    if (initialStepCount < 0) {
                        initialStepCount = totalSteps
                        Log.d(tag, "Initial step count set to: $initialStepCount")
                    } else {
                        val stepsDelta = (totalSteps - initialStepCount).toInt()
                        if (stepsDelta > 0 && stepsDelta != _steps.value) {
                            _steps.value = stepsDelta
                            Log.d(tag, "Steps updated to: $stepsDelta")
                            updateProgress()
                        } else {

                        }
                    }
                }

                Sensor.TYPE_ACCELEROMETER -> {
                    // Custom accelerometer-based step detection
                    // This is a simplified algorithm - you may need a more sophisticated approach
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    // Calculate magnitude of acceleration
                    val magnitude = sqrt((x*x + y*y + z*z).toDouble()).toFloat()

                    // More strict peak detection with higher threshold and longer cooldown
                    if (magnitude > 15 && (currentTime - lastSensorUpdate) > 200) { // higher threshold and longer cooldown
                        // Log for debugging but don't count step yet
                        Log.d(tag, "Possible step detected with magnitude: $magnitude")

                        // Only count steps with stronger motion pattern
                        if (magnitude > 18) {
                            _steps.value += 1
                            lastSensorUpdate = currentTime
                            Log.d(tag, "Accelerometer detected step, total: ${_steps.value}")
                            updateProgress()
                    } else {

                        }

                    } else {

                    }
                }

                else -> {
                    // Handle any other sensor type
                    Log.d(tag, "Unhandled sensor type: ${sensor.type}")
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(tag, "Sensor accuracy changed: $accuracy")
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
