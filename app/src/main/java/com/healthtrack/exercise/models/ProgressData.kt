package com.healthtrack.exercise

import java.util.Date

/**
 * Data class representing a user's daily progress
 */
data class ProgressData(
    val date: Date,
    val steps: Int,
    val goalAchieved: Boolean
)