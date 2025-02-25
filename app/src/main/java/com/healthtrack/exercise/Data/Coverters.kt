package com.healthtrack.exercise.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromString(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    @TypeConverter
    fun dateToString(date: LocalDate): String {
        return date.toString()
    }
}