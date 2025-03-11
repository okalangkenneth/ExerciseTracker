package com.healthtrack.exercise

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class AccelerometerStepCounter(private val onStepDetected: () -> Unit) : SensorEventListener {
    
    private val accelerationThreshold = 10.0f // Minimum acceleration to consider
    private val stepThreshold = 6.0f // Threshold for step detection
    private val minStepInterval = 250_000_000L // Minimum time between steps (250ms)
    
    private var lastStepTime = 0L
    private var lastAcceleration = 0f
    private var isAscending = false
    
    private val gravity = FloatArray(3)
    private val alpha = 0.8f // Low-pass filter constant
    
    fun start(sensorManager: SensorManager, accelerometer: Sensor) {
        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_GAME
        )
    }
    
    fun stop(sensorManager: SensorManager) {
        sensorManager.unregisterListener(this)
    }
    
    override fun onSensorChanged(event: SensorEvent) {
        // Apply low-pass filter to remove gravity
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]
        
        // Remove gravity contribution
        val x = event.values[0] - gravity[0]
        val y = event.values[1] - gravity[1]
        val z = event.values[2] - gravity[2]
        
        // Calculate acceleration magnitude
        val acceleration = sqrt(x * x + y * y + z * z)
        
        // Detect steps using peak detection
        if (acceleration > accelerationThreshold) {
            val timeDiff = event.timestamp - lastStepTime
            
            if (acceleration > lastAcceleration && !isAscending) {
                isAscending = true
            } else if (acceleration < lastAcceleration && isAscending) {
                // Peak detected
                if (lastAcceleration > stepThreshold && timeDiff > minStepInterval) {
                    onStepDetected()
                    lastStepTime = event.timestamp
                }
                isAscending = false
            }
            
            lastAcceleration = acceleration
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}