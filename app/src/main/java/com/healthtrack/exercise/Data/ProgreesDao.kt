package com.healthtrack.exercise.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM progress ORDER BY date DESC")
    fun getAllProgress(): Flow<List<ProgressEntity>>

    @Query("SELECT * FROM progress WHERE date = :date")
    suspend fun getProgressForDate(date: String): ProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ProgressEntity)

    @Query("DELETE FROM progress WHERE date = :date")
    suspend fun deleteProgressForDate(date: String)
}