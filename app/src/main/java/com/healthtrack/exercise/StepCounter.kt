package com.healthtrack.exercise

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StepCounter(private val context: Context) {
    private val tag = "StepCounter"
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    private val stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    private val stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    private var initialStepCount = -1L
    private var isRunning = false
    
    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    private val stepDetectorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            // Step detector returns 1.0 for each step
            _steps.value = _steps.value + 1
            Log.d(tag, "Step detected, total: ${_steps.value}")
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private val stepCounterListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val totalSteps = event.values[0].toLong()
            
            if (initialStepCount == -1L) {
                initialStepCount = totalSteps
            }
            
            val currentSteps = (totalSteps - initialStepCount).toInt()
            _steps.value = currentSteps
            Log.d(tag, "Step count updated: $currentSteps")
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private val accelerometerBasedCounter = AccelerometerStepCounter { 
        _steps.value = _steps.value + 1
        Log.d(tag, "Accelerometer step detected, total: ${_steps.value}")
    }

    fun start() {
        if (isRunning) return
        isRunning = true
        
        // Try to use step detector first
        if (stepDetector != null) {
            sensorManager.registerListener(
                stepDetectorListener,
                stepDetector,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            Log.d(tag, "Using step detector sensor")
        }
        // Also register step counter for accurate total counts
        else if (stepCounter != null) {
            sensorManager.registerListener(
                stepCounterListener,
                stepCounter,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            Log.d(tag, "Using step counter sensor")
        }
        // Fallback to accelerometer-based detection
        else if (accelerometer != null) {
            accelerometerBasedCounter.start(sensorManager, accelerometer)
            Log.d(tag, "Using accelerometer-based detection")
        } else {
            Log.e(tag, "No step counting sensors available")
        }
    }

    fun stop() {
        if (!isRunning) return
        isRunning = false
        
        sensorManager.unregisterListener(stepDetectorListener)
        sensorManager.unregisterListener(stepCounterListener)
        accelerometerBasedCounter.stop(sensorManager)
        
        Log.d(tag, "Step counter stopped")
    }

    fun reset() {
        _steps.value = 0
        initialStepCount = -1L
        Log.d(tag, "Step counter reset")
    }
}