package com.healthtrack.exercise.models

import java.time.LocalDate

data class DailyProgress(
    val date: LocalDate,
    val steps: Int,
    val goalAchieved: Boolean
)
