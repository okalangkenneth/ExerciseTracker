package com.healthtrack.exercise

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log

/**
 * A manual pedometer that uses timing and user settings for step detection.
 * This approach doesn't use the accelerometer at all.
 */
class SimplePedometerSensor(
    private val context: Context,
    private val onStepDetected: () -> Unit,
    private val onDebugInfo: (String) -> Unit = {}
) {
    private val tag = "SimplePedometer"

    // User configurable settings
    private var stepsPerMinute = 100 // Default value (adjustable)
    private var isActive = false
    private val handler = Handler(Looper.getMainLooper())
    private var stepCount = 0
    private var lastStepTime = 0L

    // Step detection runnable
    private val stepRunnable = object : Runnable {
        override fun run() {
            val currentTime = System.currentTimeMillis()
            // Calculate time since last step
            val stepInterval = 60_000 / stepsPerMinute // milliseconds per step

            if (isActive && (currentTime - lastStepTime >= stepInterval)) {
                // Time for a new step
                stepCount++
                lastStepTime = currentTime
                onStepDetected()
                Log.d(tag, "Step detected! Count: $stepCount")
            }

            // Schedule next check
            handler.postDelayed(this, 100) // Check every 100ms
        }
    }

    /**
     * Start the pedometer with a specific steps per minute rate
     */
    fun start(stepsPerMinute: Int = 100) {
        if (isActive) return

        this.stepsPerMinute = stepsPerMinute
        isActive = true
        lastStepTime = System.currentTimeMillis()

        // Start the step timer
        handler.post(stepRunnable)

        Log.d(tag, "Pedometer started with $stepsPerMinute steps per minute")
        onDebugInfo("Pedometer running at $stepsPerMinute steps/min")
    }

    /**
     * Stop the pedometer
     */
    fun stop() {
        if (!isActive) return

        isActive = false
        handler.removeCallbacks(stepRunnable)

        Log.d(tag, "Pedometer stopped")
        onDebugInfo("Pedometer stopped")
    }

    /**
     * Adjust the walking pace
     */
    fun setWalkingPace(stepsPerMinute: Int) {
        this.stepsPerMinute = stepsPerMinute
        onDebugInfo("Pace set to $stepsPerMinute steps/min")
    }

    /**
     * Get the current step count
     */
    fun getStepCount(): Int = stepCount

    /**
     * Reset the step counter
     */
    fun resetSteps() {
        stepCount = 0
        onDebugInfo("Steps reset to 0")
    }
}
