package com.healthtrack.exercise.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "progress")
data class ProgressEntity(
    @PrimaryKey val date: String, // LocalDate stored as String
    val steps: Int,
    val goalAchieved: Boolean
)